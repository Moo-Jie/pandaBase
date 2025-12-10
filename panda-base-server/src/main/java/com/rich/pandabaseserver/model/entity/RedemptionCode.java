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
 * 兑换码表 实体类。
 *
 * @author @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("redemption_code")
public class RedemptionCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 兑换码ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 兑换码（加密存储）
     */
    private String code;

    /**
     * 关联商品ID
     */
    private Long productId;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 状态 0-未使用 1-已使用 2-已过期
     */
    private Integer status;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 使用用户ID
     */
    private Long useUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
