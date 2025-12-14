package com.rich.pandabaseserver.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品 VO
 */
@Data
public class ProductVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片
     */
    private String imageUrl;

    /**
     * 商品类型 1-年卡 2-月卡 3-次票 4-实物商品
     */
    private Integer type;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 有效期天数（虚拟票证专用）
     */
    private Integer validityDays;

    /**
     * 是否推荐 0-否 1-是
     */
    private Boolean isRecommend;

    /**
     * 状态 0-下架 1-上架
     */
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

