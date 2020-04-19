package com.mmj.active.coupon.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.coupon.model.CouponRedeemCode;
import com.mmj.active.coupon.model.dto.CouponInfoDto;
import com.mmj.active.coupon.model.vo.ExchangeCouponVo;
import com.mmj.active.coupon.model.vo.RedeemCodeVo;
import com.mmj.common.model.UserCouponDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-09-03
 */
public interface CouponRedeemCodeService extends IService<CouponRedeemCode> {

    String addRedeemCode(RedeemCodeVo redeemCodeVo);

    String downloadRedeemCode(HttpServletRequest request, HttpServletResponse response, String batchCode);

    Map<String,Object> exchangeCoupon(ExchangeCouponVo exchangeCouponVo);

    List<UserCouponDto> getRedeemCoupon(ExchangeCouponVo exchangeCouponVo);

    void kafkaExchangeCoupon(JSONObject jsonObject);

}
