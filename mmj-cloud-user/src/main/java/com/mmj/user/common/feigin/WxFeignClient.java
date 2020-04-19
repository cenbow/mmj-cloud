package com.mmj.user.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.dto.WxMediaDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mmj-cloud-notice", fallbackFactory = WxFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface WxFeignClient {

    /**
     * 微信素材查询
     * @param wxMedia
     * @return
     */
    @RequestMapping(value = "/wxMedia/query", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WxMediaDto> query(@RequestBody WxMediaDto wxMedia);

    /**
     * 发送客服消息(包含小程序和公众号)
     * @param msg
     * @return
     */
    @RequestMapping(value = "/wxmsg/sendCustom", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<JSONObject> sendCustom(@RequestBody String msg);

    /**
     * 创建小程序里面的公众号二维码素材
     * @param msg
     * @return
     */
    @RequestMapping(value = "/wxMedia/createQrcode", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WxMediaDto> createQrcode(@RequestBody String msg);

    /**
     * 微信素材上传
     * @param wxMedia
     * @return
     */
    @RequestMapping(value = "/wxMedia/upload", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WxMediaDto> upload(@RequestBody WxMediaDto wxMedia);

    /**
     * 给用户打标签
     * @param tagParams
     * @return
     */
    @RequestMapping(value = "/wxTag/doTag", method = RequestMethod.POST)
    @ResponseBody
    ReturnData doTag(@RequestBody String tagParams);
}
