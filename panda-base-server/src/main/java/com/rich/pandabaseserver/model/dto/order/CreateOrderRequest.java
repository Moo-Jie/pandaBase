package com.rich.pandabaseserver.model.dto.order;

import lombok.Data;

/**
 * 预支付订单/统一下单请求参数
 *
 */
@Data
public class CreateOrderRequest {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 微信用户openid（前端不用传参）
     */
    private String wxOpenId;
}

