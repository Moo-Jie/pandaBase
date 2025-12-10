package com.rich.pandabaseserver.controller;

import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.dto.address.UserAddressAddRequest;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.model.vo.UserAddressVO;
import com.rich.pandabaseserver.service.UserAddressService;
import com.rich.pandabaseserver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户地址表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/address")
@Tag(name = "用户地址管理", description = "用户地址相关接口")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserService userService;

    /**
     * 添加收货地址
     *
     * @param addressAddRequest 地址添加请求
     * @param request HTTP请求
     * @return 地址ID
     */
    @PostMapping("/add")
    @Operation(summary = "添加收货地址", description = "用户添加收货地址")
    public BaseResponse<Long> addAddress(@RequestBody UserAddressAddRequest addressAddRequest,
                                         HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Long addressId = userAddressService.addAddress(addressAddRequest, loginUser.getId());
        return ResultUtils.success(addressId);
    }

    /**
     * 查询我的地址列表
     *
     * @param request HTTP请求
     * @return 地址列表
     */
    @GetMapping("/list/my")
    @Operation(summary = "查询我的地址列表", description = "查询当前用户的所有收货地址")
    public BaseResponse<List<UserAddressVO>> listMyAddresses(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        List<UserAddressVO> addressList = userAddressService.listMyAddresses(loginUser.getId());
        return ResultUtils.success(addressList);
    }

    /**
     * 根据ID获取地址详情
     *
     * @param id 地址ID
     * @param request HTTP请求
     * @return 地址详情
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "获取地址详情", description = "根据ID获取地址详情")
    public BaseResponse<UserAddressVO> getAddressById(@PathVariable Long id,
                                                      HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 查询地址
        var address = userAddressService.getById(id);
        ThrowUtils.throwIf(address == null, ErrorCode.NOT_FOUND_ERROR, "地址不存在");
        ThrowUtils.throwIf(!address.getUserId().equals(loginUser.getId()), 
                ErrorCode.NO_AUTH_ERROR, "无权限访问此地址");

        // 转换为VO
        UserAddressVO addressVO = new UserAddressVO();
        cn.hutool.core.bean.BeanUtil.copyProperties(address, addressVO);
        String fullAddress = address.getProvince() + address.getCity() + 
                address.getDistrict() + address.getDetailAddress();
        addressVO.setFullAddress(fullAddress);

        return ResultUtils.success(addressVO);
    }

    /**
     * 删除地址
     *
     * @param id 地址ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除地址", description = "删除收货地址")
    public BaseResponse<Boolean> deleteAddress(@PathVariable Long id,
                                                HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 查询地址
        var address = userAddressService.getById(id);
        ThrowUtils.throwIf(address == null, ErrorCode.NOT_FOUND_ERROR, "地址不存在");
        ThrowUtils.throwIf(!address.getUserId().equals(loginUser.getId()), 
                ErrorCode.NO_AUTH_ERROR, "无权限删除此地址");

        boolean result = userAddressService.removeById(id);
        return ResultUtils.success(result);
    }
}
