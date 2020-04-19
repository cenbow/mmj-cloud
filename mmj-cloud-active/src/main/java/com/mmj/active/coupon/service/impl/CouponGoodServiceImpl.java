package com.mmj.active.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.model.GoodInfoBaseQueryEx;
import com.mmj.active.coupon.mapper.CouponGoodMapper;
import com.mmj.active.coupon.model.CouponGood;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.CouponGoodDto;
import com.mmj.active.coupon.service.CouponClassService;
import com.mmj.active.coupon.service.CouponGoodService;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.PriceConversion;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券关联商品表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@Service
public class CouponGoodServiceImpl extends ServiceImpl<CouponGoodMapper, CouponGood> implements CouponGoodService {
    @Autowired
    private CouponClassService couponClassService;
    @Autowired
    private CouponInfoService couponInfoService;
    @Autowired
    private GoodFeignClient goodFeignClient;

    @Override
    public List<CouponGood> getCouponGood(Integer couponId) {
        CouponGood queryCouponGood = new CouponGood();
        queryCouponGood.setCouponId(couponId);
        EntityWrapper entityWrapper = new EntityWrapper(queryCouponGood);
        return selectList(entityWrapper);
    }

    @Override
    public List<CouponGoodDto> getCouponGoods(Integer couponId) {
        CouponGood queryCouponGood = new CouponGood();
        queryCouponGood.setCouponId(couponId);
        EntityWrapper entityWrapper = new EntityWrapper(queryCouponGood);
        List<CouponGood> couponGoodList = selectList(entityWrapper);
        List<CouponGoodDto> couponGoodDtoList = Lists.newArrayListWithCapacity(couponGoodList.size());
        if (Objects.nonNull(couponGoodList) && couponGoodList.size() > 0) {
            GoodInfoBaseQueryEx goodInfoBaseQueryEx = new GoodInfoBaseQueryEx();
            List<Integer> goodIdList = Lists.newArrayList();
            couponGoodList.forEach(couponGood -> {
                goodIdList.add(couponGood.getGoodId());
                CouponGoodDto couponGoodDto = new CouponGoodDto();
                BeanUtils.copyProperties(couponGood, couponGoodDto);
                couponGoodDtoList.add(couponGoodDto);
            });
            if (goodIdList.size() > 0) {
                goodInfoBaseQueryEx.setGoodIds(goodIdList);
                ReturnData<Object> pageReturnData = goodFeignClient.queryBaseList(goodInfoBaseQueryEx);
                if (pageReturnData != null && pageReturnData.getCode() == SecurityConstants.SUCCESS_CODE && pageReturnData.getData() != null) {
                    Object data = pageReturnData.getData();
                    if (data != null) {
                        Page page = JSON.parseObject(JSON.toJSONString(data), Page.class);
                        List listPage = page.getRecords();
                        if (listPage != null && !listPage.isEmpty()) {
                            List<GoodInfoBaseQueryEx> goods = JSON.parseArray(JSON.toJSONString(listPage), GoodInfoBaseQueryEx.class);
                            if (Objects.nonNull(goods) && goods.size() > 0) {
                                couponGoodDtoList.forEach(couponGoodDto -> {
                                    GoodInfoBaseQueryEx goodInfoBaseQueryEx1 = goods.stream().filter(g -> g.getGoodId().equals(couponGoodDto.getGoodId())).findFirst().orElse(null);
                                    if (Objects.nonNull(goodInfoBaseQueryEx1)) {
                                        couponGoodDto.setSellingPoint(goodInfoBaseQueryEx1.getSellingPoint());
                                        couponGoodDto.setGoodImage(goodInfoBaseQueryEx1.getImage());
                                        if (Objects.nonNull(goodInfoBaseQueryEx1.getGoodSaleExes())) {
                                            couponGoodDto.setShopAmount(PriceConversion.intToString(goodInfoBaseQueryEx1.getGoodSaleExes().get(0).getShopPrice()));
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
        return couponGoodDtoList;
    }

    @Override
    public List<CouponInfo> getCouponInfoList(String goodClass, Integer goodId) {
        //可用商品分类
        List<Integer> couponIds = couponClassService.getCouponIds(goodClass);
        CouponGood queryCouponGood = new CouponGood();
        queryCouponGood.setGoodId(goodId);
        EntityWrapper<CouponGood> couponGoodEntityWrapper = new EntityWrapper<>(queryCouponGood);
        List<CouponGood> couponGoods = selectList(couponGoodEntityWrapper);
        List<Integer> goodCouponIds1 = Lists.newArrayListWithCapacity(couponGoods.size());
        List<Integer> goodCouponIds2 = Lists.newArrayListWithCapacity(couponGoods.size());
        couponGoods.forEach(couponGood -> {
            if ("1".equals(couponGood.getScopeType())) { //可用商品
                goodCouponIds1.add(couponGood.getCouponId());
            } else { //不可用商品
                goodCouponIds2.add(couponGood.getCouponId());
            }
        });
        couponIds.addAll(goodCouponIds1);
        couponIds.removeAll(goodCouponIds2);
        //获取所有可以在商详展示的优惠券
        List<CouponInfo> couponInfoListTemp = couponInfoService.getGoodsDetailShowCouponInfo();
        //排除不可用商品优惠券
        final List<CouponInfo> activeCouponInfoAllList = couponInfoListTemp.stream().filter(couponInfo -> {
            long count = goodCouponIds2.stream().filter(couponId -> couponId.equals(couponInfo.getCouponId())).count();
            return count == 0;
        }).collect(Collectors.toList());
        if (couponIds.size() > 0) {
            //排除已查询出来的优惠券
            List<Integer> queryCouponIds = couponIds.stream().filter(couponId -> {
                long count = activeCouponInfoAllList.stream().filter(couponInfo -> couponInfo.getCouponId().equals(couponId)).count();
                return count == 0;
            }).collect(Collectors.toList());

            couponInfoListTemp = couponInfoService.batchCouponInfos(queryCouponIds);
            final List<CouponInfo> couponInfoList = Lists.newArrayListWithCapacity(couponInfoListTemp.size());
            couponInfoListTemp.forEach(couponInfo -> {
                if (Objects.nonNull(couponInfo.getDetailShow()) && couponInfo.getDetailShow() == 1) {
                    couponInfoList.add(couponInfo);
                }
            });
            activeCouponInfoAllList.addAll(couponInfoList);
        }
        return activeCouponInfoAllList;
    }
}
