package com.mmj.user.async;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MemberConfigConstant;
import com.mmj.common.constants.UserConstant;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.ChannelUserParam;
import com.mmj.common.model.ChannelUserVO;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.StringUtils;
import com.mmj.common.utils.UserCacheUtil;
import com.mmj.user.common.feigin.NoticeFeignClient;
import com.mmj.user.manager.dto.BaseUserDto;
import com.mmj.user.manager.dto.UserReceiveCouponDto;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.model.UserActive;
import com.mmj.user.manager.model.UserLogin;
import com.mmj.user.manager.service.BaseUserService;
import com.mmj.user.manager.service.CouponUserService;
import com.mmj.user.manager.service.UserActiveService;
import com.mmj.user.manager.service.UserLoginService;
import com.mmj.user.manager.vo.UseUserCouponVo;
import com.mmj.user.manager.vo.UserCouponBatchVo;
import com.mmj.user.manager.vo.UserCouponVo;
import com.mmj.user.member.model.UserKingLog;
import com.mmj.user.member.service.MemberConfigService;
import com.mmj.user.member.service.UserKingLogService;
import com.mmj.user.member.service.UserMemberSendService;
import com.mmj.user.member.service.UserMemberService;
import com.mmj.user.recommend.model.RedPackageUser;
import com.mmj.user.recommend.model.UserShard;
import com.mmj.user.recommend.service.RedPackageUserService;
import com.mmj.user.recommend.service.UserShardService;
import com.mmj.user.userFocus.service.UserFocusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 本控制器为mmj-cloud-user下共用，提供不需要进行令牌认证的方法给消息服务以及定时任务调度进行调用
 *
 * @author shenfuding
 */
