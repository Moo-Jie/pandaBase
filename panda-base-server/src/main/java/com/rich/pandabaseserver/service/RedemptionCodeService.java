package com.rich.pandabaseserver.service;

import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.entity.Product;
import com.rich.pandabaseserver.model.entity.RedemptionCode;

import java.util.List;

/**
 * 兑换码表 服务层。
 *
 * @author @author DuRuiChi
 */
public interface RedemptionCodeService extends IService<RedemptionCode> {

    /**
     * 为订单派发兑换码和会员卡（支付成功后调用）
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param product 商品信息
     * @param quantity 购买数量
     * @return 会员卡号列表
     */
    List<String> dispatchForOrder(Long orderId, Long userId, Product product, Integer quantity);

    /**
     * 生成兑换码（批量生成）
     *
     * @param productId 商品ID
     * @param batchNo 批次号
     * @param count 生成数量
     * @param expireTime 过期时间
     * @return 生成的兑换码列表
     */
    List<RedemptionCode> generateRedemptionCodes(Long productId, String batchNo, Integer count, 
                                                  java.time.LocalDateTime expireTime);
}
