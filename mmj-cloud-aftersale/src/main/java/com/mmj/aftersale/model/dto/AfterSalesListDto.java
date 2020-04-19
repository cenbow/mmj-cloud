package com.mmj.aftersale.model.dto;


import com.mmj.aftersale.common.model.OrderGoodsDto;
import com.mmj.aftersale.model.AfterSales;

import java.util.Date;
import java.util.List;

/**
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class AfterSalesListDto extends AfterSales {

    private Integer orderStatus;

    private String orderStatusDesc;

    private String freight;

    private String couponAmount;

    private String afterStatusDesc;

    private String logisticsName;

    private String logisticsNo;

    // 聚水潭状态
    private  int jstStatus;

    private List<OrderGoodsDto> goodsList;



    public AfterSalesListDto() {

    }

    public int getJstStatus() {
        return jstStatus;
    }

    public void setJstStatus(int jstStatus) {
        this.jstStatus = jstStatus;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(String couponAmount) {
        this.couponAmount = couponAmount;
    }


    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusDesc() {
        return orderStatusDesc;
    }

    public void setOrderStatusDesc(String orderStatusDesc) {
        this.orderStatusDesc = orderStatusDesc;
    }

    public String getAfterStatusDesc() {
        return afterStatusDesc;
    }

    public void setAfterStatusDesc(String afterStatusDesc) {
        this.afterStatusDesc = afterStatusDesc;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public List<OrderGoodsDto> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<OrderGoodsDto> goodsList) {
        this.goodsList = goodsList;
    }
}
