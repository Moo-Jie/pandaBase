package com.rich.pandabaseserver.model.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 支付订单信息
 * </p>
 *

 * @date 2024/9/30 17:46
 */
@Data
public class PayOrderInfo {
    /**
     * 订单标题
     */
    private String description;
    /**
     * 商户订单号
     * 只能是数字、大小写字母_-*且在同一个商户号下唯一。
     */
    private String outTradeNo;
    /**
     * 支付金额，单位：元
     */
    private BigDecimal amount;
}

