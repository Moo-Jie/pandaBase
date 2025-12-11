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
}
