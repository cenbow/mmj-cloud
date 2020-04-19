package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderLogisticsDto {


    private String consumerName;

    private  String consumerMobile;

    private  String  consumerAddr;

    private  String province;

    private  String city;

    private  String area;

    private  String  sendTime;

    private  String checkTime;




}
