package com.mmj.active.cut.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Maps;
import com.mmj.active.cut.model.CutConf;
import com.mmj.active.cut.service.CutConfService;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.mmj.common.controller.BaseController;

import java.util.Map;

/**
 * <p>
 * 砍价公共配置表 前端控制器
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@RestController
@RequestMapping("/cut/cutConf")
public class CutConfController extends BaseController {
    @Autowired
    private CutConfService cutConfService;

    @ApiOperation(value = "获取砍价公共配置")
    @RequestMapping(method = RequestMethod.POST)
    public ReturnData<CutConf> getCutConf() {
        Page<CutConf> cutConfPage = new Page<>(1, 1);
        cutConfPage = cutConfService.selectPage(cutConfPage);
        if (cutConfPage.getRecords().size() > 0) {
            CutConf cutConf = cutConfPage.getRecords().get(0);
            return initSuccessObjectResult(cutConf);
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "获取砍价微信配置")
    @RequestMapping(value = "/weixnName", method = RequestMethod.POST)
    public ReturnData<Map> getWxCutConf() {
        Page<CutConf> cutConfPage = new Page<>(1, 1);
        cutConfPage = cutConfService.selectPage(cutConfPage);
        if (cutConfPage.getRecords().size() > 0) {
            CutConf cutConf = cutConfPage.getRecords().get(0);
            Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(1);
            resultMap.put("weixnName", cutConf.getWeixnName());
            return initSuccessObjectResult(resultMap);
        }
        return initSuccessResult();
    }
}

