package com.mmj.user.member.controller;


import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mmj.common.controller.BaseController;
import com.mmj.common.model.MobileCode;
import com.mmj.user.member.service.MemberImportService;

/**
 * <p>
 * 第三方导入会员的记录表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-12
 */
@RestController
@RequestMapping("/member/import")
public class MemberImportController extends BaseController {
	
	@Autowired
	private MemberImportService memberImportService;
	
	@RequestMapping(value="/bindInfo",method=RequestMethod.POST)
	@ApiOperation("获取手机号绑定信息")
	public Object bindInfo() {
		return this.initSuccessObjectResult(memberImportService.bindInfo());
	}
	
	@RequestMapping(value="/sendValidateCode/{mobile}",method=RequestMethod.POST)
	@ApiOperation("发送短信验证码")
	public Object sendValidateCode(@PathVariable String mobile) {
		memberImportService.sendValidateCode(mobile);
		return this.initSuccessResult();
	}
	
	@RequestMapping(value="/validate",method=RequestMethod.POST)
	@ApiOperation("校验验证码是否正确，是则保存会员")
	public Object validate(@RequestBody MobileCode mobileCode) {
		memberImportService.validate(mobileCode);
		return this.initSuccessResult();
	}
	
}

