package com.rich.pandabaseserver.model.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 退款请求
 *
 * @author DuRuiChi
 */
@Data
public class RefundOrderRequest implements Serializable {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 订单总金额
     */
    private java.math.BigDecimal totalAmount;

    /**
     * 退款金额
     */
    private java.math.BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String reason;

    private static final long serialVersionUID = 1L;
}
