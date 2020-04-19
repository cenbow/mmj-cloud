package com.mmj.aftersale.constant;

/**
 * @Description: 订单类型
 * 1待付款 2待成团 3略 4待开奖 5已开奖 6待发货 7配送中 8已完成 9已关闭 10已取消
 * @Auther: KK
 * @Date: 2018/11/24
 */
public enum OrderStatus {
    PENDING_PAYMENT(1, "待付款"),
    TO_BE_A_GROUP(2, "待成团"),
    TO_BE_AWARDED(4, "待开奖"),
    AWARDED(5, "已开奖"),
    TO_BE_DELIVERED(6, "待发货"),
    PENDING_RECEIPT(7, "配送中"),
    COMPLETED(8, "已完成"),
    CLOSED(9, "已关闭"),
    CANCELLED(10, "已取消");
    private int status;
    private String message;

    OrderStatus(int status, String message) {
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
    public static String toStatusMessage(int status) {
        OrderStatus orderStatus[] = OrderStatus.values();
        for (OrderStatus orderStatus1 : orderStatus) {
            if (orderStatus1.getStatus() == status) {
                return orderStatus1.getMessage();
            }
        }
        return "";
    }
}
