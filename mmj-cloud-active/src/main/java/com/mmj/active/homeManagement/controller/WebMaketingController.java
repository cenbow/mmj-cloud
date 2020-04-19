package com.mmj.active.homeManagement.controller;

import com.mmj.active.homeManagement.common.RedisUtils;
import com.mmj.active.homeManagement.model.WebMaketing;
import com.mmj.active.homeManagement.model.WebMaketingEx;
import com.mmj.active.homeManagement.service.WebMaketingService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 营销配置表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/homeManagement/webMaketing")
@Api(value = "营销管理")
@Slf4j
public class WebMaketingController extends BaseController {
    @Autowired
    private WebMaketingService webMaketingService;
    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value ="保存/修改")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody WebMaketing webMaketing){
        return initSuccessObjectResult(webMaketingService.save(webMaketing));
    }

    @ApiOperation(value ="删除")
    @PostMapping("delete/{maketId}")
    public ReturnData<Object> delete (@PathVariable("maketId") Integer maketId){
        return initSuccessObjectResult(webMaketingService.deleteByMaketId(maketId));
    }

    @ApiOperation(value ="列表查询（boss后台）")
    @PostMapping("query/{classCode}")
    public ReturnData<WebMaketingEx> query (@PathVariable("classCode") String classCode){
        return initSuccessObjectResult(webMaketingService.query(classCode));
    }

    @ApiOperation(value ="列表查询（小程序）")
    @PostMapping("selectWebMaketing/{classCode}")
    public ReturnData<WebMaketingEx> selectWebMaketing (@PathVariable("classCode") String classCode){
         long userId = SecurityUserUtil.getUserDetails().getUserId();
         log.info("-->/homeManagement/webMaketing/selectWebMaketing-->营销管理列表查询，用户id：{}，分类编码：{}", userId, classCode);
         return initSuccessObjectResult(webMaketingService.selectWebMaketing(classCode, userId));
    }

    @ApiOperation(value ="根据id获取详情（boss后台）")
    @PostMapping("selectByMaketId/{maketId}")
    public ReturnData<WebMaketing> selectByMaketId (@PathVariable("maketId") Integer maketId){
        return initSuccessObjectResult(webMaketingService.selectByMaketId(maketId));
    }

    @ApiOperation(value ="修改排序顺序（boss后台）")
    @PostMapping("updateOrder")
    public ReturnData<Object> updateOrder (@RequestBody List<WebMaketing> list){
        boolean flag = webMaketingService.updateBatchById(list);
        //删除缓存
        webMaketingService.deleteReids(list.get(0));
        return initSuccessObjectResult(flag);
    }

}

