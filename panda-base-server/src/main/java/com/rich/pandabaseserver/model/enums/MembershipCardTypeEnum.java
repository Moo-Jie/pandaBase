package com.rich.pandabaseserver.model.enums;

import lombok.Getter;

/**
 * 会员卡类型枚举
 *
 * @author DuRuiChi
 */
@Getter
public enum MembershipCardTypeEnum {

    /**
     * 年卡
     */
    YEAR_CARD(1, "年卡"),

    /**
     * 月卡
     */
    MONTH_CARD(2, "月卡"),

    /**
     * 次票
     */
    TIMES_TICKET(3, "次票");

    private final Integer value;
    private final String text;

    MembershipCardTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static MembershipCardTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (MembershipCardTypeEnum typeEnum : MembershipCardTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}

