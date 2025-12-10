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
import com.rich.pandabaseserver.model.entity.ProductComboDetail;
import com.rich.pandabaseserver.service.ProductComboDetailService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 组合商品明细表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/productComboDetail")
public class ProductComboDetailController {

    @Autowired
    private ProductComboDetailService productComboDetailService;

    /**
     * 保存组合商品明细表。
     *
     * @param productComboDetail 组合商品明细表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody ProductComboDetail productComboDetail) {
        return productComboDetailService.save(productComboDetail);
    }

    /**
     * 根据主键删除组合商品明细表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return productComboDetailService.removeById(id);
    }

    /**
     * 根据主键更新组合商品明细表。
     *
     * @param productComboDetail 组合商品明细表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ProductComboDetail productComboDetail) {
        return productComboDetailService.updateById(productComboDetail);
    }

    /**
     * 查询所有组合商品明细表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<ProductComboDetail> list() {
        return productComboDetailService.list();
    }

    /**
     * 根据主键获取组合商品明细表。
     *
     * @param id 组合商品明细表主键
     * @return 组合商品明细表详情
     */
    @GetMapping("getInfo/{id}")
    public ProductComboDetail getInfo(@PathVariable Long id) {
        return productComboDetailService.getById(id);
    }

    /**
     * 分页查询组合商品明细表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ProductComboDetail> page(Page<ProductComboDetail> page) {
        return productComboDetailService.page(page);
    }

}
