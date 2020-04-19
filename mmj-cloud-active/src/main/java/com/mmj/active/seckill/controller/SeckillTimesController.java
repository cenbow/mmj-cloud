package com.mmj.active.seckill.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.seckill.constants.SeckillConstants;
import com.mmj.active.seckill.model.SeckillInfo;
import com.mmj.active.seckill.model.SeckillTimes;
import com.mmj.active.seckill.model.SeckillTimesEx;
import com.mmj.active.seckill.model.SeckillTimesStore;
import com.mmj.active.seckill.service.SeckillInfoService;
import com.mmj.active.seckill.service.SeckillTimesService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 秒杀期次表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-13
 */
@RestController
@RequestMapping("/seckill/seckillTimes")
public class SeckillTimesController extends BaseController {

    @Autowired
    private SeckillTimesService seckillTimesService;
    @Autowired
    private SeckillInfoService seckillInfoService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @ApiOperation(value = "秒杀期次查询")
    @RequestMapping(value = "/query/{seckillId}", method = RequestMethod.POST)
    public ReturnData<List<SeckillTimes>> query(@PathVariable Integer seckillId) {
        EntityWrapper<SeckillTimes> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SECKILL_ID", seckillId);
        entityWrapper.orderBy("SECKILL_PRIOD");
        List<SeckillTimes> seckillTimesList = seckillTimesService.selectList(entityWrapper);
        return initSuccessObjectResult(seckillTimesList);
    }

    @ApiOperation(value = "秒杀期次商品查询（站内）- 小程序")
    @RequestMapping(value = "/queryAndGood/{times}", method = RequestMethod.POST)
    public ReturnData<SeckillTimesEx> queryAndGood(@PathVariable(value = "times") String times) {
        Integer seckillPriod = 0;
        EntityWrapper<SeckillInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SECKILL_TYPE", SeckillConstants.SECKILL_TYPE_1);
        SeckillInfo seckillInfo = seckillInfoService.selectOne(entityWrapper);
        if(seckillInfo != null) {
            if (times != null && !times.isEmpty()) {
                if (SeckillConstants.SeckillTimesPriod.TIMES_NOW.equals(times)) {
                    seckillPriod = seckillInfoService.getNowPriod();
                    if (seckillPriod == null || seckillPriod == 0) {
                        return initExcetionObjectResult("没有进行中的活动");
                    }
                } else if (SeckillConstants.SeckillTimesPriod.TIMES_NEXT.equals(times)) {
                    seckillPriod = seckillInfoService.getNowPriod();
                    if (seckillPriod == null || seckillPriod == 0) {
                        return initExcetionObjectResult("没有即将开始的活动");
                    }
                } else if (SeckillConstants.SeckillTimesPriod.TIMES_TOMORROW.equals(times)) {
                    seckillPriod = seckillInfoService.getNextPriod();
                    if (seckillPriod == null || seckillPriod == 0) {
                        return initExcetionObjectResult("没有明日预告的活动");
                    }
                }
                List<SeckillTimesEx> seckillTimesExes;
                if (SeckillConstants.SeckillTimesPriod.TIMES_NOW.equals(times) || SeckillConstants.SeckillTimesPriod.TIMES_NEXT.equals(times)) {
                    seckillTimesExes = seckillTimesService.queryAndGood(1, seckillInfo.getSeckillId(), seckillPriod, times, seckillInfo.getSeckillType());
                } else {
                    seckillPriod = seckillInfoService.getNextPriod();
                    seckillTimesExes = seckillTimesService.queryAndGood(0, seckillInfo.getSeckillId(), seckillPriod, times, seckillInfo.getSeckillType());
                }
                if (seckillTimesExes == null || seckillTimesExes.isEmpty()) {
                    if (SeckillConstants.SeckillTimesPriod.TIMES_NEXT.equals(times)) {
                        seckillPriod = seckillInfoService.getNextPriod();
                        List<SeckillTimesEx> list = seckillTimesService.queryAndGood(0, seckillInfo.getSeckillId(), seckillPriod, SeckillConstants.SeckillTimesPriod.TIMES_TOMORROW, seckillInfo.getSeckillType());
                        return initSuccessObjectResult(list == null || list.isEmpty() ? null : list.get(0));
                    } else if (SeckillConstants.SeckillTimesPriod.TIMES_TOMORROW.equals(times)) {
                        List<SeckillTimesEx> list = seckillTimesService.queryAndGood(0, seckillInfo.getSeckillId(), seckillPriod, SeckillConstants.SeckillTimesPriod.TIMES_TOMORROW_NEXT, seckillInfo.getSeckillType());
                        return initSuccessObjectResult(list == null || list.isEmpty() ? null : list.get(0));
                    }
                }
                return initSuccessObjectResult(seckillTimesExes == null || seckillTimesExes.isEmpty() ? null : seckillTimesExes.get(0));
            }
            return initExcetionObjectResult("活动还没开始！");
        } else {
            return initExcetionObjectResult("活动不存在！");
        }
    }

    @ApiOperation(value = "秒杀期次商品查询（站外）- 小程序")
    @RequestMapping(value = "/queryAndGoodOut/{seckillId}", method = RequestMethod.POST)
    public ReturnData<SeckillTimesEx> queryAndGoodOut(@PathVariable Integer seckillId) {
        List<SeckillTimesEx> list = seckillTimesService.queryAndGood(null, seckillId, null, null, null);
        return initSuccessObjectResult(list == null || list.isEmpty() ? null : list.get(0));
    }


    @ApiOperation(value = "查询进行中商品的库存剩余")
    @RequestMapping(value = "/queryNowStore", method = RequestMethod.POST)
    public ReturnData<List<Map<String, Object>>> queryNowStore(@RequestBody SeckillTimesStore seckillTimesStore) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (seckillTimesStore != null) {
            Integer timesId = seckillTimesStore.getTimesId();
            List<Integer> goodIds = seckillTimesStore.getGoodIds();
            for (Integer goodId : goodIds) {
                Map<String, Object> map = new HashMap<>();
                StringBuilder a = new StringBuilder(SeckillConstants.SECKILL_STORE);
                a.append(timesId);
                a.append(":");
                a.append(goodId);

                StringBuilder b = new StringBuilder(SeckillConstants.SECKILL_VIRTUAL_STORE);
                b.append(timesId);
                b.append(":");
                b.append(goodId);
                Integer sumStore = sumStore(a.toString());
                Integer sumVirtualStore = sumStore(b.toString());

                map.put("goodId", goodId);
                map.put("activeStore", sumStore);
                map.put("activeVirtual", sumVirtualStore);
                list.add(map);

                a.setLength(0);
                b.setLength(0);
            }
        }
        return initSuccessObjectResult(list);
    }

    private Integer sumStore(String key) {
        Set<String> storeKeys = redisTemplate.keys(key + "*");
        Integer sum = 0;
        if (storeKeys != null && !storeKeys.isEmpty()) {
            Iterator<String> i = storeKeys.iterator();
            while (i.hasNext()) {
                Object o = redisTemplate.opsForValue().get(i.next());
                if (o != null && !"".equals(o)) {
                    sum += Integer.valueOf(String.valueOf(o));
                }
            }
        }
        return sum;
    }

}

