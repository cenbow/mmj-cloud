package com.mmj.active.threeSaleTenner.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.active.threeSaleTenner.model.ThreeSaleFissionEx;
import com.mmj.active.threeSaleTenner.service.ThreeSaleFissionService;
import com.mmj.common.annotation.ApiWaitForCompletion;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;

/**
 * <p>
 * 十元三件红包裂变 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-07-11
 */
@RestController
@RequestMapping("/threeSaleTenner/threeSaleFission")
@Slf4j
public class ThreeSaleFissionController extends BaseController {
    @Autowired
    private ThreeSaleFissionService threeSaleFissionService;

    /**
     * 用户下十元三件订单保存数据
     * @param threeSaleFissionEx
     * @return
     */
    @ApiOperation(value ="保存- 小程序")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody ThreeSaleFissionEx threeSaleFissionEx){
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/threeSaleFission/save-->十元三件红包裂变保存,用户id:{},参数：{}",userId ,JSON.toJSONString(threeSaleFissionEx));
        return initSuccessObjectResult(threeSaleFissionService.save(threeSaleFissionEx));
    }

    /**
     * 查询详情 - 拆红包使用
     * @return
     */
    @ApiOperation(value ="查询详情- 小程序")
    @PostMapping("query")
    public ReturnData<Object> query(){
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/threeSaleFission/query-->十元三件红包裂变,查询详情,用户id:{}",userId);
        return initSuccessObjectResult(threeSaleFissionService.query());
    }


    /**
     * 好友助力下单
     * @return
     */
    @ApiOperation(value ="好友助力下单- 小程序")
    @PostMapping("assist")
    public ReturnData<Object> assist(@RequestBody ThreeSaleFissionEx threeSaleFissionEx){
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/threeSaleFission/assist-->十元三件红包裂变,好友助力下单,用户id:{}",userId);
        return initSuccessObjectResult(threeSaleFissionService.assist(threeSaleFissionEx));
    }

    /**
     * 好友助力下单 - 确定支付(支付模块调用)
     * @return
     */
    @ApiOperation(value ="确定支付- 小程序")
    @PostMapping("updatePay/{orderNo}")
    public ReturnData<Object> updatePay(@PathVariable("orderNo") String orderNo){
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/threeSaleFission/updatePay-->十元三件红包裂变,确定支付,用户id:{},订单号：{}",userId,orderNo);
        return initSuccessObjectResult(threeSaleFissionService.updatePay(orderNo));
    }

    /**
     * 好友助力下单 - 确定收货(订单模块调用)
     * @return
     */
    @ApiOperation(value ="确定收货- 小程序")
    @PostMapping("updateConfirm")
    public ReturnData<Object> updateConfirm(@RequestBody String params){
        JSONObject jsonObject = JSON.parseObject(params);
        String orderNo = jsonObject.getString("orderNo");
        String appId = jsonObject.getString("appId");
        return initSuccessObjectResult(threeSaleFissionService.updateConfirm(orderNo,appId));
    }

    /**
     * 好友助力下单 - 取消订单(订单模块调用)
     * @return
     */
    @ApiOperation(value ="取消订单- 小程序")
    @PostMapping("cancelled/{orderNo}")
    public ReturnData<Object> cancelled(@PathVariable("orderNo") String orderNo){
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/threeSaleFission/cancelled-->十元三件红包裂变,取消订单,用户id:{},订单号：{}",userId,orderNo);
        return initSuccessObjectResult(threeSaleFissionService.cancelled(orderNo));
    }


    /**
     * 红包页面 - 根据用户id查询明细
     * @param type
     * @return
     */
    @ApiOperation(value ="根据用户id查询明细- 小程序")
    @PostMapping("queryList/{type}")
    public ReturnData<Object> queryList(@PathVariable("type") String type){
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/threeSaleFission/queryList-->十元三件红包裂变,查询明细,用户id:{},类型：{}",userId,type);
        return initSuccessObjectResult(threeSaleFissionService.queryList(type));
    }

    /**
     * 用户点击提现
     * @param
     * @return
     */
    @ApiWaitForCompletion
    @ApiOperation(value ="提现- 小程序")
    @PostMapping("doCash")
    public ReturnData<Object> doCash(){
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/threeSaleFission/doCash-->十元三件红包裂变,用户提现,用户id:{}",userId);
        return threeSaleFissionService.doCash();
    }

    /**
     * 十元三件 - 我的红包页面,判断该用户是否有红包
     * @param
     * @return
     */
    @ApiOperation(value ="该用户是否有红包- 小程序")
    @PostMapping("hasRedPackage")
    public ReturnData<Object> hasRedPackage(){
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/threeSaleFission/hasRedPackage-->十元三件红包裂变,该用户是否有红包,用户id:{}",userId);
        return initSuccessObjectResult(threeSaleFissionService.hasRedPackage());
    }


    /**
     * 十元三件红包裂变 - 定时任务,2小时内,助力好友未支付成功, 设置为已失效
     * @param
     * @return
     */
    @ApiOperation(value ="定时任务- 小程序")
    @PostMapping("updateInvalid")
    public ReturnData<Object> updateInvalid(){
        log.info("-->/threeSaleTenner/threeSaleFission/updateInvalid-->十元三件红包裂变，定时任务执行，2小时内，助力好友未支付，设置为失效");
        return initSuccessObjectResult(threeSaleFissionService.updateInvalid());
    }

    /*@ApiOperation("数据合并,仅供测试")
    @PostMapping(value = "/updateUserId")
    public ReturnData<Object> updateUserId(@RequestBody UserMerge userMerge) {
        threeSaleFissionService.updateUserId(userMerge.getOldUserId(),userMerge.getNewUserId());
        return  initSuccessObjectResult("success");
    }*/

}

