package com.mmj.notice.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxMedia;
import com.mmj.notice.service.WxMediaService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 微信素材表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-22
 */
@RestController
@RequestMapping("/wxMedia")
@Slf4j
public class WxMediaController extends BaseController {

    @Autowired
    WxMediaService wxMediaService;

    @ApiOperation("微信素材上传")
    @PostMapping("upload")
    public ReturnData<WxMedia> upload(@RequestBody WxMedia wxMedia){
        wxMedia = wxMediaService.upload(wxMedia);
        return initSuccessObjectResult(wxMedia);
    }

    @ApiOperation("微信素材查询")
    @PostMapping("query")
    public ReturnData<WxMedia> query(@RequestBody WxMedia wxMedia){
        wxMedia = wxMediaService.query(wxMedia);
        return initSuccessObjectResult(wxMedia);
    }

    @ApiOperation("创建小程序里面的公众号二维码素材")
    @PostMapping("createQrcode")
    public ReturnData<WxMedia> createQrcode(@RequestBody String params){
        log.info("wxMedia/createQrcode接受入参========" + params);
        JSONObject jsonObject = JSON.parseObject(params);
        WxMedia wxMedia = wxMediaService.createQrcode(jsonObject);
        return initSuccessObjectResult(wxMedia);
    }

    @ApiOperation("删除大于三天的临时素材")
    @PostMapping("del")
    public ReturnData del(){
        wxMediaService.del();
        return initSuccessResult();
    }
}

