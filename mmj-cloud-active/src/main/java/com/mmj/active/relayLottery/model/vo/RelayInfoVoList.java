package com.mmj.active.relayLottery.model.vo;

import com.mmj.active.relayLottery.model.RelayInfo;

import java.util.List;

public class RelayInfoVoList{

    private Integer relayId;

    private String relayName;

    private List<RelayInfoVo> list;
    //分页
    private Integer page = 1;
    private Integer size = 10;

    public Integer getRelayId() {
        return relayId;
    }

    public void setRelayId(Integer relayId) {
        this.relayId = relayId;
    }

    public String getRelayName() {
        return relayName;
    }

    public void setRelayName(String relayName) {
        this.relayName = relayName;
    }

    public List<RelayInfoVo> getList() {
        return list;
    }

    public void setList(List<RelayInfoVo> list) {
        this.list = list;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
