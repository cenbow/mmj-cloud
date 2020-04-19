package com.mmj.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
@AllArgsConstructor
public class UpdateStatusVo {

    @NotNull
    private String orderNo;

    @NotNull
    private Long userId;  // 当前订单的用户id

    @NotNull
    private int orderStatus;

    public UpdateStatusVo() {

    }

}
