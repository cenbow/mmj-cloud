package com.mmj.order.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 取消订单
 * @auther: KK
 * @date: 2019/7/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "取消订单", description = "取消订单")
public class CancelOrderDto {
    @ApiModelProperty("包裹号")
    private String packageNo;
    @ApiModelProperty("备注")
    private String remark;
}
