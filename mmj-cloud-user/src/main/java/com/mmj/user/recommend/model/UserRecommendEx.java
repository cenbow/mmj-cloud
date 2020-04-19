package com.mmj.user.recommend.model;

import java.util.List;

/**
 * boss后台
 */
public class UserRecommendEx extends  UserRecommend{

    private List<String> fileUrlList;

    public List<String> getFileUrlList() {
        return fileUrlList;
    }

    public void setFileUrlList(List<String> fileUrlList) {
        this.fileUrlList = fileUrlList;
    }
}
