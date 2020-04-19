package com.mmj.user.recommend.controller;


import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.annotation.ApiWaitForCompletion;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.recommend.model.UserShard;
import com.mmj.user.recommend.model.UserShardEx;
import com.mmj.user.recommend.service.UserRecommendService;
import com.mmj.user.recommend.service.UserShardService;

/**
 * <p>
 * 分享关联表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
@RestController
@RequestMapping("/recommend/userShard")
@Slf4j
public class UserShardController extends BaseController {

    private final UserShardService userShardService;

    private final UserRecommendService userRecommendService;

    public UserShardController(UserShardService userShardService, UserRecommendService userRecommendService) {
        this.userShardService = userShardService;
        this.userRecommendService = userRecommendService;
    }

    /**
     * @param shard
     * @return
     */
    @ApiOperation("新增用户推荐关系(包含免费绑定的关系)")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ReturnData add(@RequestBody UserShard shard) {
        try {
            userShardService.add(shard);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }


    @ApiOperation("推荐分享返现 - 保存")
    @RequestMapping(value = "/recommendSharedSave", method = RequestMethod.POST)
    public ReturnData<Object> recommendSharedSave(@RequestBody UserShard userShard) {
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/recommend/userShard/recommendSharedSave-->推荐返现,保存，用户id：{},参数:{}", userid, JSONObject.toJSONString(userShard));
        return initSuccessObjectResult(userShardService.recommendSharedSave(userShard));
    }


    @ApiOperation("返现中心 - 提现明细查询")
    @RequestMapping(value = "/queryCash/{type}", method = RequestMethod.POST)
    public ReturnData<Object> queryCash(@PathVariable("type") String type) {
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/recommend/userShard/queryCash-->推荐返现,提现明细查询，用户id：{},类型:{}", userId, type);
        Map<String, Object> map = new HashMap<>();
        List<UserShardEx> list = userShardService.queryCash(type, userId);
        map.put("list", list);

        Integer unPayAmount = userShardService.queryUnPayCashAmount(userId);
        map.put("unPayAmount", unPayAmount / 100f); //未支付小计

        Integer soonAmount = userShardService.querySoonCashAmount(userId);
        map.put("soonAmount", soonAmount / 100f); //即将到账小计

        Integer canAmount = userShardService.queryCanCashAmount(userId);
        map.put("canAmount", canAmount / 100f);  //可提现小计
        map.put("unRecommendCount", userRecommendService.selectNORecommendOrderCont(userId)); //待推荐的订单数量
        return initSuccessObjectResult(map);
    }


    @ApiOperation("返现中心 - 可提现总金额")
    @RequestMapping(value = "/querySumAmout", method = RequestMethod.POST)
    public ReturnData<Object> querySumAmout() {
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/recommend/userShard/querySumAmout-->推荐返现,可提现总金额，用户id：{}", userId);
        Integer querySumAmout = userShardService.queryCanCashAmount(userId);
        return initSuccessObjectResult(querySumAmout / 100f);
    }

    @ApiOperation("推荐返现 - 用户退款(售后调用)")
    @RequestMapping(value = "/updateRefundByOrderNo", method = RequestMethod.POST)
    public ReturnData<Boolean> updateRefundByOrderNo(@RequestBody Map<String, Object> map) {
        Long userId = Long.parseLong((String) map.get("userId"));
        String orderNo = (String) map.get("orderNo");
        log.info("-->/recommend/userShard/updateRefundByOrderNo-->推荐返现,用户退款，用户id：{},订单号:{}", userId, orderNo);
        return initSuccessObjectResult(userShardService.updateRefundByOrderNo(userId, orderNo));
    }

    @ApiOperation("推荐返现 - 查询退款金额(售后调用)")
    @RequestMapping(value = "/queryRefundByOrderNo", method = RequestMethod.POST)
    public ReturnData<Integer> queryRefundByOrderNo(@RequestBody Map<String, Object> map) {
        log.info("开始查询退款金额(售后调用)");
        Long userId = Long.valueOf(String.valueOf(map.get("userId"))).longValue();
        String orderNo = (String) map.get("orderNo");
        log.info("-->/recommend/userShard/queryRefundByOrderNo-->推荐返现,用户退款，用户id：{},订单号:{}", userId, orderNo);
        return initSuccessObjectResult(userShardService.queryRefundByOrderNo(userId, orderNo));
    }

    @ApiOperation("推荐返现,推荐成为会员 - 待支付状态取消订单(订单调用)")
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    public ReturnData<Boolean> cancelOrder(@RequestBody Map<String, Object> map) {
        Long userId = Long.valueOf(map.get("userId").toString());
        String orderNo = (String) map.get("orderNo");
        log.info("-->/recommend/userShard/cancelOrder-->推荐返现,待支付状态取消订单，用户id：{},订单号:{}", userId, orderNo);
        return initSuccessObjectResult(userShardService.cancelOrder(userId, orderNo));
    }


    @ApiOperation("推荐成为会员 - 保存")
    @RequestMapping(value = "/saveUserShardInfo", method = RequestMethod.POST)
    public ReturnData<Object> saveUserShardInfo(@RequestBody UserShard userShard) {
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/recommend/userShard/saveUserShardInfo-->推荐返现,保存，用户id：{},参数:{}", userId, JSONObject.toJSONString(userShard));
        return initSuccessObjectResult(userShardService.saveUserShardInfo(userShard));
    }

    @ApiWaitForCompletion
    @ApiOperation("用户点击提现 - 小程序")
    @RequestMapping(value = "/doCash", method = RequestMethod.POST)
    public ReturnData<Object> doCash() {
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/recommend/userShard/doCash-->推荐返现,用户点击提现，用户id：{}", userId);
        return userShardService.doCash();
    }


    @ApiOperation("查询用户被谁绑定成为会员 - 小程序")
    @RequestMapping(value = "/queryBindMember", method = RequestMethod.POST)
    public ReturnData<Object> queryBindMember() {
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/recommend/userShard/queryBindMember-->推荐返现,用户点击提现，用户id：{}", userId);
        return initSuccessObjectResult(userShardService.queryBindMember());
    }

    /**
     * 被分享人确定收货10天 - 发送零钱(定时任务)
     *
     * @return
     */
    @ApiOperation("推荐成为会员 - 给被分享人发送零钱(小程序)")
    @RequestMapping(value = "/userShardSendMoney", method = RequestMethod.POST)
    public ReturnData<Object> userShardSendMoney() {
        log.info("-->/recommend/userShard/userShardSendMoney-->推荐成为会员，定时任务执行");
        return initSuccessObjectResult(userShardService.userShardSendMoney());
    }


    @ApiOperation("推荐返现 - 生成订单和支付成功(订单调用)")
    @RequestMapping(value = "/updateRecommendShared", method = RequestMethod.POST)
    public ReturnData<Object> updateRecommendShared(@RequestBody String params) {
        JSONObject jsonObject = JSON.parseObject(params);
        Long userId = jsonObject.getLong("userId");
        String orderNo = jsonObject.getString("orderNo");
        Integer orderStatus = jsonObject.getInteger("orderStatus");
        String appId = jsonObject.getString("appId");
        log.info("-->/recommend/userShard/updateRecommendShared-->推荐返现,生成订单和支付成功，用户id：{},订单号:{},订单金额:{},订单状态:{}", userId, orderNo, null, orderStatus);
        userShardService.updateRecommendShared(userId, orderNo, appId, orderStatus);
        return initSuccessObjectResult(null);
    }

    @ApiOperation("推荐返现,推荐成为会员 - 被推荐人确定收货(订单调用)")
    @RequestMapping(value = "/updateConfirm", method = RequestMethod.POST)
    public ReturnData<Object> updateConfirm(@RequestBody Map<String, Object> map) {
        Long userid = Long.parseLong((String) map.get("userid"));
        String orderNo = (String) map.get("orderNo");
        String appId = (String) map.get("appId");
        userShardService.updateConfirm(orderNo, userid, appId);
        log.info("-->/recommend/userShard/updateConfirm-->推荐返现,被推荐人确定收货，用户id：{},订单号:{}", userid, orderNo);
        return initSuccessObjectResult("发送零钱成功!");
    }

    /*@ApiOperation("数据合并,仅供测试")
    @PostMapping(value = "/updateUserId")
    public ReturnData<Object> updateUserId(@RequestBody UserMerge userMerge) {
        userShardService.updateUserId(userMerge);
        return  initSuccessObjectResult("success");
    }*/

}

