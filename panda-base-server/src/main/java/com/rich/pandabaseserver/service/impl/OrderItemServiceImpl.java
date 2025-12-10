package com.rich.pandabaseserver.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.mapper.OrderItemMapper;
import com.rich.pandabaseserver.model.entity.OrderItem;
import com.rich.pandabaseserver.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单商品明细表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public List<OrderItem> listByOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("order_id = ?", orderId);
        return this.list(queryWrapper);
    }
}

