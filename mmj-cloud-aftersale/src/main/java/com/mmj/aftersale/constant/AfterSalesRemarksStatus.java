package com.mmj.aftersale.constant;

/**
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2018/12/24 15:16
 * 4
 */
public enum AfterSalesRemarksStatus {

    REMARKS_USER_TYPE(0, "用户备注"),
    REMARKS_CONSUMER_TYPE(1, "客服备注"),
    REMARKS_AUDIT_TYPE(2, "审核备注"),
    REMARKS_TEST_TYPE(3, "质检备注");


    private Integer status;
    private String message;

    AfterSalesRemarksStatus() {

    }

    AfterSalesRemarksStatus(Integer status, String message) {
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
        AfterSalesRemarksStatus afterSalesRemarksStatus[] = AfterSalesRemarksStatus.values();
        for (AfterSalesRemarksStatus afterSalesRemarksStatus1 : afterSalesRemarksStatus) {
            if (afterSalesRemarksStatus1.getStatus().equals(status)) {
                return afterSalesRemarksStatus1.getMessage();
            }
        }
        return "";
    }

}
