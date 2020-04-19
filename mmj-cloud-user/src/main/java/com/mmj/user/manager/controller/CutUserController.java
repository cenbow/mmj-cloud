package com.mmj.user.manager.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.manager.model.CutUser;
import com.mmj.user.manager.service.CutUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户砍价表 前端控制器
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
@RestController
@RequestMapping("/user/cutUser")
@Api(value = "砍价记录", description = "砍价记录")
public class CutUserController extends BaseController {
    @Autowired
    private CutUserService cutUserService;

    @ApiOperation("新增砍价记录")
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public ReturnData add(@RequestBody CutUser cutUser) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, cutUser.getUserId());
        cutUserService.insert(cutUser);
        return initSuccessResult();
    }

    @ApiOperation("获取砍价记录列表")
    @RequestMapping(value="/list", method=RequestMethod.POST)
    public ReturnData<List<CutUser>> list(@RequestBody CutUser cutUser) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, cutUser.getUserId());
        CutUser queryCutUser = new CutUser();
        queryCutUser.setCutNo(cutUser.getCutNo());
        EntityWrapper<CutUser> cutUserEntityWrapper = new EntityWrapper<>(queryCutUser);
        cutUserEntityWrapper.orderBy("LOG_ID", false);
        return initSuccessObjectResult(cutUserService.selectList(cutUserEntityWrapper));
    }

    @ApiOperation("我的砍价记录列表")
    @RequestMapping(value="/my", method=RequestMethod.POST)
    public ReturnData<List<CutUser>> myCutList(@RequestBody CutUser cutUser) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, cutUser.getUserId());
        return initSuccessObjectResult(cutUserService.selectByUserId(cutUser));
    }
}

