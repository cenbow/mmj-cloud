package com.mmj.user.member.controller;


import com.mmj.common.constants.UserConstant;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.model.Vo.PayIsBuyGiveVo;
import com.mmj.user.member.service.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 会员买送相关 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-11
 */
@Slf4j
@RestController
@RequestMapping("/member/send")
public class UserMemberSendController extends BaseController {

    @Autowired
    private UserMemberSendService userMemberSendService;

    @Autowired
    private UserMemberPreferentialService userMemberPreferentialService;

    @Autowired
    private UserMemberService userMemberService;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private MemberConfigService memberConfigService;

    @Autowired
    private MemberImportService memberImportService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String MAP_KEY_CONSUMEMONEY = "consumeMoney";
    private static final String MAP_KEY_MEMBERTHRESHOLD = "memberThreshold";
    private static final String MAP_KEY_BEMEMBERTYPE = "beMemberType";
    private static final String STRING_EMPTY = "";
    private static final String MAP_KEY_ISMEMBER = "isMember";
    private static final String CACHE_KEY_PREFIX = "COUSUMEMONEY:";


    @RequestMapping(value = "/isGiveBuy/{orderNo}/{userId}", method = RequestMethod.POST)
    public boolean isGiveBuy(@PathVariable("orderNo") String orderNo,
                             @PathVariable("orderNo") Long userId) {
        return userMemberSendService.getOrderIdBuyGive(orderNo, userId);
    }

    /**
     * 获取会员门槛信息和历史消费金额
     *
     * @return
     */
    @RequestMapping(value = "/getMemberThresholdAsConsume", method = RequestMethod.POST)
    @ApiOperation("获取会员门槛信息和历史消费金额")
    public ReturnData<Map<String, Object>> getMemberThresholdAsConsume() {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->获取距离多少钱成为会员，用户id：{}", userId);
        try {
            double memberImportPrice = memberImportService.getImportMemberConsumptionAmount(userId);
            //历史消费金额
            double consumeMoney = getConsumeMoney(userId);
            consumeMoney = DoubleUtil.add(memberImportPrice, consumeMoney);
            //会员成为会员门槛
            int mmjMemberCumulativeConsumption = memberConfigService.getMmjMemberCumulativeConsumption();
            Map<String, Object> map = new HashMap<>();
            map.put(MAP_KEY_CONSUMEMONEY, consumeMoney);
            map.put(MAP_KEY_MEMBERTHRESHOLD, mmjMemberCumulativeConsumption);
            UserMember userMember = userMemberService.queryUserMemberInfoByUserId(userId);
            map.put(MAP_KEY_BEMEMBERTYPE, userMember != null ? userMember.getBeMemberType() : STRING_EMPTY);
            return initSuccessObjectResult(map);
        } catch (Exception e) {
            log.error("-->获取距离多少钱成为会员，发生异常：{},{}", userId, e);
            return initErrorObjectResult("获取距离多少钱成为会员，发生异常");
        }
    }

    private double getConsumeMoney(long userId) {
        double consumeMoney = 0;
//    	String cacheKey = CACHE_KEY_PREFIX + userId;
//    	String cacheValue = redisTemplate.opsForValue().get(cacheKey);
//    	if(StringUtils.isNotBlank(cacheValue)) {
//    		log.info("-->从缓存中获取用户历史消费金额为：{}元", cacheValue);
//    		return Double.valueOf(cacheValue);
//    	}
        consumeMoney = orderFeignClient.getConsumeMoney(userId).getData();
        log.info("-->从DB中获取用户历史消费金额为：{}元", consumeMoney);
//    	redisTemplate.opsForValue().set(cacheKey, String.valueOf(consumeMoney));
        return consumeMoney;
    }


    /**
     * Boss后台获取会员门槛信息和历史消费金额
     *
     * @return
     */
    @RequestMapping(value = "/getMemberThresholdAsConsumeBoss/{userId}", method = RequestMethod.POST)
    @ApiOperation("获取会员门槛信息和历史消费金额")
    public ReturnData<Map<String, Object>> getMemberThresholdAsConsumeBoss(@PathVariable("userId") Long userId) {
        log.info("-->Boos后台获取距离多少钱成为会员，用户id：{}", userId);
        try {
            //历史消费金额
            double consumeMoney = orderFeignClient.getConsumeMoney(userId).getData();
            //会员成为会员门槛
            int mmjMemberCumulativeConsumption = memberConfigService.getMmjMemberCumulativeConsumption();
            Map<String, Object> map = new HashMap<>();
            map.put(MAP_KEY_CONSUMEMONEY, consumeMoney);
            map.put(MAP_KEY_MEMBERTHRESHOLD, mmjMemberCumulativeConsumption);
            UserMember userMember = userMemberService.queryUserMemberInfoByUserId(userId);
            map.put(MAP_KEY_BEMEMBERTYPE, userMember != null ? userMember.getBeMemberType() : STRING_EMPTY);
            return initSuccessObjectResult(map);
        } catch (Exception e) {
            log.error("-->获取距离多少钱成为会员，发生异常：{},{}", userId, e);
            return initErrorObjectResult("获取距离多少钱成为会员，发生异常");
        }
    }

