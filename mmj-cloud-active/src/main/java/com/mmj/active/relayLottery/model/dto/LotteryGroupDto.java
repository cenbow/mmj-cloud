package com.mmj.active.relayLottery.model.dto;


public class LotteryGroupDto extends GroupInfoDto {

    private Integer haveNumber;//已有多少人参与

    private Integer lackNumber;//开奖差多少人

    private long distanceOpenTime;//距离开奖时间

    private String wincode;//抽奖码

    private Integer actStatus;//活动状态

    private Long activeId;

    private String opencode;//开奖码

    private Integer isFollow;

    public Integer getHaveNumber() {
        return haveNumber;
    }

    public void setHaveNumber(Integer haveNumber) {
        this.haveNumber = haveNumber;
    }

    public Integer getLackNumber() {
        return lackNumber;
    }

    public void setLackNumber(Integer lackNumber) {
        this.lackNumber = lackNumber;
    }

    public long getDistanceOpenTime() {
        return distanceOpenTime;
    }

    public void setDistanceOpenTime(long distanceOpenTime) {
        this.distanceOpenTime = distanceOpenTime;
    }

    public String getWincode() {
        return wincode;
    }

    public void setWincode(String wincode) {
        this.wincode = wincode;
    }

    public Integer getActStatus() {
        return actStatus;
    }

    public void setActStatus(Integer actStatus) {
        this.actStatus = actStatus;
    }

    public Long getActiveId() {
        return activeId;
    }

    public void setActiveId(Long activeId) {
        this.activeId = activeId;
    }

    public String getOpencode() {
        return opencode;
    }

    public void setOpencode(String opencode) {
        this.opencode = opencode;
    }

    public Integer getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(Integer isFollow) {
        this.isFollow = isFollow;
    }
}
