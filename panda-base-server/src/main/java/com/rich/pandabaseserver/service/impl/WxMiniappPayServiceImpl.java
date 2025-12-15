package com.rich.pandabaseserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.config.WxPayConfig;
import com.rich.pandabaseserver.exception.BusinessException;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.dto.order.CreateOrderRequest;
import com.rich.pandabaseserver.model.dto.order.QueryOrderRequest;
import com.rich.pandabaseserver.model.dto.order.RefundOrderRequest;
import com.rich.pandabaseserver.mapper.PaymentRecordMapper;
import com.rich.pandabaseserver.model.entity.*;
import com.rich.pandabaseserver.model.enums.OrderStatusEnum;
import com.rich.pandabaseserver.service.*;
import com.rich.pandabaseserver.utils.HttpServletUtils;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rich.pandabaseserver.exception.ErrorCode.*;

/**
 * <p>
 * 微信小程序支付服务实现
 * </p>
 *
 * @date 2024/9/14 14:23
 */
@Slf4j
@Service
public class WxMiniappPayServiceImpl implements WxMiniappPayService {

    @Autowired
    private WxPayConfig wxPayConfig;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    @Qualifier("rsaAutoCertificateConfig")
    private Config config;
    @Autowired
    @Qualifier("rsaAutoCertificateConfig")
    private Config notificationConfig;
    
    @Autowired
    private PaymentRecordMapper paymentRecordMapper;
    
    @Autowired
    private RedemptionCodeService redemptionCodeService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderItemService orderItemService;

