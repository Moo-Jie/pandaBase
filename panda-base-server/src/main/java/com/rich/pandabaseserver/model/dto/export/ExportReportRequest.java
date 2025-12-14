package com.rich.pandabaseserver.model.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 导出报表请求
 *
 * @author DuRuiChi
 */
@Data
@Schema(description = "导出报表请求")
public class ExportReportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表类型
     * 1-会员卡数据
     * 2-兑换码数据
     * 3-兑换记录数据
     * 4-订单入账数据
     */
    @Schema(description = "报表类型")
    private Integer reportType;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private LocalDate endDate;
}

