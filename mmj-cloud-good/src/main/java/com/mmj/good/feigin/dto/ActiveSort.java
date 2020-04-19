package com.mmj.good.feigin.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 活动排序公用表
 * </p>
 *
 * @author H.J
 * @since 2019-06-25
 */
public class ActiveSort {

    @ApiModelProperty(value = "配置ID")
    private Integer confId;

    @ApiModelProperty(value = "活动类型 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀  6 优惠券 7 砍价 8 主题 9 猜你喜欢 10 免邮热卖")
    private Integer activeType;

    @ApiModelProperty(value = "活动ID")
    private Integer businessId;

    @ApiModelProperty(value = "分类编码")
    private String goodClass;

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

    public String getGoodClass() {
        return goodClass;
    }

    public void setGoodClass(String goodClass) {
        this.goodClass = goodClass;
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