    /**
     * 延迟获取会员信息
     *
     * @return
     */
    @RequestMapping(value = "/getDelayUserMember", method = RequestMethod.POST)
    @ApiOperation("延迟获取会员信息")
    public ReturnData<Map<String, Object>> getDelayUserMember() {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(userId);
        Boolean result = false;
        int count = 1;
        while (!result) {
            if (count > 10) break;
            try {
                Thread.sleep(1000);
                UserMember userMemberTwo = userMemberService.queryUserMemberInfoByUserId(userId);
                if (userMemberTwo != null && !userMemberTwo.equals(userMember)) {
                    result = true;
                    break;
                }
                userMember = userMemberTwo;
                count++;
            } catch (Exception e) {
                count++;
            }
        }
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(MAP_KEY_ISMEMBER, (userMember != null && userMember.getActive()));
        return initSuccessObjectResult(map);
    }

    /**
     * 实时获取会员信息
     *
     * @return
     */
    @RequestMapping(value = "/getUserMember", method = RequestMethod.POST)
    @ApiOperation("实时获取会员信息")
    public ReturnData<Map<String, Object>> getUserMember() {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->获取会员信息，用户id：{}", userId);
        Map<String, Object> map = new HashMap<String, Object>(1);
        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(userId);
        map.put(MAP_KEY_ISMEMBER, (userMember != null && userMember.getActive()));
        return initSuccessObjectResult(map);
    }

    /**
     * 获取买就送活动资格及剩余时间
     *
     * @return
     */
    @RequestMapping(value = "/getActiveSurplusTime", method = RequestMethod.POST)
    @ApiOperation("获取买就送活动资格及剩余时间")
    public ReturnData<Map<String, Object>> getActivitySurplusTime() {
        try {
            return initSuccessObjectResult(userMemberSendService.getActivitySurplusTime());
        } catch (Exception e) {
            log.error("-->获取买就送活动资格及剩余时间，发生异常：");
            log.error(e.getMessage(), e);
            return initErrorObjectResult("获取买就送活动资格及剩余时间，发生异常");
        }
    }

    /**
     * 获取会员省钱
     *
     * @return
     */
    @RequestMapping(value = "getEconomizeMoney", method = RequestMethod.POST)
    @ApiOperation("获取成为会员省了多少钱")
    public ReturnData<Map<String, Object>> getEconomizeMoney() {
        try {
            return initSuccessObjectResult(userMemberPreferentialService.queryEconomizeMoney());
        } catch (Exception e) {
            log.error("-->获取成为会员省了多少钱，发生异常", e);
            return initErrorObjectResult("获取成为会员省了多少钱，发生异常");
        }
    }

    /**
     * 订单是否享受了买送
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/getOrderIdBuyGive", method = RequestMethod.POST)
    @ApiOperation("记录会员省钱")
    public ReturnData<Boolean> getOrderIdBuyGive(@RequestBody String orderNo) {
        try {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            return initSuccessObjectResult(userMemberSendService.getOrderIdBuyGive(orderNo, userDetails.getUserId()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }

    /**
     * 下单查询是否享受买送资格
     *
     * @return
     */
    @RequestMapping(value = "/getPayIsBuyGive", method = RequestMethod.POST)
    @ApiOperation("下单查询是否享受买送资格")
    public ReturnData<Boolean> getPayIsBuyGive(@RequestBody PayIsBuyGiveVo payIsBuyGiveVo) {
        return initSuccessObjectResult(userMemberSendService.getPayIsBuyGiveClient(payIsBuyGiveVo));
    }

    @RequestMapping(value = "/editBuyGice", method = RequestMethod.POST)
    @ApiOperation("取消会员买送资格")
    public ReturnData<Boolean> editBuyGice(@RequestBody Long userId) {
        return initSuccessObjectResult(userMemberSendService.editBuyGice(userId));
    }

    /**
     * 获取当前用户是否在进行会员升级
     *
     * @return
     */
    @RequestMapping(value = "/getMemberOngoing", method = RequestMethod.POST)
    @ApiOperation("获取当前用户是否在进行会员升级")
    public ReturnData<Boolean> editBuyGice() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        return initSuccessObjectResult(redisTemplate.hasKey(UserConstant.ISMEMBERONGOING + jwtUserDetails.getUserId()));
    }

}
