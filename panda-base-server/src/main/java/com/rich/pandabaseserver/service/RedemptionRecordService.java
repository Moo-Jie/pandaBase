package com.rich.pandabaseserver.service;

import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.entity.RedemptionRecord;
import com.rich.pandabaseserver.model.vo.RedemptionRecordVO;

import java.util.List;

/**
 * 兑换记录表 服务层。
 *
 * @author @author DuRuiChi
 */
public interface RedemptionRecordService extends IService<RedemptionRecord> {

    /**
     * 获取用户的兑换记录列表
     *
     * @param userId 用户ID
     * @return 兑换记录VO列表
     */
    List<RedemptionRecordVO> getUserRedemptionRecords(Long userId);

    /**
     * 将实体转换为VO
     *
     * @param record 兑换记录实体
     * @return 兑换记录VO
     */
    RedemptionRecordVO getRedemptionRecordVO(RedemptionRecord record);
}
