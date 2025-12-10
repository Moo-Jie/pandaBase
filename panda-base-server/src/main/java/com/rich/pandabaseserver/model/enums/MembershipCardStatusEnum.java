package com.rich.pandabaseserver.model.enums;

import lombok.Getter;

/**
 * 会员卡状态枚举
 *
 * @author DuRuiChi
 */
@Getter
public enum MembershipCardStatusEnum {

    /**
     * 未激活
     */
    INACTIVE(0, "未激活"),

    /**
     * 已激活
     */
    ACTIVE(1, "已激活"),

    /**
     * 已过期
     */
    EXPIRED(2, "已过期"),

    /**
     * 已作废
     */
    INVALID(3, "已作废");

    private final Integer value;
    private final String text;

    MembershipCardStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static MembershipCardStatusEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (MembershipCardStatusEnum statusEnum : MembershipCardStatusEnum.values()) {
            if (statusEnum.value.equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}

