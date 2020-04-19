package com.mmj.active.relayLottery.model.dto;

public class LotteryResultDto {
    private Integer status;

    private String opencode;

    private long distanceOpenTime;//距离开奖时间  毫秒

    private Integer lackNumber;//还差多少人开奖

    private Integer lotteryResult;//开奖结果

    private Integer isFollow;//是否关注

    private Integer actid;//活动id

    private String wincode;//抽奖码

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOpencode() {
        return opencode;
    }

    public void setOpencode(String opencode) {
        this.opencode = opencode;
    }

    public long getDistanceOpenTime() {
        return distanceOpenTime;
    }

    public void setDistanceOpenTime(long distanceOpenTime) {
        this.distanceOpenTime = distanceOpenTime;
    }

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

    public Integer getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(Integer isFollow) {
        this.isFollow = isFollow;
    }

    public Integer getActid() {
        return actid;
    }

    public void setActid(Integer actid) {
        this.actid = actid;
    }

    public String getWincode() {
        return wincode;
    }

    public void setWincode(String wincode) {
        this.wincode = wincode;
    }
}
