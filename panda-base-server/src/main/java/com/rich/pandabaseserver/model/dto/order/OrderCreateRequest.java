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
    @Schema(description = "商品ID", required = true)
    private Long productId;

    /**
     * 购买数量
     */
    @Schema(description = "购买数量", required = true)
    private Integer quantity;

    /**
     * 用户ID（从登录态获取，前端不传）
     */
    @Schema(hidden = true)
    private Long userId;
}
