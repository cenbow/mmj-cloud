package com.mmj.active.callCharge.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 充值返回
 * @auther: KK
 * @date: 2019/8/31
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class PayInfoDto {

    private String sign;

    private String prepayId;

    private String nonceStr;

    private String timestamp;

    private String mwebUrl;

}
