package com.rich.pandabaseserver.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 补单类型枚举
 *
 * @author DuRuiChi
 */
@Getter
public enum RepairOrderTypeEnum {

    USER_QUERY("手动查询微信补单", 1),
    ADMIN_FORCE("管理员强制补单", 2);

    private final String text;

    private final Integer value;

    RepairOrderTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static RepairOrderTypeEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (RepairOrderTypeEnum anEnum : RepairOrderTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}









