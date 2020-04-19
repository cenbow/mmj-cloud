package com.mmj.active.cut.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.cut.model.dto.BossCutDto;
import com.mmj.active.cut.model.dto.BossCutEditDto;
import com.mmj.active.cut.model.dto.BossCutSysDto;
import com.mmj.active.cut.model.vo.*;
import com.mmj.active.cut.service.CutConfService;
import com.mmj.active.cut.service.CutInfoService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @description: boss砍价配置
 * @auther: KK
 * @date: 2019/6/11
 */
@RestController
@RequestMapping("/boss/cut")
@Api(value = "boss后管砍价配置", description = "boss后管砍价配置")
public class BossCutController extends BaseController {
    @Autowired
    private CutInfoService cutInfoService;

    @Autowired
    private CutConfService cutConfService;

    /**
     * 新增砍价
     *
     * @param addVo
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增砍价")
    public ReturnData<BossCutEditDto> add(@Valid @RequestBody BossCutAddVo addVo) {
        return initSuccessObjectResult(cutInfoService.add(addVo));
    }

    /**
     * 编辑砍价
     *
     * @param editVo
     * @return
     */
    @PostMapping("/edit")
    @ApiOperation("编辑砍价")
    public ReturnData<BossCutEditDto> edit(@Valid @RequestBody BossCutEditVo editVo) {
        return initSuccessObjectResult(cutInfoService.edit(editVo));
    }

    /**
     * 删除砍价
     *
     * @param params
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("删除砍价")
    public ReturnData delete(@Valid @RequestBody BossCutParams params) {
        cutInfoService.deleteByCutId(params.getCutId());
        return initSuccessResult();
    }

    /**
     * 根据条件查询查询
     *
     * @param queryVo
     * @return
     */
    @PostMapping("/query")
    @ApiOperation("砍价信息列表")
    public ReturnData<Page<BossCutDto>> query(@Valid @RequestBody BossCutQueryVo queryVo) {
        return initSuccessObjectResult(cutInfoService.query(queryVo));
    }

    /**
     * 查询单个砍价信息
     *
     * @param params
     * @return
     */
    @PostMapping("/query/cutId")
    @ApiOperation("查询单个砍价信息")
    public ReturnData<BossCutDto> queryByCutId(@Valid @RequestBody BossCutParams params) {
        return initSuccessObjectResult(cutInfoService.queryByCutId(params.getCutId()));
    }

    /**
     * 获取砍价公共设置信息
     *
     * @return
     */
    @PostMapping("/sys")
    @ApiOperation("获取砍价公共设置信息")
    public ReturnData<BossCutSysDto> syn() {
        return initSuccessObjectResult(cutConfService.getSys());
    }

    /**
     * 编辑砍价公共设置信息
     *
     * @param editVo
     * @return
     */
    @PostMapping("/sys/edit")
    @ApiOperation("编辑砍价公共设置信息")
    public ReturnData sysEdit(@Valid @RequestBody BossCutSysEditVo editVo) {
        cutConfService.editSys(editVo);
        return initSuccessResult();
    }
}
