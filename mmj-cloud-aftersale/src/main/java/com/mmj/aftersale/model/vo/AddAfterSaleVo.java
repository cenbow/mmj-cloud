package com.mmj.aftersale.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
public class AddAfterSaleVo {

    @NotNull
    private String orderNo;

    private String userId;   // 当前订单的用户

    private String orderType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createrTime;

    private String consumerName;//收货人

    private String consumerMobile;//售后电话




}
