package com.mmj.pay.service;

import com.mmj.pay.dto.OrderPriceDetailsInfo;
import com.mmj.pay.model.vo.CartOrderCouponParam;
import com.mmj.pay.model.vo.CartOrderGoodsDetails;
import org.springframework.stereotype.Service;


public interface CalcOrderPriceService {

    OrderPriceDetailsInfo calcOrderPrice();


    /**
     * 计算订单金额明细，提供给前端
     *
     * @return
     */
    OrderPriceDetailsInfo calcOrderPrice(CartOrderCouponParam param,Boolean checkOrder);

    /**
     * 对生成的订单进行价格计算，并将优惠和运费按金额比例分摊到各个商品
     */
    CartOrderGoodsDetails calcFinalPrice(CartOrderGoodsDetails cogd) throws Exception;












}
