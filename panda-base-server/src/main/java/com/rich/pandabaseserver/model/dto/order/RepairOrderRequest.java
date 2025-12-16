package com.rich.pandabaseserver.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 补单请求
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "补单请求")
public class RepairOrderRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 订单编号（用户补单时使用，可选）
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 订单ID（超级管理员补单时使用）
     */
    @Schema(description = "订单ID")
    private Long orderId;

    /**
     * 补单原因说明
     */
    @Schema(description = "补单原因")
    private String reason;
}









