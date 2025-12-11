package com.rich.pandabaseserver.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.mapper.BannerMapper;
import com.rich.pandabaseserver.model.entity.Banner;
import com.rich.pandabaseserver.service.BannerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Banner轮播图 服务层实现
 *
 * @author DuRuiChi
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> getActiveBanners() {
        LocalDateTime now = LocalDateTime.now();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("status = ?", 1)
                .and("(start_time IS NULL OR start_time <= ?)", now)
                .and("(end_time IS NULL OR end_time >= ?)", now)
                .orderBy("sort_order ASC");

        return this.list(queryWrapper);
    }
}

