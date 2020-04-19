package com.mmj.good.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.model.GoodBanner;
import com.mmj.good.model.GoodBannerEx;
import com.mmj.good.service.GoodBannerService;
import com.xiaoleilu.hutool.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

import java.util.List;

/**
 * <p>
 * 分类横幅表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-03
 */
@RestController
@RequestMapping("/goodBanner")
@Api(value = "商品分类banner管理")
public class GoodBannerController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodBannerController.class);

    @Autowired
    private GoodBannerService goodBannerService;

    @ApiOperation(value = "新增或更新分类Banner")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody GoodBanner entity) {
        String classCode = entity.getClassCode();
        if (classCode != null && classCode.length() != 0) {
            if (entity.getBannerId() == null) {
                EntityWrapper<GoodBanner> wrapper = new EntityWrapper<>();
                wrapper.eq("CLASS_CODE", classCode);
                wrapper.ne("SHOW_FLAG", -1);
                List<GoodBanner> goodBanners = goodBannerService.selectList(wrapper);
                if (goodBanners != null && !goodBanners.isEmpty()) {
                    return initExcetionObjectResult("该分类已经存在banner！");
                }
            }
        } else {
            return initExcetionObjectResult("分类不能为空！");
        }
        if (entity.getBannerId() == null) {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            entity.setCreaterId(userDetails.getUserId());
        }
        boolean insert = goodBannerService.insertOrUpdate(entity);
        if (insert) {
            return initSuccessResult();
        }
        return initExcetionObjectResult("新增失败！");
    }

    @ApiOperation(value = "分类Banner列表查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<Page<GoodBannerEx>> queryList(@RequestBody GoodBannerEx entity) {
        Page<GoodBannerEx> result = goodBannerService.queryListByClassCode(entity);
        return initSuccessObjectResult(result);
    }

    @ApiOperation(value = "分类Banner状态修改")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public ReturnData updateStatus(@RequestBody GoodBanner entity) {
        boolean result = goodBannerService.updateById(entity);
        if (result) {
            return initSuccessResult();
        }
        return initExcetionObjectResult("修改失败！");
    }

    @ApiOperation(value = "分类Banner详情查询")
    @RequestMapping(value = "/queryDetail/{bannerId}", method = RequestMethod.POST)
    public ReturnData<GoodBannerEx> queryDetail(@PathVariable Integer bannerId) {
        GoodBannerEx goodBannerEx = new GoodBannerEx();
        goodBannerEx.setBannerId(bannerId);
        Page<GoodBannerEx> result = goodBannerService.queryListByClassCode(goodBannerEx);
        if(result != null && result.getRecords() != null && !result.getRecords().isEmpty()){
            return initSuccessObjectResult(result.getRecords().get(0));
        }
        return initSuccessResult();
    }

}

