package com.rich.pandabaseserver.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 我的商品VO（包含会员卡和实体商品）
 *
 * @author DuRuiChi
 */
@Data
public class MyProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片
     */
    private String imageUrl;

    /**
     * 商品类型：1-年卡 2-月卡 3-次票 4-实体商品
     */
    private Integer type;

    /**
     * 商品类型文本
     */
    private String typeText;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 有效期开始时间（会员卡）
     */
    private LocalDateTime startTime;

    /**
     * 有效期结束时间（会员卡）
     */
    private LocalDateTime endTime;

    /**
     * 剩余次数（次票）
     */
    private Integer remainCount;

    /**
     * 数量（实体商品）
     */
    private Integer quantity;

    /**
     * 会员卡号（会员卡）
     */
    private String cardNumber;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}



