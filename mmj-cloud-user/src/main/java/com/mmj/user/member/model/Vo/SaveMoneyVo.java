package com.mmj.user.member.model.Vo;

import com.mmj.user.member.constant.SaveMoneySource;

/**
 * 写入会员省钱入参
 */
public class SaveMoneyVo {

    private SaveMoneySource source;

    private String money;

    private Long userId;

    private Long memberId;

    private String orderNo;

    public SaveMoneySource getSource() {
        return source;
    }

    public void setSource(SaveMoneySource source) {
        this.source = source;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "SaveMoneyVo{" +
                "source=" + source +
                ", money='" + money + '\'' +
                ", userId=" + userId +
                ", memberId=" + memberId +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }
}
