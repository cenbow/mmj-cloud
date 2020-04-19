package com.mmj.aftersale.model.vo;

import javax.validation.constraints.NotNull;

/**
 * @Author: zhangyicao
 * @Date: 2019/06/17
 *
 */
public class AfterSalesListVo {

    @NotNull
    private Integer currentPage;

    @NotNull
    private Integer pageSize;

    private String orderNo;

    private Integer orderStatus;

    private Integer afterStatus;

    private String consigneeName;

    private String checkPhone;

    private String checkName;

    private String consigneeTel;

    private String beginOrderDate;

    private String endOrderDate;

    private String beginAfSaleDate;

    private String endAfSaleDate;

    private Long createrId;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(Integer afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getCheckPhone() {
        return checkPhone;
    }

    public void setCheckPhone(String checkPhone) {
        this.checkPhone = checkPhone;
    }

    public String getBeginOrderDate() {
        return beginOrderDate;
    }

    public void setBeginOrderDate(String beginOrderDate) {
        this.beginOrderDate = beginOrderDate;
    }

    public String getEndOrderDate() {
        return endOrderDate;
    }

    public void setEndOrderDate(String endOrderDate) {
        this.endOrderDate = endOrderDate;
    }

    public String getBeginAfSaleDate() {
        return beginAfSaleDate;
    }

    public void setBeginAfSaleDate(String beginAfSaleDate) {
        this.beginAfSaleDate = beginAfSaleDate;
    }

    public String getEndAfSaleDate() {
        return endAfSaleDate;
    }

    public void setEndAfSaleDate(String endAfSaleDate) {
        this.endAfSaleDate = endAfSaleDate;
    }

    public String getConsigneeTel() {
        return consigneeTel;
    }

    public void setConsigneeTel(String consigneeTel) {
        this.consigneeTel = consigneeTel;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public Long getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }
}
