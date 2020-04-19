package com.mmj.active.callCharge.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @description: 充值消息
 * @auther: KK
 * @date: 2019/8/31
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class PayInfoVo {

    private String openId;

    @NotNull
    private String appId;

    @NotNull
    private String orderNo;

}