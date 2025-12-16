package com.rich.pandabaseserver.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 补单记录表 实体类。
 *
 * @author @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("repair_order_record")
public class RepairOrderRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 补单记录ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 微信支付交易号
     */
    private String transactionId;

    /**
     * 补单类型 1-手动查询微信补单 2-管理员强制补单
     */
    private Integer repairType;

    /**
     * 补单原因
     */
    private String repairReason;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 补单结果 1-成功 2-失败
     */
    private Integer repairResult;

    /**
     * 结果说明
     */
    private String resultMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
