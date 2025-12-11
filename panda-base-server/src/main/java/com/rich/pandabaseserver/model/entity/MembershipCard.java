package com.rich.pandabaseserver.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会员卡表 实体类。
 *
 * @author @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("membership_card")
public class MembershipCard implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 会员卡号
     */
    private String cardNumber;

    /**
     * 卡类型 1-年卡 2-月卡 3-次票
     */
    private Integer cardType;

    /**
     * 状态 0-未激活 1-已激活 2-已过期 3-已作废
     */
    private Integer status;

    /**
     * 总次数（次票专用）
     */
    private Integer totalCount;

    /**
     * 已使用次数
     */
    private Integer usedCount;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 来源订单ID（购买方式）
     */
    private Long orderId;

    /**
     * 来源兑换记录ID（兑换方式）
     */
    private Long redemptionRecordId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
