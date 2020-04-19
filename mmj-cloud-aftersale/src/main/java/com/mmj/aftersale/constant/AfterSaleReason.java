package com.mmj.aftersale.constant;

/**
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2018/12/27 21:21
 * 4
 */
public enum AfterSaleReason {

    REMARKS_USER_TYPE(0, "其他"),
    REMARKS_CONSUMER_TYPE(1, "不喜欢"),
    REMARKS_AUDIT_TYPE(2, "尺码问题"),
    REMARKS_TEST_TYPE(3, "质量问题");


    private Integer status;
    private String message;

    AfterSaleReason() {
    }

    AfterSaleReason(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取状态文字描述
     *
     * @param status
     * @return
     */
    public static String toStatusMessage(Integer status) {
        AfterSaleReason afterSaleReason[] = AfterSaleReason.values();
        for (AfterSaleReason afterSaleReason1 : afterSaleReason) {
            if (afterSaleReason1.getStatus().equals(status)) {
                return afterSaleReason1.getMessage();
            }
        }
        return "";
    }
}
