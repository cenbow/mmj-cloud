package com.mmj.user.common.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.OrderGood;
import com.mmj.user.common.model.OrderInfo;
import com.mmj.user.common.model.dto.OrderGoodsDto;
import com.mmj.user.common.model.vo.OrderFinishGoodVo;
import com.mmj.user.common.model.vo.OrderGoodVo;
import com.mmj.user.common.model.vo.OrderInfoGoodVo;
import com.mmj.user.common.model.vo.UserOrderVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderGoodFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderGoodFeignClient {

    /**
     * 根据userid和订单号, 获取这个订单下的所有商品
     * @param orderGoodVo
     * @return
     */
    @RequestMapping(value = "/async/get/orderGoods", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderGoodsDto>> getOrderGoodList(@RequestBody OrderGoodVo orderGoodVo);


    /**
     * 根据userid获取这个用户所有的订单
     * @param userOrderVo
     * @return
     */
    @RequestMapping(value = "/orderInfo/get/allOrderNos", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderInfo>> getUserAllOrderNos(@RequestBody UserOrderVo userOrderVo);

    /**
     * 根据goodsskuid ,订单号, userid,goodsid查询商品关联的信息
     * @param orderInfoGoodVo
     * @return
     */
    @RequestMapping(value = "/orderInfo/get/orderInfoByGood", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderGood>> getOrderInfoByGood(@RequestBody OrderInfoGoodVo orderInfoGoodVo);

    /**
     * 根据订单号和userid获取订单详情
     * @param userOrderVo
     * @return
     */
    @RequestMapping(value = "/orderInfo/get/orderInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<OrderInfo> getOrderInfo(@RequestBody UserOrderVo userOrderVo);

    @RequestMapping(value = "/orderInfo/get/finish/orderGoods", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderGoodsDto>> get(@RequestBody OrderFinishGoodVo orderFinishGoodVo);
}
