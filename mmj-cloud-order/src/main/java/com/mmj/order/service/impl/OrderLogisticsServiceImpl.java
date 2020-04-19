package com.mmj.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mmj.common.constants.OrderStatus;
import com.mmj.order.mapper.OrderLogisticsMapper;
import com.mmj.order.mapper.OrderPackageLogMapper;
import com.mmj.order.model.*;
import com.mmj.order.model.vo.ConsignessVo;
import com.mmj.order.model.vo.OrderAddressVo;
import com.mmj.order.model.vo.OrderLogisticsVo;
import com.mmj.order.service.OrderInfoService;
import com.mmj.order.service.OrderLogisticsService;
import com.mmj.order.service.OrderPackageService;
import com.mmj.order.utils.MessageUtils;
import com.mmj.order.utils.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 订单快递信息表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@Service
@Slf4j
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements OrderLogisticsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderLogisticsMapper orderLogisticsMapper;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderPackageService orderPackageService;

    @Autowired
    private OrderPackageLogMapper orderPackageLogMapper;

    @Autowired
    private OrderNoUtils orderNoUtils;

    @Autowired
    private MessageUtils messageUtils;

    /**
     * 修改订单收件信息
     *
     * @param orderLogisticsVo
     */
    @Override
    public void updateLogistics(OrderLogisticsVo orderLogisticsVo) {
        logger.info("已经进入订单--快递信息保存中,{}", orderLogisticsVo);
        String packageNo = orderLogisticsVo.getPackageNo();
        String orderNo;
        if (packageNo.indexOf("-") != -1) {
            orderNo = packageNo.split("-")[0];
        } else {
            OrderPackage orderPackage = orderPackageService.selectByPackageNo(packageNo);
            if (Objects.nonNull(orderPackage)) {
                orderNo = orderPackage.getOrderNo();
            } else {
                log.error("=> 聚水潭发货错误 result:{}", JSONObject.toJSONString(orderLogisticsVo));
                return;
            }
        }
        OrderInfo orderInfo = orderInfoService.getByOrderNo(orderNo);
        if (orderInfo == null) {
            return;
        }
        OrderLogistics orderLogistics = new OrderLogistics();
        OrderPackageLog orderPackageLog = new OrderPackageLog();
        if (OrderStatus.TO_BE_DELIVERED.getStatus() == orderInfo.getOrderStatus()) {
            orderInfoService.updateOrderStatus(OrderStatus.PENDING_RECEIPT.getStatus(), orderInfo.getOrderNo());
        }

        orderLogistics.setOrderId(orderInfo.getOrderId());
        orderLogistics.setOrderNo(orderInfo.getOrderNo());
        orderPackageLog.setOrderId(orderInfo.getOrderId());
        orderPackageLog.setOrderNo(orderInfo.getOrderNo());
        orderLogistics.setPackageNo(orderLogisticsVo.getPackageNo());
        orderLogistics.setCompanyName(orderLogisticsVo.getCompanyName());
        orderLogistics.setCompanyCode(orderLogisticsVo.getCompanyCode());
        orderLogistics.setLogisticsNo(orderLogisticsVo.getLogisticsNo());
        orderLogistics.setSendTime(orderLogisticsVo.getSendTime());
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("PACKAGE_NO", orderLogisticsVo.getPackageNo());
        orderLogisticsMapper.update(orderLogistics, entityWrapper);
        logger.info("已经进入订单--快递信息修改结束!");
        // 物流日志同步
        orderPackageLog.setPackageNo(orderLogisticsVo.getPackageNo());
        orderPackageLog.setLogisticsNo(orderLogisticsVo.getLogisticsNo());
        orderPackageLog.setLogisticsName(orderLogisticsVo.getCompanyName());
        orderPackageLog.setLogisticAction("0");
        orderPackageLogMapper.insert(orderPackageLog);
        this.sendLogiscsMsg(orderInfo, orderLogisticsVo);
    }

    private void sendLogiscsMsg(OrderInfo orderInfo, OrderLogisticsVo orderLogisticsVo) {
        if (!"MIN".equals(orderInfo.getOrderSource())) return;
        String mainOrderNo = orderInfo.getOrderNo();
        if (StringUtils.isEmpty(mainOrderNo)) {
            log.info("发送物流模板消息时,查询不到订单号:{}", mainOrderNo);
            return;
        }
        OrderLogistics logistics = this.getOrderLogistics(mainOrderNo);
        if (null == logistics) {
            log.info("查询不到订单的物流信息:{}", mainOrderNo);
            return;
        }
//        String cozyMsg = "您的订单已发货，收货地址：" + logistics.getProvince() + logistics.getCity()
//                + logistics.getArea() + logistics.getConsumerAddr() + "。点击可查看物流详情";
        String cozyMsg = "您的订单已发货，点击可查询订单详情。";
        LogisticsShipDto logisticsShipDto = new LogisticsShipDto(orderInfo.getCreaterId(),
                orderInfo.getOrderNo(), orderLogisticsVo.getLogisticsNo(), orderLogisticsVo.getCompanyName(),
                orderLogisticsVo.getSendTime(), cozyMsg);
        messageUtils.logisticsShip(logisticsShipDto);
    }


    private OrderLogistics getOrderLogistics(String orderNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderLogistics queryOrderLogistics = new OrderLogistics();
        queryOrderLogistics.setOrderNo(orderNo);
        return orderLogisticsMapper.selectOne(queryOrderLogistics);
    }


    /***
     *   通过订单号查询当前收件人信息
     * @param orderNo
     * @param userId
     * @return
     */
    @Override
    public List<OrderLogistics> getOrderLogistics(String orderNo, Long userId) {
        orderNoUtils.shardingKey(orderNo);
        OrderLogistics queryOrderLogistics = new OrderLogistics();
        queryOrderLogistics.setOrderNo(orderNo);
        EntityWrapper<OrderLogistics> orderLogisticsEntityWrapper = new EntityWrapper<>(queryOrderLogistics);
        return orderLogisticsMapper.selectList(orderLogisticsEntityWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderLogistics saveOrderLogistics(OrderInfo orderInfo, OrderPackage orderPackage, ConsignessVo consignessVo) {
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderNo(orderInfo.getOrderNo());
        orderLogistics.setOrderId(orderInfo.getOrderId());
        orderLogistics.setPackageNo(orderPackage.getPackageNo());
        orderLogistics.setCountry("china");
        orderLogistics.setProvince(consignessVo.getProvince());
        orderLogistics.setCity(consignessVo.getCity());
        orderLogistics.setArea(consignessVo.getArea());
        orderLogistics.setConsumerAddr(consignessVo.getConsumerAddr());
        orderLogistics.setConsumerName(consignessVo.getConsumerName());
        orderLogistics.setConsumerMobile(consignessVo.getConsumerMobile());
        orderLogistics.setCreaterId(orderInfo.getCreaterId());
        boolean result = insert(orderLogistics);
        Assert.isTrue(result, "保存订单收货信息失败");
        logger.info("当前用户:{},保存收货信息成功,订单号为:{},订单ID为:{}", orderLogistics.getCreaterId(), orderLogistics.getOrderNo(), orderLogistics.getOrderId());
        return orderLogistics;
    }

    /**
     * 修改收件人的地址信息
     *
     * @param orderAddressVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderAddress(OrderAddressVo orderAddressVo) {
        orderNoUtils.shardingKey(orderAddressVo.getOrderNo());
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setCountry(orderAddressVo.getCountry());
        orderLogistics.setProvince(orderAddressVo.getProvince());
        orderLogistics.setCity(orderAddressVo.getCity());
        orderLogistics.setArea(orderAddressVo.getArea());
        orderLogistics.setConsumerAddr(orderAddressVo.getConsumerAddr());
        orderLogistics.setConsumerMobile(orderAddressVo.getConsumerPhone());
        orderLogistics.setConsumerName(orderAddressVo.getConsumerName());
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("ORDER_NO", orderAddressVo.getOrderNo());
        return update(orderLogistics, entityWrapper);
    }

    @Override
    public Map<String, Object> getUser(OrderLogistics logistics) {
        if (logistics.getCurrentPage() < 1)
            logistics.setCurrentPage(1);
        if (logistics.getPageSize() < 0)
            logistics.setPageSize(10);
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(2);

        int page = (logistics.getCurrentPage() - 1) * logistics.getPageSize();
        int size = logistics.getPageSize();
        int total = orderLogisticsMapper.getUserCount(logistics.getConsumerName(), logistics.getConsumerMobile());
        List<OrderLogistics> list = orderLogisticsMapper.getUser(logistics.getConsumerName(), logistics.getConsumerMobile(),
                page, size);

        result.put("userList", list);
        result.put("total", total);
        return result;
    }

    @Override
    public OrderLogistics selectOneByOrderNo(String orderNo) {
        return selectOneByPackageNo(orderNo, null);
    }

    @Override
    public OrderLogistics selectOneByPackageNo(String orderNo, String packageNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderNo(orderNo);
        orderLogistics.setPackageNo(packageNo);
        return orderLogisticsMapper.selectOne(orderLogistics);
    }

    @Override
    public List<OrderLogistics> selectByOrderNo(String orderNo) {
        return selectByPackageNo(orderNo, null);
    }

    @Override
    public List<OrderLogistics> selectByPackageNo(String orderNo, String packageNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderLogistics queryOrderLogistics = new OrderLogistics();
        queryOrderLogistics.setOrderNo(orderNo);
        queryOrderLogistics.setPackageNo(packageNo);
        EntityWrapper<OrderLogistics> orderLogisticsEntityWrapper = new EntityWrapper<>(queryOrderLogistics);
        return selectList(orderLogisticsEntityWrapper);
    }
}
