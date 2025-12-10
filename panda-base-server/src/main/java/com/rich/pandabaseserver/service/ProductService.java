package com.rich.pandabaseserver.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.dto.product.ProductQueryRequest;
import com.rich.pandabaseserver.model.entity.Product;
import com.rich.pandabaseserver.model.vo.ProductVO;

/**
 * 商品表 服务层。
 *
 * @author @author DuRuiChi
 */
public interface ProductService extends IService<Product> {

    /**
     * 获取查询条件
     *
     * @param productQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ProductQueryRequest productQueryRequest);

    /**
     * 获取商品封装
     *
     * @param product
     * @return
     */
    ProductVO getProductVO(Product product);

    /**
     * 更新库存
     *
     * @param productId 商品ID
     * @param quantity 数量（正数增加，负数减少）
     * @return 是否成功
     */
    Boolean updateStock(Long productId, Integer quantity);
}
