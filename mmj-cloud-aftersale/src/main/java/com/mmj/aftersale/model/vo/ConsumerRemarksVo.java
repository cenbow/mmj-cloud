package com.mmj.aftersale.model.vo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  客户备注
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2018/12/25
 * 4
 */
public class ConsumerRemarksVo {


    @NotNull
    private String orderNo;

    private String  afterSaleNo;

    //用户备注
    @Length(min=0,max =255)
    private String userRemark;

    @NotNull
    private String createrId;

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }
}
