package com.mmj.active.cut.controller;


import com.mmj.active.cut.model.dto.*;
import com.mmj.active.cut.model.vo.CutAssistVo;
import com.mmj.active.cut.model.vo.CutDetailsVo;
import com.mmj.active.cut.model.vo.CutUserVo;
import com.mmj.active.cut.service.CutRewardService;
import com.mmj.active.cut.service.CutTaskService;
import com.mmj.active.cut.service.CutUserService;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

import javax.validation.Valid;

/**
 * <p>
 * 用户砍价表 前端控制器
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@Slf4j
@RestController
@RequestMapping("/cut/cutUser")
@Api(value = "砍价用户接口", description = "砍价用户接口")
public class CutUserController extends BaseController {
    @Autowired
    private CutUserService cutUserService;
    @Autowired
    private CutTaskService cutTaskService;
    @Autowired
    private CutRewardService cutRewardService;

    @PostMapping("/task")
    @ApiOperation("我的任务进度")
    public ReturnData<CutUserTaskDto> userCutTask() {
        return initSuccessObjectResult(cutTaskService.getCutUserTask());
    }

    @PostMapping("/firstRateReward")
    @ApiOperation("获取帮砍时的首砍奖励")
    public ReturnData<FirstRateRewardDto> firstRateReward() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(jwtUserDetails, "缺少用户信息");
        return initSuccessObjectResult(cutRewardService.getFirstRateReward(jwtUserDetails.getUserId()));
    }

    @PostMapping("/bargain")
    @ApiOperation("发起砍价")
    public ReturnData<CutUserDto> bargain(@Valid @RequestBody CutUserVo cutUserVo) {
        try {
            return initSuccessObjectResult(cutUserService.bargain(cutUserVo));
        } catch (Exception e) {
            log.error("发起砍价时发生错误", e);
            return initErrorObjectResult(StringUtils.isEmpty(e.getMessage()) ? "发起砍价失败" : e.getMessage());
        }
    }

    @PostMapping("/bargain/assist")
    @ApiOperation("帮砍")
    public ReturnData<CutAssistDto> assistBargain(@Valid @RequestBody CutAssistVo cutAssistVo) {
        try {
            CutAssistDto cutAssistDto = cutUserService.assistBargain(cutAssistVo);
            return initSuccessObjectResult(cutAssistDto);
        } catch (Exception e) {
            log.error("帮砍时发生错误", e);
            return initErrorObjectResult(StringUtils.isEmpty(e.getMessage()) ? "帮砍失败" : e.getMessage());
        }
    }
}

