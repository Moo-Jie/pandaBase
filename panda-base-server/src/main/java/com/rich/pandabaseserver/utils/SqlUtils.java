package com.rich.pandabaseserver.utils;

import cn.hutool.core.util.StrUtil;
import com.rich.pandabaseserver.constant.CommonConstant;

/**
 * SQL 工具类
 *
 * @author DuRuiChi
 */
public class SqlUtils {

    /**
     * 根据排序字段和排序顺序生成排序SQL片段
     *
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     * @return 排序SQL（如：id DESC）
     */
    public static String toOrderBy(String sortField, String sortOrder) {
        if (StrUtil.isBlank(sortField)) {
            return null;
        }
        
        // 验证排序字段，防止SQL注入
        if (!validSortField(sortField)) {
            return null;
        }
        
        boolean isAsc = CommonConstant.SORT_ORDER_ASC.equals(sortOrder);
        return sortField + (isAsc ? " ASC" : " DESC");
    }

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField 排序字段
     * @return 是否合法
     */
    private static boolean validSortField(String sortField) {
        if (StrUtil.isBlank(sortField)) {
            return false;
        }
        // 只允许字母、数字、下划线
        return sortField.matches("^[a-zA-Z0-9_]+$");
    }
}

