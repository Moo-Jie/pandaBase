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
import com.rich.pandabaseserver.model.enums.ProductTypeEnum;
import com.rich.pandabaseserver.model.enums.RedemptionCodeStatusEnum;
import com.rich.pandabaseserver.service.MembershipCardService;
import com.rich.pandabaseserver.service.ProductService;
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

    @Autowired
    private ProductService productService;

    @Autowired
    private com.rich.pandabaseserver.service.RedemptionRecordService redemptionRecordService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> generateRedemptionCodesForOrder(Long orderId, Long userId, Product product, Integer quantity) {
        // 参数校验
        ThrowUtils.throwIf(orderId == null || userId == null || product == null || quantity == null || quantity <= 0,
                ErrorCode.PARAMS_ERROR);

        List<String> codes = new ArrayList<>();

        try {
            // 批次号：ORDER + 订单ID
            String batchNo = "ORDER" + orderId;

            // 过期时间：默认1年
            LocalDateTime expireTime = LocalDateTime.now().plusYears(1);

            // 根据购买数量，为每一份创建兑换码
            for (int i = 0; i < quantity; i++) {
                // 生成唯一的兑换码（明文）
                String code = generateUniqueCode();

                // 保存兑换码（加密存储）
                RedemptionCode redemptionCode = RedemptionCode.builder()
                        .code(code) // 直接存储明文，方便用户使用
                        .productId(product.getId())
                        .batchNo(batchNo)
                        .status(RedemptionCodeStatusEnum.UNUSED.getValue())
                        .expireTime(expireTime)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();

                boolean saveResult = this.save(redemptionCode);
                ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "生成兑换码失败");

                codes.add(code);
                log.info("为订单{}生成兑换码成功，兑换码：{}", orderId, code);
            }

            return codes;
        } catch (Exception e) {
            log.error("为订单生成兑换码失败，订单ID：{}", orderId, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成兑换码失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean redeemCode(String code, Long userId, Long addressId) {
        // 1. 参数校验
        ThrowUtils.throwIf(code == null || code.trim().isEmpty(), ErrorCode.PARAMS_ERROR, "请输入兑换码");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR);

        code = code.trim();

        // 2. 查询兑换码
        com.mybatisflex.core.query.QueryWrapper queryWrapper = com.mybatisflex.core.query.QueryWrapper.create()
                .where("code = ?", code);

        RedemptionCode redemptionCode = this.getOne(queryWrapper);
        ThrowUtils.throwIf(redemptionCode == null, ErrorCode.NOT_FOUND_ERROR, "兑换码不存在，请确认来源");

        // 3. 校验兑换码状态
        if (redemptionCode.getStatus().equals(RedemptionCodeStatusEnum.USED.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "兑换码已被使用，如有疑问请联系客服");
        }
        if (redemptionCode.getStatus().equals(RedemptionCodeStatusEnum.EXPIRED.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "兑换码已过期，无法兑换");
        }

        // 4. 校验是否过期
        if (LocalDateTime.now().isAfter(redemptionCode.getExpireTime())) {
            // 更新状态为已过期
            redemptionCode.setStatus(RedemptionCodeStatusEnum.EXPIRED.getValue());
            redemptionCode.setUpdateTime(LocalDateTime.now());
            this.updateById(redemptionCode);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "兑换码已过期，无法兑换");
        }

        // 5. 查询商品信息
        Product product = productService.getById(redemptionCode.getProductId());
        ThrowUtils.throwIf(product == null, ErrorCode.NOT_FOUND_ERROR, "商品不存在");

        // 6. 实物商品需要校验地址
        if (!ProductTypeEnum.isVirtualTicket(product.getType())) {
            ThrowUtils.throwIf(addressId == null, ErrorCode.PARAMS_ERROR, "实物商品需要填写收货地址");
        }

        // 7. 更新兑换码状态
        redemptionCode.setStatus(RedemptionCodeStatusEnum.USED.getValue());
        redemptionCode.setUseTime(LocalDateTime.now());
        redemptionCode.setUseUserId(userId);
        redemptionCode.setUpdateTime(LocalDateTime.now());

        boolean updateResult = this.updateById(redemptionCode);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "兑换失败，请稍后重试");

        // 8. 创建兑换记录
        String recordNo = "REDEEM" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 6).toUpperCase();
        com.rich.pandabaseserver.model.entity.RedemptionRecord record = com.rich.pandabaseserver.model.entity.RedemptionRecord.builder()
                .recordNo(recordNo)
                .userId(userId)
                .redemptionCodeId(redemptionCode.getId())
                .productId(product.getId())
                .productType(product.getType())
                .status(ProductTypeEnum.isVirtualTicket(product.getType()) ? 1 : 0) // 虚拟商品直接完成，实物商品兑换中
                .completeTime(ProductTypeEnum.isVirtualTicket(product.getType()) ? LocalDateTime.now() : null)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        boolean saveRecordResult = redemptionRecordService.save(record);
        ThrowUtils.throwIf(!saveRecordResult, ErrorCode.OPERATION_ERROR, "创建兑换记录失败");

        // 9. 如果是虚拟商品，检查会员卡状态并处理
        if (ProductTypeEnum.isVirtualTicket(product.getType())) {
            // 查询用户是否已有任何类型的有效会员卡（年卡/月卡/次票）
            com.mybatisflex.core.query.QueryWrapper anyCardQuery = com.mybatisflex.core.query.QueryWrapper.create()
                    .where("user_id = ?", userId)
                    .and("card_type IN (1, 2, 3)") // 年卡、月卡、次票
                    .and("status = ?", MembershipCardStatusEnum.ACTIVE.getValue())
                    .and("end_time > ?", LocalDateTime.now());

            MembershipCard anyExistingCard = membershipCardService.getOne(anyCardQuery);

            if (anyExistingCard != null) {
                // 已有任何类型的有效会员卡，不允许兑换
                String cardTypeName = getCardTypeName(anyExistingCard.getCardType());
                String expireDate = anyExistingCard.getEndTime().toLocalDate().toString();
                throw new BusinessException(ErrorCode.OPERATION_ERROR, 
                        String.format("您已拥有有效的%s（有效期至%s），请等待过期后再兑换新卡", cardTypeName, expireDate));
            }

            // 没有任何有效会员卡，可以兑换，创建新卡
            String cardNumber = generateCardNumber(product.getType());
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = calculateEndTime(startTime, product.getValidityDays());

            MembershipCard card = MembershipCard.builder()
                    .userId(userId)
                    .productId(product.getId())
                    .cardNumber(cardNumber)
                    .cardType(product.getType())
                    .status(MembershipCardStatusEnum.ACTIVE.getValue())
                    .totalCount(product.getType() == 3 ? 10 : null)
                    .usedCount(0)
                    .startTime(startTime)
                    .endTime(endTime)
                    .redemptionRecordId(record.getId()) // 关联兑换记录ID
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            boolean saveResult = membershipCardService.save(card);
            ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "生成会员卡失败");

            log.info("虚拟商品兑换成功，生成新会员卡：{}，关联兑换记录：{}", cardNumber, recordNo);
        } else {
            log.info("实物商品兑换成功，等待发货，兑换记录号：{}", recordNo);
        }

        log.info("兑换码使用成功，兑换码：{}，用户ID：{}，兑换记录号：{}", code, userId, recordNo);
        return true;
    }

    /**
     * 加密兑换码（使用MD5）
     */
    private String encryptCode(String code) {
        return SecureUtil.md5(code);
    }

    /**
     * 获取卡类型名称
     */
    private String getCardTypeName(Integer cardType) {
        if (cardType == null) {
            return "会员卡";
        }
        return switch (cardType) {
            case 1 -> "年卡";
            case 2 -> "月卡";
            case 3 -> "次票";
            default -> "会员卡";
        };
    }

    @Override
    public List<String> getRedemptionCodesByOrderId(Long orderId) {
        ThrowUtils.throwIf(orderId == null, ErrorCode.PARAMS_ERROR);

        // 根据批次号查询兑换码
        String batchNo = "ORDER" + orderId;
        com.mybatisflex.core.query.QueryWrapper queryWrapper = com.mybatisflex.core.query.QueryWrapper.create()
                .where("batch_no = ?", batchNo);

        List<RedemptionCode> codeList = this.list(queryWrapper);
        if (codeList == null || codeList.isEmpty()) {
            return new ArrayList<>();
        }

        // 返回兑换码字符串列表
        return codeList.stream()
                .map(RedemptionCode::getCode)
                .toList();
    }
}
