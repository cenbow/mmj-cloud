package com.mmj.active.async.service.impl;

import com.mmj.active.common.service.FocusInfoService;
import com.mmj.active.threeSaleTenner.service.ThreeSaleFissionService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.async.service.ActiveAsyncService;
import com.mmj.active.prizewheels.service.PrizewheelsFacadeService;
import com.mmj.common.model.UserMerge;

@Slf4j
@Configuration
@EnableAsync
public class ActiveAsyncServiceImpl implements ActiveAsyncService {
	
	@Autowired
	private PrizewheelsFacadeService prizewheelsFacadeService;
	@Autowired
	private ThreeSaleFissionService threeSaleFissionService;
	@Autowired
	private FocusInfoService focusInfoService;
	
	@Async
	@Override
	public void handleOfficalAccountReplyForPrizewheels(JSONObject jsonObject) {
		if(jsonObject!= null) {
			String appid = jsonObject.getJSONObject("ex").getString("appid");
			String openId = jsonObject.getJSONObject("ex").getString("openid");
			String keyword = jsonObject.getString("Content");
			log.info("-->公众号消息-->用户{}回复关键字:{}，appid:{}", openId, keyword, appid);
			prizewheelsFacadeService.officialAccountsReply(appid, openId, keyword);
		}
	}

	@Async
	@Override
	public void mergePrizewheelsActiveTables(UserMerge userMerge) {
		log.info("-->处理用户转盘数据合并，参数：{}", JSONObject.toJSONString(userMerge));
		prizewheelsFacadeService.updateUserId(userMerge.getOldUserId(), userMerge.getNewUserId());
	}

	@Async
	@Override
	public void mergeThreeSaleFission(UserMerge userMerge) {
		log.info("-->处理用户十元三件红包裂变数据合并，参数：{}", JSONObject.toJSONString(userMerge));
		threeSaleFissionService.updateUserId(userMerge.getOldUserId(), userMerge.getNewUserId());
	}

	@Async
	@Override
	public void mergeFocusInfo(UserMerge userMerge) {
		log.info("-->处理用户十元三件红包裂变数据合并，参数：{}", JSONObject.toJSONString(userMerge));
		focusInfoService.updateUserID(userMerge);
	}

}