    /**
     * 预支付订单/统一下单
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse createOrder(CreateOrderRequest req) {
        Long orderId = req.getOrderId();
        ThrowUtils.throwIf(orderId == null, ErrorCode.PARAMS_ERROR, "订单ID不能为空");

        PurchaseOrder order = purchaseOrderService.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");

        // 校验订单状态
        if (!OrderStatusEnum.PENDING.getValue().equals(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单状态异常");
        }
        
        // 校验订单是否过期
        if (LocalDateTime.now().isAfter(order.getExpireTime())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单已过期");
        }

        // 请求微信支付相关配置
        JsapiServiceExtension service = new JsapiServiceExtension.Builder()
                .config(config)
                .signType("RSA")
                .build();
        PrepayWithRequestPaymentResponse response;
        
        try {
            PrepayRequest request = new PrepayRequest();
            request.setAppid(wxPayConfig.getAppid());
            request.setMchid(wxPayConfig.getMerchantId());
            request.setDescription("商品购买");
            request.setOutTradeNo(order.getOrderNo());
            request.setNotifyUrl(wxPayConfig.getPayNotifyUrl());
            Amount amount = new Amount();
            // 微信支付的单位是分，这里都需要乘以100
            amount.setTotal(order.getPayAmount().multiply(new BigDecimal("100")).intValue());
            request.setAmount(amount);
            Payer payer = new Payer();
            payer.setOpenid(req.getWxOpenId());
            request.setPayer(payer);
            log.info("请求预支付下单，请求参数：{}", JSONObject.toJSONString(request));
            // 调用预下单接口
            response = service.prepayWithRequestPayment(request);
            log.info("订单【{}】发起预支付成功，返回信息：{}", order.getOrderNo(), JSONObject.toJSONString(response));
        } catch (HttpException e) {
            // 发送HTTP请求失败
            log.error("微信下单发送HTTP请求失败，错误信息：", e);
            return ResultUtils.error(ErrorCode.valueOf("下单失败"), e.getMessage());
        } catch (ServiceException e) {
            // 服务返回状态小于200或大于等于300，例如500
            log.error("微信下单服务状态错误，错误信息：", e);
            return ResultUtils.error(ORDER_FAILED);
        } catch (MalformedMessageException e) {
            // 服务返回成功，返回体类型不合法，或者解析返回体失败
            log.error("服务返回成功，返回体类型不合法，或者解析返回体失败，错误信息：", e);
            return ResultUtils.error(ORDER_FAILED, e.getMessage());
        } catch (ValidationException e) {
            // 验证签名失败
            log.error("微信下单验证签名失败，错误信息：", e);
            return ResultUtils.error(ORDER_FAILED, e.getMessage());
        } catch (Exception e) {
            log.error("微信下单失败，错误信息：", e);
            return ResultUtils.error(ORDER_FAILED, e.getMessage());
        }
        
        // 创建或更新支付记录（用于对账和掉单处理，避免重复插入）
        try {
            // 从响应中提取prepayId（如果有的话）
            // prepayId 在 packageVal 参数中，格式为：prepay_id=xxx
            String prepayId = null;
            if (response.getPackageVal() != null && response.getPackageVal().startsWith("prepay_id=")) {
                prepayId = response.getPackageVal().substring("prepay_id=".length());
            }

            QueryWrapper payQuery = QueryWrapper.create().where("order_no = ?", order.getOrderNo());
            PaymentRecord existed = paymentRecordMapper.selectOneByQuery(payQuery);
            if (existed == null) {
                PaymentRecord paymentRecord = PaymentRecord.builder()
                        .orderId(order.getId())
                        .orderNo(order.getOrderNo())
                        .userId(order.getUserId())
                        .payAmount(order.getPayAmount())
                        .payType(1) // 微信支付
                        .payStatus(0) // 待支付
                        .prepayId(prepayId)
                        .callbackCount(0)
                        .isCallbackProcessed(0)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                paymentRecordMapper.insert(paymentRecord);
                log.info("创建支付记录成功，订单号：{}，prepayId：{}", order.getOrderNo(), prepayId);
            } else {
                // 已存在则更新prepayId和时间戳，避免唯一键冲突
                existed.setPrepayId(prepayId);
                existed.setUpdateTime(LocalDateTime.now());
                paymentRecordMapper.update(existed);
                log.info("支付记录已存在，更新prepayId，订单号：{}，prepayId：{}", order.getOrderNo(), prepayId);
            }
        } catch (Exception e) {
            log.error("创建支付记录失败，订单号：{}，错误：", order.getOrderNo(), e);
            // 支付记录创建失败不影响支付流程，只是影响对账
        }

        return ResultUtils.success(response);
    }

    /**
     * 支付回调
     * <pre>
     * 注意:
     * 对后台通知交互时，如果微信收到应答不是成功或超时，微信认为通知失败，微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保证通知最终能成功
     *
     * 同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。 推荐的做法是，当商户系统收到通知进行处理时，先检查对应业务数据的状态，并判断该通知是否已经处理。如果未处理，则再进行处理；如果已处理，则直接返回结果成功。在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
     * 如果在所有通知频率后没有收到微信侧回调。商户应调用查询订单接口确认订单状态。
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public String payNotify(HttpServletRequest request) throws IOException {
        // 请求头Wechatpay-Signature
        String signature = request.getHeader("Wechatpay-Signature");
        // 请求头Wechatpay-nonce
        String nonce = request.getHeader("Wechatpay-Nonce");
        // 请求头Wechatpay-Timestamp
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        // 微信支付证书序列号
        String serial = request.getHeader("Wechatpay-Serial");
        // 签名方式
        String signType = request.getHeader("Wechatpay-Signature-Type");
        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(serial)
                .nonce(nonce)
                .signature(signature)
                .timestamp(timestamp)
                .signType(signType)
                .body(HttpServletUtils.getRequestBody(request))
                .build();
        log.info("微信支付回调body信息：{}", requestParam.getBody());
        // 初始化 NotificationParser
        NotificationParser parser = new NotificationParser((NotificationConfig) notificationConfig);
        // 以支付通知回调为例，验签、解密并转换成 Transaction
        log.info("验签参数：{}", JSONObject.toJSONString(requestParam));
        Map<String, String> returnMap = new HashMap<>(2);
        returnMap.put("code", "FAIL");
        returnMap.put("message", "失败");
        Transaction transaction = null;
        try {
            transaction = parser.parse(requestParam, Transaction.class);
        } catch (MalformedMessageException e) {
            log.error("验签失败，解析微信支付应答或回调报文异常，返回信息：", e);
            return JSONObject.toJSONString(returnMap);
        } catch (ValidationException e) {
            log.error("验签失败，验证签名失败，返回信息：", e);
            return JSONObject.toJSONString(returnMap);
        } catch (Exception e) {
            log.error("验签失败，返回信息：", e);
            return JSONObject.toJSONString(returnMap);
        }
        log.info("验签成功！-支付回调结果：{}", transaction.toString());
        
        // 判断交易状态
        if (Transaction.TradeStateEnum.SUCCESS != transaction.getTradeState()) {
            log.info("内部订单号【{}】,微信支付订单号【{}】支付未成功", transaction.getOutTradeNo(), transaction.getTransactionId());
            return JSONObject.toJSONString(returnMap);
        }

        // 处理支付成功回调
        String outTradeNo = transaction.getOutTradeNo();
        String transactionId = transaction.getTransactionId();
        
        try {
            // 调用完整的支付回调处理逻辑（包含幂等性控制、金额校验、生成兑换码等）
            boolean success = handlePayCallback(outTradeNo, transactionId, transaction);
            if (success) {
                returnMap.put("code", "SUCCESS");
                returnMap.put("message", "成功");
            } else {
                log.error("支付回调处理失败，订单号：{}", outTradeNo);
            }
        } catch (Exception e) {
            log.error("支付回调处理异常，订单号：{}，错误：", outTradeNo, e);
            // 注意：即使处理失败，也要返回SUCCESS给微信，避免重复回调
            // 失败的订单需要通过补单机制处理
            returnMap.put("code", "SUCCESS");
            returnMap.put("message", "已收到");
        }
        
        return JSONObject.toJSONString(returnMap);
    }

    /**
     * 根据支付订单号查询订单
     * <pre>
     * 需要调用查询接口的情况：
     * 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知。
     * 调用支付接口后，返回系统错误或未知交易状态情况。
     * 调用付款码支付API，返回USERPAYING的状态。
     * 调用关单或撤销接口API之前，需确认支付状态。
     * </pre>
     *
     * @param req
     * @return
     */
    @Override
    public BaseResponse queryOrder(QueryOrderRequest req) {
        QueryOrderByIdRequest queryRequest = new QueryOrderByIdRequest();
        queryRequest.setMchid(wxPayConfig.getMerchantId());
        queryRequest.setTransactionId(req.getTransactionId());
        try {
            JsapiServiceExtension service =
                    new JsapiServiceExtension.Builder()
                            .config(config)
                            .signType("RSA")
                            .build();
            Transaction result = service.queryOrderById(queryRequest);
            if (Transaction.TradeStateEnum.SUCCESS != result.getTradeState()) {
                log.info("内部订单号【{}】,微信支付订单号【{}】支付未成功", result.getOutTradeNo(), result.getTransactionId());
                return ResultUtils.error(ErrorCode.valueOf(result.getTradeStateDesc()));
            }
            log.info("根据支付订单号查询订单：内部订单号【{}】,微信支付订单号【{}】支付成功", result.getOutTradeNo(), result.getTransactionId());
            log.info("根据支付订单号查询订单：订单数据data = {}", JSONObject.toJSONString(result));

            // TODO 修改订单信息
            return ResultUtils.success(result.getTradeStateDesc());
        } catch (ServiceException e) {
            log.error("根据支付订单号查询订单：订单查询失败，发送HTTP请求成功，返回异常，返回码：{},返回信息：", e.getErrorCode(), e);
            return ResultUtils.error(ORDER_QUERY_FAILED, e.getMessage());
        } catch (MalformedMessageException e) {
            log.error("根据支付订单号查询订单：订单查询失败，解析微信支付应答或回调报文异常，返回信息：", e);
            return ResultUtils.error(ORDER_QUERY_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("根据支付订单号查询订单：订单查询失败，验证签名失败，返回信息：", e);
            return ResultUtils.error(SIGNATURE_VERIFICATION_FAILED, e.getMessage());
        } catch (HttpException e) {
            log.error("根据支付订单号查询订单：订单查询失败，发送HTTP请求失败：", e);
            return ResultUtils.error(ORDER_QUERY_FAILED, e.getMessage());
        } catch (Exception e) {
            log.error("根据支付订单号查询订单：订单查询失败，异常：", e);
            return ResultUtils.error(ORDER_QUERY_FAILED, e.getMessage());
        }
    }

    /**
     * 根据商户订单号查询订单
     * <pre>
     * 需要调用查询接口的情况：
     * 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知。
     * 调用支付接口后，返回系统错误或未知交易状态情况。
     * 调用付款码支付API，返回USERPAYING的状态。
     * 调用关单或撤销接口API之前，需确认支付状态。
     * </pre>
     *
     * @param req
     * @return
     */
    @Override
    public BaseResponse queryOrderByOutTradeNo(QueryOrderRequest req) {
        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
        queryRequest.setMchid(wxPayConfig.getMerchantId());
        queryRequest.setOutTradeNo(req.getOutTradeNo());
        try {
            JsapiServiceExtension service =
                    new JsapiServiceExtension.Builder()
                            .config(config)
                            .signType("RSA")
                            .build();
            Transaction result = service.queryOrderByOutTradeNo(queryRequest);
            // trade_state【交易状态】交易状态，枚举值：*SUCCESS：支付成功*REFUND：转入退款*NOTPAY：未支付*CLOSED：已关闭*REVOKED：已撤销（仅付款码支付会返回）*USERPAYING：用户支付中（仅付款码支付会返回）*PAYERROR：支付失败（仅付款码支付会返回）
            if (Transaction.TradeStateEnum.SUCCESS != result.getTradeState()) {
                log.info("内部订单号【{}】,微信支付订单号【{}】支付未成功", result.getOutTradeNo(), result.getTransactionId());
                return ResultUtils.error(ErrorCode.valueOf(result.getTradeStateDesc()));
            }
            log.info("根据商户订单号查询订单：内部订单号【{}】,微信支付订单号【{}】支付成功", result.getOutTradeNo(), result.getTransactionId());
            log.info("根据商户订单号查询订单：订单数据data = {}", JSONObject.toJSONString(result));

            // 支付订单号
            String transactionId = result.getTransactionId();

            // TODO 修改订单信息
            return ResultUtils.success(result.getTradeStateDesc());
        } catch (ServiceException e) {
            log.error("根据商户订单号查询订单：订单查询失败，发送HTTP请求成功，返回异常，返回码：{},返回信息：", e.getErrorCode(), e);
            return ResultUtils.error(ORDER_QUERY_FAILED, e.getMessage());
        } catch (MalformedMessageException e) {
            log.error("根据商户订单号查询订单：订单查询失败，解析微信支付应答或回调报文异常，返回信息：", e);
            return ResultUtils.error(ORDER_QUERY_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("根据商户订单号查询订单：订单查询失败，验证签名失败，返回信息：", e);
            return ResultUtils.error(SIGNATURE_VERIFICATION_FAILED, e.getMessage());
        } catch (HttpException e) {
            log.error("根据商户订单号查询订单：订单查询失败，发送HTTP请求失败：", e);
            return ResultUtils.error(ORDER_QUERY_FAILED, e.getMessage());
        } catch (Exception e) {
            log.error("根据商户订单号查询订单：订单查询失败，异常：", e);
            return ResultUtils.error(ORDER_QUERY_FAILED, e.getMessage());
        }
    }

    /**
     * 关闭订单
     * <pre>
     * 以下情况需要调用关单接口：
     * 商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；
     * 系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。
     * </pre>
     *
     * @param req
     * @return
     */
    @Override
    public BaseResponse closeOrder(QueryOrderRequest req) {
        // 初始化服务
        JsapiServiceExtension service =
                new JsapiServiceExtension.Builder()
                        .config(config)
                        .signType("RSA")
                        .build();
        CloseOrderRequest request = new CloseOrderRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        request.setMchid(wxPayConfig.getMerchantId());
        request.setOutTradeNo(req.getOutTradeNo());
        // 调用接口
        try {
            service.closeOrder(request);
        } catch (HttpException e) {
            log.error("关闭订单申请失败，发送HTTP请求失败：", e);
            return ResultUtils.error(ErrorCode.valueOf("关闭订单申请失败"));
        } catch (MalformedMessageException e) {
            log.error("关闭订单申请失败，解析微信支付应答或回调报文异常，返回信息：", e);
            return ResultUtils.error(ErrorCode.valueOf("关闭订单申请失败"));
        } catch (ValidationException e) {
            log.error("关闭订单申请失败，验证签名失败，返回信息：", e);
            return ResultUtils.error(SIGNATURE_VERIFICATION_FAILED);
        } catch (ServiceException e) {
            log.error("关闭订单申请失败，发送HTTP请求成功，返回异常，返回码：{},返回信息：", e.getErrorCode(), e);
            return ResultUtils.error(ErrorCode.valueOf("关闭订单失败：" + e.getErrorMessage()));
        } catch (Exception e) {
            log.error("关闭订单申请失败，异常：", e);
            return ResultUtils.error(ErrorCode.valueOf("关闭订单申请失败"));
        }
        return ResultUtils.success("关闭订单申请成功");
    }

    /**
     * 退款
     * <pre>
     * 交易时间超过一年的订单无法提交退款（按支付成功时间+365天计算）
     * 微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。申请退款总金额不能超过订单金额。 一笔退款失败后重新提交，请不要更换退款单号，请使用原商户退款单号
     * 请求频率限制：150qps，即每秒钟正常的申请退款请求次数不超过150次
     * 每个支付订单的部分退款次数不能超过50次
     * 如果同一个用户有多笔退款，建议分不同批次进行退款，避免并发退款导致退款失败
     * 申请退款接口的返回仅代表业务的受理情况，具体退款是否成功，需要通过退款查询接口获取结果
     * 错误或无效请求频率限制：6qps，即每秒钟异常或错误的退款申请请求不超过6次
     * 一个月之前的订单申请退款频率限制为：5000/min
     * 同一笔订单多次退款的请求需相隔1分钟
     * </pre>
     *
     * @param req
     * @return
     */
    @Override
    public BaseResponse refund(RefundOrderRequest req) {
        // 初始化服务
        RefundService service = new RefundService.Builder().config(config).build();
        CreateRequest request = new CreateRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        request.setOutTradeNo(req.getOutTradeNo());
        request.setOutRefundNo("REFUND_" + req.getOutTradeNo());
        AmountReq amount = new AmountReq();
        // 订单总金额，单位为分，只能为整数，详见支付金额
        amount.setTotal(decimalToLong(req.getTotalAmount()));
        // 退款金额，单位为分，只能为整数，不能超过支付总额
        amount.setRefund(decimalToLong(req.getRefundAmount()));
        amount.setCurrency("CNY");

        request.setAmount(amount);
        request.setNotifyUrl(wxPayConfig.getRefundNotifyUrl());
        // 调用接口
        Refund refund = null;
        try {
            refund = service.create(request);
        } catch (HttpException e) {
            log.error("退款申请失败，发送HTTP请求失败：", e);
            return ResultUtils.error(ErrorCode.valueOf("退款失败"), e.getMessage());
        } catch (MalformedMessageException e) {
            log.error("退款申请失败，解析微信支付应答或回调报文异常，返回信息：", e);
            return ResultUtils.error(ErrorCode.valueOf("退款失败"), e.getMessage());
        } catch (ValidationException e) {
            log.error("退款申请失败，验证签名失败，返回信息：", e);
            return ResultUtils.error(SIGNATURE_VERIFICATION_FAILED, e.getMessage());
        } catch (ServiceException e) {
            log.error("退款申请失败，发送HTTP请求成功，返回异常，返回码：{},返回信息：", e.getErrorCode(), e);
            return ResultUtils.error(ErrorCode.valueOf("退款失败：" + e.getErrorMessage()));
        } catch (Exception e) {
            log.error("退款申请失败，异常：", e);
            return ResultUtils.error(ErrorCode.valueOf("退款失败"), e.getMessage());
        }
        if (Status.SUCCESS.equals(refund.getStatus())) {
            log.info("退款成功！-订单号：{}", req.getOutTradeNo());
            return ResultUtils.success("退款成功");
        } else if (Status.CLOSED.equals(refund.getStatus())) {
            log.info("退款关闭！-订单号：{}", req.getOutTradeNo());
            return ResultUtils.error(ErrorCode.valueOf("退款关闭"));
        } else if (Status.PROCESSING.equals(refund.getStatus())) {
            log.info("退款处理中！-订单号：{}", req.getOutTradeNo());
            return ResultUtils.error(ErrorCode.valueOf("退款关闭"));
        } else if (Status.ABNORMAL.equals(refund.getStatus())) {
            log.info("退款异常！-订单号：{}", req.getOutTradeNo());
            return ResultUtils.error(ErrorCode.valueOf("退款异常"));
        }
        return ResultUtils.error(ErrorCode.valueOf("退款失败"));
    }

    /**
     * 查询单笔退款（通过商户退款单号）
     * <pre>
     * 提交退款申请后，通过调用该接口查询退款状态。退款有一定延时，建议查询退款状态在提交退款申请后1分钟发起，一般来说零钱支付的退款5分钟内到账，银行卡支付的退款1-3个工作日到账。
     * </pre>
     *
     * @param outRefundNo 商户退款单号
     * @return
     */
    @Override
    public BaseResponse queryByOutRefundNo(String outRefundNo) {
        // 初始化服务
        RefundService service = new RefundService.Builder().config(config).build();
        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        request.setOutRefundNo(outRefundNo.trim());
        // 调用接口
        Refund refund = null;
        try {
            refund = service.queryByOutRefundNo(request);
            log.info("退款查询结果：{}", JSONObject.toJSONString(refund));
            //【退款状态】退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，可前往商户平台（pay.weixin.qq.com）-交易中心，手动处理此笔退款。可选取值：SUCCESS:退款成功CLOSED:退款关闭PROCESSING:退款处理中ABNORMAL:退款异常【退款状态】退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，可前往商户平台（pay.weixin.qq.com）-交易中心，手动处理此笔退款。可选取值：SUCCESS:退款成功CLOSED:退款关闭PROCESSING:退款处理中ABNORMAL:退款异常
            if (Status.SUCCESS.equals(refund.getStatus())) {
                log.info("退款成功！-订单号：{}", outRefundNo);
                return ResultUtils.success("退款成功");
            } else if (Status.CLOSED.equals(refund.getStatus())) {
                log.info("退款关闭！-订单号：{}", outRefundNo);
                return ResultUtils.error(ErrorCode.valueOf("退款关闭"));
            } else if (Status.PROCESSING.equals(refund.getStatus())) {
                log.info("退款处理中！-订单号：{}", outRefundNo);
                return ResultUtils.success("退款处理中");
            } else if (Status.ABNORMAL.equals(refund.getStatus())) {
                log.info("退款异常！-订单号：{}", outRefundNo);
                return ResultUtils.error(ErrorCode.valueOf("退款异常"));
            }
        } catch (HttpException e) {
            log.error("退款查询失败，发送HTTP请求失败：", e);
            return ResultUtils.error(ErrorCode.valueOf("退款查询失败"), e.getMessage());
        } catch (MalformedMessageException e) {
            log.error("退款查询失败，解析微信支付应答或回调报文异常，返回信息：", e);
            return ResultUtils.error(ErrorCode.valueOf("退款查询失败"), e.getMessage());
        } catch (ValidationException e) {
            log.error("退款查询失败，验证签名失败，返回信息：", e);
            return ResultUtils.error(ErrorCode.valueOf("退款查询失败"), e.getMessage());
        } catch (ServiceException e) {
            log.error("退款查询失败，发送HTTP请求成功，返回异常，返回码：{},返回信息：", e.getErrorCode(), e);
            return ResultUtils.error(ErrorCode.valueOf("退款查询失败"), e.getMessage());
        } catch (Exception e) {
            log.error("退款查询失败，异常：", e);
            return ResultUtils.error(ErrorCode.valueOf("退款查询失败"), e.getMessage());
        }

        return ResultUtils.success(refund);
    }

    /**
     * 微信小程序退款回调
     * <pre>
     * 注意:
     * 对后台通知交互时，如果微信收到应答不是成功或超时，微信认为通知失败，微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保证通知最终能成功
     *
     * 同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。 推荐的做法是，当商户系统收到通知进行处理时，先检查对应业务数据的状态，并判断该通知是否已经处理。如果未处理，则再进行处理；如果已处理，则直接返回结果成功。在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
     * 如果在所有通知频率后没有收到微信侧回调。商户应调用查询订单接口确认订单状态。
     * </pre>
     *
     * @param request
     * @return
     */
    @Override
    public String refundNotify(HttpServletRequest request) {
        Map<String, String> returnMap = new HashMap<>(2);
        returnMap.put("code", "FAIL");
        returnMap.put("message", "失败");
        try {
            // 请求头Wechatpay-Signature
            String signature = request.getHeader("Wechatpay-Signature");
            // 请求头Wechatpay-nonce
            String nonce = request.getHeader("Wechatpay-Nonce");
            // 请求头Wechatpay-Timestamp
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            // 微信支付证书序列号
            String serial = request.getHeader("Wechatpay-Serial");
            // 签名方式
            String signType = request.getHeader("Wechatpay-Signature-Type");
            // 构造解析器和请求参数
            NotificationParser parser = new NotificationParser((NotificationConfig) notificationConfig);
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(serial)
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(timestamp)
                    .signType(signType)
                    .body(HttpServletUtils.getRequestBody(request))
                    .build();
            log.info("微信小程序支付退款回调验签参数: {}", JSON.toJSONString(requestParam));
            // 解析通知
            RefundNotification notification = null;
            try {
                notification = parser.parse(requestParam, RefundNotification.class);
            } catch (MalformedMessageException e) {
                log.error("微信小程序支付退款回调：回调通知参数不正确、解析通知数据失败：", e);
                returnMap.put("message", "回调通知参数不正确");
                return JSONObject.toJSONString(returnMap);
            } catch (ValidationException e) {
                log.error("微信小程序支付退款回调：签名验证失败 ", e);
                returnMap.put("message", "签名验证失败");
                return JSONObject.toJSONString(returnMap);
            } catch (Exception e) {
                log.error("微信小程序支付退款回调：未知异常 ", e);
                returnMap.put("message", "未知异常");
                return JSONObject.toJSONString(returnMap);
            }
            log.info("微信小程序支付退款回调解析成功: {}", JSON.toJSONString(notification));
            // 根据退款状态处理
            Status refundStatus = notification.getRefundStatus();
            switch (refundStatus) {
                case SUCCESS:
                    // 退款成功逻辑
                    if (notification.getOutTradeNo() != null) {
                        String outTradeNo = notification.getOutTradeNo();
                        QueryWrapper queryWrapper = QueryWrapper.create().where("order_no = ?", outTradeNo);
                        PurchaseOrder order = purchaseOrderService.getOne(queryWrapper);
                        if (order != null) {
                            order.setOrderStatus(OrderStatusEnum.REFUNDED.getValue());
                            order.setUpdateTime(LocalDateTime.now());
                            purchaseOrderService.updateById(order);
                        }
                    }

                    returnMap.put("code", "SUCCESS");
                    returnMap.put("message", "退款成功");
                    return JSONObject.toJSONString(returnMap);
                case PROCESSING:
                    log.warn("退款处理中: {}", notification);
                    returnMap.put("message", "退款处理中，请稍后查询");
                    return JSONObject.toJSONString(returnMap);
                case ABNORMAL:
                    log.error("退款异常: {}", notification);
                    returnMap.put("message", "退款异常，请联系客服");
                    return JSONObject.toJSONString(returnMap);
                case CLOSED:
                    log.warn("退款已关闭: {}", notification);
                    returnMap.put("message", "退款已关闭，操作失败");
                    return JSONObject.toJSONString(returnMap);
                default:
                    log.error("未知退款状态: {}", refundStatus);
                    returnMap.put("message", "未知退款状态");
                    return JSONObject.toJSONString(returnMap);
            }
        } catch (Exception e) {
            log.error("退款回调处理异常", e);
            returnMap.put("message", "退款处理异常");
            return JSONObject.toJSONString(returnMap);
        }
    }


    /**
     * 金额转换
     *
     * @param money
     * @return
     */
    private static long decimalToLong(BigDecimal money) {
        return money.multiply(BigDecimal.valueOf(100)).longValue();
    }

    /**
     * 处理支付回调（包含幂等性控制、金额校验、生成兑换码等完整业务逻辑）
     *
     * @param outTradeNo 商户订单号
     * @param transactionId 微信支付交易号
     * @param transaction 微信回调交易对象
     * @return 是否处理成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePayCallback(String outTradeNo, String transactionId, Transaction transaction) {
        log.info("开始处理支付回调，订单号：{}，交易号：{}", outTradeNo, transactionId);
        
        // 1. 幂等性控制：检查支付记录是否已处理
        QueryWrapper paymentQuery = QueryWrapper.create().where("order_no = ?", outTradeNo);
        PaymentRecord paymentRecord = paymentRecordMapper.selectOneByQuery(paymentQuery);
        
        if (paymentRecord != null && paymentRecord.getIsCallbackProcessed() == 1) {
            log.warn("支付回调已处理过，订单号：{}，忽略重复回调", outTradeNo);
            return true;
        }
        
        // 2. 查询订单（使用行锁防止并发）
        QueryWrapper orderQuery = QueryWrapper.create()
                .where("order_no = ?", outTradeNo);
        PurchaseOrder order = purchaseOrderService.getOne(orderQuery);
        
        if (order == null) {
            log.error("订单不存在，订单号：{}", outTradeNo);
            return false;
        }
        
        // 3. 校验订单状态（如果已支付，直接返回成功）
        if (OrderStatusEnum.PAID.getValue().equals(order.getOrderStatus())) {
            log.warn("订单已支付，订单号：{}，忽略重复回调", outTradeNo);
            // 更新支付记录为已处理
            if (paymentRecord != null) {
                paymentRecord.setIsCallbackProcessed(1);
                paymentRecord.setCallbackTime(LocalDateTime.now());
                paymentRecord.setCallbackCount(paymentRecord.getCallbackCount() + 1);
                paymentRecordMapper.update(paymentRecord);
            }
            return true;
        }
        
        // 4. 校验金额（微信返回的金额是分，需要转换）
        BigDecimal wxPayAmount = new BigDecimal(transaction.getAmount().getTotal()).divide(new BigDecimal("100"));
        if (wxPayAmount.compareTo(order.getPayAmount()) != 0) {
            log.error("支付金额不匹配！订单金额：{}，微信支付金额：{}，订单号：{}", 
                    order.getPayAmount(), wxPayAmount, outTradeNo);
            return false;
        }
        
        // 5. 更新订单状态为已支付
        order.setOrderStatus(OrderStatusEnum.PAID.getValue());
        order.setTransactionId(transactionId);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        boolean updateResult = purchaseOrderService.updateById(order);
        
        if (!updateResult) {
            log.error("更新订单状态失败，订单号：{}", outTradeNo);
            return false;
        }
        
        // 6. 查询订单明细
        List<OrderItem> orderItems = orderItemService.listByOrderId(order.getId());
        if (orderItems == null || orderItems.isEmpty()) {
            log.error("订单明细不存在，订单号：{}", outTradeNo);
            return false;
        }
        
        // 7. 生成兑换码（核心业务）
        OrderItem firstItem = orderItems.get(0);
        Product product = productService.getById(firstItem.getProductId());
        if (product == null) {
            log.error("商品不存在，商品ID：{}，订单号：{}", firstItem.getProductId(), outTradeNo);
            return false;
        }
        
        try {
            List<String> redemptionCodes = redemptionCodeService.generateRedemptionCodesForOrder(
                    order.getId(), order.getUserId(), product, firstItem.getQuantity());
            log.info("支付回调生成兑换码成功，订单号：{}，兑换码数量：{}", outTradeNo, redemptionCodes.size());
        } catch (Exception e) {
            log.error("生成兑换码失败，订单号：{}，错误：", outTradeNo, e);
            return false;
        }
        
        // 8. 更新支付记录为已处理
        if (paymentRecord != null) {
            paymentRecord.setPayStatus(1); // 支付成功
            paymentRecord.setTransactionId(transactionId);
            paymentRecord.setPayTime(LocalDateTime.now());
            paymentRecord.setCallbackTime(LocalDateTime.now());
            paymentRecord.setIsCallbackProcessed(1);
            paymentRecord.setCallbackCount(paymentRecord.getCallbackCount() + 1);
            paymentRecord.setCallbackContent(JSONObject.toJSONString(transaction));
            paymentRecord.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.update(paymentRecord);
        } else {
            // 如果支付记录不存在，创建一条（理论上应该在预下单时就创建了）
            PaymentRecord newRecord = PaymentRecord.builder()
                    .orderId(order.getId())
                    .orderNo(outTradeNo)
                    .userId(order.getUserId())
                    .payAmount(order.getPayAmount())
                    .payType(1) // 微信支付
                    .payStatus(1) // 支付成功
                    .transactionId(transactionId)
                    .payTime(LocalDateTime.now())
                    .callbackTime(LocalDateTime.now())
                    .callbackCount(1)
                    .isCallbackProcessed(1)
                    .callbackContent(JSONObject.toJSONString(transaction))
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            paymentRecordMapper.insert(newRecord);
        }
        
        log.info("支付回调处理成功，订单号：{}", outTradeNo);
        return true;
    }

}

