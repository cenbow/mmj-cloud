package com.mmj.user.manager.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.manager.model.UserActive;
import com.mmj.user.manager.service.UserActiveService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户活动参与表 前端控制器
 * </p>
 *
 * @author lyf
 * @since 2019-06-06
 */
@RestController
@RequestMapping("/user/userActive")
@Slf4j
public class UserActiveController extends BaseController {

    @Autowired
    private UserActiveService userActiveService;

    @ApiOperation(value = "根据用户id查询用户信息接口")
    @RequestMapping(value = "/queryWinner", method = RequestMethod.POST)
    public ReturnData<Map<String, Object>> queryWinner(@RequestBody UserActive userActive) {
        try {
            return initSuccessObjectResult(userActiveService.queryWinner(userActive));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation(value = "根据用户id查询用户信息接口")
    @RequestMapping(value = "/activeQueryWinner", method = RequestMethod.POST)
    public UserActive activeQueryWinner(@RequestBody UserActive userActive) {
        return userActiveService.activeQueryWinner(userActive);
    }


    @ApiOperation(value = "查询参与活动的用户接口")
    @RequestMapping(value = "/queryJoinUserList", method = RequestMethod.POST)
    public List<UserActive> queryJoinUserList(@RequestBody UserActive userActive) {
        return userActiveService.queryJoinUserList(userActive);
    }

    @ApiOperation(value = "查询我的抽奖码接口")
    @RequestMapping(value = "/getMyCode", method = RequestMethod.POST)
    public ReturnData getMyCode(@RequestBody UserActive userActive) {
        try {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            Assert.notNull(userActive, "用户未登录");
            userActive.setUserId(userDetails.getUserId());
            EntityWrapper<UserActive> wrapper = new EntityWrapper<>(userActive);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userActive.getUserId());
            List<UserActive> list = userActiveService.selectList(wrapper);
            log.info("查询我的抽奖码:{}", list);
            List<String> codeList = new ArrayList<>();
            for (UserActive active : list) {
                codeList.add(active.getLotteryCode());
            }
            return initSuccessObjectResult(codeList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }

    }
}

