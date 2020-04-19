package com.mmj.active.homeManagement.controller;


import com.mmj.active.homeManagement.model.WebTop;
import com.mmj.active.homeManagement.model.WebTopEx;
import com.mmj.active.homeManagement.service.WebTopService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 顶部配置表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/homeManagement/webTop")
@Api(value = "顶部大图")
@Slf4j
public class WebTopController extends BaseController {
    @Autowired
    private WebTopService webTopService;

    @ApiOperation(value ="新增/修改")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody WebTop entity){
        return initSuccessObjectResult(webTopService.save(entity));
    }

    @ApiOperation(value ="删除")
    @PostMapping("delete/{topId}")
    public ReturnData<Object> delete(@PathVariable("topId") Integer topId){
        return initSuccessObjectResult(webTopService.deleteByTopId(topId));
    }

    @ApiOperation(value ="列表查询-boss后台")
    @PostMapping("query/{classCode}")
    public ReturnData<WebTopEx> query(@PathVariable("classCode") String classCode){
         return initSuccessObjectResult(webTopService.query(classCode));
    }

    @ApiOperation(value ="列表查询（小程序）")
    @PostMapping("selectWebTop/{classCode}")
    public ReturnData<WebTopEx> selectWebTop(@PathVariable("classCode") String classCode){
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/homeManagement/webTop/selectWebTop-->顶部大图列表查询，用户id：{}，分类编码：{}",userid,classCode);
        return initSuccessObjectResult(webTopService.selectWebTop(classCode, userid));
    }

    @ApiOperation(value ="根据id获取详情-boss后台")
    @PostMapping("selectByTopId/{topId}")
    public ReturnData<WebTop> selectByTopId(@PathVariable("topId") Integer topId){
        return initSuccessObjectResult(webTopService.selectById(topId));
    }
}

