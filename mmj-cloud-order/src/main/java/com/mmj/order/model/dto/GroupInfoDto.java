package com.mmj.order.model.dto;

import com.mmj.order.common.model.dto.GoodInfo;

/**
 * @Description: 拼团信息
 * @Auther: zhangyicao
 * @Date: 2019/06/11
 */
public class GroupInfoDto {
    private Integer userLevel;
    private String orderNo;
    private OrderGroupDto group;
    private OrderGoodsDto goods;
    private GoodInfo goodInfo;
    private int collageNumber = 0;

    public GroupInfoDto() {
    }

    public GroupInfoDto(Integer userLevel, OrderGroupDto group, OrderGoodsDto goods, GoodInfo goodInfo) {
        this.userLevel = userLevel;
        this.group = group;
        this.goods = goods;
        this.goodInfo = goodInfo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public OrderGroupDto getGroup() {
        return group;
    }

    public void setGroup(OrderGroupDto group) {
        this.group = group;
    }

    public OrderGoodsDto getGoods() {
        return goods;
    }

    public void setGoods(OrderGoodsDto goods) {
        this.goods = goods;
    }

    public GoodInfo getGoodInfo() {
        return goodInfo;
    }

    public void setGoodInfo(GoodInfo goodInfo) {
        this.goodInfo = goodInfo;
    }

    public int getCollageNumber() {
        return collageNumber;
    }

    public void setCollageNumber(int collageNumber) {
        this.collageNumber = collageNumber;
    }
}
