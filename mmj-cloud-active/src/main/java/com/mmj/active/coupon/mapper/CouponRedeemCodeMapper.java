package com.mmj.active.coupon.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.active.coupon.model.CouponRedeemCode;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-09-03
 */
public interface CouponRedeemCodeMapper extends BaseMapper<CouponRedeemCode> {
    void batchInsert(List<CouponRedeemCode> list);

    List<CouponRedeemCode> selectRedeemCodes(String batchCode);
}
