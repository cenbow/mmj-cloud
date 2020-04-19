package com.mmj.order.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class OrderAddressVo implements Serializable {


    @NotNull
    private String orderNo;

    @NotNull
    private Long userId;


    private String country;   //  COUNTRY

    @NotNull
    private String province;

    @NotNull
    private String city;

    @NotNull
    private  String area;

    @NotNull
    private String consumerAddr;

    @NotNull
    private String consumerName;

    @NotNull
    private String consumerPhone;


}
