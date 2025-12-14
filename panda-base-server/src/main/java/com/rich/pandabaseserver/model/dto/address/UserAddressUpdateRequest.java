package com.rich.pandabaseserver.model.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户地址更新请求
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "用户地址更新请求")
public class UserAddressUpdateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    @Schema(description = "地址ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 收货人姓名
     */
    @Schema(description = "收货人姓名")
    private String receiverName;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 省
     */
    @Schema(description = "省")
    private String province;

    /**
     * 市
     */
    @Schema(description = "市")
    private String city;

    /**
     * 区
     */
    @Schema(description = "区")
    private String district;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String detailAddress;

    /**
     * 是否默认地址
     */
    @Schema(description = "是否默认地址")
    private Boolean isDefault;
}

