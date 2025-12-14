package com.rich.pandabaseserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.entity.MembershipCard;
import com.rich.pandabaseserver.mapper.MembershipCardMapper;
import com.rich.pandabaseserver.model.entity.Product;
import com.rich.pandabaseserver.model.vo.MembershipCardVO;
import com.rich.pandabaseserver.service.MembershipCardService;
import com.rich.pandabaseserver.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员卡表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Slf4j
@Service
public class MembershipCardServiceImpl extends ServiceImpl<MembershipCardMapper, MembershipCard> implements MembershipCardService {

    @Autowired
    private ProductService productService;

    @Override
    public List<MembershipCardVO> getUserMembershipCards(Long userId) {
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);

        // 查询用户的所有会员卡
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", userId)
                .orderBy("create_time DESC");

        List<MembershipCard> cards = this.list(queryWrapper);
        if (cards == null || cards.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为 VO
        List<MembershipCardVO> cardVOList = new ArrayList<>();
        for (MembershipCard card : cards) {
            cardVOList.add(getMembershipCardVO(card));
        }

        return cardVOList;
    }

    @Override
    public MembershipCardVO getMembershipCardVO(MembershipCard membershipCard) {
        if (membershipCard == null) {
            return null;
        }

        MembershipCardVO cardVO = new MembershipCardVO();
        BeanUtil.copyProperties(membershipCard, cardVO);

        // 查询商品信息
        if (membershipCard.getProductId() != null) {
            Product product = productService.getById(membershipCard.getProductId());
            if (product != null) {
                cardVO.setProductName(product.getName());
            }
        }

        // 设置卡类型文本
        String cardTypeText = getCardTypeText(membershipCard.getCardType());
        cardVO.setCardTypeText(cardTypeText);

        // 设置状态文本
        String statusText = getStatusText(membershipCard.getStatus());
        cardVO.setStatusText(statusText);

        // 计算剩余次数（次票专用）
        if (membershipCard.getCardType() == 3) { // 次票
            if (membershipCard.getTotalCount() != null && membershipCard.getUsedCount() != null) {
                cardVO.setRemainCount(membershipCard.getTotalCount() - membershipCard.getUsedCount());
            }
        }

        // 计算剩余天数
        if (membershipCard.getEndTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(membershipCard.getEndTime())) {
                long days = ChronoUnit.DAYS.between(now, membershipCard.getEndTime());
                cardVO.setRemainDays(days);
            } else {
                cardVO.setRemainDays(0L);
            }
        }

        return cardVO;
    }

    /**
     * 获取卡类型文本
     */
    private String getCardTypeText(Integer cardType) {
        if (cardType == null) {
            return "未知";
        }
        return switch (cardType) {
            case 1 -> "年卡";
            case 2 -> "月卡";
            case 3 -> "次票";
            default -> "未知";
        };
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "未激活";
            case 1 -> "已激活";
            case 2 -> "已过期";
            case 3 -> "已作废";
            default -> "未知";
        };
    }

    @Override
    public Boolean verifyCard(Long cardId, Long userId) {
        ThrowUtils.throwIf(cardId == null || userId == null, ErrorCode.PARAMS_ERROR);

        // 1. 查询会员卡
        MembershipCard card = this.getById(cardId);
        ThrowUtils.throwIf(card == null, ErrorCode.NOT_FOUND_ERROR, "会员卡不存在");
        ThrowUtils.throwIf(!card.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR, "无权限使用此会员卡");

        // 2. 检查会员卡状态
        if (card.getStatus() == null || !card.getStatus().equals(1)) {
            throw new com.rich.pandabaseserver.exception.BusinessException(ErrorCode.OPERATION_ERROR, "会员卡未激活或已失效");
        }

        // 3. 检查是否过期
        LocalDateTime now = LocalDateTime.now();
        if (card.getEndTime() != null && now.isAfter(card.getEndTime())) {
            // 自动设置为过期状态
            card.setStatus(2);
            card.setUpdateTime(now);
            this.updateById(card);
            throw new com.rich.pandabaseserver.exception.BusinessException(ErrorCode.OPERATION_ERROR, "会员卡已过期");
        }

        // 4. 次票需要检查次数
        if (card.getCardType() != null && card.getCardType() == 3) {
            Integer totalCount = card.getTotalCount() != null ? card.getTotalCount() : 0;
            Integer usedCount = card.getUsedCount() != null ? card.getUsedCount() : 0;

            if (usedCount >= totalCount) {
                throw new com.rich.pandabaseserver.exception.BusinessException(ErrorCode.OPERATION_ERROR, "次票使用次数已用完");
            }

            // 增加使用次数
            card.setUsedCount(usedCount + 1);
            card.setUpdateTime(now);
            boolean updateResult = this.updateById(card);
            ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "核销失败");

            return true;
        }

        // 5. 年卡/月卡直接返回成功（只需要有效期内即可）
        return true;
    }

    @Override
    public Boolean checkCardAvailable(Long cardId, Long userId) {
        ThrowUtils.throwIf(cardId == null || userId == null, ErrorCode.PARAMS_ERROR);

        MembershipCard card = this.getById(cardId);
        if (card == null || !card.getUserId().equals(userId)) {
            return false;
        }

        // 检查状态
        if (card.getStatus() == null || !card.getStatus().equals(1)) {
            return false;
        }

        // 检查过期时间
        LocalDateTime now = LocalDateTime.now();
        if (card.getEndTime() != null && now.isAfter(card.getEndTime())) {
            return false;
        }

        // 次票检查次数
        if (card.getCardType() != null && card.getCardType() == 3) {
            Integer totalCount = card.getTotalCount() != null ? card.getTotalCount() : 0;
            Integer usedCount = card.getUsedCount() != null ? card.getUsedCount() : 0;
            return usedCount < totalCount;
        }

        return true;
    }

    @Override
    public void autoExpireCards() {
        // 查询所有已激活但已过期的年卡和月卡
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("status = ?", 1)
                .and("end_time < ?", LocalDateTime.now())
                .and("card_type IN (?, ?)", 1, 2); // 1-年卡, 2-月卡

        List<MembershipCard> expiredCards = this.list(queryWrapper);

        if (expiredCards == null || expiredCards.isEmpty()) {
            log.info("没有需要处理的过期年卡/月卡");
            return;
        }

        log.info("开始处理过期年卡/月卡，共{}张", expiredCards.size());

        // 批量更新为已作废状态（状态3）
        for (MembershipCard card : expiredCards) {
            card.setStatus(3); // 3-已作废
            card.setUpdateTime(LocalDateTime.now());
        }

        boolean updateResult = this.updateBatch(expiredCards);
        if (updateResult) {
            log.info("过期年卡/月卡处理完成，共更新{}张为已作废状态", expiredCards.size());
        } else {
            log.error("过期年卡/月卡批量更新失败");
        }
    }
}
