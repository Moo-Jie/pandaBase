package com.rich.pandabaseserver.service;

import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.entity.MembershipCard;
import com.rich.pandabaseserver.model.vo.MembershipCardVO;

import java.util.List;

/**
 * 会员卡表 服务层。
 *
 * @author @author DuRuiChi
 */
public interface MembershipCardService extends IService<MembershipCard> {

    /**
     * 获取用户的会员卡列表
     *
     * @param userId 用户ID
     * @return 会员卡列表
     */
    List<MembershipCardVO> getUserMembershipCards(Long userId);

    /**
     * 获取会员卡VO
     *
     * @param membershipCard 会员卡实体
     * @return 会员卡VO
     */
    MembershipCardVO getMembershipCardVO(MembershipCard membershipCard);

    /**
     * 核销会员卡（使用一次）
     *
     * @param cardId 会员卡ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean verifyCard(Long cardId, Long userId);

    /**
     * 检查会员卡是否有效且可用
     *
     * @param cardId 会员卡ID
     * @param userId 用户ID
     * @return 是否可用
     */
    Boolean checkCardAvailable(Long cardId, Long userId);

    /**
     * 自动处理过期会员卡
     */
    void autoExpireCards();
}
