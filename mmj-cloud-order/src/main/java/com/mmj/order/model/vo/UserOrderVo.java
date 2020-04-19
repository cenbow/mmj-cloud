package com.mmj.order.model.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class UserOrderVo {

    private String userId;

    private String orderNo;

    private boolean hasAfterSale = true;

}
