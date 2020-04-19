package com.mmj.active.homeManagement.model;

import java.util.List;

public class WebTopEx {
    private List<WebTop> webTopExList;

    private Integer showId;

    private Integer topShow;

    public List<WebTop> getWebTopExList() {
        return webTopExList;
    }

    public void setWebTopExList(List<WebTop> webTopExList) {
        this.webTopExList = webTopExList;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Integer getTopShow() {
        return topShow;
    }

    public void setTopShow(Integer topShow) {
        this.topShow = topShow;
    }
}
