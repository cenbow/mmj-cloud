package com.mmj.order.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @Description: 快递查询
 * @Auther: KK
 * @Date: 2018/10/15
 */
@ApiModel("快递请求参数")
public class LogisticsVo {
    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("快递单号")
    @NotNull
    private String lId;
    @ApiModelProperty("快递公司简称")
    private String lcCode;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getlId() {
        return lId;
    }

    public void setlId(String lId) {
        this.lId = lId;
    }

    public String getLcCode() {
        return lcCode;
    }

    public void setLcCode(String lcCode) {
        this.lcCode = lcCode;
    }
}
