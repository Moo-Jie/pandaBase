package com.rich.pandabaseserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户地址 VO
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "用户地址 VO")
public class UserAddressVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    @Schema(description = "地址ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

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
     * 完整地址（组合）
     */
    @Schema(description = "完整地址")
    private String fullAddress;

    /**
     * 是否默认地址
     */
    @Schema(description = "是否默认地址")
    private Boolean isDefault;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
