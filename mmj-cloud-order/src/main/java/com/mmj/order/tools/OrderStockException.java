package com.mmj.order.tools;

import com.mmj.common.model.GoodStock;
import com.mmj.order.model.OrderGood;
import lombok.Data;

import java.util.List;

/**
 * @description: 扣减库存异常
 * @auther: KK
 * @date: 2019/9/9
 */
@Data
public class OrderStockException extends IllegalArgumentException {
    private List<OrderGood> orderGoodList;

    public OrderStockException(String s, List<OrderGood> orderGoodList) {
        super(s);
        this.orderGoodList = orderGoodList;
    }

    public OrderStockException(String message, List<OrderGood> orderGoodList, Throwable cause) {
        super(message, cause);
        this.orderGoodList = orderGoodList;
    }
}
