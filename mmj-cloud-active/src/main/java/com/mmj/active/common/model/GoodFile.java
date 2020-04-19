package com.mmj.active.common.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class GoodFile {
    @ApiModelProperty(value = "文件ID")
    private Integer fileId;

    @ApiModelProperty(value = "组编码")
    private String groupCode;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "规格ID")
    private Integer modelId;

    @ApiModelProperty(value = "文件服务商 ALIYUN TENGXUN")
    private String fileServer;

    @ApiModelProperty(value = "附件类型 SELLING_POINT：卖点 IMAGE：商品图片 MAINVIDEO：主视频 VIDEOTITLE：视频封面 WECHAT：小程序分享 H5：H5分享 DETAIL：详情 DETAILVIDEO 详情视频 DETAILTITLE：视频封面 SALEMODEL:规格图片 ACTIVE:活动图片")
    private String fileType;

    @ApiModelProperty(value = "文件路由")
    private String fileUrl;

    @ApiModelProperty(value = "是否封面")
    private Integer titleFlag;

    @ApiModelProperty(value = "排序")
    private Integer fileOrder;

    @ApiModelProperty(value = "附件标签 逗号隔开")
    private String fileLabel;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "活动类型 0 商品 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀  6 优惠券 7 砍价")
    private Integer activeType;

    @ApiModelProperty(value = "活动ID")
    private Integer businessId;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getTitleFlag() {
        return titleFlag;
    }

    public void setTitleFlag(Integer titleFlag) {
        this.titleFlag = titleFlag;
    }

    public Integer getFileOrder() {
        return fileOrder;
    }

    public void setFileOrder(Integer fileOrder) {
        this.fileOrder = fileOrder;
    }

    public String getFileLabel() {
        return fileLabel;
    }

    public void setFileLabel(String fileLabel) {
        this.fileLabel = fileLabel;
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

    public Integer getActiveType() {
        return activeType;
    }

    public void setActiveType(Integer activeType) {
        this.activeType = activeType;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }
}
