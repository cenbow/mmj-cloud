package com.mmj.job.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 活动模块服务
 * @auther: KK
 * @date: 2019/6/19
 */
@FeignClient(name = "mmj-cloud-active", fallbackFactory = ActiveFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface ActiveFeignClient {

    /**
     * 自动开奖 (一分钟调用一次/有效期30秒/当前任务超时后后续任务往后推)
     *
     * @return
     */
    @RequestMapping(value = "/async/autoDrawLottery", method = RequestMethod.POST)
    @ResponseBody
    ReturnData autoDrawLottery();

    /**
     * 清理过期活动，每小时执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/queryTimeOut", method = RequestMethod.POST)
    @ResponseBody
    ReturnData queryTimeOut();

    /**
     * 修改当前期次，每天23：59：59 执行一次
     *
     * @return
     */
//    @RequestMapping(value = "/seckill/seckillInfo/changePriod", method = RequestMethod.POST)
//    @ResponseBody
//    ReturnData changePriod();

    /**
     * 秒杀活动开启提醒，每小时 执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/seckillRemind", method = RequestMethod.POST)
    @ResponseBody
    ReturnData seckillRemind();

    /**
     * 搜索-热销前十商品更新：每周3中午12点
     *
     * @return
     */
    @RequestMapping(value = "/async/resetHotSellGoods", method = RequestMethod.POST)
    @ResponseBody
    ReturnData resetHotSellGoods();

    /**
     * 修改站外活动，每天23：59：59 执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/queryTimeOutOther", method = RequestMethod.POST)
    @ResponseBody
    ReturnData queryTimeOutOther();

    /**
     * 修改站内当前期次，每天23：59：59 执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/changePriodIn", method = RequestMethod.POST)
    @ResponseBody
    ReturnData changePriod();

    /**
     * 减少虚拟库存，每10秒 执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/decActiveVirtualIn", method = RequestMethod.POST)
    @ResponseBody
    ReturnData decActiveVirtual1();

    /**
     * 减少虚拟库存，每10秒 执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/decActiveVirtualOther", method = RequestMethod.POST)
    @ResponseBody
    ReturnData decActiveVirtual2();

    /**
     * 十元三件红包裂变 - 定时任务,2小时内,助力好友未支付成功, 设置为已失效
     *
     * @return
     */
    @RequestMapping(value = "/threeSaleTenner/threeSaleFission/updateInvalid", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> updateInvalid();

    /**
     * 转盘活动 - 给最近活跃用户增加买买币 规则：1分钟执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/prizewheels/autoIncrement", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> autoIncrement();

    /**
     * 更新会员日优惠券发放数量 规则：每周三凌晨0点更新
     *
     * @return
     */
    @RequestMapping(value = "/async/coupon/member/sendNum", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Boolean> updateMemberDaySendTotalCount();

    /**
     * 话费商品-每天10点重置发放数量
     *
     * @return
     */
    @RequestMapping(value = "/async/recharge/restartTask", method = RequestMethod.POST)
    @ResponseBody
    ReturnData restartTask();

    /**
     * 话费商品-每一分钟统计发放数量，并持久化到数据
     *
     * @return
     */
    @RequestMapping(value = "/async/recharge/statSendNumber", method = RequestMethod.POST)
    @ResponseBody
    ReturnData statSendNumber();

    @RequestMapping(value = "/async/prizewheels/notice", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> sendSignNoticeForPrizewheelsUser();
}
