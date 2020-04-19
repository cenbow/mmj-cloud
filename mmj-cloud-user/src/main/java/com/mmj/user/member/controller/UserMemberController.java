package com.mmj.user.member.controller;


import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.member.dto.SaveUserMemberDto;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.model.Vo.DegradeVo;
import com.mmj.user.member.service.MemberConfigService;
import com.mmj.user.member.service.UserMemberService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-11
 */
@Slf4j
@RestController
@RequestMapping("/member")
public class UserMemberController extends BaseController {
	
	@Autowired
	private UserMemberService userMemberService;
	
	@Autowired
	private MemberConfigService memberConfigService;
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	@ApiOperation("保存会员")
	public ReturnData<Object> save(@RequestBody SaveUserMemberDto dto) {
		log.info("-->UserMemberController-->保存会员信息，参数：{}", JSONObject.toJSONString(dto));
		return this.initSuccessObjectResult(userMemberService.save(dto));
	}
	
	@RequestMapping(value="/degrade", method=RequestMethod.POST)
	@ApiOperation("会员降级")
	public ReturnData<Object> degrade(@RequestBody DegradeVo degradeVo) {
		log.info("-->UserMemberController-->会员降级，参数：{}", degradeVo.toString());
		return this.initSuccessObjectResult(userMemberService.degrade(degradeVo));
	}
	
	@RequestMapping(value="/query/{userId}", method=RequestMethod.POST)
	@ApiOperation("查询会员信息")
	public ReturnData<UserMember> queryUserMemberInfoByUserId(@PathVariable("userId") Long userId) {
		log.info("-->UserMemberController-->查询会员信息，参数：{}", userId);
		return this.initSuccessObjectResult(userMemberService.queryUserMemberInfoByUserId(userId));
	}
	
	@RequestMapping(value="/config", method=RequestMethod.POST)
	@ApiOperation("获取会员权益配置数据")
	public ReturnData<Object> queryConfig() {
		Map<String, Object> resultMap = new HashMap<String, Object>(6);
		int mmjUsersCountExceed = memberConfigService.getMmjUsersCountExceed();
		resultMap.put("mmjUsersCountExceed", mmjUsersCountExceed);
		int mmjMemberCumulativeConsumption = memberConfigService.getMmjMemberCumulativeConsumption();
		resultMap.put("mmjMemberCumulativeConsumption", mmjMemberCumulativeConsumption);
		int mmjMemberWorth = memberConfigService.getMmjMemberWorth();
		resultMap.put("mmjMemberWorth", mmjMemberWorth);
		int memberActivityHowManyDaysToEnd = memberConfigService.getMemberActivityHowManyDaysToEnd();
		resultMap.put("memberActivityHowManyDaysToEnd", memberActivityHowManyDaysToEnd);
        String getMmjMemberDayStr = memberConfigService.getMmjMemberDayStr();
        resultMap.put("mmjMemberDayStr", getMmjMemberDayStr);
        resultMap.put("mmjMemberFirstOrderDayLimit", memberConfigService.getMmjMemberFirstOrderDayLimit());
		return this.initSuccessObjectResult(resultMap);
	}
	
	@RequestMapping(value="/info", method=RequestMethod.POST)
	@ApiOperation("个人中心会员信息")
	public ReturnData<Object> queryMemberInfo() {
		return this.initSuccessObjectResult(this.userMemberService.queryUserMemberInfoForUC());
	}


	@RequestMapping(value="/buy", method=RequestMethod.POST)
	@ApiOperation("直接购买会员")
	public ReturnData<Object> buy(HttpServletRequest request) {
		String appType = request.getHeader("appType");
		return this.initSuccessObjectResult(userMemberService.buy(appType));
	}

}

