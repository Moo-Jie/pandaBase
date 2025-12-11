package com.rich.pandabaseserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.mapper.RedemptionCodeMapper;
import com.rich.pandabaseserver.model.entity.Product;
import com.rich.pandabaseserver.model.entity.RedemptionCode;
import com.rich.pandabaseserver.model.entity.RedemptionRecord;
import com.rich.pandabaseserver.mapper.RedemptionRecordMapper;
import com.rich.pandabaseserver.model.enums.ProductTypeEnum;
import com.rich.pandabaseserver.model.vo.RedemptionRecordVO;
import com.rich.pandabaseserver.service.ProductService;
import com.rich.pandabaseserver.service.RedemptionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 兑换记录表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Service
public class RedemptionRecordServiceImpl extends ServiceImpl<RedemptionRecordMapper, RedemptionRecord>  implements RedemptionRecordService{

    @Autowired
    private ProductService productService;

    @Autowired
    private RedemptionCodeMapper redemptionCodeMapper;

    @Override
    public List<RedemptionRecordVO> getUserRedemptionRecords(Long userId) {
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);

        // 查询用户的所有兑换记录
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", userId)
                .orderBy("create_time DESC");

        List<RedemptionRecord> records = this.list(queryWrapper);
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为 VO
        List<RedemptionRecordVO> recordVOList = new ArrayList<>();
        for (RedemptionRecord record : records) {
            recordVOList.add(getRedemptionRecordVO(record));
        }

        return recordVOList;
    }

    @Override
    public RedemptionRecordVO getRedemptionRecordVO(RedemptionRecord record) {
        if (record == null) {
            return null;
        }

        RedemptionRecordVO recordVO = new RedemptionRecordVO();
        BeanUtil.copyProperties(record, recordVO);

        // 查询商品信息
        if (record.getProductId() != null) {
            Product product = productService.getById(record.getProductId());
            if (product != null) {
                recordVO.setProductName(product.getName());
            }
        }

        // 查询兑换码（脱敏显示）
        if (record.getRedemptionCodeId() != null) {
            RedemptionCode code = redemptionCodeMapper.selectOneById(record.getRedemptionCodeId());
            if (code != null && code.getCode() != null) {
                // 脱敏：只显示前4位和后4位
                String codeStr = code.getCode();
                if (codeStr.length() > 8) {
                    recordVO.setRedemptionCode(codeStr.substring(0, 4) + "***" + codeStr.substring(codeStr.length() - 4));
                } else {
                    recordVO.setRedemptionCode("***");
                }
            }
        }

        // 设置商品类型文本
        ProductTypeEnum typeEnum = ProductTypeEnum.getByValue(record.getProductType());
        if (typeEnum != null) {
            recordVO.setProductTypeText(typeEnum.getText());
        }

        // 设置状态文本
        String statusText = getStatusText(record.getStatus());
        recordVO.setStatusText(statusText);

        return recordVO;
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "兑换中";
            case 1 -> "已完成";
            case 2 -> "已发货";
            default -> "未知";
        };
    }
}
