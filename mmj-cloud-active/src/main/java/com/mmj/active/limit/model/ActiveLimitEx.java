package com.mmj.active.limit.model;

import com.mmj.active.common.model.ActiveGood;

import java.util.List;

public class ActiveLimitEx extends ActiveLimit {

    private List<Integer> goodIds;

    private List<ActiveLimitDetail> activeLimitDetails;

    private List<ActiveGood> activeGoods;//款式限定商品

    public List<Integer> getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(List<Integer> goodIds) {
        this.goodIds = goodIds;
    }

    public List<ActiveLimitDetail> getActiveLimitDetails() {
        return activeLimitDetails;
    }

    public void setActiveLimitDetails(List<ActiveLimitDetail> activeLimitDetails) {
        this.activeLimitDetails = activeLimitDetails;
    }

    public List<ActiveGood> getActiveGoods() {
        return activeGoods;
    }

    public void setActiveGoods(List<ActiveGood> activeGoods) {
        this.activeGoods = activeGoods;
    }
}
