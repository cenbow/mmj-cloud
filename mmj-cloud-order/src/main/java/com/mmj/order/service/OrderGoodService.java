package com.mmj.order.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.order.common.model.dto.OrderCheckDto;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.dto.OrderGoodsDto;
import com.mmj.order.model.vo.OrderSavePackageVo;
import com.mmj.order.model.vo.OrderSaveVo;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单商品表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
public interface OrderGoodService extends IService<OrderGood> {

    /**
     * 商品封装及商品库存校验
     *
     * @param orderSaveVo
     * @param groupOrderCheckDto
     * @return
     */
    List<OrderGood> checkOrderGoods(OrderSaveVo orderSaveVo, OrderCheckDto groupOrderCheckDto);

    /**
     * 保存订单商品信息
     *
     * @param orderInfo
     * @param orderGoodList
     */
    void saveOrderGoods(OrderInfo orderInfo, List<OrderGood> orderGoodList);

    /**
     * 通过订单号查询商品信息
     *
     * @param orderNo
     * @param userId
     * @return
     */
    List<OrderGoodsDto> selectByOrderNo(String orderNo, Long userId);

    /**
     * 通过订单号查询商品信息
     *
     * @param orderNo
     * @return
     */
    List<OrderGood> selectByOrderNo(String orderNo);

    /**
     * 通过包裹号查询商品信息
     *
     * @param orderNo
     * @param packageNo
     * @return
     */
    List<OrderGood> selectByPackageNo(String orderNo, String packageNo);

    /**
     * 订单拆单是-拆分金额
     *
     * @param orderInfo
     * @param orderGoodList
     */
    void unpickGoodsAmount(OrderInfo orderInfo, List<OrderGood> orderGoodList);

    /**
     * 占用SKU库存记录
     *
     * @param orderNo
     * @param orderType
     * @param createTime
     * @param orderGoodList
     */
    void occupyStock(String orderNo, Integer orderType, Date createTime, List<OrderGood> orderGoodList);

    /**
     * 校验是否占用库存
     *
     * @param orderNo
     * @return
     */
    boolean checkOccupyTime(String orderNo, Integer orderType);

    /**
     * 扣减SKU库存记录
     *
     * @param orderNo
     * @param orderType
     * @param orderGoodList
     */
    void deductStock(String orderNo, Integer orderType, List<OrderGood> orderGoodList);

    /**
     * 释放SKU库存记录
     *
     * @param orderNo
     * @param orderType
     * @param orderGoodList
     */
    void relieveStock(String orderNo, Integer orderType, List<OrderGood> orderGoodList);

    /**
     * 回退SKU库存记录
     *
     * @param orderNo
     * @param orderType
     * @param orderGoodList
     */
    void rollbackStock(String orderNo, Integer orderType, List<OrderGood> orderGoodList);
}
