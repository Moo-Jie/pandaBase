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
 * 支付流水表 实体类（用于对账和掉单处理）
 *
 * @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("payment_record")
public class PaymentRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 支付记录ID
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
     * 用户ID
     */
    private Long userId;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付方式 1-微信支付
     */
    private Integer payType;

    /**
     * 支付状态 0-待支付 1-支付成功 2-支付失败 3-已关闭
     */
    private Integer payStatus;

    /**
     * 微信支付交易号
     */
    private String transactionId;

    /**
     * 微信预支付ID
     */
    private String prepayId;

    /**
     * 支付成功时间
     */
    private LocalDateTime payTime;

    /**
     * 回调时间
     */
    private LocalDateTime callbackTime;

    /**
     * 回调次数（用于检测重复回调）
     */
    private Integer callbackCount;

    /**
     * 回调是否已处理 0-未处理 1-已处理
     */
    private Integer isCallbackProcessed;

    /**
     * 回调内容（JSON）
     */
    private String callbackContent;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}



