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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录表 实体类
 *
 * @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("refund_record")
public class RefundRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 退款记录ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 退款单号
     */
    private String refundNo;

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
     * 用户ID
     */
    private Long userId;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款状态 0-处理中 1-成功 2-失败 3-已关闭
     */
    private Integer refundStatus;

    /**
     * 微信退款单号
     */
    private String wxRefundId;

    /**
     * 退款成功时间
     */
    private LocalDateTime refundSuccessTime;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 操作人ID（客服/管理员）
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}















