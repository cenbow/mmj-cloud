package com.mmj.aftersale.model.vo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class MoneyReturnVo {

    @NotNull
    private String afterSaleNo;

    @NotNull
    private BigDecimal refundAmount;

    @NotNull
    @Length(min = 1, max = 255)
    private String refundRemarks;

    @NotNull
    private String createrId;

    public MoneyReturnVo() {

    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }


    public String getRefundRemarks() {
        return refundRemarks;
    }

    public void setRefundRemarks(String refundRemarks) {
        this.refundRemarks = refundRemarks;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }
}
