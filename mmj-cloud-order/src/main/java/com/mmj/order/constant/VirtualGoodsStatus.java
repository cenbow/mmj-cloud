package com.mmj.order.constant;

/**
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2019/4/29 11:48
 * 4
 */
public enum VirtualGoodsStatus {

    YOUHUIJUAN_STATUS(1, "优惠券"),
    MMJ_STATUS(2, "买买金"),
    HUAFEI_STATUS(3, "话费");

    private int status;
    private String message;

    VirtualGoodsStatus(int status, String message) {
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
        VirtualGoodsStatus virtualGoodsStatus[] = VirtualGoodsStatus.values();
        for (VirtualGoodsStatus virtualGoodsStatus1 : virtualGoodsStatus) {
            if (virtualGoodsStatus1.getStatus() == status) {
                return virtualGoodsStatus1.getMessage();
            }
        }
        return "";
    }
}
