package com.mmj.notice.controller;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxMedia;
import com.mmj.notice.service.WxTagService;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 微信标签控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-22
 */
@RestController
@RequestMapping("/wxTag")
public class WxTagController extends BaseController {

    @Autowired
    WxTagService wxTagService;

    @ApiOperation("给用户打标签")
    @RequestMapping(value="doTag",method=RequestMethod.POST)
    public ReturnData upload(@RequestBody String tagParams){
        wxTagService.doTag(JSONObject.parseObject(tagParams));
        return initSuccessResult();
    }

    @ApiOperation("查询公众号对应的标签")
    @RequestMapping("query/{appid}")
    public ReturnData<Object> query(@PathVariable String appid){
        JSONObject tagResult = wxTagService.query(appid);
        return initSuccessObjectResult(tagResult);
    }
}
