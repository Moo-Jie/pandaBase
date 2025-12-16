package com.rich.pandabaseserver.model.enums;

import lombok.Getter;

/**
 * 兑换码状态枚举
 *
 * @author DuRuiChi
 */
@Getter
public enum RedemptionCodeStatusEnum {

    /**
     * 未使用
     */
    UNUSED(0, "未使用"),

    /**
     * 已使用
     */
    USED(1, "已使用"),

    /**
     * 已过期
     */
    EXPIRED(2, "已过期"),

    /**
     * 已作废
     */
    VOID(3, "已作废");

    private final Integer value;
    private final String text;

    RedemptionCodeStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static RedemptionCodeStatusEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (RedemptionCodeStatusEnum statusEnum : RedemptionCodeStatusEnum.values()) {
            if (statusEnum.value.equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}

