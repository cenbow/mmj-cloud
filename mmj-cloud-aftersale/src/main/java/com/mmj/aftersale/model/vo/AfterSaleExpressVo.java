package com.mmj.aftersale.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.validation.constraints.NotNull;

/**
 * @Description: 填写寄回快递信息
 * @Auther: zhangyicao
 * @Date: 2019/06/17
 */
public class AfterSaleExpressVo {

	@JsonSerialize(using = ToStringSerializer.class)

    @NotNull
    private String afterSaleNo;
    @NotNull
    private String logisticsNo;
    @NotNull
    private String logisticsName;
    @NotNull
    private String logisticsCode;

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }
}
