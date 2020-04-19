package com.mmj.active.coupon.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.active.coupon.model.CouponInfo;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 优惠券信息表 Mapper 接口
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@Repository
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {
    /**
     * 优惠券发放总数量增量
     *
     * @param couponId
     * @return
     */
    Integer incrTotalSendNumber(Integer couponId);
}
