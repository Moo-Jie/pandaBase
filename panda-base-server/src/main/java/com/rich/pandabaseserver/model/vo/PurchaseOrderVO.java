package com.rich.pandabaseserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购买订单 VO
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "购买订单 VO")
public class PurchaseOrderVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long id;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 收货地址ID
     */
    @Schema(description = "收货地址ID")
    private Long addressId;

    /**
     * 收货人姓名
     */
    @Schema(description = "收货人姓名")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @Schema(description = "收货人手机号")
    private String receiverPhone;

    /**
     * 省份
     */
    @Schema(description = "省份")
    private String province;

    /**
     * 城市
     */
    @Schema(description = "城市")
    private String city;

    /**
     * 区县
     */
    @Schema(description = "区县")
    private String district;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String detailAddress;

    /**
     * 完整地址（拼接后的）
     */
    @Schema(description = "完整地址")
    private String fullAddress;

    /**
     * 订单总金额
     */
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    /**
     * 实际支付金额
     */
    @Schema(description = "实际支付金额")
    private BigDecimal payAmount;

    /**
     * 订单状态 0-待支付 1-已支付 2-已取消 3-已退款
     */
    @Schema(description = "订单状态")
    private Integer orderStatus;

    /**
     * 订单状态文本
     */
    @Schema(description = "订单状态文本")
    private String orderStatusText;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    /**
     * 取消时间
     */
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    @Schema(description = "取消原因")
    private String cancelReason;

    /**
     * 订单过期时间
     */
    @Schema(description = "订单过期时间")
    private LocalDateTime expireTime;

    /**
     * 微信支付交易号
     */
    @Schema(description = "微信支付交易号")
    private String transactionId;

    /**
     * 退款单号
     */
    @Schema(description = "退款单号")
    private String refundNo;

    /**
     * 退款时间
     */
    @Schema(description = "退款时间")
    private LocalDateTime refundTime;

    /**
     * 退款金额
     */
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 订单商品明细列表
     */
    @Schema(description = "订单商品明细列表")
    private List<OrderItemVO> orderItems;

    /**
     * 兑换码列表
     */
    @Schema(description = "兑换码列表")
    private List<String> redemptionCodes;
}
