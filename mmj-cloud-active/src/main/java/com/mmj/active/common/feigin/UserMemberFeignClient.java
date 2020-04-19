package com.mmj.active.common.feigin;

import com.mmj.active.common.model.UserMember;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "mmj-cloud-user", fallbackFactory = UserMemberFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface UserMemberFeignClient {

     @ResponseBody
     @RequestMapping(value="/member/query/{userId}", method= RequestMethod.POST)
     ReturnData<UserMember> queryUserMemberInfoByUserId(@PathVariable("userId") Long userId);
}
