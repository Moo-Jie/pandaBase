package com.rich.pandabaseserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 导出结果 VO
 *
 * @author DuRuiChi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "导出结果 VO")
public class ExportResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 文件URL
     */
    @Schema(description = "文件URL")
    private String fileUrl;

    /**
     * 导出记录数
     */
    @Schema(description = "导出记录数")
    private Integer recordCount;

    /**
     * 提示信息
     */
    @Schema(description = "提示信息")
    private String message;
}

