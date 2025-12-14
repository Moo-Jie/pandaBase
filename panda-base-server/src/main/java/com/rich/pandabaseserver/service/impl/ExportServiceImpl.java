package com.rich.pandabaseserver.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.rich.pandabaseserver.exception.BusinessException;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.model.dto.export.ExportReportRequest;
import com.rich.pandabaseserver.model.entity.*;
import com.rich.pandabaseserver.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 导出服务实现
 *
 * @author DuRuiChi
 */
@Slf4j
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private MembershipCardService membershipCardService;

    @Autowired
    private RedemptionCodeService redemptionCodeService;

    @Autowired
    private RedemptionRecordService redemptionRecordService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Override
    public void exportReport(ExportReportRequest request, HttpServletResponse response) {
        Integer reportType = request.getReportType();
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        String fileNamePrefix;
        Workbook workbook;

        try {
            switch (reportType) {
                case 1 -> {
                    // 会员卡数据
                    fileNamePrefix = "会员卡数据";
                    workbook = createMembershipCardsWorkbook(startDate, endDate);
                }
                case 2 -> {
                    // 兑换码数据
                    fileNamePrefix = "兑换码数据";
                    workbook = createRedemptionCodesWorkbook(startDate, endDate);
                }
                case 3 -> {
                    // 兑换记录数据
                    fileNamePrefix = "兑换记录数据";
                    workbook = createRedemptionRecordsWorkbook(startDate, endDate);
                }
                case 4 -> {
                    // 订单入账数据
                    fileNamePrefix = "订单入账数据";
                    workbook = createOrderRecordsWorkbook(startDate, endDate);
                }
                default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的报表类型");
            }

            // 生成文件名
            String timestamp = DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss");
            String fileName = fileNamePrefix + "_" + timestamp + ".xlsx";

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            // 写入响应流
            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                workbook.close();
            }

        } catch (Exception e) {
            log.error("导出报表失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "导出失败：" + e.getMessage());
        }
    }

    /**
     * 创建会员卡数据工作簿
     */
    private Workbook createMembershipCardsWorkbook(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("create_time >= ?", startDateTime)
                .and("create_time <= ?", endDateTime)
                .orderBy("create_time DESC");

        List<MembershipCard> cards = membershipCardService.list(queryWrapper);

        // 获取关联数据
        Set<Long> userIds = cards.stream().map(MembershipCard::getUserId).collect(Collectors.toSet());
        Set<Long> productIds = cards.stream().map(MembershipCard::getProductId).collect(Collectors.toSet());
        
        Map<Long, User> userMap = getUserMap(userIds);
        Map<Long, Product> productMap = getProductMap(productIds);

        // 创建Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("会员卡数据");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "用户ID", "账号", "用户昵称", "手机号", "商品ID", "商品名称", "卡号", 
                "卡类型", "状态", "总次数", "已用次数", "开始时间", "结束时间", "订单ID", "兑换记录ID", "创建时间"};
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        int rowNum = 1;
        for (MembershipCard card : cards) {
            Row row = sheet.createRow(rowNum++);
            User user = userMap.get(card.getUserId());
            Product product = productMap.get(card.getProductId());

            int col = 0;
            row.createCell(col++).setCellValue(card.getId());
            row.createCell(col++).setCellValue(getUserOpenid(user)); // 修改为 OpenID
            row.createCell(col++).setCellValue(getUserAccount(user)); // 添加账号
            row.createCell(col++).setCellValue(getUserNickname(user));
            row.createCell(col++).setCellValue(getUserPhone(user));
            row.createCell(col++).setCellValue(card.getProductId());
            row.createCell(col++).setCellValue(getProductName(product));
            row.createCell(col++).setCellValue(getValueOrNone(card.getCardNumber()));
            row.createCell(col++).setCellValue(getCardTypeName(card.getCardType()));
            row.createCell(col++).setCellValue(getCardStatusName(card.getStatus()));
            row.createCell(col++).setCellValue(card.getTotalCount() != null ? String.valueOf(card.getTotalCount()) : "无限制");
            row.createCell(col++).setCellValue(card.getUsedCount());
            row.createCell(col++).setCellValue(formatDateTime(card.getStartTime()));
            row.createCell(col++).setCellValue(formatDateTime(card.getEndTime()));
            row.createCell(col++).setCellValue(card.getOrderId() != null ? String.valueOf(card.getOrderId()) : "无");
            row.createCell(col++).setCellValue(card.getRedemptionRecordId() != null ? String.valueOf(card.getRedemptionRecordId()) : "无");
            row.createCell(col++).setCellValue(formatDateTime(card.getCreateTime()));
        }

        adjustColumnWidths(sheet, headers.length);
        return workbook;
    }

    /**
     * 创建兑换码数据工作簿
     */
    private Workbook createRedemptionCodesWorkbook(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("create_time >= ?", startDateTime)
                .and("create_time <= ?", endDateTime)
                .orderBy("create_time DESC");

        List<RedemptionCode> codes = redemptionCodeService.list(queryWrapper);

        // 获取关联数据
        Set<Long> productIds = codes.stream().map(RedemptionCode::getProductId).collect(Collectors.toSet());
        Set<Long> userIds = codes.stream()
                .map(RedemptionCode::getUseUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Product> productMap = getProductMap(productIds);
        Map<Long, User> userMap = getUserMap(userIds);

        // 创建Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("兑换码数据");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "兑换码", "商品ID", "商品名称", "批次号", "状态", "过期时间", 
                "使用时间", "使用用户ID", "使用账号", "使用用户昵称", "创建时间"};
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        int rowNum = 1;
        for (RedemptionCode code : codes) {
            Row row = sheet.createRow(rowNum++);
            Product product = productMap.get(code.getProductId());
            User user = userMap.get(code.getUseUserId());

            int col = 0;
            row.createCell(col++).setCellValue(code.getId());
            row.createCell(col++).setCellValue(getValueOrNone(code.getCode()));
            row.createCell(col++).setCellValue(code.getProductId());
            row.createCell(col++).setCellValue(getProductName(product));
            row.createCell(col++).setCellValue(getValueOrNone(code.getBatchNo()));
            row.createCell(col++).setCellValue(getCodeStatusName(code.getStatus()));
            row.createCell(col++).setCellValue(formatDateTime(code.getExpireTime()));
            row.createCell(col++).setCellValue(formatDateTime(code.getUseTime()));
            row.createCell(col++).setCellValue(getUserOpenid(user)); // 修改为 OpenID
            row.createCell(col++).setCellValue(getUserAccount(user)); // 添加账号
            row.createCell(col++).setCellValue(getUserNickname(user));
            row.createCell(col++).setCellValue(formatDateTime(code.getCreateTime()));
        }

        adjustColumnWidths(sheet, headers.length);
        return workbook;
    }

    /**
     * 创建兑换记录数据工作簿
     */
    private Workbook createRedemptionRecordsWorkbook(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("create_time >= ?", startDateTime)
                .and("create_time <= ?", endDateTime)
                .orderBy("create_time DESC");

        List<RedemptionRecord> records = redemptionRecordService.list(queryWrapper);

        // 获取关联数据
        Set<Long> userIds = records.stream().map(RedemptionRecord::getUserId).collect(Collectors.toSet());
        Set<Long> productIds = records.stream().map(RedemptionRecord::getProductId).collect(Collectors.toSet());
        Set<Long> codeIds = records.stream().map(RedemptionRecord::getRedemptionCodeId).collect(Collectors.toSet());

        Map<Long, User> userMap = getUserMap(userIds);
        Map<Long, Product> productMap = getProductMap(productIds);
        Map<Long, RedemptionCode> codeMap = getCodeMap(codeIds);

        // 创建Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("兑换记录数据");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "记录编号", "用户ID", "账号", "用户昵称", "兑换码ID", "兑换码", 
                "商品ID", "商品名称", "商品类型", "状态", "收货地址", "物流单号", 
                "发货时间", "完成时间", "创建时间"};
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        int rowNum = 1;
        for (RedemptionRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            User user = userMap.get(record.getUserId());
            Product product = productMap.get(record.getProductId());
            RedemptionCode code = codeMap.get(record.getRedemptionCodeId());

            int col = 0;
            row.createCell(col++).setCellValue(record.getId());
            row.createCell(col++).setCellValue(getValueOrNone(record.getRecordNo()));
            row.createCell(col++).setCellValue(getUserOpenid(user)); // 修改为 OpenID
            row.createCell(col++).setCellValue(getUserAccount(user)); // 添加账号
            row.createCell(col++).setCellValue(getUserNickname(user));
            row.createCell(col++).setCellValue(record.getRedemptionCodeId());
            row.createCell(col++).setCellValue(code != null ? code.getCode() : "无");
            row.createCell(col++).setCellValue(record.getProductId());
            row.createCell(col++).setCellValue(getProductName(product));
            row.createCell(col++).setCellValue(getProductTypeName(record.getProductType()));
            row.createCell(col++).setCellValue(getRecordStatusName(record.getStatus()));
            row.createCell(col++).setCellValue(getValueOrNone(record.getShippingAddress()));
            row.createCell(col++).setCellValue(getValueOrNone(record.getTrackingNumber()));
            row.createCell(col++).setCellValue(formatDateTime(record.getShipTime()));
            row.createCell(col++).setCellValue(formatDateTime(record.getCompleteTime()));
            row.createCell(col++).setCellValue(formatDateTime(record.getCreateTime()));
        }

        adjustColumnWidths(sheet, headers.length);
        return workbook;
    }

    /**
     * 创建订单入账数据工作簿
     */
    private Workbook createOrderRecordsWorkbook(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        // 查询已支付且未退款的订单
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("pay_time >= ?", startDateTime)
                .and("pay_time <= ?", endDateTime)
                .and("order_status = ?", 1) // 已支付
                .orderBy("pay_time DESC");

        List<PurchaseOrder> orders = purchaseOrderService.list(queryWrapper);

        // 获取关联数据
        Set<Long> userIds = orders.stream().map(PurchaseOrder::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = getUserMap(userIds);

        // 创建Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("订单入账数据");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "订单号", "用户ID", "账号", "用户昵称", "手机号", "订单总金额", "实付金额", 
                "订单状态", "支付时间", "微信交易号", "创建时间"};
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        int rowNum = 1;
        for (PurchaseOrder order : orders) {
            Row row = sheet.createRow(rowNum++);
            User user = userMap.get(order.getUserId());

            int col = 0;
            row.createCell(col++).setCellValue(order.getId());
            row.createCell(col++).setCellValue(getValueOrNone(order.getOrderNo()));
            row.createCell(col++).setCellValue(getUserOpenid(user)); // 修改为 OpenID
            row.createCell(col++).setCellValue(getUserAccount(user)); // 添加账号
            row.createCell(col++).setCellValue(getUserNickname(user));
            row.createCell(col++).setCellValue(getUserPhone(user));
            row.createCell(col++).setCellValue(order.getTotalAmount().doubleValue());
            row.createCell(col++).setCellValue(order.getPayAmount().doubleValue());
            row.createCell(col++).setCellValue(getOrderStatusName(order.getOrderStatus()));
            row.createCell(col++).setCellValue(formatDateTime(order.getPayTime()));
            row.createCell(col++).setCellValue(getValueOrNone(order.getTransactionId()));
            row.createCell(col++).setCellValue(formatDateTime(order.getCreateTime()));
        }

        adjustColumnWidths(sheet, headers.length);
        return workbook;
    }

    // --- 辅助方法 ---

    private Map<Long, User> getUserMap(Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        return userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (v1, v2) -> v1));
    }

    private Map<Long, Product> getProductMap(Set<Long> productIds) {
        if (CollUtil.isEmpty(productIds)) {
            return Collections.emptyMap();
        }
        return productService.listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity(), (v1, v2) -> v1));
    }

    private Map<Long, RedemptionCode> getCodeMap(Set<Long> codeIds) {
        if (CollUtil.isEmpty(codeIds)) {
            return Collections.emptyMap();
        }
        return redemptionCodeService.listByIds(codeIds).stream()
                .collect(Collectors.toMap(RedemptionCode::getId, Function.identity(), (v1, v2) -> v1));
    }

    private String getUserNickname(User user) {
        return user != null && StrUtil.isNotBlank(user.getNickname()) ? user.getNickname() : "无";
    }

    private String getUserPhone(User user) {
        return user != null && StrUtil.isNotBlank(user.getPhone()) ? user.getPhone() : "无";
    }

    private String getUserOpenid(User user) {
        return user != null && StrUtil.isNotBlank(user.getOpenid()) ? user.getOpenid() : "无";
    }

    private String getUserAccount(User user) {
        return user != null && StrUtil.isNotBlank(user.getAccount()) ? user.getAccount() : "无";
    }

    private String getProductName(Product product) {
        return product != null && StrUtil.isNotBlank(product.getName()) ? product.getName() : "无";
    }

    private String getValueOrNone(String value) {
        return StrUtil.isNotBlank(value) ? value : "无";
    }

    private void adjustColumnWidths(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            // 最小宽度 10个字符
            if (width < 256 * 10) {
                sheet.setColumnWidth(i, 256 * 10);
            }
            // 最大宽度 50个字符
            if (width > 256 * 50) {
                sheet.setColumnWidth(i, 256 * 50);
            }
        }
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    /**
     * 辅助方法：格式化日期时间
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "无";
        }
        return DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    private String getCardTypeName(Integer type) {
        if (type == null) return "无";
        return switch (type) {
            case 1 -> "年卡";
            case 2 -> "月卡";
            case 3 -> "次票";
            default -> "未知";
        };
    }

    private String getCardStatusName(Integer status) {
        if (status == null) return "无";
        return switch (status) {
            case 0 -> "未激活";
            case 1 -> "已激活";
            case 2 -> "已过期";
            case 3 -> "已作废";
            default -> "未知";
        };
    }

    private String getCodeStatusName(Integer status) {
        if (status == null) return "无";
        return switch (status) {
            case 0 -> "未使用";
            case 1 -> "已使用";
            case 2 -> "已过期";
            default -> "未知";
        };
    }

    private String getProductTypeName(Integer type) {
        if (type == null) return "无";
        return switch (type) {
            case 1 -> "年卡";
            case 2 -> "月卡";
            case 3 -> "次票";
            case 4 -> "实物商品";
            default -> "未知";
        };
    }

    private String getRecordStatusName(Integer status) {
        if (status == null) return "无";
        return switch (status) {
            case 0 -> "兑换中";
            case 1 -> "已完成";
            case 2 -> "已发货";
            default -> "未知";
        };
    }

    private String getOrderStatusName(Integer status) {
        if (status == null) return "无";
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已取消";
            case 3 -> "已退款";
            default -> "未知";
        };
    }
}
