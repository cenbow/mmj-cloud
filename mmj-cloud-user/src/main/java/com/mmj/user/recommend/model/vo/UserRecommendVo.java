package com.mmj.user.recommend.model.vo;

import com.mmj.user.recommend.model.UserRecommend;
import com.mmj.user.recommend.model.UserRecommendFile;

import java.util.List;
/**
 * 小程序
 */
public class UserRecommendVo extends UserRecommend {

    private List<UserRecommendFile> usrList;

    private String price;  //商品价格

    private Integer fileFormat; //格式: 1:图片 2:视频

    private String goodType; //商品类型

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public List<UserRecommendFile> getUsrList() {
        return usrList;
    }

    public void setUsrList(List<UserRecommendFile> usrList) {
        this.usrList = usrList;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(Integer fileFormat) {
        this.fileFormat = fileFormat;
    }
}
