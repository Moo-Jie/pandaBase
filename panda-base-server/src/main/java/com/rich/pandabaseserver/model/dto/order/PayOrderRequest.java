package com.rich.pandabaseserver.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付订单请求
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "支付订单请求")
public class PayOrderRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @Schema(description = "订单ID", required = true)
    private Long orderId;

    /**
     * 用户地址ID（如果是实物商品必填）
     */
    @Schema(description = "用户地址ID")
    private Long addressId;

    /**
     * 用户ID（从登录态获取，前端不传）
     */
    @Schema(hidden = true)
    private Long userId;
}
