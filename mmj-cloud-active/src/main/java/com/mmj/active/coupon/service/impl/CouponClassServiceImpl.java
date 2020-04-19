package com.mmj.active.coupon.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.active.coupon.mapper.CouponClassMapper;
import com.mmj.active.coupon.model.CouponClass;
import com.mmj.active.coupon.service.CouponClassService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 优惠券关联商品分类表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@Service
public class CouponClassServiceImpl extends ServiceImpl<CouponClassMapper, CouponClass> implements CouponClassService {

    @Override
    public List<CouponClass> getCouponClass(Integer couponId) {
        CouponClass queryCouponClass = new CouponClass();
        queryCouponClass.setCouponId(couponId);
        EntityWrapper<CouponClass> goodClassEntityWrapper = new EntityWrapper(queryCouponClass);
        return selectList(goodClassEntityWrapper);
    }

    @Override
    public List<Integer> getCouponIds(String goodClass) {
        CouponClass queryCouponClass = new CouponClass();
        queryCouponClass.setGoodClass(goodClass);
        EntityWrapper<CouponClass> goodClassEntityWrapper = new EntityWrapper(queryCouponClass);
        List<CouponClass> couponClasses = selectList(goodClassEntityWrapper);
        List<Integer> couponIds = Lists.newArrayListWithCapacity(couponClasses.size());
        couponClasses.forEach(couponClass -> couponIds.add(couponClass.getCouponId()));
        return couponIds;
    }
}
