package com.mmj.aftersale.model.vo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class RefuseTeturnGoodsVo {


    @NotNull
    private String orderNo;

    @NotNull
    private String createrId;

    @Length(min = 0, max = 255)
    private String remark;

    public RefuseTeturnGoodsVo() {

    }

    public RefuseTeturnGoodsVo(String orderNo, String remark) {
        this.orderNo = orderNo;
        this.remark = remark;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }
}
