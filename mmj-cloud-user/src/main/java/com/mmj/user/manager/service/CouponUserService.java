package com.mmj.user.manager.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.dto.*;
import com.mmj.user.manager.model.CouponUser;
import com.mmj.user.manager.vo.*;

import java.util.List;

/**
 * <p>
 * 用户关联优惠券表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-07-04
 */
public interface CouponUserService extends IService<CouponUser> {
    /**
     * 用户添加优惠券
     *
     * @param userCouponVo
     * @return -1出错 0已领取 1领取成功 2已发完
     */
    UserReceiveCouponDto receive(UserCouponVo userCouponVo);

    /**
     * 用户批量添加优惠券
     *
     * @param userCouponBatchVo
     */
    void batchReceive(UserCouponBatchVo userCouponBatchVo);

    /**
     * 使用优惠券
     *
     * @param useUserCouponVo
     * @return
     */
    boolean use(UseUserCouponVo useUserCouponVo);

    /**
     * 判断用户是否已经领取该优惠券
     *
     * @param userCouponVo
     * @return
     */
    boolean hasReceive(UserCouponVo userCouponVo);

    /**
     * 批量判断用户是否已经领取优惠券
     *
     * @param batchUserCouponVo
     * @return
     */
    List<UserCouponReceiveDto> batchHasReceive(BatchUserCouponVo batchUserCouponVo);

    /**
     * 获取订单号关联的优惠券
     *
     * @param orderCouponVo
     * @return
     */
    List<UserCouponDto> myOrderCouponList(OrderCouponVo orderCouponVo);

    /**
     * 我的优惠券
     *
     * @return
     */
    List<UserCouponDto> myCouponList();

    /**
     * 根据优惠券编码获取优惠券信息
     *
     * @param couponCode
     * @return
     */
    UserCouponDto myCouponInfo(Integer couponCode);

    /**
     * 根据多个优惠券编码获取优惠券信息
     *
     * @param couponCodes
     * @return
     */
    List<UserCouponDto> myCouponInfoList(List<Integer> couponCodes);

    /**
     * 通过优惠券ID查询用户优惠券信息
     *
     * @param couponId
     * @return
     */
    List<UserCouponDto> myCouponInfoByCouponId(Integer couponId);

    /**
     * 会员日优惠券
     *
     * @return
     */
    MemberCouponDto memberCouponInfoList();

    /**
     * 获取商品可用优惠券
     *
     * @param goodsCouponVo
     * @return
     */
    List<GoodsCouponDto> goodsCouponInfoList(GoodsCouponVo goodsCouponVo);

    /**
     * 获取用户中心优惠券信息
     *
     * @return
     */
    PersonalCouponInfoDto personalCouponInfo();

    /**
     * 下单前获取可用优惠券
     *
     * @param produceOrderCouponVo
     * @return
     */
    ProduceOrderCouponDto produceOrderCoupon(ProduceOrderCouponVo produceOrderCouponVo);
}
