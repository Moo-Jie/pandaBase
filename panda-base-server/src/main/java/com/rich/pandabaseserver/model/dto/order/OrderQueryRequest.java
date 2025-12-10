package com.rich.pandabaseserver.model.dto.order;

import com.rich.pandabaseserver.common.response.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 订单查询请求
 *
 * @author DuRuiChi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单查询请求")
public class OrderQueryRequest extends PageRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long id;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 订单状态 0-待支付 1-已支付 2-已取消 3-已退款
     */
    @Schema(description = "订单状态")
    private Integer orderStatus;
}

