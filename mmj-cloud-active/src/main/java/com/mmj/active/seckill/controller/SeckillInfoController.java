package com.mmj.active.seckill.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.seckill.constants.SeckillConstants;
import com.mmj.active.seckill.model.SeckillInfo;
import com.mmj.active.seckill.model.SeckillInfoEx;
import com.mmj.active.seckill.model.SeckillTimes;
import com.mmj.active.seckill.service.SeckillInfoService;
import com.mmj.active.seckill.service.SeckillTimesService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.SecurityUserUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 秒杀信息表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-13
 */
@RestController
@RequestMapping("/seckill/seckillInfo")
public class SeckillInfoController extends BaseController {

    Logger logger = LoggerFactory.getLogger(SeckillInfoController.class);

    @Autowired
    private SeckillInfoService seckillInfoService;
    @Autowired
    private SeckillTimesService seckillTimesService;
    @Autowired
    private GoodFeignClient goodFeignClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "新增或更新秒杀活动信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody SeckillInfoEx entityEx) {
        seckillInfoService.save(entityEx);
        return initSuccessResult();
    }

    @ApiOperation(value = "秒杀活动列表查询（站外）")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<Page<SeckillInfo>> queryList(@RequestBody SeckillInfoEx seckillInfoEx) {
        EntityWrapper<SeckillInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SECKILL_TYPE", SeckillConstants.SECKILL_TYPE_2);
        entityWrapper.ge(seckillInfoEx.getEveryStartTime() != null, "EVERY_START_TIME", seckillInfoEx.getEveryStartTime());
        entityWrapper.le(seckillInfoEx.getEveryEndTime() != null, "EVERY_END_TIME", seckillInfoEx.getEveryEndTime());
        entityWrapper.ge(seckillInfoEx.getCreaterTimeStart() != null, "CREATER_TIME", seckillInfoEx.getCreaterTimeStart());
        entityWrapper.le(seckillInfoEx.getCreaterTimeEnd() != null, "CREATER_TIME", seckillInfoEx.getCreaterTimeEnd());
        entityWrapper.like(seckillInfoEx.getSeckillName() != null, "SECKILL_NAME", seckillInfoEx.getSeckillName());
        entityWrapper.eq(seckillInfoEx.getCreaterId() != null, "CREATER_ID", seckillInfoEx.getCreaterId());
        entityWrapper.orderBy("CREATER_TIME");
        Page<SeckillInfo> page = new Page<>(seckillInfoEx.getCurrentPage(), seckillInfoEx.getPageSize());
        return initSuccessObjectResult(seckillInfoService.selectPage(page, entityWrapper));
    }


    @ApiOperation(value = "秒杀活动详情查询（站内/外）")
    @RequestMapping(value = "/queryDetail/{seckillType}/{seckillId}", method = RequestMethod.POST)
    public ReturnData<SeckillInfoEx> queryDetail(@PathVariable(name = "seckillType") Integer seckillType, @PathVariable(name = "seckillId", required = false) Integer seckillId) {
        SeckillInfoEx query;
        if (SeckillConstants.SECKILL_TYPE_1 == seckillType) {
            query = seckillInfoService.queryDetail(seckillType, null);
        } else {
            query = seckillInfoService.queryDetail(seckillType, seckillId);
        }
        if (query != null && seckillType == SeckillConstants.SECKILL_TYPE_1) {
            query.setSeckillPriodNow((Integer) redisTemplate.opsForValue().get(SeckillConstants.SeckillTimesPriod.SECKILL_PRIOD_NOW_1));
        }
        return initSuccessObjectResult(query);
    }

    @ApiOperation(value = "秒杀活动信息查询（站内/外）")
    @RequestMapping(value = "/query/{seckillType}/{seckillId}", method = RequestMethod.POST)
    public ReturnData<List<SeckillInfo>> query(@PathVariable(name = "seckillType") Integer seckillType, @PathVariable(name = "seckillId", required = false) Integer seckillId) {
        EntityWrapper<SeckillInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SECKILL_TYPE", seckillType);
        if (SeckillConstants.SECKILL_TYPE_2 == seckillType) {
            entityWrapper.eq(seckillId != null, "SECKILL_ID", seckillId);
        }
        List<SeckillInfo> seckillInfos = seckillInfoService.selectList(entityWrapper);
        return initSuccessObjectResult(seckillInfos);
    }

    @ApiOperation(value = "秒杀每人每天限购校验")
    @RequestMapping(value = "/checkLimit/{businessId}/{saleId}/{store}", method = RequestMethod.POST)
    public ReturnData checkLimit(@PathVariable(value = "businessId") Integer businessId, @PathVariable(value = "saleId") Integer saleId, @PathVariable(value = "store") Integer store) {
        SeckillTimes seckillTimes = seckillTimesService.selectById(businessId);
        if (seckillTimes != null) {
            SeckillInfo seckillInfo = seckillInfoService.selectById(seckillTimes.getSeckillId());
            if (seckillInfo != null) {
                Integer everyoneLimit = seckillInfo.getEveryoneLimit();//每人每天限购数量
                JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
                String key = SeckillConstants.SECKILL_ORDER_LIMIT + jwtUser.getUserId() + ":" + businessId + ":" + saleId;
                Long inc = redisTemplate.opsForValue().increment(key, store);
                if (inc > everyoneLimit) {
                    redisTemplate.opsForValue().increment(key, -store);
                    return initExcetionObjectResult("每人每天限购" + everyoneLimit + "件！");
                }
                return initSuccessResult();
            } else {
                return initExcetionObjectResult("活动不存在！");
            }
        } else {
            return initExcetionObjectResult("活动不存在！");
        }
    }

    @ApiOperation(value = "秒杀活动信息删除（站外）")
    @RequestMapping(value = "/delete/{seckillId}", method = RequestMethod.POST)
    public ReturnData delete(@PathVariable(name = "seckillId") Integer seckillId) throws Exception {
        seckillInfoService.delete(seckillId);
        return initSuccessResult();
    }

    @ApiOperation(value = "查询进行中档期（站内）")
    @RequestMapping(value = "/queryDetailActive", method = RequestMethod.POST)
    public ReturnData<SeckillInfoEx> queryDetailActive() {
        SeckillInfoEx query = seckillInfoService.queryDetailActive(SeckillConstants.SECKILL_TYPE_1, null);
        return initSuccessObjectResult(query);
    }

    /**
     * seckill-job
     * 修改站内当前期次，每天23：59：59 执行一次
     */
    @RequestMapping(value = "/changePriodIn", method = RequestMethod.POST)
    public ReturnData changePriodIn(@RequestBody(required = false) Integer nextPriod) {
        List<GoodStock> cp = null;
        try {
            if (nextPriod != null && "".equals(nextPriod)) {
                nextPriod = null;
            }
            cp = seckillInfoService.cp(nextPriod, SeckillConstants.SECKILL_TYPE_1);
        } catch (Exception e) {
            logger.error(e.getMessage(), new Throwable(e));
            if (cp != null && cp.isEmpty()) {
                cp.stream().forEach(g -> g.setStatus(CommonConstant.GoodStockStatus.RELIEVE));
                goodFeignClient.relieve(cp);
            }
            return initExcetionObjectResult("修改当前期次失败：" + DateUtils.SDF1.format(new Date()) + e.getMessage());
        }
        return initSuccessResult();
    }
}

