package com.rich.pandabaseserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.model.dto.product.ProductQueryRequest;
import com.rich.pandabaseserver.model.entity.Product;
import com.rich.pandabaseserver.mapper.ProductMapper;
import com.rich.pandabaseserver.model.vo.ProductVO;
import com.rich.pandabaseserver.service.ProductService;
import com.rich.pandabaseserver.utils.SqlUtils;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

/**
 * 商品表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public QueryWrapper getQueryWrapper(ProductQueryRequest productQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (productQueryRequest == null) {
            return queryWrapper;
        }
        Long id = productQueryRequest.getId();
        String name = productQueryRequest.getName();
        String description = productQueryRequest.getDescription();
        Long categoryId = productQueryRequest.getCategoryId();
        Integer type = productQueryRequest.getType();
        Boolean isRecommend = productQueryRequest.getIsRecommend();
        Integer status = productQueryRequest.getStatus();
        String sortField = productQueryRequest.getSortField();
        String sortOrder = productQueryRequest.getSortOrder();

        if (id != null) {
            queryWrapper.where("id = ?", id);
        }
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.and("name LIKE ?", "%" + name + "%");
        }
        if (StrUtil.isNotBlank(description)) {
            queryWrapper.and("description LIKE ?", "%" + description + "%");
        }
        if (categoryId != null) {
            queryWrapper.and("category_id = ?", categoryId);
        }
        if (type != null) {
            queryWrapper.and("type = ?", type);
        }
        if (isRecommend != null) {
            queryWrapper.and("is_recommend = ?", isRecommend);
        }
        if (status != null) {
            queryWrapper.and("status = ?", status);
        }
        
        String orderBySql = SqlUtils.toOrderBy(sortField, sortOrder);
        if (StrUtil.isNotBlank(orderBySql)) {
            queryWrapper.orderBy(orderBySql);
        }
        return queryWrapper;
    }

    @Override
    public ProductVO getProductVO(Product product) {
        if (product == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        BeanUtil.copyProperties(product, productVO);
        return productVO;
    }

    @Override
    public Boolean updateStock(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity == 0) {
            return false;
        }
        
        Product product = this.getById(productId);
        if (product == null) {
            return false;
        }
        
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            return false;
        }
        
        product.setStock(newStock);
        return this.updateById(product);
    }
}
