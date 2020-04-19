package com.mmj.aftersale.model.vo;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 退货请求体
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class AfterSalesResultVo {

    @NotNull
    private String afterSaleNo;

    @NotNull
    private boolean auditStatus;

    @NotNull
    private String createrId;

    private String remarks;

    private Integer addressId;



    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public AfterSalesResultVo() {

    }

    public boolean isAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(boolean auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }
}
