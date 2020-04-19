package com.mmj.active.cut.controller;


import com.mmj.active.cut.model.dto.*;
import com.mmj.active.cut.model.vo.CutDetailsVo;
import com.mmj.active.cut.service.CutInfoService;
import com.mmj.active.cut.service.CutUserService;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 砍价信息表 前端控制器
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@RestController
@RequestMapping("/cut/cutInfo")
@Api(value = "砍价信息接口", description = "砍价信息接口")
public class CutInfoController extends BaseController {
    @Autowired
    private CutInfoService cutInfoService;
    @Autowired
    private CutUserService cutUserService;

    @PostMapping
    @ApiOperation("我的砍价列表")
    public ReturnData<List<MyCutListDto>> myCutList() {
        return initSuccessObjectResult(cutUserService.myCutList());
    }

    @PostMapping("/details")
    @ApiOperation("砍价详情")
    public ReturnData<CutDetailsDto> cutDetails(@Valid @RequestBody CutDetailsVo detailsVo) {
        return initSuccessObjectResult(cutUserService.details(detailsVo));
    }

    @PostMapping("/address")
    @ApiOperation("获取发起砍价地址")
    public ReturnData<CutAddressDto> cutAddressByCutNo(@Valid @RequestBody CutDetailsVo detailsVo) {
        return initSuccessObjectResult(cutUserService.address(detailsVo));
    }

    @PostMapping("/freeList")
    @ApiOperation("免费拿榜单")
    public ReturnData<List<CutFreeListDto>> cutFreeList() {
        return initSuccessObjectResult(cutUserService.cutFreeList());
    }

    @PostMapping("/assistList")
    @ApiOperation("砍价榜单")
    public ReturnData<List<AssistCutListDto>> assistCutList() {
        return initSuccessObjectResult(cutUserService.assistCutList());
    }

    @PostMapping("/peopleList")
    @ApiOperation("人脉榜单")
    public ReturnData<List<PeopleCutListDto>> peopleCutList() {
        return initSuccessObjectResult(cutUserService.peopleCutList());
    }

    @PostMapping("/good")
    @ApiOperation("砍价商品列表")
    public ReturnData<List<CutGoodListDto>> goodList() {
        return initSuccessObjectResult(cutInfoService.goodList());
    }

}

