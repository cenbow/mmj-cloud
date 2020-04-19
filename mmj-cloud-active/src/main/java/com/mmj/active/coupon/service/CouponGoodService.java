package com.mmj.active.coupon.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.coupon.model.CouponGood;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.CouponGoodDto;

import java.util.List;

/**
 * <p>
 * 优惠券关联商品表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
public interface CouponGoodService extends IService<CouponGood> {
    /**
     * 获取优惠券关联商品
     * @param couponId
     * @return
     */
    List<CouponGood> getCouponGood(Integer couponId);

    /**
     * 通过商品ID和商品分类编码查询该商品所有可用优惠券
     *
     * @param goodClass
     * @param goodId
     * @return
     */
    List<CouponInfo> getCouponInfoList(String goodClass, Integer goodId);

    /**
     * 获取优惠券关联商品-可展示价格的店铺商品
     *
     * @param couponId
     * @return
     */
    List<CouponGoodDto> getCouponGoods(Integer couponId);
}
