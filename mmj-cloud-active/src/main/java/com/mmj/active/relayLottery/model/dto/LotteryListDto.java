package com.mmj.active.relayLottery.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

public class LotteryListDto {

    private String periodName;//期名称

    private String goodImage;

    private String goodName;

    private Integer goodId;

    private Integer lotteryactivityid;

    private Integer relayNumber;//活动接力人数

    private Integer lackNumber;//还差多少人

    private Integer status;//拼团状态

    private Long groupPeople;//拼团所需人数

    private Long currentPeople;//当前人数

    private List<Member> headList;//头像List

    private String orderNo;

    private String groupNo;

    public static class Member {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long userid;
        private String headimgurl;
        private String nickname;
        private String unionid;

        public Long getUserid() {
            return userid;
        }

        public void setUserid(Long userid) {
            this.userid = userid;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public Member(Long userid,String headimgurl,String nickname,String unionid){
            this.userid = userid;
            this.headimgurl = headimgurl;
            this.nickname = nickname;
            this.unionid = unionid;
        }
    }


    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getGoodImage() {
        return goodImage;
    }

    public void setGoodImage(String goodImage) {
        this.goodImage = goodImage;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public Integer getLotteryactivityid() {
        return lotteryactivityid;
    }

    public void setLotteryactivityid(Integer lotteryactivityid) {
        this.lotteryactivityid = lotteryactivityid;
    }

    public Integer getRelayNumber() {
        return relayNumber;
    }

    public void setRelayNumber(Integer relayNumber) {
        this.relayNumber = relayNumber;
    }

    public Integer getLackNumber() {
        return lackNumber;
    }

    public void setLackNumber(Integer lackNumber) {
        this.lackNumber = lackNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getGroupPeople() {
        return groupPeople;
    }

    public void setGroupPeople(Long groupPeople) {
        this.groupPeople = groupPeople;
    }

    public Long getCurrentPeople() {
        return currentPeople;
    }

    public void setCurrentPeople(Long currentPeople) {
        this.currentPeople = currentPeople;
    }

    public List<Member> getHeadList() {
        return headList;
    }

    public void setHeadList(List<Member> headList) {
        this.headList = headList;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }
}
