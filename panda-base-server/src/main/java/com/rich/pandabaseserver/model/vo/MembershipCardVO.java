package com.rich.pandabaseserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员卡 VO
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "会员卡 VO")
public class MembershipCardVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String productName;

    /**
     * 会员卡号
     */
    @Schema(description = "会员卡号")
    private String cardNumber;

    /**
     * 卡类型 1-年卡 2-月卡 3-次票
     */
    @Schema(description = "卡类型")
    private Integer cardType;

    /**
     * 卡类型文本
     */
    @Schema(description = "卡类型文本")
    private String cardTypeText;

    /**
     * 状态 0-未激活 1-已激活 2-已过期 3-已作废
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 状态文本
     */
    @Schema(description = "状态文本")
    private String statusText;

    /**
     * 总次数（次票专用）
     */
    @Schema(description = "总次数")
    private Integer totalCount;

    /**
     * 已使用次数
     */
    @Schema(description = "已使用次数")
    private Integer usedCount;

    /**
     * 剩余次数
     */
    @Schema(description = "剩余次数")
    private Integer remainCount;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 剩余天数
     */
    @Schema(description = "剩余天数")
    private Long remainDays;

    /**
     * 来源订单ID
     */
    @Schema(description = "来源订单ID")
    private Long orderId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}





















