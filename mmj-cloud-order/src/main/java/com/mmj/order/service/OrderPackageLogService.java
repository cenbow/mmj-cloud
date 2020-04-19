package com.mmj.order.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.order.model.OrderPackageLog;

import java.util.List;

/**
 * <p>
 * 订单包裹记录表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
public interface OrderPackageLogService extends IService<OrderPackageLog> {
    /**
     * 通过订单号查询子订单列表
     *
     * @param orderNo
     * @return
     */
    List<OrderPackageLog> selectByOrderNo(String orderNo);

    /**
     * 通过包裹号查询
     *
     * @param packageNo
     * @return
     */
    OrderPackageLog selectByPackageNo(String packageNo);
}
