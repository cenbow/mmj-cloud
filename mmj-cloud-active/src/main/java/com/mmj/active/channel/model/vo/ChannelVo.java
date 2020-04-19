package com.mmj.active.channel.model.vo;

import com.mmj.active.channel.model.dto.ChannelDayDto;

import java.io.Serializable;
import java.util.List;

public class ChannelVo implements Serializable {
    private String channelName;

    private String channelLink;

    private List<ChannelDayDto> dayList;

    private Integer personSumCount;  //扫码总人数

    private Integer scanSumCount;  //扫码总次数(扫码生成分销码人数)

    private Integer  personDaySumCount;   //固定时间段扫码总人数

    private Integer scanDaySumCount;     //固定时间段扫码总次数

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelLink() {
        return channelLink;
    }

    public void setChannelLink(String channelLink) {
        this.channelLink = channelLink;
    }

    public List<ChannelDayDto> getDayList() {
        return dayList;
    }

    public void setDayList(List<ChannelDayDto> dayList) {
        this.dayList = dayList;
    }

    public Integer getPersonSumCount() {
        return personSumCount;
    }

    public void setPersonSumCount(Integer personSumCount) {
        this.personSumCount = personSumCount;
    }

    public Integer getScanSumCount() {
        return scanSumCount;
    }

    public void setScanSumCount(Integer scanSumCount) {
        this.scanSumCount = scanSumCount;
    }

    public Integer getPersonDaySumCount() {
        return personDaySumCount;
    }

    public void setPersonDaySumCount(Integer personDaySumCount) {
        this.personDaySumCount = personDaySumCount;
    }

    public Integer getScanDaySumCount() {
        return scanDaySumCount;
    }

    public void setScanDaySumCount(Integer scanDaySumCount) {
        this.scanDaySumCount = scanDaySumCount;
    }
}
