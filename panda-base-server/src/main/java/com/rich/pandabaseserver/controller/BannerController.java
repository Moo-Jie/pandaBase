package com.rich.pandabaseserver.controller;

import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.model.entity.Banner;
import com.rich.pandabaseserver.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Banner轮播图 控制层
 *
 * @author DuRuiChi
 */
@RestController
@RequestMapping("/banner")
@Tag(name = "Banner管理", description = "轮播图相关接口")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取有效的Banner列表
     *
     * @return Banner列表
     */
    @GetMapping("/list/active")
    @Operation(summary = "获取有效Banner列表", description = "获取当前有效的轮播图列表，按排序返回")
    public BaseResponse<List<Banner>> getActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return ResultUtils.success(banners);
    }
}

