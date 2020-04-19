package com.mmj.active.threeSaleTenner.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.threeSaleTenner.model.ThreeSaleTenner;
import com.mmj.common.model.ReturnData;

import java.util.Map;

/**
 * <p>
 * 十元三件活动表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-12
 */
public interface ThreeSaleTennerService extends IService<ThreeSaleTenner> {

    ReturnData<Object> save(ThreeSaleTenner threeSaleTenner);

    ThreeSaleTenner query();

    Map<String,Object> selectIsBuy(Long userid,Integer infoId);

    ThreeSaleTenner selectThreeSaleTenner();

    Map<String,Object> addShareTime(Long userid, Integer status,String orderNo);

    boolean addBuyCout(Long userid);

    Page<ActiveGood> selectGoods(ThreeSaleTenner ThreeSaleTenner);

}
