package com.rich.pandabaseserver.controller;

import com.rich.pandabaseserver.annotation.AuthCheck;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.dto.export.ExportReportRequest;
import com.rich.pandabaseserver.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 报表导出控制器
 *
 * @author DuRuiChi
 */
@Slf4j
@RestController
@RequestMapping("/export")
@Tag(name = "报表导出接口")
public class ExportController {

    @Autowired
    private ExportService exportService;

    /**
     * 导出报表
     */
    @PostMapping("/report")
    @AuthCheck(mustRole = 2) // 管理员及以上才能导出
    @Operation(summary = "导出报表")
    public void exportReport(
            @RequestBody ExportReportRequest request,
            HttpServletResponse response
    ) {
        // 参数校验
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(request.getReportType() == null, ErrorCode.PARAMS_ERROR, "报表类型不能为空");
        ThrowUtils.throwIf(request.getStartDate() == null, ErrorCode.PARAMS_ERROR, "开始日期不能为空");
        ThrowUtils.throwIf(request.getEndDate() == null, ErrorCode.PARAMS_ERROR, "结束日期不能为空");

        // 日期验证
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new com.rich.pandabaseserver.exception.BusinessException(ErrorCode.PARAMS_ERROR, "开始日期不能大于结束日期");
        }

        log.info("导出报表请求，类型：{}，时间范围：{} 至 {}",
                request.getReportType(), request.getStartDate(), request.getEndDate());

        // 调用服务层导出
        exportService.exportReport(request, response);
    }
}
