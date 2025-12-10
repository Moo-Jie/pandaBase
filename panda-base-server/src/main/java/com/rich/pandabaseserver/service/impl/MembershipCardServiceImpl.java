package com.rich.pandabaseserver.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.model.entity.MembershipCard;
import com.rich.pandabaseserver.mapper.MembershipCardMapper;
import com.rich.pandabaseserver.service.MembershipCardService;
import org.springframework.stereotype.Service;

/**
 * 会员卡表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Service
public class MembershipCardServiceImpl extends ServiceImpl<MembershipCardMapper, MembershipCard>  implements MembershipCardService{

}
