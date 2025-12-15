package com.rich.pandabaseserver.model.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 退款订单请求参数
 * </p>
 *

 * @date 2024/9/30 19:19
 */
@Data
public class RefundOrderRequest {
    /**
     * 订单号：业务侧的订单号
     */
    private String transactionId;
    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 原订单金额 说明：原支付交易的订单总金额，这里单位为元。
     */
    private BigDecimal totalAmount;

    /**
     * 退款金额 说明：退款金额，这里单位为元，不能超过原订单支付金额。
     */
    private BigDecimal refundAmount;
}

