package com.mmj.good.model;

import java.util.List;

public class GoodCompareEx extends GoodCompare {

    List<GoodCompareDetail> goodCompareDetails;

    public List<GoodCompareDetail> getGoodCompareDetails() {
        return goodCompareDetails;
    }

    public void setGoodCompareDetails(List<GoodCompareDetail> goodCompareDetails) {
        this.goodCompareDetails = goodCompareDetails;
    }
}
