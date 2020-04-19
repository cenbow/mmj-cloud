package com.mmj.active.async;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.callCharge.service.CallChargeGoodsService;
import com.mmj.active.callCharge.service.CallChargeRecordService;
import com.mmj.active.common.config.GzhNotifyConfig;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.feigin.WxMessageFeignClient;
import com.mmj.active.common.model.FocusInfo;
import com.mmj.active.common.service.FocusInfoService;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.CouponInfoDto;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.active.coupon.service.CouponRedeemCodeService;
import com.mmj.active.grouplottery.model.LotteryConf;
import com.mmj.active.grouplottery.service.LotteryConfService;
import com.mmj.active.grouplottery.service.LotteryRemindService;
import com.mmj.active.homeManagement.service.WebShowService;
import com.mmj.active.prizewheels.service.PrizewheelsFacadeService;
import com.mmj.active.search.service.SearchConfigurationService;
import com.mmj.active.seckill.constants.SeckillConstants;
import com.mmj.active.seckill.model.SeckillInfo;
import com.mmj.active.seckill.model.SeckillTimes;
import com.mmj.active.seckill.service.SeckillInfoService;
import com.mmj.active.seckill.service.SeckillTimesService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.RechargeVo;
import com.mmj.common.utils.DateUtils;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 本控制器为mmj-cloud-active下共用，提供不需要进行令牌认证的方法给消息服务以及定时任务调度进行调用
 *
 * @author shenfuding
 */
@Slf4j
@RestController
@RequestMapping("/async")
@Api(value = "活动模块异步处理控制器")
public class ActiveAsyncController extends BaseController {

    Logger logger = LoggerFactory.getLogger(ActiveAsyncController.class);

    @Autowired
    private PrizewheelsFacadeService prizewheelsFacadeService;

    @Autowired
    private SeckillInfoService seckillInfoService;

    @Autowired
    private SearchConfigurationService searchConfigurationService;

    @Autowired
    private LotteryConfService confService;

    @Autowired
    private CouponInfoService couponInfoService;

    @Autowired
    private CallChargeGoodsService callChargeGoodsService;

    @Autowired
    private CouponRedeemCodeService couponRedeemCodeService;

    @Autowired
    private FocusInfoService focusInfoService;

    @Autowired
    private SeckillTimesService seckillTimesService;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private GzhNotifyConfig gzhNotifyConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LotteryRemindService remindService;

    @Autowired
    private CallChargeRecordService callChargeRecordService;

    @Autowired
    private WebShowService webShowService;

    @Autowired
    private WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;


    @Value("${weChatTmpId.minAppid}")
    private String minAppid;//小程序appid


