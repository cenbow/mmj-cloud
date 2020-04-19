package com.mmj.active.common.model.vo;

import com.mmj.common.model.BaseModel;

import java.util.List;

public class ActiveGoodOrderVo extends BaseModel {

    private String activeType;      //活动类型 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀 6 优惠券 7 砍价

    private String goodStatus;      //商品状态 -1：删除 0：暂不发布 1：立即上架 2：自动上架

    private Integer virtualFlag;    //是否虚拟商品

    private Integer memberFlag;     //是否会员商品

    private Integer delFlag;        //是否删除

    private Integer autoShow;       //是否自动展示

    private String fileServer;      //文件服务商 ALIYUN TENGXUN

    private Integer showFlag;       //是否显示

    private List<Integer> goodIds;  //商品id

    private List<Integer> noGoodIds;  //例外商品id

    private String orderBy;         //排序sql

    public String getActiveType() {
        return activeType;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public String getGoodStatus() {
        return goodStatus;
    }

    public void setGoodStatus(String goodStatus) {
        this.goodStatus = goodStatus;
    }

    public Integer getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Integer virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public Integer getMemberFlag() {
        return memberFlag;
    }

    public void setMemberFlag(Integer memberFlag) {
        this.memberFlag = memberFlag;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getAutoShow() {
        return autoShow;
    }

    public void setAutoShow(Integer autoShow) {
        this.autoShow = autoShow;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    public Integer getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(Integer showFlag) {
        this.showFlag = showFlag;
    }

    public List<Integer> getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(List<Integer> goodIds) {
        this.goodIds = goodIds;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<Integer> getNoGoodIds() {
        return noGoodIds;
    }

    public void setNoGoodIds(List<Integer> noGoodIds) {
        this.noGoodIds = noGoodIds;
    }
}
