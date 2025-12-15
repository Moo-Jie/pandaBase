package com.rich.pandabaseserver.model.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author DuRuiChi
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 待支付
     */
    PENDING(0, "待支付"),

    /**
     * 已支付
     */
    PAID(1, "已支付"),

    /**
     * 已取消
     */
    CANCELLED(2, "已取消"),

    /**
     * 已退款
     */
    REFUNDED(3, "已退款"),

    /**
     * 已过期
     */
    EXPIRED(4, "已过期");

    private final Integer value;
    private final String text;

    OrderStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static OrderStatusEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (OrderStatusEnum statusEnum : OrderStatusEnum.values()) {
            if (statusEnum.value.equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
