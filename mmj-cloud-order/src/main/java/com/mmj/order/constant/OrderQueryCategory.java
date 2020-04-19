package com.mmj.order.constant;

/**
 * @Description: 订单查询类型
 * @Auther: KK
 * @Date: 2018/11/24
 */
public enum OrderQueryCategory {
    ALL(0),
    WAIT_PAY(1),
    WAIT_GROUP(2),
    WAIT_SHIP(6),
    WAIT_RECEIPT(7),
    AFTER_SALE(3);
    private int status;

    OrderQueryCategory(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
