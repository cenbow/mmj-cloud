package com.mmj.active.common.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.third.recharge.RechargeDetailsVo;
import com.mmj.common.model.third.recharge.RechargeDto;
import com.mmj.common.model.third.recharge.RechargeVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "mmj-cloud-third", fallbackFactory = ThirdFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface ThirdFeignClient {
    /**
     * 话费充值服务-订单详情
     *
     * @param rechargeDetailsVo
     * @return
     */
    @RequestMapping(value = "/recharge/details", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<RechargeDto> details(@RequestBody RechargeDetailsVo rechargeDetailsVo);

    /**
     * 话费充值服务-话费充值
     *
     * @param rechargeVo
     * @return
     */
    @RequestMapping(value = "/recharge/produce", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<RechargeDto> recharge(@RequestBody RechargeVo rechargeVo);
}
