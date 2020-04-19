package com.mmj.active.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.model.OrderGroup;
import com.mmj.active.common.model.OrderGroupJoin;
import com.mmj.active.common.model.OrderInfo;
import com.mmj.active.common.model.dto.DecrGoodNum;
import com.mmj.active.common.model.dto.OrderSaveVo;
import com.mmj.active.common.model.dto.SaveOrderDto;
import com.mmj.active.relayLottery.model.dto.GroupInfoDto;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.UserLotteryDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderFeignClient {

    @RequestMapping(value = "/order/getByOrderNo", method = RequestMethod.GET)
    @ResponseBody
    OrderInfo getByOrderNo(@RequestParam("orderNo") String orderNo);

    @RequestMapping(value = "/order/orderGroup/completedGroupList", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderGroup>> completedGroupList(@RequestBody OrderGroup orderGroup);

    @RequestMapping(value = "/order/orderGroup/completedGroupCount", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Integer> completedGroupCount(@RequestBody OrderGroup orderGroup);

    @RequestMapping(value = "/order/orderGroup/groupInfo", method = RequestMethod.POST)
    @ResponseBody
    GroupInfoDto groupInfo(@RequestParam("userid") Long userid,
                           @RequestParam("groupNo") String groupNo,
                           @RequestParam("orderNo") String orderNo);


    @RequestMapping(value = "/order/orderGroup/redPackList", method = RequestMethod.POST)
    @ResponseBody
    List<Map<String, Object>> getRedPackList();

    /**
     * 扣减库存
     *
     * @param decrGoodNum
     * @return
     */
    @RequestMapping(value = "/async/decr", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Boolean> decr(@RequestBody DecrGoodNum decrGoodNum);


    @RequestMapping(value = "/order/orderGroup/groupInfo", method = RequestMethod.POST)
    OrderGroup getGroupInfo(@RequestBody OrderGroup group);


    @RequestMapping(value = "/orderInfo/getForFeign/{orderNo}", method = RequestMethod.POST)
    OrderInfo getForFeign(@PathVariable("orderNo") String orderNo);

    @RequestMapping(value = "/order/orderGroup/groupJoin", method = RequestMethod.POST)
    List<OrderGroupJoin> getGroupJoin(OrderGroupJoin join);

    @RequestMapping(value = "/orderInfo/getLotteryId/{groupNo}", method = RequestMethod.POST)
    Integer getLotteryId(@PathVariable("groupNo") String groupNo);

    /**
     * 下单
     *
     * @param orderSaveVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/batch/produce", method = RequestMethod.POST)
    ReturnData<SaveOrderDto> saveOrder(OrderSaveVo orderSaveVo);

    /**
     * 判断是否新用户
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "orderInfo/check/oldUser", method = RequestMethod.POST)
    ReturnData<Boolean> checkNewUser(@RequestBody Map<String, Object> map);


    /**
     * 修改订单状态并上传聚水潭
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/async/toBeDelivered/{orderNo}", method = RequestMethod.POST)
    boolean toBeDelivered(@PathVariable("orderNo") String orderNo);

    /**
     * 查询用户收货的手机号
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/orderInfo/logistics/getMobile", method = RequestMethod.POST)
    String getMobile(@RequestBody JSONObject jsonObject);

    @RequestMapping(value = "/order/orderGroup/getJoinUser/{lotteryId}", method = RequestMethod.POST)
    List<UserLotteryDto> getJoinUser(@PathVariable("lotteryId") Integer lotteryId);

    @RequestMapping(value = "/async/getLotteryWaitPay/{busId}", method = RequestMethod.POST)
    List<OrderInfo> getLotteryWaitPay(@PathVariable("busId") Integer busId);

    /**
     * 扣减库存
     *
     * @param decrGoodNum
     * @return
     */
    @RequestMapping(value = "/async/decr", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Boolean> decrAsync(@RequestBody DecrGoodNum decrGoodNum);

    @RequestMapping(value = "/async/get/orderGoodImg/{orderNo}", method = RequestMethod.POST)
    String orderGoodImg(@PathVariable("orderNo") String orderNo);

    @RequestMapping(value = "/async/getAddress/{orderNo}", method = RequestMethod.POST)
    String getLogistics(@PathVariable("orderNo") String orderNo);
}
