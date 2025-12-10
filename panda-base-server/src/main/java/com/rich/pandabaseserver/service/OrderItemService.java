package com.rich.pandabaseserver.service;

import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.entity.OrderItem;

import java.util.List;

/**
 * 订单商品明细表 服务层。
 *
 * @author @author DuRuiChi
 */
public interface OrderItemService extends IService<OrderItem> {

    /**
     * 根据订单ID查询订单明细列表
     *
     * @param orderId 订单ID
     * @return 订单明细列表
     */
    List<OrderItem> listByOrderId(Long orderId);
}

