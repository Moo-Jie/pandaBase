package com.rich.pandabaseserver.controller;

import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.dto.order.CreateOrderRequest;
import com.rich.pandabaseserver.model.dto.order.QueryOrderRequest;
import com.rich.pandabaseserver.model.dto.order.RefundOrderRequest;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.service.UserService;
import com.rich.pandabaseserver.service.WxMiniappPayService;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * <p>
 * 微信小程序支付 前端控制器
 * </p>
 *
 * @date 2024/9/14 14:22
 */
@Slf4j
@RestController
@RequestMapping("/wxMiniappPay")
public class WxMiniappPayController {
    @Autowired
    private WxMiniappPayService wxMiniappPayService;

    @Autowired
    private UserService userService;

    /**
     * 预支付订单/统一下单
     *
     * @param req
     * @return
     */
    @PostMapping("/createOrder")
    public BaseResponse<PrepayWithRequestPaymentResponse> createOrder(@Validated @RequestBody CreateOrderRequest req, HttpServletRequest request) {
        log.info("------预支付订单/统一下单------");
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        //微信小程序登录用户openid，用户标识 说明：用户在商户appid下的唯一标识。
        String openid = loginUser.getOpenid();
        req.setWxOpenId(openid);
        return this.wxMiniappPayService.createOrder(req);
    }

    /**
     * 支付回调
     *
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/payNotify")
    public BaseResponse<String> payNotify(HttpServletRequest request) throws IOException {
        log.info("------收到微信支付回调通知------");
        return ResultUtils.success(this.wxMiniappPayService.payNotify(request));
    }

    /**
     * 根据支付订单号查询订单
     * <pre>
     *     主动查询订单结果
     *     支付订单号：业务侧的订单号
     * </pre>
     *
     * @param req
     * @return
     */
    @PostMapping("/queryOrder")
    public BaseResponse queryOrder(@Validated @RequestBody QueryOrderRequest req) {
        log.info("------根据支付订单号查询订单------");
        return this.wxMiniappPayService.queryOrder(req);
    }

    /**
     * 根据商户订单号查询订单
     * <pre>
     *     主动查询订单结果
     *     支付订单号：商户订单号
     * </pre>
     *
     * @param req
     * @return
     */
    @PostMapping("/queryOrderByOutTradeNo")
    public BaseResponse queryOrderByOutTradeNo(@Validated @RequestBody QueryOrderRequest req) {
        log.info("------根据商户订单号查询订单------");
        return this.wxMiniappPayService.queryOrderByOutTradeNo(req);
    }

    /**
     * 关闭订单
     *
     * @param req
     * @return
     */

    @PostMapping("/closeOrder")
    public BaseResponse closeOrder(@Validated @RequestBody QueryOrderRequest req) {
        log.info("------微信小程序支付关闭订单------");
        return this.wxMiniappPayService.closeOrder(req);
    }

    /**
     * 退款申请
     *
     * @param req
     * @return
     */
    @PostMapping("/refund")
    public BaseResponse refund(@Validated @RequestBody RefundOrderRequest req) {
        log.info("------微信支付退款------");
        return this.wxMiniappPayService.refund(req);
    }

    /**
     * 查询单笔退款（通过商户退款单号）
     *
     * @param outRefundNo 商户退款单号
     * @return
     */
    @GetMapping("/queryByOutRefundNo")
    public BaseResponse queryByOutRefundNo(String outRefundNo) {
        log.info("------微信支付查询单笔退款------");
        return this.wxMiniappPayService.queryByOutRefundNo(outRefundNo);
    }

    /**
     * 微信小程序退款回调
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/refundNotify")
    public BaseResponse<String> refundNotify(HttpServletRequest request) throws Exception {
        log.info("------微信支付微信小程序退款回调------");
        return ResultUtils.success(this.wxMiniappPayService.refundNotify(request));
    }
}

