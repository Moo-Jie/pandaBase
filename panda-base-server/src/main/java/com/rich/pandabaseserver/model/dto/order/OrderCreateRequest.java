package com.rich.pandabaseserver.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单创建请求
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "订单创建请求")
public class OrderCreateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    /**
     * 购买数量
     */
    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    /**
     * 收货地址ID（实物商品和组合商品必填）
     */
    @Schema(description = "收货地址ID")
    private Long addressId;

    /**
     * 用户ID（从登录态获取，前端不传）
     */
    @Schema(hidden = true)
    private Long userId;
}
