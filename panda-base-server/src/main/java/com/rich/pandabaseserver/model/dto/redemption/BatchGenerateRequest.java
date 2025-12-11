package com.rich.pandabaseserver.model.dto.redemption;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 批量生成兑换码请求
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "批量生成兑换码请求")
public class BatchGenerateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 批次号
     */
    @Schema(description = "批次号（不填自动生成）")
    private String batchNo;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    private Integer count;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}

