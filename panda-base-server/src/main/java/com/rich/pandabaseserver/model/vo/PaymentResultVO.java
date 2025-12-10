package com.rich.pandabaseserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付结果 VO
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "支付结果 VO")
public class PaymentResultVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long orderId;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 支付是否成功
     */
    @Schema(description = "支付是否成功")
    private Boolean success;

    /**
     * 支付结果消息
     */
    @Schema(description = "支付结果消息")
    private String message;

    /**
     * 微信支付交易号
     */
    @Schema(description = "微信支付交易号")
    private String transactionId;

    /**
     * 会员卡号列表（如果购买的是虚拟票证）
     */
    @Schema(description = "会员卡号列表")
    private java.util.List<String> cardNumbers;
}

