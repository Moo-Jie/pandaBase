package com.rich.pandabaseserver.model.enums;

import lombok.Getter;

/**
 * 商品类型枚举
 *
 * @author DuRuiChi
 */
@Getter
public enum ProductTypeEnum {

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
    TIMES_TICKET(3, "次票"),

    /**
     * 实物商品
     */
    PHYSICAL_GOODS(4, "实物商品"),

    /**
     * 组合商品
     */
    COMBO(5, "组合商品");

    private final Integer value;
    private final String text;

    ProductTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 判断是否为虚拟票证（年卡、月卡、次票）
     */
    public static boolean isVirtualTicket(Integer type) {
        return type != null && (type == YEAR_CARD.value || type == MONTH_CARD.value || type == TIMES_TICKET.value);
    }

    /**
     * 根据值获取枚举
     */
    public static ProductTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ProductTypeEnum typeEnum : ProductTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}

