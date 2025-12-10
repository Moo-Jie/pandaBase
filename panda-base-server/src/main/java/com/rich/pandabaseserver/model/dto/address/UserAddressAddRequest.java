package com.rich.pandabaseserver.model.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户地址添加请求
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "用户地址添加请求")
public class UserAddressAddRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 收货人姓名
     */
    @Schema(description = "收货人姓名", required = true)
    private String receiverName;

    /**
     * 手机号
     */
    @Schema(description = "手机号", required = true)
    private String phone;

    /**
     * 省
     */
    @Schema(description = "省", required = true)
    private String province;

    /**
     * 市
     */
    @Schema(description = "市", required = true)
    private String city;

    /**
     * 区
     */
    @Schema(description = "区", required = true)
    private String district;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址", required = true)
    private String detailAddress;

    /**
     * 是否默认地址
     */
    @Schema(description = "是否默认地址")
    private Boolean isDefault;

    /**
     * 用户ID（从登录态获取，前端不传）
     */
    @Schema(hidden = true)
    private Long userId;
}
