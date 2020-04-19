package com.mmj.aftersale.model.dto;

import java.util.Date;

/**
 *   退款金额详情
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class MoneyRefundDto {
     private String id;

     private String openId;

     private String outTradeNo;

     private Date createTime;

     private int refundFee;

     private int totalFee;

    public MoneyRefundDto() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(int refundFee) {
        this.refundFee = refundFee;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }
}
