package com.mmj.oauth.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.controller.BaseController;
import com.mmj.common.utils.SnowflakeIdWorker;
import com.mmj.oauth.dto.ConfigParam;
import com.mmj.oauth.dto.OfficialAccountUser;
import com.mmj.oauth.dto.WxUserParamDto;
import com.mmj.oauth.service.UserLoginService;
import com.mmj.oauth.service.WxUserService;

@Slf4j
@RestController
@RequestMapping("/wx/user")
public class WxUserController extends BaseController {
	
	@Autowired
	private WxUserService wxUserService;
	
	@Autowired
	private UserLoginService userLoginService;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
	
	
	@RequestMapping(value="/info", method=RequestMethod.POST)
	@ApiOperation("获取用户信息")
	public Object getUserInfo(@RequestBody WxUserParamDto paramDto) {
		log.info("-->/wx/user/getUserInfo/-->获取用户信息");
		return initSuccessObjectResult(wxUserService.getUserInfo(paramDto));
	}
	
	@RequestMapping(value="/phoneAuth", method=RequestMethod.POST)
	@ApiOperation("手机号授权")
	public Object phoneAuth(@RequestBody WxUserParamDto paramDto) {
		log.info("-->/wx/getUserInfo/-->手机号授权");
		return initSuccessObjectResult(wxUserService.phoneAuth(paramDto));
	}
	
	@RequestMapping(value="/publicSave", method=RequestMethod.POST)
	@ApiOperation("公众号保存用户")
	public Object publicSave(@RequestBody OfficialAccountUser user) {
		log.info("-->/wx/user/publicSave/-->公众号保存用户");
		return initSuccessObjectResult(wxUserService.publicSave(user));
	}
	
	@RequestMapping(value="/unfollow/{openId}", method=RequestMethod.POST)
	@ApiOperation("用户取消关注")
	public Object unfollow(@PathVariable String openId) {
		wxUserService.unfollow(openId);
		return initSuccessResult();
	}
	
	@RequestMapping(value="/getConfig", method=RequestMethod.POST)
	@ApiOperation("获取config参数")
	public Object getConfig(@RequestBody ConfigParam param) {
		return initSuccessObjectResult(wxUserService.getConfig(param.getAppId(), param.getUrl()));
	}
	
	@RequestMapping(value="/testTopic", method=RequestMethod.POST)
	public Object testTopic(@RequestBody ConfigParam param) {
		JSONObject json = new JSONObject();
		json.put("newUserId", 10002);
		json.put("oldUserId", 1001);
		log.info("-->测试主题消息:{}", json.toJSONString());
		kafkaTemplate.send("USER_MERGE",String.valueOf(snowflakeIdWorker.nextId()), json.toJSONString());
		return initSuccessResult();
	}
	
	
}
