package com.mmj.order.constant;

/**
 * 售后状态
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2018/12/22 14:29
 * 4
 */
public enum AfterSalesStatus {

    RETURN_MONEY_APPLY(1, "退款申请中"),
    RETURN_GOODS_APPLLY(2, "退货申请中"),
    RETURN_GOODS_PASS(3, "退货申请通过"),
    RETRUN_GOODS_REFUSE(4, "退货申请拒绝"),
    RETURN_BACK_GOODS(5, "已寄回退货"),
    QUALITY_TEST_PASS(6, "质检通过"),
    QUALITY_TEST_REFISE(7, "质检不通过"),
    RETURN_MONEY_FINISH(8, "已退款"),
    RETURN_MONEY_REFUSE(9, "退款申请拒绝");




    private Integer status;
    private String message;

    AfterSalesStatus(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
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
    public static String toStatusMessage(Integer status) {
        AfterSalesStatus afterSalesStatus[] = AfterSalesStatus.values();
        for (AfterSalesStatus afterSalesStatus1 : afterSalesStatus) {
            if (afterSalesStatus1.getStatus().equals(status)) {
                return afterSalesStatus1.getMessage();
            }
        }
        return "";
    }

}
