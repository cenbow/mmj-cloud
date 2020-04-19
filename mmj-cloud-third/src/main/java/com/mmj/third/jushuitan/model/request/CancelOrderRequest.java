package com.mmj.third.jushuitan.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 取消订单
 * @auther: KK
 * @date: 2019/6/6
 */
@Data
@ApiModel("取消订单")
public class CancelOrderRequest {
    /**
     * 订单号
     */
    @JsonProperty("so_id")
    @JSONField(name = "so_id")
    @ApiModelProperty("订单号")
    private String soId;
    @ApiModelProperty("备注")
    private String remark;
}
