package com.mmj.active.coupon.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.BossCouponDto;
import com.mmj.active.coupon.model.dto.CouponInfoDto;
import com.mmj.active.coupon.model.dto.CouponNumDto;
import com.mmj.active.coupon.model.vo.BossCouponAddVo;
import com.mmj.active.coupon.model.vo.BossCouponQueryVo;
import com.mmj.active.coupon.model.vo.DetailShowVo;

import java.util.List;

/**
 * <p>
 * 优惠券信息表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
public interface CouponInfoService extends IService<CouponInfo> {

    /**
     * 获取可在商详展示的优惠券(使用范围 - 1：所有商品可用)
     *
     * @return
     */
    List<CouponInfo> getGoodsDetailShowCouponInfo();

    /**
     * 优惠券查询
     *
     * @param queryVo
     * @return
     */
    Page<BossCouponDto> query(BossCouponQueryVo queryVo);

    /**
     * 新增优惠券
     *
     * @param couponAddVo
     */
    void add(BossCouponAddVo couponAddVo);

    /**
     * 优惠券是否在商详展示
     *
     * @param detailShowVo
     */
    void detailShow(DetailShowVo detailShowVo);

    /**
     * 获取优惠券列表
     *
     * @param couponIds
     * @return
     */
    List<CouponInfo> batchCouponInfos(List<Integer> couponIds);

    /**
     * 批量获取优惠券当天发放数量
     *
     * @param couponIds
     * @return
     */
    List<CouponNumDto> batchTodayNums(List<Integer> couponIds);

    /**
     * 获取优惠券当天发放数量
     *
     * @param couponId
     * @return
     */
    CouponNumDto toDayNum(Integer couponId);

    /**
     * 已发放优惠券计数
     *
     * @param couponId
     */
    void issued(Integer couponId);

    /**
     * 将优惠券模板转换为DTO（包含每日发放量）
     *
     * @param couponInfo
     * @return
     */
    CouponInfoDto toCouponInfoDto(CouponInfo couponInfo);

    /**
     * 将优惠券模板批量转换为DTO（包含每日发放量）
     *
     * @param couponInfoList
     * @return
     */
    List<CouponInfoDto> toCouponInfoDto(List<CouponInfo> couponInfoList);

    /**
     * 更新会员日优惠券发放数量
     */
    boolean updateMemberDaySendTotalCount();
}
