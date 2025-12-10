package com.rich.pandabaseserver.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.model.entity.ProductCategory;
import com.rich.pandabaseserver.mapper.ProductCategoryMapper;
import com.rich.pandabaseserver.service.ProductCategoryService;
import org.springframework.stereotype.Service;

/**
 * 商品分类表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory>  implements ProductCategoryService{

}
