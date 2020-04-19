package com.mmj.third.jushuitan.utils;

/**
 * @description: 聚水潭状态
 * @auther: KK
 * @date: 2019/6/6
 */
public enum JushuitanStatus {
    //买买家订单状态 1待付款 2待成团 3略 4待开奖 5已开奖 6待发货 7配送中 8已完成 9已关闭 10已取消
    WaitPay(0, "待付款"),
    Delivering(6, "发货中"),
    Merged(7, "被合并"),
    Question(7, "异常"),
    Split(6, "被拆分"),
    WaitOuterSent(6, "等供销商|外仓发货"),
    WaitConfirm(6, "已付款待审核"),
    WaitFConfirm(6, "已客审待财审"),
    Sent(7, "已发货"),
    Cancelled(10, "取消");

    private int status;
    private String message;

    JushuitanStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static String toStatus(int status) {
        JushuitanStatus[] statuses = JushuitanStatus.values();
        for (JushuitanStatus jushuitanStatus : statuses) {
            if (jushuitanStatus.status == status) {
                return jushuitanStatus.toString();
            }
        }
        return WaitPay.toString();
    }

    public static void main(String[] args) {
        System.out.println(toStatus(2));
    }
}