@Slf4j
@RestController
@RequestMapping("/async")
@Api(value = "用户模块异步处理控制器")
public class UserAsyncController extends BaseController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserShardService userShardService;

    @Autowired
    private RedPackageUserService redPackageUserService;

    @Autowired
    private CouponUserService couponUserService;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserMemberService userMemberService;

    @Autowired
    private MemberConfigService memberConfigService;

    @Autowired
    private NoticeFeignClient noticeFeignClient;

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private UserFocusService userFocusService;

    @Autowired
    private UserActiveService userActiveService;

    @Autowired
    private UserKingLogService userKingLogService;

    @Autowired
    private UserMemberSendService userMemberSendService;

    @RequestMapping(value = "/user/{id}", method = RequestMethod.POST)
    public BaseUser getById(@PathVariable("id") Long id) {
        return baseUserService.getById(id);
    }

    @RequestMapping(value = "/cache/get/{key}", method = RequestMethod.GET)
    @ApiOperation("根据key获取缓存数据，精准匹配")
    public Object get(@PathVariable String key) {
        return this.initSuccessObjectResult(redisTemplate.opsForValue().get(key));
    }

    @RequestMapping(value = "/cache/del/{key}", method = RequestMethod.GET)
    @ApiOperation("根据key删除缓存数据，精准匹配")
    public Object del(@PathVariable String key) {
        redisTemplate.delete(key);
        return this.initSuccessResult();
    }

    @RequestMapping(value = "/cache/delLike/{key}", method = RequestMethod.GET)
    @ApiOperation("根据key模糊删除缓存数据")
    public Object delLike(@PathVariable String key) {
        redisTemplate.delete(redisTemplate.keys(key + "*"));
        return this.initSuccessResult();
    }


    @RequestMapping(value = "/getUserOpenId", method = RequestMethod.POST)
    public String getUserOpenId(@RequestBody JSONObject object) {
        long userId = object.getLongValue("userId");
        String appId = object.getString("appId");
        BaseUserDto dto = baseUserService.queryUserInfoByUserId(userId, appId);
        if (null == dto)
            return null;
        return dto.getOpenId();
    }


    /**
     * 给feign调用的
     */
    @ApiOperation("获取免费送用户绑定的订单")
    @RequestMapping(value = "/userShard/get/{shardTo}", method = RequestMethod.POST)
    public ReturnData<UserShard> get(@PathVariable("shardTo") Long shardTo) {
        try {
            UserShard us = userShardService.getByToUserId(shardTo);
            return initSuccessObjectResult(us);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    /**
     * 收货时删除绑定关系
     * 给feign调用的
     */
    @ApiOperation("收货时删除绑定关系")
    @RequestMapping(value = "/userShard/del/{shardTo}", method = RequestMethod.POST)
    public boolean del(@PathVariable("shardTo") Long shardTo) {
        try {
            return userShardService.del(shardTo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @RequestMapping(value = "/addRedPackage", method = RequestMethod.POST)
    public boolean addRedPackage(@RequestBody RedPackageUser redPackage) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, redPackage.getUserId());
        return redPackageUserService.insert(redPackage);
    }

    @RequestMapping(value = "/coupon/use", method = RequestMethod.POST)
    @ApiOperation(value = "使用优惠券")
    public ReturnData<Object> use(@Valid @RequestBody UseUserCouponVo useUserCouponVo) {
        log.info("-->异步使用优惠券，参数：{}", JSONObject.toJSONString(useUserCouponVo));
        boolean status = couponUserService.use(useUserCouponVo);
        return status ? initSuccessResult() : initErrorObjectResult("使用优惠券失败");
    }

    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @ApiOperation(value = "异步添加优惠券")
    public ReturnData<UserReceiveCouponDto> receive(@Valid @RequestBody UserCouponVo userCouponVo) {
        log.info("-->异步添加优惠券，参数：{}", JSONObject.toJSONString(userCouponVo));
        try {
            UserReceiveCouponDto userReceiveCouponDto = couponUserService.receive(userCouponVo);
            log.info("-->异步添加优惠券，返回：{}", JSONObject.toJSONString(userReceiveCouponDto));
            return initSuccessObjectResult(userReceiveCouponDto);
        } catch (Exception e) {
            log.error("异步添加优惠券异常 error:{}", e.toString());
            return initErrorObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "领取优惠券失败");
        }
    }

    @RequestMapping(value = "/receive/batch", method = RequestMethod.POST)
    @ApiOperation(value = "异步批量添加优惠券")
    public ReturnData batchReceive(@Valid @RequestBody UserCouponBatchVo userCouponBatchVo) {
        log.info("-->异步批量添加优惠券，参数：{}", JSONObject.toJSONString(userCouponBatchVo));
        couponUserService.batchReceive(userCouponBatchVo);
        return initSuccessResult();
    }

    @ApiOperation(value = "根据openId/unionId/phone获取用户登录账号信息")
    @RequestMapping(value = "/loginInfo/{loginName}", method = RequestMethod.POST)
    public ReturnData<UserLogin> getUserLoginInfoByUserName(@PathVariable("loginName") String loginName) {
        return this.initSuccessObjectResult(userLoginService.getUserLoginInfoByUserName(loginName));
    }

    @RequestMapping(value = "/totalCount", method = RequestMethod.POST)
    @ApiOperation("查询会员总数量")
    public ReturnData<Integer> totalCount() {
        return this.initSuccessObjectResult(userMemberService.queryMemberTotalCount());
    }

    @RequestMapping(value = "/member/config/updateMemberActivityStartDate", method = RequestMethod.POST)
    @ApiOperation("更新会员活动开始时间和显示的会员人数")
    public ReturnData<Object> updateMemberActivityStartDate() {
        log.info("-->updateMemberActivityStartDate-->每天凌晨计算会员活动开始时间");
        int day = memberConfigService.getMemberActivityHowManyDaysToEnd();
        if (day > 0) {
            log.info("-->updateMemberActivityStartDate-->活动未结束，还有{}天，程序返回...", day);
            return initSuccessResult();
        }
        log.info("-->ResetMemberActivityDaysTask-->活动已结束，重新计算活动开始时间");
        Date now = new Date();
        String startDate = DateUtils.SDF10.format(now) + " 00:00:01";
        int userMemberTotalExceed = memberConfigService.getMmjUsersCountExceed() + 100;
        BaseDict baseDict1 = noticeFeignClient.queryGlobalConfigByDictCode(MemberConfigConstant.MMJ_MEMBER_ACTIVITY_START_DATE).getData();
        if (baseDict1 != null) {
            baseDict1.setDictValue(startDate);
            noticeFeignClient.saveBaseDict(baseDict1);
        }
        BaseDict baseDict2 = noticeFeignClient.queryGlobalConfigByDictCode(MemberConfigConstant.MMJ_USERS_COUNT_EXCEED).getData();
        if (baseDict2 != null) {
            baseDict2.setDictValue(String.valueOf(userMemberTotalExceed));
            noticeFeignClient.saveBaseDict(baseDict2);
        }
        log.info("-->ResetMemberActivityDaysTask-->更新完毕，新的会员活动开始时间为：{}，新的会员人数为{}万", startDate, userMemberTotalExceed);
        return initSuccessResult();
    }

    @RequestMapping(value = "/channel/getChannelUsers", method = RequestMethod.POST)
    @ApiOperation("获取渠道用户")
    public ReturnData<List<ChannelUserVO>> getChannelUsers(@RequestBody ChannelUserParam param) {
        log.info("-->获取渠道用户，参数:{}", JSONObject.toJSONString(param));
        return this.initSuccessObjectResult(baseUserService.getChannelUsers(param));
    }

    /**
     * JOB-同时前一天的关注数据
     * 每天3点同步，同时执行7个定时任务
     * module
     * 1 2 3 4 5 6 7
     * type
     * 1
     *
     * @param module
     * @param type
     * @return
     */
    @RequestMapping(value = "/syncFocusData/{module}/{type}", method = RequestMethod.POST)
    @ApiOperation(value = "同步关注数据")
    public ReturnData syncFocusData(@PathVariable("module") Integer module, @PathVariable("type") Integer type) {
        userFocusService.sync(module, type);
        return initSuccessResult();
    }

    @ApiOperation(value = "查询参与活动的用户接口")
    @RequestMapping(value = "/getActiveByCode", method = RequestMethod.POST)
    public List<UserActive> getActiveByCode(@RequestBody UserActive userActive) {
        return userActiveService.getActiveByCode(userActive);
    }

    @RequestMapping(value = "/orderKingProd", method = RequestMethod.POST)
    public int orderKingProd(@RequestBody Map<String, Object> map) {
        return userKingLogService.orderKingProd(map);
    }

    @RequestMapping(value = "/isGiveBuy/{orderNo}/{userId}", method = RequestMethod.POST)
    public boolean isGiveBuy(@PathVariable("orderNo") String orderNo,
                             @PathVariable("userId") Long userId) {
        return userMemberSendService.getOrderIdBuyGive(orderNo, userId);
    }

    @ApiOperation("锁定用户，禁止用户访问买买家")
    @RequestMapping(value = "/lock/{userId}", method = RequestMethod.POST)
    public Object lockUser(@PathVariable("userId") Long userId) {
        BaseUser baseUser = new BaseUser();
        baseUser.setUserId(userId);
        baseUser.setUserStatus(UserConstant.STATUS_LOCK);
        baseUser.setModifyId(userId);
        baseUser.setModifyTime(new Date());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        baseUserService.updateById(baseUser);
        redisTemplate.delete(UserCacheUtil.getBaseUserCacheKey(userId));
        redisTemplate.delete(UserCacheUtil.getBaseUserCacheKey(userId));
        log.info("-->锁定用户：{}", userId);
        // 删除令牌缓存
        String tokenCacheKey = redisTemplate.opsForValue().get("USERID:TOKEN:" + userId);
        redisTemplate.delete(tokenCacheKey);
        log.info("-->删除用户{}的令牌缓存", userId);
        return initSuccessResult();
    }

    @ApiOperation("解锁用户，禁止用户访问买买家")
    @RequestMapping(value = "/unlock/{userId}", method = RequestMethod.POST)
    public Object unlockUser(@PathVariable("userId") Long userId) {
        BaseUser baseUser = new BaseUser();
        baseUser.setUserId(userId);
        baseUser.setUserStatus(UserConstant.STATUS_NORMAL);
        baseUser.setModifyId(userId);
        baseUser.setModifyTime(new Date());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        baseUserService.updateById(baseUser);
        redisTemplate.delete(UserCacheUtil.getBaseUserCacheKey(userId));
        redisTemplate.delete(UserCacheUtil.getBaseUserCacheKey(userId));
        log.info("-->解锁用户：{}", userId);
        // 删除令牌缓存
        String tokenCacheKey = redisTemplate.opsForValue().get("USERID:TOKEN:" + userId);
        redisTemplate.delete(tokenCacheKey);
        log.info("-->删除用户{}的令牌缓存", userId);
        return initSuccessResult();
    }

    @RequestMapping(value = "/clickShare", method = RequestMethod.POST)
    public ReturnData<String> clickShare(@RequestBody UserKingLog uLog) {
        if (org.apache.commons.lang.StringUtils.isBlank(uLog.getShareType()) || null == uLog.getUserId()
                || null == uLog.getGoodId()) {
            log.info("mark 参数异常");
            return initErrorObjectResult("参数错误");
        }
        try {
            userKingLogService.clickInsert(uLog);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @RequestMapping(value = "/exchageProc", method = RequestMethod.POST)
    public Integer exchageProc(@RequestBody OrdersMQDto dto) {
        try {
            return userKingLogService.procMMKing(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }
}
