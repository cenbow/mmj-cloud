package com.mmj.third.kuaidi100.service;


import com.mmj.third.kuaidi100.model.AutoResponse;
import com.mmj.third.kuaidi100.model.PollQueryResponse;
import com.mmj.third.kuaidi100.model.PollResponse;

import java.util.List;

/**
 * @Description: 快递100服务
 * @Auther: KK
 * @Date: 2018/10/13
 */
public interface KuaiDi100Service {

    /**
     * @Description: 实时查询
     * @author: KK
     * @date: 2018/10/15
     * @param: [orderNo, lId, lcCode]
     * @return: com.mmj.ecommerce.util.kuaidi100.entity.PollQueryResponse
     */
    PollQueryResponse query(String orderNo, String lId, String lcCode);

    /**
     * @Description: 通过单号判断所属快递公司
     * @author: KK
     * @date: 2018/10/17
     * @param: [lId]
     * @return: java.util.List<com.mmj.ecommerce.common.kuaidi100.entity.AutoResponse>
     */
    List<AutoResponse> auto(String lId);
}
