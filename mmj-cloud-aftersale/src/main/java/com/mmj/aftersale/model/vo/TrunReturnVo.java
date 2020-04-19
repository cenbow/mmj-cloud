package com.mmj.aftersale.model.vo;

import javax.validation.constraints.NotNull;

/**
 * 转退货的请求体
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class TrunReturnVo {

    @NotNull
    private String orderNo;

    @NotNull
    private boolean status;

    @NotNull
    private String createrId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }
}
