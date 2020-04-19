package com.mmj.order.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class BossListDto {


    private String orderNo;

    private Integer orderStatus;

    private String orderStatusDesc;

    private String orderDate;

    private Date payTime;

    private String orderAmount;

    private String freight;

    private String couponAmount;

    private String afterSaleNo;

    private List<OrderGoodsDto> goods;

    private String userId;  // 下单用户的Id


}
