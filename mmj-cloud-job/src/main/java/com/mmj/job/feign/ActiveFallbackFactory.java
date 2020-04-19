package com.mmj.job.feign;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * @description: 活动模块熔断
 * @auther: KK
 * @date: 2019/6/19
 */
@Component
@Slf4j
public class ActiveFallbackFactory implements FallbackFactory<ActiveFeignClient> {

    private final ReturnData returnData = new ReturnData(SecurityConstants.EXCEPTION_CODE, "fail");

    @Override
    public ActiveFeignClient create(Throwable throwable) {
        log.error("====> {}", throwable.getMessage());
        return new ActiveFeignClient() {
            @Override
            public ReturnData autoDrawLottery() {
                return returnData;
            }

            @Override
            public ReturnData queryTimeOut() {
                return returnData;
            }

//            @Override
//            public ReturnData changePriod() {
//                return returnData;
//            }

            @Override
            public ReturnData<Object> updateInvalid() {
                return returnData;
            }

            @Override
            public ReturnData seckillRemind() {
                return returnData;
            }

            @Override
            public ReturnData resetHotSellGoods() {
                return returnData;
            }

            @Override
            public ReturnData queryTimeOutOther() {
                return returnData;
            }

            @Override
            public ReturnData changePriod() {
                return returnData;
            }

            @Override
            public ReturnData decActiveVirtual1() {
                return returnData;
            }

            @Override
            public ReturnData decActiveVirtual2() {
                return returnData;
            }

            @Override
            public ReturnData<Object> autoIncrement() {
                return returnData;
            }

            @Override
            public ReturnData<Boolean> updateMemberDaySendTotalCount() {
                return returnData;
            }

            @Override
            public ReturnData restartTask() {
                return returnData;
            }

            @Override
            public ReturnData statSendNumber() {
                return returnData;
            }

			@Override
			public ReturnData<Object> sendSignNoticeForPrizewheelsUser() {
				return returnData;
			}
        };
    }
}
