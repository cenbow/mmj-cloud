package com.mmj.good.feigin;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.good.feigin.dto.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
public class ActiveFallbackFactory implements FallbackFactory<ActiveFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(ActiveFallbackFactory.class);

    @Override
    public ActiveFeignClient create(Throwable cause) {
        logger.info("Good-ActiveFallbackFactory error message is {}", cause.getMessage());
        return new ActiveFeignClient() {
            @Override
            public ReturnData<List<ActiveSort>> queryList(@RequestBody ActiveSort activeOrder) {
                throw new BusinessException("调用查询活商品排序接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<Integer>> queryGoodIds(@RequestBody ActiveGood activeGood) {
                throw new BusinessException("调用活动商品id查询接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<ThreeSaleTenner> selectThreeSaleTenner() {
                throw new BusinessException("调用十元三件排序规则查询接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<TopicInfo> getTopicById(@PathVariable int topicId){
                throw new BusinessException("调用专题查询接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> saveWebAhow(@RequestBody WebShow webShow) {
                throw new BusinessException("调用新增一级分类初始化数据接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> delectByGoodClass(@PathVariable("goodClass")String goodClass) {
                throw new BusinessException("调用删除一级分类 - boss后台接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<WebShowcaseEx> selectByShowecaseId(@PathVariable("showecaseId") Integer showecaseId) {
                throw new BusinessException("根据id获取详情-boss后台," + cause.getMessage(), 500);
            }
        };
    }
}
