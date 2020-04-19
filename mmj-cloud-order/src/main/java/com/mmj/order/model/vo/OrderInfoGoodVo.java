package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderInfoGoodVo {


    private Long userId;

    private String orderNo;

    private Integer saleId;

    private Integer goodId;


}
