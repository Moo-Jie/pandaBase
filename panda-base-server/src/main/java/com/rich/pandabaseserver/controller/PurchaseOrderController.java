package com.rich.pandabaseserver.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.annotation.AuthCheck;
import com.rich.pandabaseserver.model.dto.order.OrderCreateRequest;
import com.rich.pandabaseserver.model.dto.order.OrderQueryRequest;
import com.rich.pandabaseserver.model.dto.order.PayOrderRequest;
import com.rich.pandabaseserver.model.dto.order.RefundOrderRequest;
import com.rich.pandabaseserver.model.dto.order.RepairOrderRequest;
import com.rich.pandabaseserver.model.entity.PurchaseOrder;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.model.vo.PaymentResultVO;
import com.rich.pandabaseserver.model.vo.PurchaseOrderVO;
import com.rich.pandabaseserver.service.PurchaseOrderService;
import com.rich.pandabaseserver.service.UserService;
import com.rich.pandabaseserver.service.WxMiniappPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 购买订单表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/order")
@Tag(name = "订单管理", description = "订单相关接口")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxMiniappPayService wxMiniappPayService;

    /**
     * 创建订单
     *
     * @param orderCreateRequest 订单创建请求
     * @param request HTTP请求
     * @return 订单ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单", description = "用户点击立即购买后创建订单")
    public BaseResponse<Long> createOrder(@RequestBody OrderCreateRequest orderCreateRequest,
                                          HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        orderCreateRequest.setUserId(loginUser.getId());
        Long orderId = purchaseOrderService.createOrder(orderCreateRequest);

        return ResultUtils.success(orderId);
    }

    /**
     * 支付订单
     *
     * @param payOrderRequest 支付订单请求
     * @param request HTTP请求
     * @return 支付结果
     */
    @PostMapping("/pay")
    @Operation(summary = "支付订单", description = "用户填写地址后点击立即支付")
    public BaseResponse<PaymentResultVO> payOrder(@RequestBody PayOrderRequest payOrderRequest,
                                                  HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        payOrderRequest.setUserId(loginUser.getId());
        PaymentResultVO result = purchaseOrderService.payOrder(payOrderRequest);

        return ResultUtils.success(result);
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单", description = "用户主动取消待支付订单")
    public BaseResponse<Boolean> cancelOrder(@PathVariable Long orderId,
                                             HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Boolean result = purchaseOrderService.cancelOrder(orderId, loginUser.getId(), "用户主动取消");
        return ResultUtils.success(result);
    }

    /**
     * 申请退款
     *
     * @param refundRequest 退款请求
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/refund")
    @Operation(summary = "申请退款", description = "用户申请退款（仅限已支付且未使用的订单）")
    public BaseResponse<Boolean> refundOrder(@RequestBody RefundOrderRequest refundRequest,
                                             HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Boolean result = purchaseOrderService.refundOrder(refundRequest.getOrderId(), loginUser.getId(), refundRequest.getReason());
        return ResultUtils.success(result);
    }

    /**
     * 分页查询我的订单
     *
     * @param orderQueryRequest 查询请求
     * @param request HTTP请求
     * @return 订单分页列表
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页查询我的订单", description = "个人中心查看购买订单")
    public BaseResponse<Page<PurchaseOrderVO>> listMyOrdersByPage(@RequestBody OrderQueryRequest orderQueryRequest,
                                                                   HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        long current = orderQueryRequest.getPageNum();
        long size = orderQueryRequest.getPageSize();
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 只查询当前用户的订单
        orderQueryRequest.setUserId(loginUser.getId());

        Page<PurchaseOrder> orderPage = purchaseOrderService.page(
                Page.of(current, size),
                purchaseOrderService.getQueryWrapper(orderQueryRequest)
        );

        Page<PurchaseOrderVO> orderVOPage = new Page<>(current, size, orderPage.getTotalRow());
        orderVOPage.setRecords(
                orderPage.getRecords().stream()
                        .map(purchaseOrderService::getPurchaseOrderVO)
                        .toList()
        );

        return ResultUtils.success(orderVOPage);
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @param request HTTP请求
     * @return 订单详情
     */
    @GetMapping("/get/vo/{id}")
    @Operation(summary = "获取订单详情", description = "查看订单详情")
    public BaseResponse<PurchaseOrderVO> getOrderVOById(@PathVariable Long id,
                                                        HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        PurchaseOrder order = purchaseOrderService.getById(id);
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        ThrowUtils.throwIf(!order.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限查看此订单");

        return ResultUtils.success(purchaseOrderService.getPurchaseOrderVO(order));
    }

    /**
     * 检查用户是否购买过指定商品
     *
     * @param productId 商品ID
     * @param request HTTP请求
     * @return 是否已购买
     */
    @GetMapping("/check/purchased")
    @Operation(summary = "检查是否已购买", description = "检查用户是否购买过指定商品")
    public BaseResponse<Boolean> checkPurchased(@RequestParam Long productId,
                                                HttpServletRequest request) {
        ThrowUtils.throwIf(productId == null || productId <= 0, ErrorCode.PARAMS_ERROR, "商品ID无效");

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Boolean purchased = purchaseOrderService.checkUserPurchased(loginUser.getId(), productId);
        return ResultUtils.success(purchased);
    }

    /**
     * 超级管理员强制补单
     *
     * @param repairRequest 补单请求
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/admin/forceRepair")
    @Operation(summary = "超级管理员强制补单", description = "超级管理员可直接为指定订单补单，无需查询微信支付状态")
    @AuthCheck(mustRole = 3) // 仅超级管理员可访问
    public BaseResponse forceRepairOrder(@RequestBody RepairOrderRequest repairRequest,
                                        HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        
        Long orderId = repairRequest.getOrderId();
        String orderNo = repairRequest.getOrderNo();

        if ((orderId == null || orderId <= 0) && orderNo != null && !orderNo.trim().isEmpty()) {
            QueryWrapper orderQuery = QueryWrapper.create().where("order_no = ?", orderNo.trim());
            PurchaseOrder order = purchaseOrderService.getOne(orderQuery);
            ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");
            orderId = order.getId();
        }

        ThrowUtils.throwIf(orderId == null || orderId <= 0, ErrorCode.PARAMS_ERROR, "订单ID不能为空");
        
        return wxMiniappPayService.forceRepairOrderByAdmin(orderId, loginUser.getId(), loginUser.getNickname());
    }
}
