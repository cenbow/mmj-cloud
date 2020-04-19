package com.mmj.user.manager.controller;


import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.manager.dto.BaseUserDto;
import com.mmj.user.manager.dto.SearchUserParamDto;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.service.BaseUserService;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.service.UserMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * <p>
 * 用户信息查询相关服务
 * </p>
 *
 * @author shenfuding
 * @since 2019-05-06
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(value = "用户管理")
public class BaseUserController extends BaseController {

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private UserMemberService userMemberService;

    @ApiOperation(value = "根据用户id查询用户基础信息")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.POST)
    public BaseUser getById(@PathVariable("id") Long id) {
        return baseUserService.getById(id);
    }

    @ApiOperation(value = "根据用户id查询是否会员")
    @RequestMapping(value = "/isMember/{userId}", method = RequestMethod.POST)
    public ReturnData<Boolean> isMember(@PathVariable("userId") Long userId) {
        UserMember um = userMemberService.queryUserMemberInfoByUserId(userId);
        boolean isMember = um != null ? um.getActive() : false;
        return this.initSuccessObjectResult(isMember);
    }

    @ApiOperation(value = "搜索用户")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ReturnData<Set<BaseUserDto>> search(@RequestBody SearchUserParamDto param) {
        log.info("-->搜索用户，参数：{}", JSONObject.toJSONString(param));
        return this.initSuccessObjectResult(baseUserService.search(param));
    }

    @ApiOperation(value = "根据userId和appId查询用户关键信息:openId、昵称、头像地址")
    @RequestMapping(value = "/queryUserInfo", method = RequestMethod.POST)
    public ReturnData<BaseUserDto> queryUserInfoByUserId(@RequestBody String param) {
        log.info("-->queryUserInfoByUserId-->参数:{}", param);
        JSONObject jsonParam = JSONObject.parseObject(param);
        long userId = jsonParam.getLongValue("userId");
        String appId = jsonParam.getString("appId");
        return this.initSuccessObjectResult(baseUserService.queryUserInfoByUserId(userId, appId));
    }

    @ApiOperation(value = "根据手机号查询用户关键信息:openId、昵称、头像地址")
    @RequestMapping(value = "/queryUserInfo/{phone}", method = RequestMethod.POST)
    public ReturnData<BaseUserDto> queryUserInfoByPhone(@PathVariable("phone") String phone) {
        log.info("-->queryUserInfoByPhone-->参数:{}", phone);
        return this.initSuccessObjectResult(baseUserService.queryUserInfoByPhone(phone));
    }

}

