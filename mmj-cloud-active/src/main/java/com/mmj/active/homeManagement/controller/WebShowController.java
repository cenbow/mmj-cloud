package com.mmj.active.homeManagement.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.homeManagement.constant.WebAlertConstant;
import com.mmj.active.homeManagement.model.WebShow;
import com.mmj.active.homeManagement.service.WebShowService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;

/**
 * <p>
 * 页面展示表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/homeManagement/webShow")
@Api(value = "页面展示（总开关）")
public class WebShowController extends BaseController {
	
    @Autowired
    private WebShowService webShowService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value ="获取详情-boss后台")
    @PostMapping("query/{classCode}")
    public ReturnData<WebShow> query(@PathVariable("classCode") String classCode){
       EntityWrapper<WebShow> webShowEntityWrapper = new EntityWrapper<>();
       webShowEntityWrapper.eq("CLASS_CODE",classCode);
       return initSuccessObjectResult(webShowService.selectList(webShowEntityWrapper).get(0));
    }

    @ApiOperation(value ="修改")
    @PostMapping("update")
    public ReturnData<Object> update(@RequestBody WebShow webShow){
        return initSuccessObjectResult(webShowService.updateWebShow(webShow));
    }

    @ApiOperation(value ="商品调用 - 新增一级分类初始化数据")
    @PostMapping("saveWebAhow")
    public ReturnData<Object> saveWebAhow(@RequestBody WebShow webShow){
        webShow.setShowFlag(WebAlertConstant.webShow.NO_SHOW_FLAG);
        webShow.setTopShow(WebAlertConstant.webShow.NO_TOP_SHOW);
        webShow.setMaketingShow(WebAlertConstant.webShow.NO_MAKETING_SHOW);
        webShow.setShowcaseShow(WebAlertConstant.webShow.NO_SHOWCASE_SHOW);
        webShow.setWxshardShow(WebAlertConstant.webShow.NO_WXSHARD_SHOW);
        webShow.setGoddOrder(WebAlertConstant.webShow.NO_GODD_ORDER);
        webShow.setCreaterId(SecurityUserUtil.getUserDetails().getUserId());
        webShow.setCreaterTime(new Date());
        return initSuccessObjectResult(webShowService.insert(webShow));

    }

    @ApiOperation(value ="获取一级分类 - boss后台")
    @PostMapping("selectGoodClass")
    public ReturnData<Object> selectGoodClass(){
        return initSuccessObjectResult(webShowService.selectGoodClass());
    }

    @ApiOperation(value ="从缓存中获取版本号 - 小程序")
    @PostMapping("selectCode/{classCode}")
    public ReturnData<Object> selectCode(@PathVariable("classCode") String classCode){
         return initSuccessObjectResult(webShowService.selectCode(classCode));
    }

    @ApiOperation(value ="模糊匹配删除缓存")
    @PostMapping("deleteRedis")
    public ReturnData<Object> deleteRedis(@RequestBody String params){
         JSONObject jsonObject = JSONObject.parseObject(params);
         String key = jsonObject.getString("key");
         Set<String> keys = redisTemplate.keys(key + "*");
         redisTemplate.delete(keys);
         return initSuccessObjectResult("删除缓存成功");
    }


    @ApiOperation(value ="删除一级分类 - boss后台")
    @PostMapping("delectByGoodClass/{goodClass}")
    public ReturnData<Object> delectByGoodClass(@PathVariable("goodClass")String goodClass){
        return initSuccessObjectResult(webShowService.delectByGoodClass(goodClass));
    }

    /**
     * 新用户变成老用户, 或者是老用户变成会员
     * @return
     */
    @ApiOperation(value ="修改版本号 - 各模块调用")
    @PostMapping("updateIndexCode/{userIdentity}")
    public ReturnData<Object> updateIndexCode(@PathVariable("userIdentity")String userIdentity){
        webShowService.updateIndexCode(userIdentity);
        return initSuccessObjectResult("修改版本号成功");
    }
}

