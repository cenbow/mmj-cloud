package com.mmj.good.feigin.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class TopicInfo {

    @ApiModelProperty(value = "主题ID")
    private Integer topicId;

    @ApiModelProperty(value = "主题名称")
    private String topicName;

    @ApiModelProperty(value = "横幅URL")
    private String topicBanner;

    @ApiModelProperty(value = "排序")
    private Integer orderId;

    @ApiModelProperty(value = "分享URL")
    private String shardUrl;

    @ApiModelProperty(value = "分享标题")
    private String shardTitle;

    @ApiModelProperty(value = "模版")
    private Integer topicTemplate;

    @ApiModelProperty(value = "专题商品类型")
    private Integer topicGoodType;

    @ApiModelProperty(value = "分类CLASS、商品GOOD 类型编码CLASSCODE用逗号隔开")
    private String topicGoodClass;

    @ApiModelProperty(value = "创建人")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicBanner() {
        return topicBanner;
    }

    public void setTopicBanner(String topicBanner) {
        this.topicBanner = topicBanner;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getShardUrl() {
        return shardUrl;
    }

    public void setShardUrl(String shardUrl) {
        this.shardUrl = shardUrl;
    }

    public String getShardTitle() {
        return shardTitle;
    }

    public void setShardTitle(String shardTitle) {
        this.shardTitle = shardTitle;
    }

    public Integer getTopicTemplate() {
        return topicTemplate;
    }

    public void setTopicTemplate(Integer topicTemplate) {
        this.topicTemplate = topicTemplate;
    }

    public Integer getTopicGoodType() {
        return topicGoodType;
    }

    public void setTopicGoodType(Integer topicGoodType) {
        this.topicGoodType = topicGoodType;
    }

    public String getTopicGoodClass() {
        return topicGoodClass;
    }

    public void setTopicGoodClass(String topicGoodClass) {
        this.topicGoodClass = topicGoodClass;
    }

    public Long getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }

    public Date getCreaterTime() {
        return createrTime;
    }

    public void setCreaterTime(Date createrTime) {
        this.createrTime = createrTime;
    }

    public Long getModifyId() {
        return modifyId;
    }

    public void setModifyId(Long modifyId) {
        this.modifyId = modifyId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
