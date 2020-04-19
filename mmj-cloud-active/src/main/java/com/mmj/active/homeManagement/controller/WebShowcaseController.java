package com.mmj.active.homeManagement.controller;


import com.mmj.active.homeManagement.model.WebShowcaseEx;
import com.mmj.active.homeManagement.service.WebShowcaseService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 橱窗配置表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-09
 */
@RestController
@RequestMapping("/homeManagement/webShowcase")
@Slf4j
public class WebShowcaseController extends BaseController {
    @Autowired
    private WebShowcaseService webShowcaseService;

    @ApiOperation(value ="新增/修改")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody WebShowcaseEx entity){
         return webShowcaseService.save(entity);
    }

    @ApiOperation(value ="删除")
    @PostMapping("delete/{showecaseId}")
    public ReturnData<Object> delete(@PathVariable("showecaseId") Integer showecaseId){
       return initSuccessObjectResult(webShowcaseService.deleteWebShowcase(showecaseId));
    }

    @ApiOperation(value ="列表查询-boss后台")
    @PostMapping("queryByGoodClass/{goodClass}")
    public ReturnData<Object> queryByGoodClass(@PathVariable("goodClass") String goodClass){
        return initSuccessObjectResult(webShowcaseService.queryByGoodClass(goodClass));
    }

    @ApiOperation(value ="列表查询-小程序")
    @PostMapping("selectWebShowcase/{goodClass}")
    public ReturnData<Object> selectWebShowcase(@PathVariable("goodClass") String goodClass){
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/homeManagement/webShowcase/selectWebShowcase-->橱窗管理列表查询，用户id：{}，分类编码：{}", userId, goodClass);
        return initSuccessObjectResult(webShowcaseService.selectWebShowcase(goodClass, userId));
    }

    @ApiOperation(value ="根据id获取详情-boss后台")
    @PostMapping("selectByShowecaseId/{showecaseId}")
    public ReturnData<WebShowcaseEx> selectByShowecaseId(@PathVariable("showecaseId") Integer showecaseId){
         return initSuccessObjectResult(webShowcaseService.selectByShowecaseId(showecaseId));
    }
}

