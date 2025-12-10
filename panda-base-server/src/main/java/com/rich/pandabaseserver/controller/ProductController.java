package com.rich.pandabaseserver.controller;

import com.mybatisflex.core.paginate.Page;
import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.dto.product.ProductQueryRequest;
import com.rich.pandabaseserver.model.entity.Product;
import com.rich.pandabaseserver.model.vo.ProductVO;
import com.rich.pandabaseserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 分页查询商品（封装类）
     *
     * @param productQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ProductVO>> listProductVOByPage(@RequestBody ProductQueryRequest productQueryRequest) {
        long current = productQueryRequest.getPageNum();
        long size = productQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        // 默认只查询上架商品
        if (productQueryRequest.getStatus() == null) {
            productQueryRequest.setStatus(1); 
        }

        Page<Product> productPage = productService.page(Page.of(current, size),
                productService.getQueryWrapper(productQueryRequest));
        
        Page<ProductVO> productVOPage = new Page<>(current, size, productPage.getTotalRow());
        productVOPage.setRecords(productPage.getRecords().stream().map(productService::getProductVO).toList());
        
        return ResultUtils.success(productVOPage);
    }

    /**
     * 根据主键获取商品详情（封装类）
     *
     * @param id 商品主键
     * @return 商品详情
     */
    @GetMapping("/get/vo")
    public BaseResponse<ProductVO> getProductVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Product product = productService.getById(id);
        ThrowUtils.throwIf(product == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(productService.getProductVO(product));
    }

}
