package com.mmj.good.feigin.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

public class ThreeSaleTenner {

    @ApiModelProperty(value = "配置ID")
    private Integer infoId;

    @ApiModelProperty(value = "活动状态")
    private Integer activityStatus;

    @ApiModelProperty(value = "排序类型 CUSTOM 自定义 RULE 规则")
    private String orderType;

    @ApiModelProperty(value = "筛选规则 SALE 按销量 WAREHOUSE 按库存 CREATER 按创建时间 MODIFY 按编辑时间 THIRD 按三级分类")
    private String filterRule;

    @ApiModelProperty(value = "顺序 ASC升序 DESC 倒序 按三级分类时的值为规则拼接升降序")
    private String orderBy;

    @ApiModelProperty(value = "每人每天限购次数")
    private Integer everyNum;

    @ApiModelProperty(value = "金额")
    private BigDecimal activeAmount;

    @ApiModelProperty(value = "每次可购买数量")
    private Integer timesNum;

    @ApiModelProperty(value = "参与间隔时间（小时）")
    private Integer limitHours;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

    public Integer getInfoId() {
        return infoId;
    }

    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(String filterRule) {
        this.filterRule = filterRule;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getEveryNum() {
        return everyNum;
    }

    public void setEveryNum(Integer everyNum) {
        this.everyNum = everyNum;
    }

    public BigDecimal getActiveAmount() {
        return activeAmount;
    }

    public void setActiveAmount(BigDecimal activeAmount) {
        this.activeAmount = activeAmount;
    }

    public Integer getTimesNum() {
        return timesNum;
    }

    public void setTimesNum(Integer timesNum) {
        this.timesNum = timesNum;
    }

    public Integer getLimitHours() {
        return limitHours;
    }

    public void setLimitHours(Integer limitHours) {
        this.limitHours = limitHours;
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
