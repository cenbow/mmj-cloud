package com.mmj.order.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.model.OrderPackage;
import com.mmj.order.model.vo.ConsignessVo;
import com.mmj.order.model.vo.OrderAddressVo;
import com.mmj.order.model.vo.OrderLogisticsVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单快递信息表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
public interface OrderLogisticsService extends IService<OrderLogistics> {

    /**
     * 下单时保存收货人信息
     *
     * @param orderInfo
     * @param orderPackage
     * @param consignessVo
     */
    OrderLogistics saveOrderLogistics(OrderInfo orderInfo, OrderPackage orderPackage, ConsignessVo consignessVo);

    /**
     * 通过订单号查询一条快递信息
     *
     * @param orderNo
     * @return
     */
    OrderLogistics selectOneByOrderNo(String orderNo);

    /**
     * 通过包裹号查询一条快递信息
     *
     * @param orderNo
     * @param packageNo
     * @return
     */
    OrderLogistics selectOneByPackageNo(String orderNo, String packageNo);

    /**
     * 通过订单号获取快递信息
     *
     * @param orderNo
     * @return
     */
    List<OrderLogistics> selectByOrderNo(String orderNo);

    /**
     * 通过包裹号获取快递信息
     *
     * @param orderNo
     * @param packageNo
     * @return
     */
    List<OrderLogistics> selectByPackageNo(String orderNo, String packageNo);

    /**
     * 保存订单收件人信息
     *
     * @param orderLogisticsVo
     */
    void updateLogistics(OrderLogisticsVo orderLogisticsVo);

    /**
     * 通过订单号获取收件人信息
     *
     * @param orderNo
     * @return
     */
    List<OrderLogistics> getOrderLogistics(String orderNo, Long userId);


    /**
     * 修改收件人的地址信息
     *
     * @param orderAddressVo
     * @return
     */
    boolean updateOrderAddress(OrderAddressVo orderAddressVo);


    Map<String, Object> getUser(OrderLogistics logistics);
}
