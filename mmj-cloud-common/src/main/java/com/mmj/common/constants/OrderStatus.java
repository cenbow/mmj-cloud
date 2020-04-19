package com.mmj.common.constants;

/**
 * 订单状态
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2019/6/3 16:05
 * 4
 */
public enum OrderStatus {
    PENDING_PAYMENT(1, "待付款"),
    TO_BE_A_GROUP(2, "待成团"), //待分享
    PAYMENTED(3, "已支付"),
    TO_BE_AWARDED(4, "待开奖"), //业务待处理中
    //    AWARDED(5, "已开奖"),
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
