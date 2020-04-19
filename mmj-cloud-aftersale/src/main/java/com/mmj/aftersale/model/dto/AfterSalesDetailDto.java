package com.mmj.aftersale.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class AfterSalesDetailDto {

    private String  orderNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date afterSaleDate;

    private String userRemarks;

    private List<String> userImages;

    public AfterSalesDetailDto() {

    }

    public AfterSalesDetailDto(String orderNo, Date afterSaleDate, String userRemarks, List<String> userImages) {
        this.orderNo = orderNo;
        this.afterSaleDate = afterSaleDate;
        this.userRemarks = userRemarks;
        this.userImages = userImages;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getAfterSaleDate() {
        return afterSaleDate;
    }

    public void setAfterSaleDate(Date afterSaleDate) {
        this.afterSaleDate = afterSaleDate;
    }

    public String getUserRemarks() {
        return userRemarks;
    }

    public void setUserRemarks(String userRemarks) {
        this.userRemarks = userRemarks;
    }

    public List<String> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<String> userImages) {
        this.userImages = userImages;
    }



            ;

}
