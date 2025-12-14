package com.rich.pandabaseserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 兑换记录 VO
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "兑换记录 VO")
public class RedemptionRecordVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 兑换记录编号
     */
    @Schema(description = "兑换记录编号")
    private String recordNo;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 兑换码ID
     */
    @Schema(description = "兑换码ID")
    private Long redemptionCodeId;

    /**
     * 兑换码（脱敏显示）
     */
    @Schema(description = "兑换码（脱敏显示）")
    private String redemptionCode;

    /**
     * 兑换码（完整）
     */
    @Schema(description = "兑换码（完整）")
    private String fullRedemptionCode;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String productName;

    /**
     * 商品类型 1-年卡 2-月卡 3-次票 4-实物商品
     */
    @Schema(description = "商品类型")
    private Integer productType;

    /**
     * 商品类型文本
     */
    @Schema(description = "商品类型文本")
    private String productTypeText;

    /**
     * 状态 0-兑换中 1-已完成 2-已发货（实物）
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 状态文本
     */
    @Schema(description = "状态文本")
    private String statusText;

    /**
     * 收货地址JSON
     */
    @Schema(description = "收货地址")
    private String shippingAddress;

    /**
     * 物流单号
     */
    @Schema(description = "物流单号")
    private String trackingNumber;

    /**
     * 发货时间
     */
    @Schema(description = "发货时间")
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

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
}

