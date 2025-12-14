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
 * 兑换记录表 实体类。
 *
 * @author @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("redemption_record")
public class RedemptionRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 兑换记录编号
     */
    private String recordNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 兑换码ID
     */
    private Long redemptionCodeId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品类型 1-年卡 2-月卡 3-次票 4-实物商品
     */
    private Integer productType;

    /**
     * 状态 0-兑换中 1-已完成 2-已发货（实物）
     */
    private Integer status;

    /**
     * 收货地址JSON
     */
    private String shippingAddress;

    /**
     * 物流单号
     */
    private String trackingNumber;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
