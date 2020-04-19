package com.mmj.good.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.good.feigin.dto.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mmj-cloud-active", fallbackFactory = ActiveFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface ActiveFeignClient {
    /**
     * 活动排序信息查询
     *
     * @param activeOrder
     * @return
     */
    @RequestMapping(value = "/activeSort/queryList/", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<ActiveSort>> queryList(@RequestBody ActiveSort activeOrder);

    /**
     * 活动商品id查询
     *
     * @param activeGood
     * @return
     */
    @RequestMapping(value = "/activeGood/queryGoodIds", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<Integer>> queryGoodIds(@RequestBody ActiveGood activeGood);

    /**
     * 十元三件排序规则查询
     *
     * @return
     */
    @RequestMapping(value = "/threeSaleTenner/selectThreeSaleTenner", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<ThreeSaleTenner> selectThreeSaleTenner();

    /**
     * 通过Id查找专题
     *
     * @param topicId
     * @return
     */
    @RequestMapping(value = "/topicInfo/getTopicById/{topicId}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<TopicInfo> getTopicById(@PathVariable(value = "topicId") int topicId);

    /**
     * 商品调用 - 新增一级分类初始化数据
     *
     * @param webShow
     * @return
     */
    @RequestMapping(value = "/homeManagement/webShow/saveWebAhow", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> saveWebAhow(@RequestBody WebShow webShow);


    /**
     * 删除一级分类 - boss后台
     */
    @RequestMapping(value="/homeManagement/webShow/delectByGoodClass/{goodClass}",method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> delectByGoodClass(@PathVariable("goodClass")String goodClass);

    /**
     * 根据id获取详情-boss后台
     */
    @RequestMapping(value="/homeManagement/webShowcase/selectByShowecaseId/{showecaseId}",method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WebShowcaseEx> selectByShowecaseId(@PathVariable("showecaseId") Integer showecaseId);
}
