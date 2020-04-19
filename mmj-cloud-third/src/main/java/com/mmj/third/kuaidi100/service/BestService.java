package com.mmj.third.kuaidi100.service;


import com.mmj.third.kuaidi100.model.PollQueryResponse;

/**
 * @description: 百世快递服务
 * @auther: KK
 * @date: 2019/6/4
 */
public interface BestService {
    /**
     * @Description: 实时查询
     * @author: KK
     * @date: 2018/10/15
     * @param: [orderNo, lId]
     * @return: com.mmj.ecommerce.util.kuaidi100.entity.PollQueryResponse
     */
    PollQueryResponse query(String orderNo, String lId);
}
