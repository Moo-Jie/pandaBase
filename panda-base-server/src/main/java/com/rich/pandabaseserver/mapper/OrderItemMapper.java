package com.rich.pandabaseserver.mapper;

import com.mybatisflex.core.BaseMapper;
import com.rich.pandabaseserver.model.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品明细表 映射层。
 *
 * @author @author DuRuiChi
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}

