package com.rich.pandabaseserver.model.dto.redemption;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 兑换请求
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "兑换请求")
public class RedeemRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 兑换码
     */
    @Schema(description = "兑换码", required = true)
    private String code;

    /**
     * 地址ID（实物商品必填）
     */
    @Schema(description = "地址ID")
    private Long addressId;

    /**
     * 用户ID（从登录态获取，前端不传）
     */
    @Schema(hidden = true)
    private Long userId;
}




















