package com.mmj.user.common.model.vo;

import com.mmj.user.recommend.model.UserRecommend;

import java.util.List;
import java.util.Map;
/**
 * 小程序
 */
public class UserRecommendVo extends UserRecommend {

    private List<Map<String,Object>> usrList;

    private String price;  //商品价格

    private Integer fileFormat; //格式: 1:图片 2:视频

    private String goodType; //商品类型

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public List<Map<String, Object>> getUsrList() {
        return usrList;
    }

    public void setUsrList(List<Map<String, Object>> usrList) {
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