    @RequestMapping(value = "/getRemind/{userId}/{from}", method = RequestMethod.POST)
    public int getRemind(@PathVariable("userId") Long userId,
                         @PathVariable("from") Integer from) {
        try {
            return remindService.getRemind(userId, from);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    @RequestMapping(value = "/prizewheels/autoIncrement", method = RequestMethod.POST)
    @ApiOperation(value = "定时任务调用，增加买买币")
    public Object autoIncrementCoins() {
        prizewheelsFacadeService.autoIncrementCoins();
        return this.initSuccessResult();
    }

    /**
     * job
     * 热销前十商品更新：每周3中午12点
     */
    @RequestMapping(value = "/resetHotSellGoods", method = RequestMethod.POST)
    public ReturnData resetHotSellGoods() {
        try {
            searchConfigurationService.resetHotSellGoods();
        } catch (Exception e) {
            logger.error(e.getMessage(), new Throwable(e));
            return initExcetionObjectResult("热销前十商品更新失败：" + DateUtils.SDF1.format(new Date()) + e.getMessage());
        }
        return initSuccessResult();
    }

    /**
     * seckill-job
     * 清理过期活动，每小时执行一次
     */
    @RequestMapping(value = "/queryTimeOutIn", method = RequestMethod.POST)
    public ReturnData queryTimeOutIn() {
        //站内活动
        try {
            //seckillInfoService.qto(SeckillConstants.SECKILL_TYPE_1);
        } catch (Exception e) {
            logger.error(e.getMessage(), new Throwable(e));
            return initExcetionObjectResult("清理过期活动-站内活动失败：" + DateUtils.SDF1.format(new Date()) + e.getMessage());
        }
        return initSuccessResult();
    }

    /**
     * seckill-job
     * 清理站外活动，每天23：59：59 执行一次
     */
    @RequestMapping(value = "/queryTimeOutOther", method = RequestMethod.POST)
    public ReturnData queryTimeOutOther() {
        //站外活动
        try {
            seckillInfoService.qto(SeckillConstants.SECKILL_TYPE_2);
        } catch (Exception e) {
            logger.error(e.getMessage(), new Throwable(e));
            return initExcetionObjectResult("清理过期活动-站外活动失败：" + DateUtils.SDF1.format(new Date()) + e.getMessage());
        }
        return initSuccessResult();
    }

    /**
     * seckill-job
     * 修改站内当前期次，每天23：59：59 执行一次
     */
    @RequestMapping(value = "/changePriodIn", method = RequestMethod.POST)
    public ReturnData changePriodIn() {
        List<GoodStock> cp = null;
        try {
            cp = seckillInfoService.cp(null, SeckillConstants.SECKILL_TYPE_1);
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

    /**
     * seckill-job
     * 减少虚拟库存，每10秒 执行一次
     */
    @RequestMapping(value = "/decActiveVirtualIn", method = RequestMethod.POST)
    public ReturnData decActiveVirtualIn() {
        try {
            seckillInfoService.dav(SeckillConstants.SECKILL_TYPE_1);
        } catch (Exception e) {
            logger.error(e.getMessage(), new Throwable(e));
            return initExcetionObjectResult("减少虚拟库存：" + DateUtils.SDF1.format(new Date()) + e.getMessage());
        }
        return initSuccessResult();
    }

    /**
     * seckill-job
     * 减少虚拟库存，每10秒 执行一次
     */
    @RequestMapping(value = "/decActiveVirtualOther", method = RequestMethod.POST)
    public ReturnData decActiveVirtualOther() {
        try {
            seckillInfoService.dav(SeckillConstants.SECKILL_TYPE_2);
        } catch (Exception e) {
            logger.error(e.getMessage(), new Throwable(e));
            return initExcetionObjectResult("减少虚拟库存：" + DateUtils.SDF1.format(new Date()) + e.getMessage());
        }
        return initSuccessResult();
    }

    public static void main(String[] args) {
        DateTime date = DateUtil.date();
        String time = (date.hour(true) + 1) + ":00:00";
        String dataStr = DateUtils.SDF10.format(date) + " " + time;
        System.out.println(time);
        System.out.println(dataStr);
    }

    /**
     * seckill-job
     * 秒杀活动开启提醒，每小时55分 执行一次
     */
    @RequestMapping(value = "/seckillRemind", method = RequestMethod.POST)
    public ReturnData seckillRemind() {
        DateTime date = DateUtil.date();
        String time = (date.hour(true) + 1) + ":00:00";
        String dataStr = DateUtils.SDF10.format(date) + " " + time;
        try {
            //站内
            Integer nowPriod = seckillInfoService.getNowPriod();
            EntityWrapper<SeckillTimes> timesWrapper = new EntityWrapper<>();
            timesWrapper.eq("SECKILL_PRIOD", nowPriod);
            timesWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.YES);
            timesWrapper.eq("START_TIME", DateUtils.SDF1.parse("2019-10-01 " + time));
            List<SeckillTimes> seckillTimeses = seckillTimesService.selectList(timesWrapper);
            if (seckillTimeses != null && !seckillTimeses.isEmpty()) {
                Integer timesId = seckillTimeses.get(0).getTimesId();
                Map<Object, Object> entries = redisTemplate.opsForHash().entries(SeckillConstants.ACTIVE_SECKILL_USER_ORDER_IN + timesId);
                if (entries != null && !entries.isEmpty()) {
                    Set<Object> userIds = entries.keySet();
                    Iterator<Object> i = userIds.iterator();
                    while (i.hasNext()) {
                        Object userId = i.next();
                        sendSeckillMsg(String.valueOf(userId), dataStr, String.valueOf(entries.get(userId)), seckillTimeses.get(0).getStartTime());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("秒杀活动开启提醒-in：" + DateUtils.SDF1.format(new Date()), new Throwable(e));
        }

        try {
            EntityWrapper<SeckillInfo> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("SECKILL_TYPE", 2);
            entityWrapper.eq("EVERY_START_TIME", DateUtils.SDF1.parse(dataStr));
            List<SeckillInfo> seckillInfos = seckillInfoService.selectList(entityWrapper);
            if (seckillInfos != null && seckillInfos.isEmpty()) {
                for (SeckillInfo seckillInfo : seckillInfos) {
                    Map<Object, Object> entries = redisTemplate.opsForHash().entries(SeckillConstants.ACTIVE_SECKILL_USER_ORDER_OUT + seckillInfo.getSeckillId());
                    if (entries != null && !entries.isEmpty()) {
                        Set<Object> userIds = entries.keySet();
                        Iterator<Object> i = userIds.iterator();
                        while (i.hasNext()) {
                            Object userId = i.next();
                            sendSeckillMsg(String.valueOf(userId), dataStr, String.valueOf(entries.get(userId)), seckillInfo.getEveryStartTime());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("秒杀活动开启提醒-out：" + DateUtils.SDF1.format(new Date()), new Throwable(e));
        }
        return initSuccessResult();
    }

    public void sendSeckillMsg(String userId, String dataStr, String goodName, Date startTime) {
        EntityWrapper<FocusInfo> focusInfoEntityWrapper = new EntityWrapper<>();
        focusInfoEntityWrapper.eq("MODULE", 1);
        focusInfoEntityWrapper.eq("USER_ID", userId);
        FocusInfo focusInfo = focusInfoService.selectOne(focusInfoEntityWrapper);
        try {
            if (focusInfo != null) {
                gzhNotifyConfig.sendAfter1(focusInfo.getOpenId(), dataStr, goodName);
            } else {
                BaseUser userById = userFeignClient.getUserById(Long.valueOf(userId));
                JSONObject tempParams = new JSONObject();
                tempParams.put("appid", minAppid);
                tempParams.put("touser", userById.getOpenId());
                tempParams.put("page", "pkgTimeSeckill/main&ed=1");
                tempParams.put("template_id", "ZTMbO2wlslWgDw_VicGBk35o5pkfwocPM48ooCk4_jw"); //其他小程序也能接收到這個模板id

                JSONObject data = new JSONObject();
                Map<String, Object> keyword1 = new HashMap<String, Object>();// 开始时间
                keyword1.put("value", DateUtils.SDF6.format(startTime));
                keyword1.put("color", "#173177");
                data.put("keyword1", keyword1);

                Map<String, Object> keyword2 = new HashMap<String, Object>();// 温馨提示
                keyword2.put("value", "您订阅的限时秒杀活动将在5分钟之后开始，请您及时查看，以免错过优惠哦!");
                keyword2.put("color", "#173177");
                data.put("keyword2", keyword2);

                data.put("keyword1", keyword1);
                data.put("keyword2", keyword2);
                tempParams.put("data", data);
                logger.info("-->秒杀发送小程序模板消息：", tempParams.toJSONString());
                wxMessageFeignClient.sendTemplateM(tempParams.toJSONString());  //发送模板消息
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 定时任务调用
     *
     * @return
     */
    @ApiOperation(value = "自动开奖服务")
    @RequestMapping(value = "/autoDrawLottery", method = RequestMethod.POST)
    public ReturnData<Boolean> autoDrawLottery() {
        try {
            confService.autoDrawLottery();
            return initSuccessResult();
        } catch (Exception e) {
            log.error("自动开奖报错 " + e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }

    @ApiOperation("已发放优惠券计数")
    @RequestMapping(value = "/issue/{couponId}", method = RequestMethod.POST)
    public ReturnData issued(@PathVariable("couponId") Integer couponId) {
        try {
            couponInfoService.issued(couponId);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("已发放优惠券计数:{}", e.toString());
            return initExcetionObjectResult(e.getMessage());
        }

    }

    @ApiOperation("更新会员日优惠券发放数量")
    @RequestMapping(value = "/coupon/member/sendNum", method = RequestMethod.POST)
    public ReturnData<Boolean> updateMemberDaySendTotalCount() {
        return initSuccessObjectResult(couponInfoService.updateMemberDaySendTotalCount());
    }

    /**
     * 批量获取优惠券列表
     *
     * @param couponIds
     * @return
     */
    @PostMapping("/batch")
    @ApiOperation("批量获取优惠券列表")
    public ReturnData<List<CouponInfoDto>> batchGetCouponInfoList(@RequestBody List<Integer> couponIds) {
        List<CouponInfo> couponInfoList = couponInfoService.batchCouponInfos(couponIds);
        return initSuccessObjectResult(couponInfoService.toCouponInfoDto(couponInfoList));
    }

    @PostMapping("/recharge/restartTask")
    @ApiOperation("每天10点重置发放数量")
    public ReturnData restartTask() {
        callChargeGoodsService.restartTask();
        return initSuccessResult();
    }

    @PostMapping("/recharge/statSendNumber")
    @ApiOperation("每一分钟统计发放数量，并持久化到数据")
    public ReturnData statSendNumber() {
        callChargeGoodsService.statSendNumber();
        return initSuccessResult();
    }

    @ApiOperation("给通过转盘活动关注公众号的用户发送签到提醒的微信模版消息")
    @RequestMapping(value = "/prizewheels/notice", method = RequestMethod.POST)
    public ReturnData<Object> sendSignNoticeForPrizewheelsUser() {
        prizewheelsFacadeService.sendSignNotice();
        return initSuccessResult();
    }

    /**
     * 下载兑换码
     *
     * @param request
     * @param response
     * @param batchCode
     * @return
     */
    @RequestMapping(value = "/downloadRedeemCode/{batchCode}", method = RequestMethod.GET)
    @ApiOperation(value = "下载兑换码")
    public ReturnData<Object> downloadRedeemCode(HttpServletRequest request,
                                                 HttpServletResponse response, @PathVariable("batchCode") String batchCode) {
        return initSuccessObjectResult(couponRedeemCodeService.downloadRedeemCode(request, response, batchCode));
    }

    /**
     * 给订单服务使用的
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据活动id查询活动信息")
    @RequestMapping(value = "/getLottery/{id}", method = RequestMethod.POST)
    public LotteryConf getById(@PathVariable("id") Integer id) {
        return confService.selectById(id);
    }

    @PostMapping("/recharge")
    @ApiOperation("话费充值")
    public ReturnData recharge(@RequestBody RechargeVo rechargeVo) {
        callChargeRecordService.recharge(rechargeVo.getOrderNo());
        return initSuccessResult();
    }


    /**
     * 新用户变成老用户, 或者是老用户变成会员
     *
     * @return
     */
    @ApiOperation(value = "修改版本号 - 各模块调用")
    @PostMapping("/updateIndexCode/{userIdentity}")
    public ReturnData<Object> updateIndexCode(@PathVariable("userIdentity") String userIdentity) {
        webShowService.updateIndexCode(userIdentity);
        return initSuccessObjectResult("修改版本号成功");
    }
}
