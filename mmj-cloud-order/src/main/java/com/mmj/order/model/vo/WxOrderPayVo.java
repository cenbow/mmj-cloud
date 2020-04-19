package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class WxOrderPayVo {

     private  String outTradeNo;

     private  String appId;

     private  String goodDesc;

     private  String openId;

     private  Integer totalFee;




}
