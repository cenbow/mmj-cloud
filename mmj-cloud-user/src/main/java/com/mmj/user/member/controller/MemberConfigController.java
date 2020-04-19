package com.mmj.user.member.controller;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.feigin.NoticeFeignClient;
import com.mmj.user.member.service.MemberConfigService;

@Slf4j
@RequestMapping("/member/config")
@RestController
public class MemberConfigController extends BaseController {
	
	@Autowired
	private MemberConfigService memberConfigService;
	
	@Autowired
	private NoticeFeignClient noticeFeignClient;
	
	@RequestMapping(value="/loadAll", method=RequestMethod.POST)
	public ReturnData<Object> loadAll() {
		log.info("-->查询会员配置");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("mmjUsersCountExceed", memberConfigService.getMmjUsersCountExceed());
		resultMap.put("mmjMemberWorth", memberConfigService.getMmjMemberWorth());
		resultMap.put("mmjMemberFirstOrderDayLimit", memberConfigService.getMmjMemberFirstOrderDayLimit());
		resultMap.put("mmjMemberDay", memberConfigService.getMmjMemberDay());
		resultMap.put("mmjMemberDayStr", memberConfigService.getMmjMemberDayStr());
		resultMap.put("mmjMemberCumulativeConsumption", memberConfigService.getMmjMemberCumulativeConsumption());
		resultMap.put("mmjMemberActivityStartDate", memberConfigService.getMmjMemberActivityStartDate());
		resultMap.put("mmjMemberActivityDayContinue", memberConfigService.getMmjMemberActivityDayContinue());
		resultMap.put("memberActivityHowManyDaysToEnd", memberConfigService.getMemberActivityHowManyDaysToEnd());
		resultMap.put("isMemberDay", memberConfigService.isMemberDay());
		resultMap.put("nextMemberDayIntervalMilliseconds", memberConfigService.getNextMemberDayIntervalMilliseconds());
		return this.initSuccessObjectResult(resultMap);
	}
	
}
