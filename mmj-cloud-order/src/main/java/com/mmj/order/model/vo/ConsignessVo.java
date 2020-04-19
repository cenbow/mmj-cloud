package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 收货人信息
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2019/6/4 11:11
 * 4
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class ConsignessVo {

    private String country;

    @NotNull
    private String province;

    @NotNull
    private String city;

    @NotNull
    private String area;

    @NotNull
    private String consumerAddr;

    @NotNull
    private String consumerName;

    @NotNull
    private String consumerMobile;


}
