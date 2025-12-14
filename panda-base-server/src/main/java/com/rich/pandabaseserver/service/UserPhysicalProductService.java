package com.rich.pandabaseserver.service;

import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.entity.UserPhysicalProduct;

import java.util.List;

/**
 * 用户实体商品表 服务层
 *
 * @author DuRuiChi
 */
public interface UserPhysicalProductService extends IService<UserPhysicalProduct> {

    /**
     * 获取用户的所有实体商品
     *
     * @param userId 用户ID
     * @return 实体商品列表
     */
    List<UserPhysicalProduct> getMyPhysicalProducts(Long userId);
}



