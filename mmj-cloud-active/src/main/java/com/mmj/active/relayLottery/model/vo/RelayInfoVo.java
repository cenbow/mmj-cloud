package com.mmj.active.relayLottery.model.vo;

import com.mmj.active.relayLottery.model.RelayInfo;

public class RelayInfoVo extends RelayInfo {

    private String goodImage;

    private Integer goodId;

    private String goodName;

    private Integer saleId;

    public String getGoodImage() {
        return goodImage;
    }

    public void setGoodImage(String goodImage) {
        this.goodImage = goodImage;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }
}
