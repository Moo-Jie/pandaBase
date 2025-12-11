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
}
