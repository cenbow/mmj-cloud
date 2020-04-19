package com.mmj.active.relayLottery.model.dto;

import java.util.Date;
import java.util.List;

public class OpenLotteryInfo {
    private Integer lackNumber;//还差多少人开奖

    private Integer lotteryResult;//开奖结果

    private String goodsName;//商品名称

    private String periodsName;//期数

    private Integer runLotteryNumber;//所需参与人数

    private Date startTime;//开始时间

    private Date endTime;//结束时间

    private Date runTime;//开奖时间

    private List<LotteryListDto.Member> headList;//头像列表

    private Integer joinNumber;//参与人数

    private String openCode;//开奖编码

    private LotteryListDto.Member head;//开奖人

    public Integer getLackNumber() {
        return lackNumber;
    }

    public void setLackNumber(Integer lackNumber) {
        this.lackNumber = lackNumber;
    }

    public Integer getLotteryResult() {
        return lotteryResult;
    }

    public void setLotteryResult(Integer lotteryResult) {
        this.lotteryResult = lotteryResult;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPeriodsName() {
        return periodsName;
    }

    public void setPeriodsName(String periodsName) {
        this.periodsName = periodsName;
    }

    public Integer getRunLotteryNumber() {
        return runLotteryNumber;
    }

    public void setRunLotteryNumber(Integer runLotteryNumber) {
        this.runLotteryNumber = runLotteryNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public List<LotteryListDto.Member> getHeadList() {
        return headList;
    }

    public void setHeadList(List<LotteryListDto.Member> headList) {
        this.headList = headList;
    }

    public Integer getJoinNumber() {
        return joinNumber;
    }

    public void setJoinNumber(Integer joinNumber) {
        this.joinNumber = joinNumber;
    }

    public String getOpenCode() {
        return openCode;
    }

    public void setOpenCode(String openCode) {
        this.openCode = openCode;
    }

    public LotteryListDto.Member getHead() {
        return head;
    }

    public void setHead(LotteryListDto.Member head) {
        this.head = head;
    }
}
