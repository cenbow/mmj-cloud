package com.mmj.active.topic.model.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 活动排序
 */
public class ActiveSortDto {
    @ApiModelProperty(value = "配置ID")
    private Integer confId;

    @ApiModelProperty(value = "活动ID")
    private Integer businessId;

    @ApiModelProperty(value = "排序类型 RANDOM 随机 RULE 规则")
    private String orderType;

    @ApiModelProperty(value = "筛选规则 SALE 按销量 WAREHOUSE 按库存 CREATER 按创建时间 MODIFY 按编辑时间 THIRD 按三级分类")
    private String filterRule;

    @ApiModelProperty(value = "顺序 ASC升序 DESC 倒序 按三级分类时的值为规则拼接升降序")
    private String orderBy;

    public Integer getConfId() {
        return confId;
    }

    public void setConfId(Integer confId) {
        this.confId = confId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
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
}
