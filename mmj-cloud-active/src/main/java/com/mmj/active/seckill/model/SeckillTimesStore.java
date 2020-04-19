package com.mmj.active.seckill.model;

import java.util.List;

public class SeckillTimesStore {

    private Integer timesId;

    private List<Integer> goodIds;

    public Integer getTimesId() {
        return timesId;
    }

    public void setTimesId(Integer timesId) {
        this.timesId = timesId;
    }

    public List<Integer> getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(List<Integer> goodIds) {
        this.goodIds = goodIds;
    }
}
