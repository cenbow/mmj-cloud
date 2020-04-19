package com.mmj.active.threeSaleTenner.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.threeSaleTenner.model.ThreeSaleTenner;
import com.mmj.active.threeSaleTenner.service.ThreeSaleTennerService;
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
 * 十元三件活动表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-11
 */
@RestController
@RequestMapping(value = "/threeSaleTenner",produces = {"application/json;charset=utf-8"})
@Api("十元三件")
@Slf4j
public class ThreeSaleTennerController extends BaseController {
    @Autowired
    private ThreeSaleTennerService threeSaleTennerService;

    @ApiOperation(value ="保存/修改 - boss后台")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody ThreeSaleTenner threeSaleTenner){
        return threeSaleTennerService.save(threeSaleTenner);
    }

    @ApiOperation(value ="获取详情- boss后台")
    @PostMapping("query")
    public ReturnData<ThreeSaleTenner> query(){
        return initSuccessObjectResult(threeSaleTennerService.query());
    }


    @ApiOperation(value ="获取详情- 小程序")
    @PostMapping("selectThreeSaleTenner")
    public ReturnData<ThreeSaleTenner> selectThreeSaleTenner(){
        return initSuccessObjectResult(threeSaleTennerService.selectThreeSaleTenner());
    }

    @ApiOperation(value ="查询购买资格 - 小程序")
    @PostMapping("selectIsBuy/{infoId}")
    public ReturnData<Object> selectIsBuy(@PathVariable("infoId") Integer infoId){
         Long userid = SecurityUserUtil.getUserDetails().getUserId();
         log.info("-->/threeSaleTenner/selectIsBuy-->十元三件,查询购买资格，用户id：{}",userid);
         return initSuccessObjectResult(threeSaleTennerService.selectIsBuy(userid,infoId));
    }


    @ApiOperation(value ="修改缓存中的分享时间 - 小程序")
    @PostMapping("addShareTime")
    public ReturnData<Object> addShareTime(@RequestBody String params){ // 1:待付款  2：取消付款 3：已支付  4:已分享
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        JSONObject jsonObject = JSON.parseObject(params);
        Integer status = jsonObject.getInteger("status");
        String orderNo = jsonObject.getString("orderNo");
        log.info("-->/threeSaleTenner/addShareTime-->十元三件,修改分享时间，用户id：{},状态:{},订单号:{}",userid,status,orderNo);
        return initSuccessObjectResult(threeSaleTennerService.addShareTime(userid,status,orderNo));
    }


    @ApiOperation(value ="增加购买次数 - 小程序")
    @PostMapping("addBuyCout")
    public ReturnData<Object> addBuyCout(){
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/threeSaleTenner/addBuyCout-->十元三件,增加购买次数，用户id：{}",userid);
        return initSuccessObjectResult(threeSaleTennerService.addBuyCout(userid));
    }


    @ApiOperation(value ="查询商品 - 小程序")
    @PostMapping("selectGoods")
    public ReturnData<Page<ActiveGood>> selectGoods(@RequestBody ThreeSaleTenner ThreeSaleTenner){
        log.info("-->/threeSaleTenner/selectGoods-->十元三件,商品查询，参数:{}",JSON.toJSONString(ThreeSaleTenner));
        return initSuccessObjectResult(threeSaleTennerService.selectGoods(ThreeSaleTenner));
    }

}

