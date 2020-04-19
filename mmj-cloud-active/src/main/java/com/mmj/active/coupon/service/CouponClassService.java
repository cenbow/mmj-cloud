package com.mmj.active.coupon.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.coupon.model.CouponClass;

import java.util.List;

/**
 * <p>
 * 优惠券关联商品分类表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
public interface CouponClassService extends IService<CouponClass> {

    /**
     * 获取优惠券可用分类
     *
     * @param couponId
     * @return
     */
    List<CouponClass> getCouponClass(Integer couponId);

    /**
     * 通过商品分类编码查询该商品分类所有可用优惠券ID
     *
     * @param goodClass
     * @return
     */
    List<Integer> getCouponIds(String goodClass);
}
