package com.mmj.aftersale.constant;

public enum OrderTypeStatus {
    ORDINARY(1, "普通订单"),
    LOTTERY(2, "抽奖订单"),
    GROUP_BUY(3, "团购订单"),
    TEN_YUAN_SHOP(4, "十元店"),
    TEN_FOR_THREE_PIECE(5, "十元三件"),
    BARGAIN(6, "砍价类型"),
    ZERO_SHOPPING(7, "零元购"),
    RELAY_LOTTERY(8, "接力购抽奖"),
    NEW_CUSTOMER_FREE_POST(9, "新客免邮"),
    OTHER_CHANNELS(10, "其他渠道"),
    FREE_ORDER(11, "免费送订单"),
    MM_KING(12, "买买金兑换订单"),
    TWO_GROUP(13, "二人团"),
    NEWCOMERS(14, "新人团"),
    SPIKE(15, "秒杀订单"),
    TRY_OUT(16, "试用订单");

    private int status;
    private String message;

    OrderTypeStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 获取状态文字描述
     *
     * @param status
     * @return
     */
    public static String OrderTypeStatus(int status) {
        OrderTypeStatus orderTypeStatus[] = OrderTypeStatus.values();
        for (OrderTypeStatus orderTypeStatus1 : orderTypeStatus) {
            if (orderTypeStatus1.getStatus() == status) {
                return orderTypeStatus1.getMessage();
            }
        }
        return "";
    }
}
