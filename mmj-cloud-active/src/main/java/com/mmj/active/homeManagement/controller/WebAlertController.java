package com.mmj.active.homeManagement.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.homeManagement.model.WebAlert;
import com.mmj.active.homeManagement.service.WebAlertService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 弹窗管理 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/homeManagement/webAlert")
@Api("首页弹窗管理")
@Slf4j
public class WebAlertController extends BaseController {
	
    private static final String MMJ_USER_NEWUSER_TOPIC = "mmj.user.newuser.topic";
    
	@Autowired
    private WebAlertService webAlertService;

    @ApiOperation(value ="保存/修改")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody WebAlert webAlert){
         return initSuccessObjectResult(webAlertService.save(webAlert));
    }

    @ApiOperation(value ="删除")
    @PostMapping("delete/{alertId}")
    public ReturnData<Object> delete(@PathVariable("alertId") Integer alertId){
        boolean flag = webAlertService.deleteById(alertId);
        return initSuccessObjectResult(flag);
    }

    @ApiOperation(value ="列表查询-boss后台")
    @PostMapping("query")
    public ReturnData<Page<WebAlert>> query(@RequestBody WebAlert webAlert){
         return initSuccessObjectResult(webAlertService.query(webAlert));
    }

    @ApiOperation(value ="弹窗查询 - 小程序")
    @PostMapping("selectWebAlert/{source}")
    public ReturnData<Map<String, Object>> selectWebAlert(@PathVariable("source") String source){
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/homeManagement/webAlert/selectWebAlert-->弹窗查询，用户id：{}", userId);
        Map<String, Object> map = webAlertService.selectWebAlert(userId, source);
        return initSuccessObjectResult(map);
    }


    @ApiOperation(value ="用户点击弹窗 - 小程序")
    @PostMapping("clickWebAlert/{alertId}")
    public ReturnData<Object> clickWebAlert(@PathVariable("alertId") Integer alertId){
         Long userid = SecurityUserUtil.getUserDetails().getUserId();
         log.info("-->/homeManagement/webAlert/clickWebAlert-->用户点击弹窗，用户id：{}，弹窗id：{}",userid,alertId);
         return webAlertService.clickWebAlert(alertId,userid);
    }


    @ApiOperation(value ="根据id获取详情-oss后台")
    @PostMapping("selectByAlertId/{alertId}")
    public ReturnData<WebAlert> selectByAlertId(@PathVariable("alertId") Integer alertId){
        return initSuccessObjectResult(webAlertService.selectByAlertId(alertId));
    }


    @ApiOperation(value ="新用户专题落地页查询 - 小程序")
    @PostMapping("selectNewUsreTopic")
    public ReturnData<BaseDict> selectNewUsreTopic(){
        log.info("-->/homeManagement/webAlert/selectNewUsreTopic-->新用户专题落地页查询");
        return initSuccessObjectResult(webAlertService.selectNewUsreTopic());
    }

    @ApiOperation(value ="新用户专题落地页查询 - boss后台")
    @PostMapping("queryNewUsreTopic")
    public ReturnData<BaseDict> queryNewUsreTopic(){
        return initSuccessObjectResult(webAlertService.queryNewUserTopic(MMJ_USER_NEWUSER_TOPIC));
    }


    @ApiOperation(value ="弹窗查询 - APP")
    @PostMapping("selectWebAlertByApp")
    public ReturnData<Object> selectWebAlertByApp(HttpServletRequest request){
        log.info("-->/homeManagement/webAlert/selectWebAlertByApp-->弹窗查询，用户id：{}");
        return initSuccessObjectResult(webAlertService.selectWebAlertByApp(request));
    }

    @ApiOperation(value ="用户点击弹窗 - APP")
    @PostMapping("clickWebAlertByApp/{alertId}")
    public ReturnData<Object> clickWebAlertByApp(@PathVariable("alertId") Integer alertId){
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/homeManagement/webAlert/clickWebAlert-->用户点击弹窗，用户id：{}，弹窗id：{}",userid,alertId);
        return webAlertService.clickWebAlertByApp(alertId,userid);
    }

}

