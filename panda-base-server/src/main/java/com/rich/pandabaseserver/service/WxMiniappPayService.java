package com.rich.pandabaseserver.service;

import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.model.dto.order.CreateOrderRequest;
import com.rich.pandabaseserver.model.dto.order.QueryOrderRequest;
import com.rich.pandabaseserver.model.dto.order.RefundOrderRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>
 * 微信小程序支付服务类
 * </p>
 *

 * @date 2024/9/14 14:22
 */
public interface WxMiniappPayService {

    /**
     * 预支付订单/统一下单
     * @param req
     * @return
     */
    BaseResponse<PrepayWithRequestPaymentResponse> createOrder(CreateOrderRequest req);

    /**
     * 支付回调
     * @param request
     * @return
     * @throws IOException
     */
    String payNotify(HttpServletRequest request) throws IOException;

    /**
     * 根据支付订单号查询订单
     * @param req
     * @return
     */
    BaseResponse queryOrder(QueryOrderRequest req);

    /**
     * 根据商户订单号查询订单
     * @param req
     * @return
     */
    BaseResponse queryOrderByOutTradeNo(QueryOrderRequest req);

    /**
     * 关闭订单
     * @param req
     * @return
     */
    BaseResponse closeOrder(QueryOrderRequest req);

    /**
     * 退款
     * @param req
     * @return
     */
    BaseResponse refund(RefundOrderRequest req);

    /**
     * 查询单笔退款（通过商户退款单号）
     * @param outRefundNo 商户退款单号
     * @return
     */
    BaseResponse queryByOutRefundNo(String outRefundNo);

    /**
     * 微信小程序退款回调
     * @param request
     * @return
     */
    String refundNotify(HttpServletRequest request);
}

