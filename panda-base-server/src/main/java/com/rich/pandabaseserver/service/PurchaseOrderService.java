package com.rich.pandabaseserver.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.dto.order.OrderCreateRequest;
import com.rich.pandabaseserver.model.dto.order.OrderQueryRequest;
import com.rich.pandabaseserver.model.dto.order.PayOrderRequest;
import com.rich.pandabaseserver.model.entity.PurchaseOrder;
import com.rich.pandabaseserver.model.vo.PaymentResultVO;
import com.rich.pandabaseserver.model.vo.PurchaseOrderVO;

/**
 * 购买订单表 服务层。
 *
 * @author @author DuRuiChi
 */
public interface PurchaseOrderService extends IService<PurchaseOrder> {

    /**
     * 创建订单
     *
     * @param orderCreateRequest 订单创建请求
     * @return 订单ID
     */
    Long createOrder(OrderCreateRequest orderCreateRequest);

    /**
     * 支付订单
     *
     * @param payOrderRequest 支付订单请求
     * @return 支付结果
     */
    PaymentResultVO payOrder(PayOrderRequest payOrderRequest);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param cancelReason 取消原因
     * @return 是否成功
     */
    Boolean cancelOrder(Long orderId, Long userId, String cancelReason);

    /**
     * 获取查询条件包装器
     *
     * @param orderQueryRequest 订单查询请求
     * @return QueryWrapper
     */
    QueryWrapper getQueryWrapper(OrderQueryRequest orderQueryRequest);

    /**
     * 获取订单VO
     *
     * @param purchaseOrder 订单实体
     * @return 订单VO
     */
    PurchaseOrderVO getPurchaseOrderVO(PurchaseOrder purchaseOrder);

    /**
     * 订单过期自动取消（定时任务调用）
     */
    void autoExpireOrders();

    /**
     * 检查用户是否购买过指定商品
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已购买
     */
    Boolean checkUserPurchased(Long userId, Long productId);
}
