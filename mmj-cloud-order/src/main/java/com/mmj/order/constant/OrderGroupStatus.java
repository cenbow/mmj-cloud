package com.mmj.order.constant;

/**
 * @Description: 拼团状态
 * @Auther: KK
 * @Date: 2018/12/20
 */
public enum OrderGroupStatus {

    JOINING(0, "进行中"),
    COMPLETED(1, "已完成"),
    EXPIRE(2, "已过期"),
    CANCELLED(3, "已取消(拼主取消订单)"),
    WAIT_AWARD(4, "待开奖"),
    AWARDED(5, "已开奖"),
    END(6, "已结束(活动已结束)");

    private int status;
    private String describe;

    OrderGroupStatus(int status, String describe) {
        this.status = status;
        this.describe = describe;
    }

    public int getStatus() {
        return status;
    }

    public String getDescribe() {
        return describe;
    }

    public static String OrderTypeStatus(int status) {
        OrderGroupStatus orderTypeStatus[] = OrderGroupStatus.values();
        for (OrderGroupStatus orderTypeStatus1 : orderTypeStatus) {
            if (orderTypeStatus1.getStatus() == status) {
                return orderTypeStatus1.getDescribe();
            }
        }
        return "";
    }
}
