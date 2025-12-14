package com.rich.pandabaseserver.service;

import com.rich.pandabaseserver.model.dto.export.ExportReportRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 导出服务接口
 *
 * @author DuRuiChi
 */
public interface ExportService {

    /**
     * 导出报表
     *
     * @param request 导出请求
     * @param response HTTP响应
     */
    void exportReport(ExportReportRequest request, HttpServletResponse response);
}
