package com.mmj.active.prizewheels.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.prizewheels.service.PrizewheelsFacadeService;
import com.mmj.common.annotation.ApiPreventFrequently;
import com.mmj.common.annotation.ApiWaitForCompletion;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;

/**
 * <p>
 * 转盘活动 - 账户表，包含买买币余额、红包余额 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Slf4j
@RestController
@RequestMapping("/prizewheels")
public class PrizewheelsController extends BaseController {
	
	@Autowired
	private PrizewheelsFacadeService facadeService;
	
	@ApiPreventFrequently
	@ApiWaitForCompletion
	@RequestMapping(value = "/checkNewUser", method = RequestMethod.POST)
	@ApiOperation(value = "检查是否新用户，是则发红包")
	public ReturnData<Object> checkNewUser() {
		log.info("-->checkNewUser-->检查转盘新用户");
		return this.initSuccessObjectResult(facadeService.checkNewUser());
	}
	
	@RequestMapping(value = "/load", method = RequestMethod.POST)
	@ApiOperation(value = "获取转盘页面初始化数据")
	public ReturnData<Object> load() {
		log.info("-->load-->获取转盘页面初始化数据");
		return this.initSuccessObjectResult(facadeService.loadPrizewheelsInitData());
	}
	
	@ApiPreventFrequently
	@ApiWaitForCompletion
	@RequestMapping(value = "/addRedpacketBalance/{from}", method = RequestMethod.POST)
	@ApiOperation(value = "红包-放入我的余额，并返回实时余额")
	public ReturnData<Object> addRedpacketBalance(@PathVariable("from") String from) {
		log.info("-->addRedpacketBalance-->用户点击放入我的余额, from: {}", from);
		return this.initSuccessObjectResult(facadeService.addRedpacketBalance(from));
	}
	
	@ApiPreventFrequently(timeInterval=500)
	@ApiWaitForCompletion
	@RequestMapping(value = "/addCoinsBalance/{from}", method = RequestMethod.POST)
	@ApiOperation(value = "买买币-放入我的余额，并返回实时余额")
	public Object addCoinsBalance(@PathVariable("from") String from) {
		log.info("-->addCoinsBalance-->用户点击放入我的余额, from: {}", from);
		return this.initSuccessObjectResult(facadeService.addCoinsBalance(from));
	}
	
	@ApiPreventFrequently
	@ApiWaitForCompletion
	@RequestMapping(value = "/sign", method = RequestMethod.POST)
	@ApiOperation(value = "转盘任务一签到")
	public Object sign() {
		log.info("-->sign-->转盘活动签到");
		return this.initSuccessObjectResult(facadeService.sign());
	}
	
	@RequestMapping(value = "/preWithdraw", method = RequestMethod.POST)
	@ApiOperation(value = "获取预提现数据：红包实时余额和头像")
	public Object preWithdraw() {
		log.info("-->preWithdraw-->提现前获取用户账户上的实时红包余额");
		return this.initSuccessObjectResult(facadeService.prepareWithdraw());
	}
	
	@ApiPreventFrequently
	@ApiWaitForCompletion
	@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
	@ApiOperation(value = "提现")
	public Object withdraw(@RequestBody String param) {
		log.info("-->withdraw-->转盘余额提现-->param: {}", param);
		JSONObject jsonObject = JSONObject.parseObject(param);
		Double withdrawMoney = jsonObject.getDouble("withdrawMoney");
		return this.initSuccessObjectResult(facadeService.withdraw(withdrawMoney));
	}
	
	@ApiPreventFrequently
	@ApiWaitForCompletion
	@RequestMapping(value = "/prizeDraw", method = RequestMethod.POST)
	@ApiOperation(value = "点击抽奖")
	public Object prizeDraw() {
		log.info("-->prizeDraw-->点击抽奖");
		return this.initSuccessObjectResult(facadeService.prizeDraw());
	}

	@RequestMapping(value = "/myPrizes", method = RequestMethod.POST)
	@ApiOperation(value = "获取我的奖品")
	public Object myPrizes() {
		log.info("-->myPrize-->获取我的奖品记录");
		return this.initSuccessObjectResult(facadeService.getMyPrizes());
	}
	
	@RequestMapping(value = "/coinsDetails", method = RequestMethod.POST)
	@ApiOperation(value = "获取我的买买币明细")
	public Object getMyCoinsDetail() {
		log.info("-->getMyCoinsDetail-->获取我的买买币明细");
		return this.initSuccessObjectResult(facadeService.getMyCoinsDetail());
	}
	
	@RequestMapping(value = "/task", method = RequestMethod.POST)
	@ApiOperation(value = "获取我的转盘任务数据")
	public Object getMyTaskData() {
		log.info("-->getMyTaskData-->获取我的转盘任务数据");
		return this.initSuccessObjectResult(facadeService.getMyTaskData());
	}
	
	@RequestMapping(value = "/withdrawRecords", method = RequestMethod.POST)
	@ApiOperation(value = "获取提现记录")
	public Object getWithdrawRecord() {
		log.info("-->getWithdrawRecord-->获取提现记录");
		return this.initSuccessObjectResult(facadeService.getWithdrawRecord());
	}
	
	@ApiPreventFrequently
	@ApiWaitForCompletion
	@RequestMapping(value = "/clickFriendShare/{shareUserId}", method = RequestMethod.POST)
	@ApiOperation(value = "点击好友分享的转盘")
	public Object clickFriendShare(@PathVariable Long shareUserId) {
		log.info("-->clickFriendShare-->点击好友分享的转盘, shareUserId: {}", shareUserId);
		facadeService.preClickFriendShare(shareUserId);
		return this.initSuccessResult(); 
	}
	
}


