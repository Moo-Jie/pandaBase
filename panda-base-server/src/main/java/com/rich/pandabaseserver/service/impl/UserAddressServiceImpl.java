package com.rich.pandabaseserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.mapper.UserAddressMapper;
import com.rich.pandabaseserver.model.dto.address.UserAddressAddRequest;
import com.rich.pandabaseserver.model.dto.address.UserAddressUpdateRequest;
import com.rich.pandabaseserver.model.entity.UserAddress;
import com.rich.pandabaseserver.model.vo.UserAddressVO;
import com.rich.pandabaseserver.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户地址表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Slf4j
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAddress(UserAddressAddRequest userAddressAddRequest, Long userId) {
        // 参数校验
        ThrowUtils.throwIf(userAddressAddRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR);

        // 校验必填字段
        ThrowUtils.throwIf(userAddressAddRequest.getReceiverName() == null, ErrorCode.PARAMS_ERROR, "收货人姓名不能为空");
        ThrowUtils.throwIf(userAddressAddRequest.getPhone() == null, ErrorCode.PARAMS_ERROR, "手机号不能为空");
        ThrowUtils.throwIf(userAddressAddRequest.getProvince() == null, ErrorCode.PARAMS_ERROR, "省份不能为空");
        ThrowUtils.throwIf(userAddressAddRequest.getCity() == null, ErrorCode.PARAMS_ERROR, "城市不能为空");
        ThrowUtils.throwIf(userAddressAddRequest.getDistrict() == null, ErrorCode.PARAMS_ERROR, "区域不能为空");
        ThrowUtils.throwIf(userAddressAddRequest.getDetailAddress() == null, ErrorCode.PARAMS_ERROR, "详细地址不能为空");

        // 如果设置为默认地址，需要先取消其他默认地址
        if (Boolean.TRUE.equals(userAddressAddRequest.getIsDefault())) {
            clearDefaultAddress(userId);
        }

        // 创建地址
        UserAddress address = UserAddress.builder()
                .userId(userId)
                .receiverName(userAddressAddRequest.getReceiverName())
                .phone(userAddressAddRequest.getPhone())
                .province(userAddressAddRequest.getProvince())
                .city(userAddressAddRequest.getCity())
                .district(userAddressAddRequest.getDistrict())
                .detailAddress(userAddressAddRequest.getDetailAddress())
                .isDefault(userAddressAddRequest.getIsDefault() != null ? userAddressAddRequest.getIsDefault() : false)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        boolean saveResult = this.save(address);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "添加地址失败");

        log.info("添加地址成功，用户ID：{}，地址ID：{}", userId, address.getId());
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAddress(UserAddressUpdateRequest userAddressUpdateRequest, Long userId) {
        // 参数校验
        ThrowUtils.throwIf(userAddressUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userAddressUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR, "地址ID不能为空");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR);

        // 查询地址是否存在
        UserAddress address = this.getById(userAddressUpdateRequest.getId());
        ThrowUtils.throwIf(address == null, ErrorCode.NOT_FOUND_ERROR, "地址不存在");
        ThrowUtils.throwIf(!address.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR, "无权限修改此地址");

        // 如果设置为默认地址，需要先取消其他默认地址
        if (Boolean.TRUE.equals(userAddressUpdateRequest.getIsDefault())) {
            clearDefaultAddress(userId);
        }

        // 更新地址信息（只更新非空字段）
        if (userAddressUpdateRequest.getReceiverName() != null) {
            address.setReceiverName(userAddressUpdateRequest.getReceiverName());
        }
        if (userAddressUpdateRequest.getPhone() != null) {
            address.setPhone(userAddressUpdateRequest.getPhone());
        }
        if (userAddressUpdateRequest.getProvince() != null) {
            address.setProvince(userAddressUpdateRequest.getProvince());
        }
        if (userAddressUpdateRequest.getCity() != null) {
            address.setCity(userAddressUpdateRequest.getCity());
        }
        if (userAddressUpdateRequest.getDistrict() != null) {
            address.setDistrict(userAddressUpdateRequest.getDistrict());
        }
        if (userAddressUpdateRequest.getDetailAddress() != null) {
            address.setDetailAddress(userAddressUpdateRequest.getDetailAddress());
        }
        if (userAddressUpdateRequest.getIsDefault() != null) {
            address.setIsDefault(userAddressUpdateRequest.getIsDefault());
        }
        address.setUpdateTime(LocalDateTime.now());

        boolean updateResult = this.updateById(address);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新地址失败");

        log.info("更新地址成功，用户ID：{}，地址ID：{}", userId, address.getId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDefaultAddress(Long addressId, Long userId) {
        // 参数校验
        ThrowUtils.throwIf(addressId == null, ErrorCode.PARAMS_ERROR, "地址ID不能为空");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR);

        // 查询地址是否存在
        UserAddress address = this.getById(addressId);
        ThrowUtils.throwIf(address == null, ErrorCode.NOT_FOUND_ERROR, "地址不存在");
        ThrowUtils.throwIf(!address.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR, "无权限修改此地址");

        // 先取消其他默认地址
        clearDefaultAddress(userId);

        // 设置为默认地址
        address.setIsDefault(true);
        address.setUpdateTime(LocalDateTime.now());
        boolean updateResult = this.updateById(address);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "设置默认地址失败");

        log.info("设置默认地址成功，用户ID：{}，地址ID：{}", userId, addressId);
        return true;
    }

    @Override
    public List<UserAddressVO> listMyAddresses(Long userId) {
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR);

        // 查询用户所有地址，按默认地址和创建时间排序
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", userId)
                .orderBy("is_default DESC, create_time DESC");

        List<UserAddress> addressList = this.list(queryWrapper);

        return addressList.stream().map(this::getUserAddressVO).collect(Collectors.toList());
    }

    /**
     * 取消用户的其他默认地址
     */
    private void clearDefaultAddress(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", userId)
                .and("is_default = ?", true);

        List<UserAddress> defaultAddresses = this.list(queryWrapper);
        if (defaultAddresses != null && !defaultAddresses.isEmpty()) {
            for (UserAddress address : defaultAddresses) {
                address.setIsDefault(false);
                address.setUpdateTime(LocalDateTime.now());
            }
            this.updateBatch(defaultAddresses);
        }
    }

    /**
     * 获取地址VO
     */
    private UserAddressVO getUserAddressVO(UserAddress address) {
        if (address == null) {
            return null;
        }

        UserAddressVO addressVO = new UserAddressVO();
        BeanUtil.copyProperties(address, addressVO);

        // 组合完整地址
        String fullAddress = address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress();
        addressVO.setFullAddress(fullAddress);

        return addressVO;
    }
}
