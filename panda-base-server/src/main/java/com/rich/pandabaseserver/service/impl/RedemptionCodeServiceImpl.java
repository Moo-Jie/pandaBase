package com.rich.pandabaseserver.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.exception.BusinessException;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.mapper.RedemptionCodeMapper;
import com.rich.pandabaseserver.model.entity.MembershipCard;
import com.rich.pandabaseserver.model.entity.Product;
import com.rich.pandabaseserver.model.entity.RedemptionCode;
import com.rich.pandabaseserver.model.enums.MembershipCardStatusEnum;
import com.rich.pandabaseserver.model.enums.MembershipCardTypeEnum;
import com.rich.pandabaseserver.model.enums.ProductTypeEnum;
import com.rich.pandabaseserver.model.enums.RedemptionCodeStatusEnum;
import com.rich.pandabaseserver.service.MembershipCardService;
import com.rich.pandabaseserver.service.RedemptionCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 兑换码表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Slf4j
@Service
public class RedemptionCodeServiceImpl extends ServiceImpl<RedemptionCodeMapper, RedemptionCode> implements RedemptionCodeService {

    @Autowired
    private MembershipCardService membershipCardService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> dispatchForOrder(Long orderId, Long userId, Product product, Integer quantity) {
        // 参数校验
        ThrowUtils.throwIf(orderId == null || userId == null || product == null || quantity == null || quantity <= 0,
                ErrorCode.PARAMS_ERROR);

        // 只有虚拟票证才需要派发
        if (!ProductTypeEnum.isVirtualTicket(product.getType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该商品类型不支持自动派发");
        }

        List<String> cardNumbers = new ArrayList<>();

        try {
            // 根据购买数量，为每一份创建会员卡
            for (int i = 0; i < quantity; i++) {
                // 1. 生成会员卡号
                String cardNumber = generateCardNumber(product.getType());

                // 2. 确定会员卡类型
                Integer cardType = product.getType(); // 1-年卡 2-月卡 3-次票

                // 3. 计算有效期
                LocalDateTime startTime = LocalDateTime.now();
                LocalDateTime endTime = calculateEndTime(startTime, product.getValidityDays());

                // 4. 创建会员卡
                MembershipCard card = MembershipCard.builder()
                        .userId(userId)
                        .productId(product.getId())
                        .cardNumber(cardNumber)
                        .cardType(cardType)
                        .status(MembershipCardStatusEnum.ACTIVE.getValue()) // 直接激活
                        .totalCount(cardType == 3 ? 10 : null) // 次票设置总次数，这里默认10次
                        .usedCount(0)
                        .startTime(startTime)
                        .endTime(endTime)
                        .orderId(orderId)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();

                boolean saveResult = membershipCardService.save(card);
                ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "创建会员卡失败");

                cardNumbers.add(cardNumber);
                log.info("为订单{}派发会员卡成功，卡号：{}", orderId, cardNumber);
            }

            return cardNumbers;
        } catch (Exception e) {
            log.error("派发会员卡失败，订单ID：{}", orderId, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "派发会员卡失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RedemptionCode> generateRedemptionCodes(Long productId, String batchNo, Integer count,
                                                        LocalDateTime expireTime) {
        // 参数校验
        ThrowUtils.throwIf(productId == null || batchNo == null || count == null || count <= 0 || expireTime == null,
                ErrorCode.PARAMS_ERROR);

        List<RedemptionCode> codeList = new ArrayList<>();

        try {
            for (int i = 0; i < count; i++) {
                // 生成唯一的兑换码
                String code = generateUniqueCode();

                // 加密存储
                String encryptedCode = encryptCode(code);

                RedemptionCode redemptionCode = RedemptionCode.builder()
                        .code(encryptedCode)
                        .productId(productId)
                        .batchNo(batchNo)
                        .status(RedemptionCodeStatusEnum.UNUSED.getValue())
                        .expireTime(expireTime)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();

                codeList.add(redemptionCode);
            }

            // 批量保存
            boolean saveResult = this.saveBatch(codeList);
            ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "批量生成兑换码失败");

            log.info("批量生成兑换码成功，批次号：{}，数量：{}", batchNo, count);
            return codeList;
        } catch (Exception e) {
            log.error("批量生成兑换码失败，批次号：{}", batchNo, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量生成兑换码失败");
        }
    }

    /**
     * 生成会员卡号
     * 格式：CARD + yyyyMMddHHmmss + 4位随机数
     */
    private String generateCardNumber(Integer cardType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = IdUtil.randomUUID().substring(0, 4).toUpperCase();
        return "CARD" + timestamp + random;
    }

    /**
     * 计算会员卡过期时间
     */
    private LocalDateTime calculateEndTime(LocalDateTime startTime, Integer validityDays) {
        if (validityDays == null || validityDays <= 0) {
            // 默认1年
            return startTime.plusYears(1);
        }
        return startTime.plusDays(validityDays);
    }

    /**
     * 生成唯一的兑换码
     * 格式：RED + 时间戳 + 随机字符
     */
    private String generateUniqueCode() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = IdUtil.randomUUID().substring(0, 8).toUpperCase();
        return "RED" + timestamp + random;
    }

    /**
     * 加密兑换码（使用MD5）
     */
    private String encryptCode(String code) {
        return SecureUtil.md5(code);
    }
}
