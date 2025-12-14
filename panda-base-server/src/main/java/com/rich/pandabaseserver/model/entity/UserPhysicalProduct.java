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
 * 用户实体商品表 实体类
 *
 * @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_physical_product")
public class UserPhysicalProduct implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 实体商品ID
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
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 状态 0-未核销 1-已核销
     */
    private Integer status;

    /**
     * 来源类型 1-兑换码兑换 2-订单购买
     */
    private Integer sourceType;

    /**
     * 来源兑换记录ID
     */
    private Long redemptionRecordId;

    /**
     * 来源订单ID
     */
    private Long orderId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}



