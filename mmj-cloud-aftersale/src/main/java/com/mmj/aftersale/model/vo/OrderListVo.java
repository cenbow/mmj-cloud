package com.mmj.aftersale.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;


/**
 * 小程序列表
 */

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderListVo {

    /**
     * 订单类型：待支付-WAIT_PAY，待成团-WAIT_GROUP，待发货-WAIT_SHIP，配送中-WAIT_RECEIPT，售后-AFTER_SALE
     */

    @NotNull
    private String category = "ALL";

    @NotNull
    private Integer currentPage;

    private Integer pageSize;

    private String openid;


    private String userId;


}
