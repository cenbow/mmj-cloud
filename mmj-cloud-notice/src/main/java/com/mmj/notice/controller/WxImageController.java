package com.mmj.notice.controller;

import com.alibaba.fastjson.JSON;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.service.WxImageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 绘图服务
 */
@RestController
@RequestMapping("wxImage")
public class WxImageController extends BaseController {

    @Autowired
    WxImageService wxImageService;

    /**
     * 创建图片
     * @param params
     * @return
     */
    @ApiOperation("创建图片")
    @PostMapping("createImage")
    public ReturnData<String> createImage(@RequestBody String params) {
        String image = wxImageService.createImage(JSON.parseObject(params));
        return initSuccessObjectResult(image);
    }

    @ApiOperation("创建小程序码")
    @PostMapping("createQrcodeM")
    public ReturnData<String> createQrcodeM(@RequestBody String params){
        String image = wxImageService.createQrcodeM(JSON.parseObject(params));
        return initSuccessObjectResult(image);
    }

    @ApiOperation("创建公众号码")
    @PostMapping("createQrcode")
    public ReturnData<String> createQrcode(@RequestBody String params){
        String image = wxImageService.createQrcode(JSON.parseObject(params));
        return initSuccessObjectResult(image);
    }

    @ApiOperation("会员返现合成图")
    @RequestMapping("memberFx")
    public ReturnData<String> memberFx(){
        String image = wxImageService.memberFx();
        return initSuccessObjectResult(image);
    }

    @ApiOperation("会员商品推荐合成图")
    @RequestMapping("memberRecmond/{id}")
    public ReturnData<String> memberRecmond(@PathVariable("id") String id){
        String image = wxImageService.memberRecmond(id);
        return initSuccessObjectResult(image);
    }
}
