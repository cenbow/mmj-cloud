package com.mmj.order.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderListDto {

    private String orderNo;
    private Integer orderType;
    private String orderTypeDesc;

/*    private String packageNo;*/
    private Integer orderStatus;
    private Integer afterSaleStatus;
    private String afterSaleStatusDesc;
    private String orderStatusDesc;
    private String orderAmount;
    private String createDate;
    private String expireDate;
    private List<OrderListLogisticsDto> logistics;
    private List<OrderGoodsDto> good;
    //有填写快递单号
    private boolean hasExpress = true;
    private String afterSaleNo;

    //是否有推荐 0默认 1待评价 2已评价待分享
    private int hasRecommend = 0;
    private Integer recommendId;

}
