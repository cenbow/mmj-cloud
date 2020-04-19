package com.mmj.order.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mmj.order.common.model.vo.CartOrderGoodsDetails;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.model.OrderPayment;
import com.mmj.order.model.vo.OrderSaveVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class SaveOrderDto {

    private String orderId;

    private String orderNo;

    private String orderAmount;

    private String groupNo;

    private Integer orderType;

    private Long userId;

    @JsonIgnore
    private OrderSaveVo orderSaveVo;

    @JsonIgnore
    private OrderInfo orderInfo;

    @JsonIgnore
    private List<OrderGood> orderGoods;

    @JsonIgnore
    private OrderLogistics orderLogistics;

    @JsonIgnore
    private OrderPayment orderPayment;
}
