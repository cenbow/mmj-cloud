package com.mmj.order.common.feign;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.ActiveGoodOrder;
import com.mmj.common.model.active.ActiveGoodStoreResult;
import com.mmj.common.model.active.RechargeVo;
import com.mmj.order.common.model.ActiveGood;
import com.mmj.order.common.model.ActiveGoodStore;
import com.mmj.order.common.model.LotteryConf;
import com.mmj.order.common.model.RelayInfo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
public class ActiveFallbackFactory implements FallbackFactory<ActiveFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(ActiveFallbackFactory.class);

    @Override
    public ActiveFeignClient create(Throwable cause) {
        logger.info("FallbackFactory error message is {},case:{}", cause.getMessage(), cause);
        return new ActiveFeignClient() {
            @Override
            public LotteryConf getLotteryById(Integer id) {
                throw new BusinessException("查询抽奖活动接口报错," + cause.getMessage());
            }

            @Override
            public RelayInfo getRelayInfo(Integer id) {
                throw new BusinessException("查询接力购抽奖活动接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<ActiveGoodStoreResult> orderCheck(@RequestBody ActiveGoodStore activeGoodStore) {
                throw new BusinessException("调用活动下单验证报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<ActiveGood>> queryDetail(@RequestBody ActiveGood activeGood) {
                throw new BusinessException("调用查询活动商品报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Page<ActiveGood>> queryBaseList(ActiveGood activeGood) {
                throw new BusinessException("调用活动商品列表查询，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<ActiveGood>> queryOrderGood(ActiveGoodOrder activeGoodOrder) {
                throw new BusinessException("订单查询商品错误，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<ActiveGood>> recharge(RechargeVo rechargeVo) {
                throw new BusinessException("话费充值错误，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> updateIndexCode(String userIdentity) {
                throw new BusinessException("修改用户首页版本号错误，" + cause.getMessage(), 500);
            }
        };
    }
}
