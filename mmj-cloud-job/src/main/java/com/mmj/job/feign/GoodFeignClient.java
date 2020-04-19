package com.mmj.job.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 商品模块服务
 * @auther: KK
 * @date: 2019/8/3
 */
@FeignClient(name = "mmj-cloud-good", fallbackFactory = GoodFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface GoodFeignClient {

    /**
     * 库存-清理过期数据 每1分钟执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/cleanExpire", method = RequestMethod.POST)
    @ResponseBody
    ReturnData cleanExpire();

    /**
     * 库存-清理数据 每个月1号0点执行
     *
     * @return
     */
    @RequestMapping(value = "/async/clean", method = RequestMethod.POST)
    @ResponseBody
    ReturnData clean();

    /**
     * 同步聚水潭-非组合
     *
     * @return
     */
    @RequestMapping(value = "/async/synGoodsStock", method = RequestMethod.POST)
    @ResponseBody
    ReturnData synGoodsStock();

    /**
     * 同步聚水潭-组合
     *
     * @return
     */
    @RequestMapping(value = "/async/synGoodsStock", method = RequestMethod.POST)
    @ResponseBody
    ReturnData synGoodsStockZh();

    /**
     * 库存同步接口
     *
     * @return
     */
    @RequestMapping(value = "/async/synStock1", method = RequestMethod.POST)
    @ResponseBody
    ReturnData synStock1();

}
