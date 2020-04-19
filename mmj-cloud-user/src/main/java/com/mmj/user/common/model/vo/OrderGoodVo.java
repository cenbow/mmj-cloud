package com.mmj.user.common.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderGoodVo {

    @NotNull
    private String orderNo;

    @NotNull
    private String userId;  // 下单用户id

    public OrderGoodVo(String orderNo, String userId) {
        this.orderNo = orderNo;
        this.userId = userId;
    }

    public OrderGoodVo() {
    }
}
