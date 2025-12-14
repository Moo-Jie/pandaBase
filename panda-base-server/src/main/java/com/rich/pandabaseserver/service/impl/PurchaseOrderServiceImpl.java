package com.rich.pandabaseserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.constant.CommonConstant;
import com.rich.pandabaseserver.exception.BusinessException;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.mapper.PurchaseOrderMapper;
import com.rich.pandabaseserver.model.dto.order.OrderCreateRequest;
import com.rich.pandabaseserver.model.dto.order.OrderQueryRequest;
import com.rich.pandabaseserver.model.dto.order.PayOrderRequest;
import com.rich.pandabaseserver.model.entity.*;
import com.rich.pandabaseserver.model.enums.OrderStatusEnum;
import com.rich.pandabaseserver.model.enums.ProductTypeEnum;
import com.rich.pandabaseserver.model.vo.OrderItemVO;
import com.rich.pandabaseserver.model.vo.PaymentResultVO;
import com.rich.pandabaseserver.model.vo.PurchaseOrderVO;
import com.rich.pandabaseserver.service.*;
import com.rich.pandabaseserver.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购买订单表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Slf4j
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RedemptionCodeService redemptionCodeService;


    @Autowired
    private UserAddressService userAddressService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderCreateRequest orderCreateRequest) {
        // 1. 参数校验
        ThrowUtils.throwIf(orderCreateRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(orderCreateRequest.getUserId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(orderCreateRequest.getProductId() == null, ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        ThrowUtils.throwIf(orderCreateRequest.getQuantity() == null || orderCreateRequest.getQuantity() <= 0,
                ErrorCode.PARAMS_ERROR, "购买数量必须大于0");

        Long userId = orderCreateRequest.getUserId();
        Long productId = orderCreateRequest.getProductId();
        Integer quantity = orderCreateRequest.getQuantity();
        Long addressId = orderCreateRequest.getAddressId();

        // 2. 查询商品信息
        Product product = productService.getById(productId);
        ThrowUtils.throwIf(product == null, ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        ThrowUtils.throwIf(product.getStatus() != 1, ErrorCode.OPERATION_ERROR, "商品已下架");

        // 3. 验证地址（实物商品和组合商品必须有地址）
        Integer productType = product.getType();
        boolean needAddress = (productType == ProductTypeEnum.PHYSICAL_GOODS.getValue() 
                || productType == ProductTypeEnum.COMBO.getValue());
        
        UserAddress address = null;
        if (needAddress) {
            // 实物商品或组合商品必须提供地址
            ThrowUtils.throwIf(addressId == null, ErrorCode.PARAMS_ERROR, "请选择收货地址");
            
            // 查询并验证地址
            address = userAddressService.getById(addressId);
            ThrowUtils.throwIf(address == null, ErrorCode.NOT_FOUND_ERROR, "收货地址不存在");
            ThrowUtils.throwIf(!address.getUserId().equals(userId), 
                    ErrorCode.NO_AUTH_ERROR, "无权使用此地址");
        }

        // 4. 检查库存
        if (product.getStock() < quantity) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
        }

        // 5. 计算金额
        BigDecimal price = product.getPrice();
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal payAmount = totalAmount; // 暂无优惠

        // 6. 生成订单编号
        String orderNo = generateOrderNo();

        // 7. 创建订单（包含地址信息）
        PurchaseOrder.PurchaseOrderBuilder orderBuilder = PurchaseOrder.builder()
                .orderNo(orderNo)
                .userId(userId)
                .totalAmount(totalAmount)
                .payAmount(payAmount)
                .orderStatus(OrderStatusEnum.PENDING.getValue())
                .expireTime(LocalDateTime.now().plusMinutes(CommonConstant.ORDER_EXPIRE_MINUTES))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now());
        
        // 如果需要地址，保存地址信息到订单中（冗余存储，防止用户删除地址）
        if (needAddress && address != null) {
            orderBuilder
                    .addressId(address.getId())
                    .receiverName(address.getReceiverName())
                    .receiverPhone(address.getPhone())
                    .province(address.getProvince())
                    .city(address.getCity())
                    .district(address.getDistrict())
                    .detailAddress(address.getDetailAddress());
        }
        
        PurchaseOrder order = orderBuilder.build();
        boolean saveResult = this.save(order);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "创建订单失败");

        // 8. 创建订单明细
        OrderItem orderItem = OrderItem.builder()
                .orderId(order.getId())
                .productId(productId)
                .productName(product.getName())
                .productImage(product.getImageUrl())
                .price(price)
                .quantity(quantity)
                .subtotal(totalAmount)
                .createTime(LocalDateTime.now())
                .build();

        boolean saveItemResult = orderItemService.save(orderItem);
        ThrowUtils.throwIf(!saveItemResult, ErrorCode.OPERATION_ERROR, "创建订单明细失败");

        // 9. 扣减库存
        boolean updateStockResult = productService.updateStock(productId, -quantity);
        ThrowUtils.throwIf(!updateStockResult, ErrorCode.OPERATION_ERROR, "扣减库存失败");

        log.info("创建订单成功，订单号：{}，用户ID：{}，商品ID：{}，需要地址：{}", 
                orderNo, userId, productId, needAddress);
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO payOrder(PayOrderRequest payOrderRequest) {
        // 1. 参数校验
        ThrowUtils.throwIf(payOrderRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(payOrderRequest.getUserId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(payOrderRequest.getOrderId() == null, ErrorCode.PARAMS_ERROR, "订单ID不能为空");

        Long userId = payOrderRequest.getUserId();
        Long orderId = payOrderRequest.getOrderId();
        Long addressId = payOrderRequest.getAddressId();

        // 2. 查询订单
        PurchaseOrder order = this.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        ThrowUtils.throwIf(!order.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR, "无权限操作此订单");

        // 3. 校验订单状态
        if (!order.getOrderStatus().equals(OrderStatusEnum.PENDING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单状态异常，无法支付");
        }

        // 4. 校验订单是否过期
        if (LocalDateTime.now().isAfter(order.getExpireTime())) {
            // 自动取消订单
            this.cancelOrder(orderId, userId, "订单已过期");
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单已过期");
        }

        // 5. 查询订单商品明细
        List<OrderItem> orderItems = orderItemService.listByOrderId(orderId);
        ThrowUtils.throwIf(orderItems == null || orderItems.isEmpty(), ErrorCode.NOT_FOUND_ERROR, "订单明细不存在");

        // 6. 判断商品类型，如果是实物商品需要校验地址
        OrderItem firstItem = orderItems.get(0);
        Product product = productService.getById(firstItem.getProductId());
        ThrowUtils.throwIf(product == null, ErrorCode.NOT_FOUND_ERROR, "商品不存在");

        // 如果是实物商品或组合商品，需要地址
        if (product.getType().equals(ProductTypeEnum.PHYSICAL_GOODS.getValue())
                || product.getType().equals(ProductTypeEnum.COMBO.getValue())) {
            ThrowUtils.throwIf(addressId == null, ErrorCode.PARAMS_ERROR, "实物商品需要填写收货地址");
            UserAddress address = userAddressService.getById(addressId);
            ThrowUtils.throwIf(address == null || !address.getUserId().equals(userId),
                    ErrorCode.PARAMS_ERROR, "收货地址无效");
        }

        // 7. 调用微信支付
        // TODO：模拟支付成功，有微信公众平台应用ID和商户号后再实现具体逻辑
        String transactionId = mockWeChatPay(order);

        // 8. 更新订单状态为已支付
        order.setOrderStatus(OrderStatusEnum.PAID.getValue());
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId(transactionId);
        order.setUpdateTime(LocalDateTime.now());
        boolean updateResult = this.updateById(order);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新订单状态失败");

        // 9. 无论虚拟商品还是实物商品，都生成兑换码
        List<String> redemptionCodes;
        redemptionCodes = redemptionCodeService.generateRedemptionCodesForOrder(orderId, userId, product, firstItem.getQuantity());
        
        log.info("订单支付成功，已生成兑换码，订单ID：{}，兑换码数量：{}", orderId, redemptionCodes.size());

        // 10. 构建支付结果
        PaymentResultVO result = new PaymentResultVO();
        result.setOrderId(orderId);
        result.setOrderNo(order.getOrderNo());
        result.setSuccess(true);
        result.setMessage("支付成功，请前往个人中心-礼品兑换进行兑换");
        result.setTransactionId(transactionId);
        result.setCardNumbers(redemptionCodes);

        log.info("支付订单成功，订单号：{}，用户ID：{}", order.getOrderNo(), userId);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long orderId, Long userId, String cancelReason) {
        // 1. 参数校验
        ThrowUtils.throwIf(orderId == null, ErrorCode.PARAMS_ERROR, "订单ID不能为空");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR);

        // 2. 查询订单
        PurchaseOrder order = this.getById(orderId);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        ThrowUtils.throwIf(!order.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR, "无权限操作此订单");

        // 3. 只有待支付状态的订单才能取消
        if (!order.getOrderStatus().equals(OrderStatusEnum.PENDING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "当前订单状态无法取消");
        }

        // 4. 更新订单状态
        order.setOrderStatus(OrderStatusEnum.CANCELLED.getValue());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(StrUtil.isBlank(cancelReason) ? "用户主动取消" : cancelReason);
        order.setUpdateTime(LocalDateTime.now());
        boolean updateResult = this.updateById(order);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "取消订单失败");

        // 5. 恢复库存
        List<OrderItem> orderItems = orderItemService.listByOrderId(orderId);
        for (OrderItem item : orderItems) {
            productService.updateStock(item.getProductId(), item.getQuantity());
        }

        log.info("取消订单成功，订单号：{}，用户ID：{}，原因：{}", order.getOrderNo(), userId, cancelReason);
        return true;
    }

    @Override
    public QueryWrapper getQueryWrapper(OrderQueryRequest orderQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (orderQueryRequest == null) {
            return queryWrapper;
        }

        Long id = orderQueryRequest.getId();
        String orderNo = orderQueryRequest.getOrderNo();
        Long userId = orderQueryRequest.getUserId();
        Integer orderStatus = orderQueryRequest.getOrderStatus();
        String sortField = orderQueryRequest.getSortField();
        String sortOrder = orderQueryRequest.getSortOrder();

        if (id != null) {
            queryWrapper.where("id = ?", id);
        }
        if (orderNo != null) {
            queryWrapper.and("order_no = ?", orderNo);
        }
        if (userId != null) {
            queryWrapper.and("user_id = ?", userId);
        }
        if (orderStatus != null) {
            queryWrapper.and("order_status = ?", orderStatus);
        }

        String orderBySql = SqlUtils.toOrderBy(sortField, sortOrder);
        if (cn.hutool.core.util.StrUtil.isNotBlank(orderBySql)) {
            queryWrapper.orderBy(orderBySql);
        }
        return queryWrapper;
    }

    @Override
    public PurchaseOrderVO getPurchaseOrderVO(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        PurchaseOrderVO orderVO = new PurchaseOrderVO();
        BeanUtil.copyProperties(purchaseOrder, orderVO);

        // 设置订单状态文本
        OrderStatusEnum statusEnum = OrderStatusEnum.getByValue(purchaseOrder.getOrderStatus());
        if (statusEnum != null) {
            orderVO.setOrderStatusText(statusEnum.getText());
        }

        // 拼接完整地址
        if (purchaseOrder.getProvince() != null) {
            String fullAddress = (purchaseOrder.getProvince() != null ? purchaseOrder.getProvince() : "") +
                    (purchaseOrder.getCity() != null ? purchaseOrder.getCity() : "") +
                    (purchaseOrder.getDistrict() != null ? purchaseOrder.getDistrict() : "") +
                    (purchaseOrder.getDetailAddress() != null ? purchaseOrder.getDetailAddress() : "");
            orderVO.setFullAddress(fullAddress);
        }

        // 查询订单明细
        List<OrderItem> orderItems = orderItemService.listByOrderId(purchaseOrder.getId());
        if (orderItems != null && !orderItems.isEmpty()) {
            List<OrderItemVO> itemVOList = orderItems.stream().map(item -> {
                OrderItemVO itemVO = new OrderItemVO();
                BeanUtil.copyProperties(item, itemVO);
                return itemVO;
            }).collect(Collectors.toList());
            orderVO.setOrderItems(itemVOList);
        }

        // 查询兑换码列表（只有已支付的订单才有兑换码）
        if (OrderStatusEnum.PAID.getValue().equals(purchaseOrder.getOrderStatus())) {
            List<String> redemptionCodes = redemptionCodeService.getRedemptionCodesByOrderId(purchaseOrder.getId());
            orderVO.setRedemptionCodes(redemptionCodes);
        }

        return orderVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoExpireOrders() {
        // 查询所有过期的待支付订单
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("order_status = ?", OrderStatusEnum.PENDING.getValue())
                .and("expire_time < ?", LocalDateTime.now());

        List<PurchaseOrder> expiredOrders = this.list(queryWrapper);
        
        if (expiredOrders == null || expiredOrders.isEmpty()) {
            log.info("没有需要处理的过期订单");
            return;
        }

        log.info("开始自动取消过期订单，共{}个", expiredOrders.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (PurchaseOrder order : expiredOrders) {
            try {
                this.cancelOrder(order.getId(), order.getUserId(), "订单已过期，系统自动取消");
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("自动取消订单失败，订单号：{}，错误：{}", order.getOrderNo(), e.getMessage(), e);
            }
        }
        
        log.info("过期订单处理完成，成功：{}个，失败：{}个", successCount, failCount);
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        // 格式：ORDER + yyyyMMddHHmmss + 6位随机数
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = IdUtil.randomUUID().substring(0, 6).toUpperCase();
        return "ORDER" + timestamp + random;
    }

    @Override
    public Boolean checkUserPurchased(Long userId, Long productId) {
        // 参数校验
        ThrowUtils.throwIf(userId == null || productId == null, ErrorCode.PARAMS_ERROR);

        // 查询用户是否有该商品的已支付订单
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", userId)
                .and("order_status = ?", OrderStatusEnum.PAID.getValue());

        List<PurchaseOrder> orders = this.list(queryWrapper);
        if (orders == null || orders.isEmpty()) {
            return false;
        }

        // 检查订单明细中是否包含该商品
        for (PurchaseOrder order : orders) {
            List<OrderItem> orderItems = orderItemService.listByOrderId(order.getId());
            if (orderItems != null) {
                for (OrderItem item : orderItems) {
                    if (item.getProductId().equals(productId)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 模拟微信支付（TODO：实际对接微信支付）
     */
    private String mockWeChatPay(PurchaseOrder order) {
        // TODO：实际项目中需要调用微信支付API
        log.info("模拟微信支付，订单号：{}，金额：{}", order.getOrderNo(), order.getPayAmount());
        return "WX" + System.currentTimeMillis();
    }
}
