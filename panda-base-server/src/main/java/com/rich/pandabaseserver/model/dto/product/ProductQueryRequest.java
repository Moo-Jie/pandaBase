package com.rich.pandabaseserver.model.dto.product;

import com.rich.pandabaseserver.common.response.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * 商品查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductQueryRequest extends PageRequest implements Serializable {
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
     * 商品描述
     */
    private String description;
    
    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品类型
     */
    private Integer type;

    /**
     * 是否推荐
     */
    private Boolean isRecommend;

    /**
     * 状态
     */
    private Integer status;
}

