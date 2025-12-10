package com.rich.pandabaseserver.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.rich.pandabaseserver.model.entity.ProductCategory;
import com.rich.pandabaseserver.service.ProductCategoryService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 商品分类表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/productCategory")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 保存商品分类表。
     *
     * @param productCategory 商品分类表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody ProductCategory productCategory) {
        return productCategoryService.save(productCategory);
    }

    /**
     * 根据主键删除商品分类表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return productCategoryService.removeById(id);
    }

    /**
     * 根据主键更新商品分类表。
     *
     * @param productCategory 商品分类表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ProductCategory productCategory) {
        return productCategoryService.updateById(productCategory);
    }

    /**
     * 查询所有商品分类表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<ProductCategory> list() {
        return productCategoryService.list();
    }

    /**
     * 根据主键获取商品分类表。
     *
     * @param id 商品分类表主键
     * @return 商品分类表详情
     */
    @GetMapping("getInfo/{id}")
    public ProductCategory getInfo(@PathVariable Long id) {
        return productCategoryService.getById(id);
    }

    /**
     * 分页查询商品分类表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ProductCategory> page(Page<ProductCategory> page) {
        return productCategoryService.page(page);
    }

}
