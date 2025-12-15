package com.rich.pandabaseserver.model.dto.order;

import lombok.Data;

/**
 * <p>
 * 订单查询请求
 * </p>
 *

 * @date 2024/9/30 19:19
 */
@Data
public class QueryOrderRequest {
    /**
     * 订单号：业务侧的订单号
     */
    private String transactionId;
    /**
     * 商户订单号
     */
    private String outTradeNo;
}

