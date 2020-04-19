package com.mmj.active.homeManagement.controller;

import com.mmj.active.homeManagement.model.WebWxshardEx;
import com.mmj.active.homeManagement.service.WebWxshardService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 小程序分享配置 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/homeManagement/webWxshard")
@Api(value = "小程序分享")
public class WebWxshardController extends BaseController {
	
    @Autowired
    private WebWxshardService webWxshardService;

    @ApiOperation(value ="新增/修改")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody WebWxshardEx entity){
        return initSuccessObjectResult(webWxshardService.save(entity));
    }

    @ApiOperation(value ="获取详情(boss后台)")
    @PostMapping("query/{classCode}")
    public ReturnData<Object> query(@PathVariable("classCode") String classCode){
       return initSuccessObjectResult(webWxshardService.query(classCode));
    }

    @ApiOperation(value ="获取详情(小程序)")
    @PostMapping("selectWebWxshard/{classCode}")
    public ReturnData<Object> selectWebWxshard(@PathVariable("classCode") String classCode){
        return initSuccessObjectResult(webWxshardService.selectWebWxshard(classCode));
    }

}

