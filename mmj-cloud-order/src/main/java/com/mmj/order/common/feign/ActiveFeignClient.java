package com.mmj.order.common.feign;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.ActiveGoodOrder;
import com.mmj.common.model.active.ActiveGoodStoreResult;
import com.mmj.common.model.active.RechargeVo;
import com.mmj.order.common.model.ActiveGood;
import com.mmj.order.common.model.ActiveGoodStore;
import com.mmj.order.common.model.LotteryConf;
import com.mmj.order.common.model.RelayInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mmj-cloud-active", fallbackFactory = ActiveFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface ActiveFeignClient {

    @RequestMapping(value = "/async/getLottery/{id}", method = RequestMethod.POST)
    @ResponseBody
    LotteryConf getLotteryById(@PathVariable("id") Integer id);


    @RequestMapping(value = "/api/relayLottery/getRelayInfo", method = RequestMethod.GET)
    @ResponseBody
    RelayInfo getRelayInfo(@RequestParam("id") Integer id);

    @RequestMapping(value = "activeGood/orderCheck", method = RequestMethod.POST)
    ReturnData<ActiveGoodStoreResult> orderCheck(@RequestBody ActiveGoodStore activeGoodStore);

    @RequestMapping(value = "activeGood/querySale", method = RequestMethod.POST)
    ReturnData<List<ActiveGood>> queryDetail(@RequestBody ActiveGood activeGood);


    /**
     * 活动商品列表查询
     */
    @RequestMapping(value = "/activeGood/queryBaseList", method = RequestMethod.POST)
    ReturnData<Page<ActiveGood>> queryBaseList(@RequestBody ActiveGood activeGood);

    /**
     * 订单查询商品
     *
     * @param activeGoodOrder
     * @return
     */
    @RequestMapping(value = "/activeGood/queryOrderGood", method = RequestMethod.POST)
    ReturnData<List<ActiveGood>> queryOrderGood(@RequestBody ActiveGoodOrder activeGoodOrder);

    /**
     * 话费充值
     *
     * @param rechargeVo
     * @return
     */
    @RequestMapping(value = "/async/recharge", method = RequestMethod.POST)
    ReturnData<List<ActiveGood>> recharge(@RequestBody RechargeVo rechargeVo);

    /**
     * 修改用户首页版本号
     *
     * @param userIdentity
     * @return
     */
    @PostMapping("/async/updateIndexCode/{userIdentity}")
    ReturnData<Object> updateIndexCode(@PathVariable("userIdentity") String userIdentity);
}
