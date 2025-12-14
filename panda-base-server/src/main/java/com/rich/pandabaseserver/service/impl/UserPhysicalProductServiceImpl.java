package com.rich.pandabaseserver.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.mapper.UserPhysicalProductMapper;
import com.rich.pandabaseserver.model.entity.UserPhysicalProduct;
import com.rich.pandabaseserver.service.UserPhysicalProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户实体商品表 服务层实现
 *
 * @author DuRuiChi
 */
@Service
public class UserPhysicalProductServiceImpl extends ServiceImpl<UserPhysicalProductMapper, UserPhysicalProduct> implements UserPhysicalProductService {

    @Override
    public List<UserPhysicalProduct> getMyPhysicalProducts(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("user_id", userId)
                .orderBy("create_time", false); // 按创建时间倒序
        return this.list(queryWrapper);
    }
}



