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
 * 库存变更日志表 实体类（用于库存审计）
 *
 * @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stock_change_log")
public class StockChangeLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 变更类型 1-下单扣减 2-取消恢复 3-退款恢复 4-管理员调整
     */
    private Integer changeType;

    /**
     * 变更数量（正数增加，负数减少）
     */
    private Integer changeQuantity;

    /**
     * 变更前库存
     */
    private Integer beforeStock;

    /**
     * 变更后库存
     */
    private Integer afterStock;

    /**
     * 关联订单ID
     */
    private Long relatedOrderId;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}















