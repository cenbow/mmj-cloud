package com.mmj.notice.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.notice.model.OfficialAccountUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "mmj-cloud-oauth", fallbackFactory = OauthFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OauthFeignClient {

    /**
     * 公众号关注用户保存
     * @param officialAccountUser
     * @return
     */
    @RequestMapping(value = "/wx/user/publicSave", method = RequestMethod.POST)
    @ResponseBody
    ReturnData savePublic(@RequestBody OfficialAccountUser officialAccountUser);

    /**
     * 取消关注公众号
     * @param openId
     * @return
     */
    @RequestMapping(value="/wx/user/unfollow/{openId}", method=RequestMethod.POST)
    @ResponseBody
    ReturnData unsubUser(@PathVariable(value = "openId") String openId);

}
