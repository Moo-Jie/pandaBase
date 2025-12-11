package com.rich.pandabaseserver.service;

import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.entity.Banner;

import java.util.List;

/**
 * Banner轮播图 服务层
 *
 * @author DuRuiChi
 */
public interface BannerService extends IService<Banner> {

    /**
     * 获取有效的Banner列表
     *
     * @return Banner列表
     */
    List<Banner> getActiveBanners();
}

