package com.rich.pandabaseserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品明细 VO
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "订单商品明细 VO")
public class OrderItemVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @Schema(description = "明细ID")
    private Long id;

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long orderId;

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
     * 商品图片
     */
    @Schema(description = "商品图片")
    private String productImage;

    /**
     * 单价
     */
    @Schema(description = "单价")
    private BigDecimal price;

    /**
     * 购买数量
     */
    @Schema(description = "购买数量")
    private Integer quantity;

    /**
     * 小计金额
     */
    @Schema(description = "小计金额")
    private BigDecimal subtotal;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

