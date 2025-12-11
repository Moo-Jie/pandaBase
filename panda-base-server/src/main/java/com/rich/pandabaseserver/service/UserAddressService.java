package com.rich.pandabaseserver.service;

import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.dto.address.UserAddressAddRequest;
import com.rich.pandabaseserver.model.dto.address.UserAddressUpdateRequest;
import com.rich.pandabaseserver.model.entity.UserAddress;
import com.rich.pandabaseserver.model.vo.UserAddressVO;
import java.util.List;

/**
 * 用户地址表 服务层。
 *
 * @author @author DuRuiChi
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 添加收货地址
     *
     * @param userAddressAddRequest
     * @param userId
     * @return
     */
    Long addAddress(UserAddressAddRequest userAddressAddRequest, Long userId);

    /**
     * 更新收货地址
     *
     * @param userAddressUpdateRequest
     * @param userId
     * @return
     */
    Boolean updateAddress(UserAddressUpdateRequest userAddressUpdateRequest, Long userId);

    /**
     * 设置默认地址
     *
     * @param addressId
     * @param userId
     * @return
     */
    Boolean setDefaultAddress(Long addressId, Long userId);

    /**
     * 获取当前用户的所有地址
     *
     * @param userId
     * @return
     */
    List<UserAddressVO> listMyAddresses(Long userId);
}
