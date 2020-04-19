package com.mmj.order.constant;

/**
 * <p>
 * 拼团类型
 * </p>
 * @since 2019-06-10
 */
public enum OrderGroupType {
    TWO_GROUP(1, "二人团"),
    LOTTERY(2, "抽奖"),
    NEWCOMERS(3, "接力购"),
    SPIKE(4, "秒杀"),
    RELAY_LOTTERY(5, "接力购抽奖"),
    FREE_ORDER(6,"免费送");

    private int type;
    private String name;

    OrderGroupType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
