package com.mmj.aftersale.model.vo;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 质检请求体
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class AfterSalesTestVo {

    @NotNull
    private String afterSaleNo;

    @NotNull
    private boolean auditStatus;

    @NotNull
    private String createrId;

    private String remarks;

    public AfterSalesTestVo(String afterSaleNo, boolean auditStatus, String remarks) {
        this.afterSaleNo = afterSaleNo;
        this.auditStatus = auditStatus;
        this.remarks = remarks;
    }

    public AfterSalesTestVo(){

    }

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
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
