package com.mmj.notice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.common.mq.WxMessageProduce;
import com.mmj.notice.service.WxMessageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.util.Map;

@RestController
@RequestMapping("/wxmsg")
@Slf4j
@Api(value = "微信客服消息")
public class WxMessageController extends BaseController {

    @Autowired
    WxMessageService wxMessageService;

    @Autowired
    WxMessageProduce wxMessageProduce;

    @ApiOperation(value="接受公众号的客服消息")
    @RequestMapping(value = "accept", produces = "application/xml")
    public Object accept(HttpServletRequest request) {
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        if(isGet){ //校验地址参数是否正确
            Map<String, String[]> parameterMap = request.getParameterMap();
            if(wxMessageService.checkSignature(parameterMap)){
                return parameterMap.get("echostr")[0];
            };
        }
        //获取消息
        try {
            ServletInputStream inputStream = request.getInputStream();
            wxMessageService.transform(inputStream);
            if(null != inputStream){
                inputStream.close();
            }
        } catch (IOException e) {
            log.error("公众号客服消息获取流失败" , new Throwable(e));
        }
        return null;
    }


    @ApiOperation(value="接受小程序的客服消息")
    @RequestMapping(value = "acceptM", produces = "application/xml")
    public Object acceptM(HttpServletRequest request) {
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        if(isGet){ //校验地址参数是否正确
            Map<String, String[]> parameterMap = request.getParameterMap();
            if(wxMessageService.checkSignature(parameterMap)){
                return parameterMap.get("echostr")[0];
            };
        }
        //获取消息
        try {
            ServletInputStream inputStream = request.getInputStream();
            wxMessageService.transformM(request.getInputStream());
            if(null != inputStream){
                inputStream.close();
            }
        } catch (IOException e) {
            log.error("小程序客服消息获取流失败"  ,new Throwable(e));
        }
        return null;
    }

    @ApiOperation(value="发送公众号模板消息")
    @RequestMapping(value="sendTemplate",method=RequestMethod.POST)
    public ReturnData<JSONObject> sendTemplate(@RequestBody String msg){
        JSONObject msgJson = JSON.parseObject(msg);
        JSONObject msgResult = wxMessageService.sendTemplate(msgJson);
        return initSuccessObjectResult(msgResult);
    }

    @ApiOperation(value="发送小程序模板消息")
    @RequestMapping(value="sendTemplateM",method=RequestMethod.POST)
    public ReturnData<JSONObject> sendTemplateM(@RequestBody String msg){
        JSONObject msgJson = JSON.parseObject(msg);
        JSONObject msgResult = wxMessageService.sendTemplateM(msgJson);
        return initSuccessObjectResult(msgResult);
    }

    @ApiOperation(value="发送客服消息(包含小程序和公众号)")
    @RequestMapping(value="sendCustom",method=RequestMethod.POST)
    public ReturnData<JSONObject> sendCustom(@RequestBody String msg){
        JSONObject msgJson = JSON.parseObject(msg);
        JSONObject msgResult = wxMessageService.sendCustom(msgJson);
        return initSuccessObjectResult(msgResult);
    }

    @ApiOperation(value="快速发送短信,一般用作验证码")
    @RequestMapping(value="sendSms",method=RequestMethod.POST)
    public ReturnData<Object> sendSms(@RequestBody String params){
        JSONObject msgJson = JSON.parseObject(params);
        return initSuccessObjectResult(wxMessageService.sendSms(msgJson));
    }

    @ApiOperation(value="慢速发送短信,一般用作营销短信")
    @RequestMapping(value="sendSmsl",method=RequestMethod.POST)
    public ReturnData<Object> sendSmsl(@RequestBody String params){
        JSONObject msgJson = JSON.parseObject(params);
        return initSuccessObjectResult(wxMessageService.sendSmsl(msgJson));
    }
}
