package com.mmj.active.search.model;

import java.util.List;

public class SearchConfigurationEx extends SearchConfiguration {

    private String defaultKeyword;

    private String userName;

    private String image;

    private double shopPrice;

    private String activeType;

    private String spu;

    private boolean switchs;

    private List<Custom> customs;

    public String getDefaultKeyword() {
        return defaultKeyword;
    }

    public void setDefaultKeyword(String defaultKeyword) {
        this.defaultKeyword = defaultKeyword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(double shopPrice) {
        this.shopPrice = shopPrice;
    }

    public String getActiveType() {
        return activeType;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public String getSpu() {
        return spu;
    }

    public void setSpu(String spu) {
        this.spu = spu;
    }

    public boolean isSwitchs() {
        return switchs;
    }

    public void setSwitchs(boolean switchs) {
        this.switchs = switchs;
    }

    public List<Custom> getCustoms() {
        return customs;
    }

    public void setCustoms(List<Custom> customs) {
        this.customs = customs;
    }
}
