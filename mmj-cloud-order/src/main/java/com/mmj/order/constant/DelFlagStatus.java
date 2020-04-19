package com.mmj.order.constant;

public enum DelFlagStatus {

    NOT_DEL_STATUS(0, "逻辑上已删除"),
    DEL_STATUS(1, "逻辑上未删除");
    private int status;
    private String message;


    DelFlagStatus(int status, String message) {
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
        DelFlagStatus delFlagStatus[] = DelFlagStatus.values();
        for (DelFlagStatus sta : delFlagStatus) {
            if (sta.getStatus() == status) {
                return sta.getMessage();
            }
        }
        return "";
    }


}
