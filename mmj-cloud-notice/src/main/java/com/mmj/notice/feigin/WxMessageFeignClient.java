package com.mmj.notice.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.notice.model.OfficialAccountUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "mmj-cloud-user", fallbackFactory = WxMessageFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface WxMessageFeignClient {
    /**
     * 根据wxNo查询微信app配置
     * @param wxNo
     * @return
     */
    @RequestMapping(value = "/wx/config/queryByWxNo/{wxNo}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WxConfig> queryByWxNo(@PathVariable(value = "wxNo")String wxNo);

    /**
     * 根据appid查询微信app配置
     * @param appId
     * @return
     */
    @RequestMapping(value = "/wx/config/queryByAppId/{appId}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WxConfig> queryByAppId(@PathVariable(value = "appId") String appId);

    /**
     * 查询会员配置
     * @return
     */
    @RequestMapping(value = "/member/config", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> queryMemberConfig();

    /**
     * 根据id查询推荐详情 - 小程序
     * @param recommendId
     * @return
     */
    @RequestMapping(value="/recommend/userRecommend/selectByRecommendId/{recommendId}", method=RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> selectByRecommendId(@PathVariable(value = "recommendId") String recommendId);
}
