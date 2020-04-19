package com.mmj.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmj.common.constants.OrderClassify;
import com.mmj.common.constants.OrderStatus;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.RechargeVo;
import com.mmj.common.model.order.OrderStatusMQDto;
import com.mmj.common.model.order.OrdersPackageMQDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.OrderUtils;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.StringUtils;
import com.mmj.order.common.feign.ActiveFeignClient;
import com.mmj.order.common.feign.UserFeignClient;
import com.mmj.order.common.model.vo.UserCouponBatchVo;
import com.mmj.order.mapper.OrderPackageMapper;
import com.mmj.order.model.*;
import com.mmj.order.model.dto.VirtualGoodDto;
import com.mmj.order.model.request.OrdersUploadRequest;
import com.mmj.order.model.vo.OrderLogisticsVo;
import com.mmj.order.service.*;
import com.mmj.order.tools.Depot;
import com.mmj.order.tools.PackageParse;
import com.mmj.order.tools.SkuDepot;
import com.mmj.order.utils.MQProducer;
import com.mmj.order.utils.MessageUtils;
import com.mmj.order.utils.OrderNoUtils;
import com.mmj.order.utils.OrderSearchSynchronizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 订单包裹信息 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-20
 */
@Service
@Slf4j
public class OrderPackageServiceImpl extends ServiceImpl<OrderPackageMapper, OrderPackage> implements OrderPackageService {
    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderGoodService orderGoodService;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private OrderNoUtils orderNoUtils;

    @Autowired
    private MQProducer mqProducer;

    @Autowired
    private OrderSearchSynchronizer orderSearchSynchronizer;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ActiveFeignClient activeFeignClient;

    @Autowired
    private MessageUtils messageUtils;

    /**
     * 通过主订单号批量修改子订单状态
     *
     * @param orderNos
     * @param orderStatus
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateOrderPackageStatusByOrderNo(List<String> orderNos, Integer orderStatus) {
        orderNoUtils.shardingKey(orderNos.get(0));
        EntityWrapper<OrderPackage> orderPackageEntityWrapper = new EntityWrapper<>();
        orderPackageEntityWrapper.in("ORDER_NO", orderNos);
        List<OrderPackage> orderPackageList = selectList(orderPackageEntityWrapper);
        List<String> packageNos = Lists.newArrayListWithCapacity(orderPackageList.size());
        orderPackageList.forEach(orderPackage -> {
            packageNos.add(orderPackage.getPackageNo());
        });
        return batchUpdateOrderPackageStatusByPackageNo(packageNos, orderStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateOrderPackageStatusByPackageNo(List<String> packageNos, Integer orderStatus) {
        log.info("=> 修改包裹订单状态 包裹号:{}，修改状态值:{}", packageNos, orderStatus);
        orderNoUtils.shardingKey(packageNos.get(0));
        OrderPackage updateOrderPackage = new OrderPackage();
        updateOrderPackage.setOrderStatus(orderStatus);
        EntityWrapper<OrderPackage> orderPackageEntityWrapper = new EntityWrapper<>();
        orderPackageEntityWrapper.in("PACKAGE_NO", packageNos);
        boolean result = update(updateOrderPackage, orderPackageEntityWrapper);
        if (result) {
            List<OrderStatusMQDto> orderStatusMQDtoList = Lists.newArrayListWithCapacity(packageNos.size());
            packageNos.forEach(packageNo -> {
                orderStatusMQDtoList.add(new OrderStatusMQDto(packageNo, orderStatus));
            });
            orderSearchSynchronizer.updateStatus(orderStatusMQDtoList);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUploadErpStatus(List<String> packageNos) {
        log.info("=> 修改包裹上传聚水潭状态 包裹号:{}", packageNos);
        orderNoUtils.shardingKey(packageNos.get(0));
        OrderPackage updateOrderPackage = new OrderPackage();
        updateOrderPackage.setUploadErp(true);
        EntityWrapper<OrderPackage> orderPackageEntityWrapper = new EntityWrapper<>();
        orderPackageEntityWrapper.in("PACKAGE_NO", packageNos);
        boolean result = update(updateOrderPackage, orderPackageEntityWrapper);
        Assert.isTrue(result, "更新包裹上传ERP状态失败:" + packageNos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderPackage saveOrderPackage(OrderInfo orderInfo, String packageNo, Integer virtualGood, String packageDesc) {
        OrderPackage orderPackage = new OrderPackage();
        orderPackage.setPackageNo(StringUtils.isEmpty(packageNo) ? OrderUtils.gainOrderNo(orderInfo.getCreaterId(), orderInfo.getOrderType(), orderInfo.getOrderSource(), OrderClassify.SON) : packageNo);
        orderPackage.setOrderId(orderInfo.getOrderId());
        orderPackage.setOrderNo(orderInfo.getOrderNo());
        orderPackage.setOrderStatus(orderInfo.getOrderStatus());
        orderPackage.setOrderType(orderInfo.getOrderType());
        orderPackage.setOrderAmount(orderInfo.getOrderAmount());
        orderPackage.setGoodAmount(orderInfo.getGoodAmount());
        orderPackage.setLogisticsAmount(orderInfo.getExpressAmount());
        orderPackage.setDiscountAmount(orderInfo.getDiscountAmount());
        orderPackage.setCouponAmount(orderInfo.getCouponAmount());
        orderPackage.setVirtualGood(virtualGood);
        orderPackage.setGoldPrice(orderInfo.getGoldPrice());
        orderPackage.setMemberOrder(orderInfo.getMemberOrder());
        orderPackage.setDelFlag(0);
        orderPackage.setCreaterId(orderInfo.getCreaterId());
        orderPackage.setPackageDesc(packageDesc);
        boolean result = insert(orderPackage);
        Assert.isTrue(result, "保存包裹订单信息失败");
        log.info("当前用户{}，完成订单包裹信息保存,订单号为:{},订单ID为:{}", orderInfo.getCreaterId(), orderInfo.getOrderNo(), orderInfo.getOrderId());
        return orderPackage;
    }

    /**
     * 通过订单号查询子订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public List<OrderPackage> selectByOrderNo(String orderNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderPackage queryOrderPackage = new OrderPackage();
        queryOrderPackage.setOrderNo(orderNo);
        EntityWrapper<OrderPackage> orderPackageEntityWrapper = new EntityWrapper<>(queryOrderPackage);
        return selectList(orderPackageEntityWrapper);
    }

    @Override
    public OrderPackage selectByPackageNo(String packageNo) {
        orderNoUtils.shardingKey(packageNo);
        OrderPackage queryOrderPackage = new OrderPackage();
        queryOrderPackage.setPackageNo(packageNo);
        EntityWrapper<OrderPackage> orderPackageEntityWrapper = new EntityWrapper<>(queryOrderPackage);
        return selectOne(orderPackageEntityWrapper);
    }

    @Override
    public void unpick(final OrderInfo orderInfo) {
//        boolean isGroupOrder = OrderUtils.isGroupOrder(orderInfo.getOrderType());
//        if (isGroupOrder) {
//            log.warn("=> 订单拆单-结束拆单，团订单不拆单 orderNo:{}", orderInfo.getOrderNo());
//            return;
//        }
//        if (0 == orderInfo.getOrderAmount()) {
//            log.warn("=> 订单拆单-结束拆单，订单金额为零 orderNo:{}", orderInfo.getOrderNo());
//            return;
//        }
        List<OrderGood> orderGoodList = orderGoodService.selectByOrderNo(orderInfo.getOrderNo());
        if (orderGoodList.size() == 0) {
            log.info("=> 订单拆单-结束拆单,订单号:{},商品SKU数量为:{}", orderInfo.getOrderNo(), orderGoodList.size());
            return;
        }
        List<OrderPackage> orderPackageList = selectByOrderNo(orderInfo.getOrderNo());
        log.warn("=> 订单拆单-订单号:{},包裹数量为:{}", orderInfo.getOrderNo(), orderPackageList.size());
        if (orderPackageList.size() == 0 || orderPackageList.size() > 1) {
            return;
        }
//        /**
//         * 拆分金额字段：优惠券优惠金额/活动优惠金额/运费/买买金兑换金额
//         */
//        orderGoodService.unpickGoodsAmount(orderInfo, orderGoodList);
        /**
         * 根据商品所属仓库拆单
         */
        OrderPackage oneOrderPackage = orderPackageList.get(0); //下单时包裹
        //仓库-商品
        List<Depot> depotList = getGoodsDepot(orderGoodList);
        List<OrderPackage> insertOrderPackages = Lists.newArrayListWithCapacity(depotList.size());
        List<String> orderPackageNos = Lists.newArrayListWithCapacity(depotList.size());
        List<OrderGood> updateOrderGoodByPackageNo = Lists.newArrayListWithCapacity(orderGoodList.size());
        List<OrdersPackageMQDto> ordersPackageMQDtoList = Lists.newArrayListWithCapacity(depotList.size());
        for (int i = 0, size = depotList.size(); i < size; i++) {
            Depot depot = depotList.get(i);
            OrderPackage orderPackage = new OrderPackage();
            if (i == 0) {
                orderPackage.setPackageId(oneOrderPackage.getPackageId());
                orderPackage.setOrderNo(oneOrderPackage.getOrderNo());
                orderPackage.setPackageNo(oneOrderPackage.getPackageNo());
                orderPackage.setModifyTime(new Date());
            } else {
                orderPackage.setOrderId(orderInfo.getOrderId());
                orderPackage.setOrderNo(orderInfo.getOrderNo());
                orderPackage.setPackageNo(OrderUtils.gainOrderNo(orderInfo.getCreaterId(), orderInfo.getOrderType(), orderInfo.getOrderSource(), OrderClassify.SON));
                orderPackage.setOrderType(orderInfo.getOrderType());
                orderPackage.setDelFlag(0);
                orderPackage.setCreaterId(orderInfo.getCreaterId());
                orderPackage.setMemberOrder(orderInfo.getMemberOrder());
                orderPackage.setPackageDesc(oneOrderPackage.getPackageDesc());
            }
            orderPackage.setOrderStatus(orderInfo.getOrderStatus());
            orderPackage.setGoodAmount(0);
            orderPackage.setCouponAmount(0);
            orderPackage.setDiscountAmount(0);
            orderPackage.setLogisticsAmount(0);
            orderPackage.setGoldPrice(0);
            orderPackage.setVirtualGood(depot.getDepotId().startsWith("virtual") ? 1 : 0);
            List<SkuDepot> skuDepots = depot.getSkuDepots();
            List<OrdersPackageMQDto.Goods> packageGoods = Lists.newArrayListWithCapacity(skuDepots.size());
            skuDepots.stream().forEach(skuDepot ->
                    orderGoodList.stream().forEach(orderGood -> {
                        if (orderGood.getGoodSku().equals(skuDepot.getSkuId())) {
                            log.info("=> 订单拆单 orderNo:{},packageNo:{},goodSku:{},depotId:{}", orderPackage.getOrderNo(), orderPackage.getPackageNo(), orderGood.getGoodSku(), depot.getDepotId());
                            //封装包裹同步es数据
                            OrdersPackageMQDto.Goods pg = new OrdersPackageMQDto.Goods();
                            pg.setGoodSku(orderGood.getGoodSku());
                            pg.setGoodImage(orderGood.getGoodImage());
                            pg.setGoodName(orderGood.getGoodName());
                            pg.setGoodNum(orderGood.getGoodNum());
                            packageGoods.add(pg);
                            //给商品设置子订单号
                            OrderGood updateOrderGood = new OrderGood();
                            updateOrderGood.setOgId(orderGood.getOgId());
                            updateOrderGood.setPackageNo(orderPackage.getPackageNo());
                            updateOrderGood.setWarehouseId(depot.getDepotId());
                            updateOrderGood.setCouponAmount(orderGood.getCouponAmount());
                            updateOrderGood.setDiscountAmount(orderGood.getDiscountAmount());
                            updateOrderGood.setGoldPrice(orderGood.getGoldPrice());
                            updateOrderGood.setLogisticsAmount(orderGood.getGoldPrice());
                            updateOrderGoodByPackageNo.add(updateOrderGood);
                            //给包裹计算金额
                            int unitPrice = orderGood.getGoodPrice();
                            orderPackage.setGoodAmount(orderPackage.getGoodAmount() + unitPrice * orderGood.getGoodNum());
                            orderPackage.setCouponAmount(orderPackage.getCouponAmount() + orderGood.getCouponAmount());
                            orderPackage.setDiscountAmount(orderPackage.getDiscountAmount() + orderGood.getDiscountAmount());
                            orderPackage.setLogisticsAmount(orderPackage.getLogisticsAmount() + orderGood.getLogisticsAmount());
                            orderPackage.setGoldPrice(orderPackage.getGoldPrice() + orderGood.getGoldPrice());
                        }
                    })
            );
            // 计算包裹内支付金额
            orderPackage.setOrderAmount(orderPackage.getGoodAmount() + orderPackage.getLogisticsAmount() - orderPackage.getCouponAmount() - orderPackage.getDiscountAmount() - orderPackage.getGoldPrice());
            insertOrderPackages.add(orderPackage);
            orderPackageNos.add(orderPackage.getPackageNo());
            //封装包裹消息-同步ES数据
            OrdersPackageMQDto ordersPackageMQDto = new OrdersPackageMQDto();
            ordersPackageMQDto.setOrderNo(orderInfo.getOrderNo());
            ordersPackageMQDto.setPackageNo(orderPackage.getPackageNo());
            ordersPackageMQDto.setOrderDate(new Date());
            ordersPackageMQDto.setVirtualGood(orderPackage.getVirtualGood());
            ordersPackageMQDto.setOrderAmount(orderPackage.getOrderAmount());
            ordersPackageMQDto.setGoods(packageGoods);
            ordersPackageMQDtoList.add(ordersPackageMQDto);
        }
        log.info("=> 订单拆单，拆单结果-订单号:{},拆单包裹数:{}", orderInfo.getOrderNo(), insertOrderPackages.size());
        OrderPackage updateOrderPackage = insertOrderPackages.remove(0);
        updateOrderPackage.setOrderAmount(updateOrderPackage.getOrderAmount() == 0 ? null : updateOrderPackage.getOrderAmount());
        updateOrderPackage.setGoodAmount(updateOrderPackage.getGoodAmount() == 0 ? null : updateOrderPackage.getGoodAmount());
        updateOrderPackage.setCouponAmount(updateOrderPackage.getCouponAmount() == 0 ? null : updateOrderPackage.getCouponAmount());
        updateOrderPackage.setDiscountAmount(updateOrderPackage.getDiscountAmount() == 0 ? null : updateOrderPackage.getDiscountAmount());
        updateOrderPackage.setGoldPrice(updateOrderPackage.getGoldPrice() == 0 ? null : updateOrderPackage.getGoldPrice());
        updateOrderPackage.setLogisticsAmount(updateOrderPackage.getLogisticsAmount() == 0 ? null : updateOrderPackage.getLogisticsAmount());
        boolean result = updateById(updateOrderPackage);
        log.info("=> 订单拆单，更新原有订单包裹信息 orderNo:{},packageId:{},result:{}", orderInfo.getOrderNo(), updateOrderPackage.getPackageId(), result);
        if (insertOrderPackages.size() > 0) {
            result = insertBatch(insertOrderPackages);
            log.info("=> 订单拆单，新增订单包裹信息 orderNo:{},packageSize:{},result:{}", orderInfo.getOrderNo(), insertOrderPackages.size(), result);
        }
        /**
         * 批量更新订单商品包裹号
         */
        result = orderGoodService.updateBatchById(updateOrderGoodByPackageNo);
        log.info("=> 订单拆单，批量更新订单商品->包裹号 orderNo:{},updateSize:{},result:{}", orderInfo.getOrderNo(), updateOrderGoodByPackageNo.size(), result);
        orderSearchSynchronizer.sendPackageEs(ordersPackageMQDtoList);
        mqProducer.sendPackageToBeDelivered(orderPackageNos);
    }

    /**
     * 拆单操作
     *
     * @param orderGoodList
     */
    private List<Depot> getGoodsDepot(List<OrderGood> orderGoodList) {
        List<SkuDepot> skuDepots = Lists.newArrayList();
        for (int i = 0, size = orderGoodList.size(); i < size; i++) {
            String goodSku = orderGoodList.get(i).getGoodSku();
            String warehouseIds = orderGoodList.get(i).getWarehouseId();
            String[] depots;
            if ("1".equals(orderGoodList.get(i).getVirtualFlag())) { //虚拟商品
                depots = new String[]{"virtual_" + i};
            } else if (StringUtils.isEmpty(warehouseIds)) {
                depots = new String[]{"default"};
            } else {
                depots = orderGoodList.get(i).getWarehouseId().split(",");
            }
            SkuDepot skuDepot = new SkuDepot(goodSku, depots);
            skuDepots.add(skuDepot);
        }
        PackageParse packageParse = new PackageParse(skuDepots);
        List<Depot> depots = packageParse.spinOff();
        log.info("拆单后结果为:{}", depots);
        return depots;
    }

    @Override
    public void orderToBeDelivered(List<String> orderNos) {
        if (Objects.isNull(orderNos) || orderNos.size() == 0) {
            log.error("=> 无发货订单号");
            return;
        }
        orderNoUtils.shardingKey(orderNos.get(0));

        EntityWrapper<OrderInfo> orderInfoEntityWrapper = new EntityWrapper<>();
        orderInfoEntityWrapper.in("ORDER_NO", orderNos);
        List<OrderInfo> orderInfoList = orderInfoService.selectList(orderInfoEntityWrapper);
        if (Objects.isNull(orderInfoList) || orderInfoList.size() == 0) {
            return;
        }
        orderInfoList.forEach(order -> {
            mqProducer.sendPackageParse(order);
        });
//        EntityWrapper<OrderPackage> orderPackageEntityWrapper = new EntityWrapper<>();
//        orderPackageEntityWrapper.in("ORDER_NO", orderNos);
//        List<OrderPackage> orderPackageList = selectList(orderPackageEntityWrapper);
//        if (Objects.isNull(orderPackageList) || orderPackageList.size() == 0) {
//            return;
//        }
//        Map<String, List<OrderPackage>> orderPackages = orderPackageList.stream().collect(Collectors.groupingBy(OrderPackage::getOrderNo));
//        orderPackages.forEach((k, v) -> toBeDelivered(v));
//        orderInfoService.batchUpdateOrderStatus(orderNos, OrderStatus.TO_BE_DELIVERED.getStatus());
//        List<String> packageNo = Lists.newArrayListWithCapacity(orderPackageList.size());
//        orderPackageList.forEach(orderPackage -> packageNo.add(orderPackage.getPackageNo()));
//        batchUpdateOrderPackageStatusByPackageNo(packageNo, OrderStatus.TO_BE_DELIVERED.getStatus());
    }

    @Override
    public void packageToBeDelivered(List<String> packageNos) {
        if (Objects.isNull(packageNos) || packageNos.size() == 0) {
            log.error("=> 无发货订单号");
            return;
        }
        orderNoUtils.shardingKey(packageNos.get(0));
        EntityWrapper<OrderPackage> orderPackageEntityWrapper = new EntityWrapper<>();
        orderPackageEntityWrapper.in("PACKAGE_NO", packageNos);
        List<OrderPackage> orderPackageList = selectList(orderPackageEntityWrapper);
        if (Objects.isNull(orderPackageList) || orderPackageList.size() == 0) {
            log.error("=> 发货 shardingKey:{},包裹号：{}，未查询到包裹信息", BaseContextHandler.get(SecurityConstants.SHARDING_KEY), packageNos);
            return;
        }

        List<String> orderNos = Lists.newArrayListWithCapacity(1);
        orderNos.add(orderPackageList.get(0).getOrderNo());
        orderInfoService.batchUpdateOrderStatus(orderNos, OrderStatus.TO_BE_DELIVERED.getStatus());
        batchUpdateOrderPackageStatusByPackageNo(packageNos, OrderStatus.TO_BE_DELIVERED.getStatus());
        toBeDelivered(orderPackageList);
    }

    /**
     * 虚拟商品发货
     */
    @Override
    public void virtualGoodToBeDelivered(OrderPackage orderPackage, List<OrderGood> orderGoods, boolean auto) {
        if (Objects.isNull(orderPackage.getVirtualGood()) || orderPackage.getVirtualGood() != 1) {//不是虚拟商品
            return;
        }
        orderGoods.forEach(good -> {
            log.info("=> 处理发货订单，虚拟商品发货 goods:{}", JSONObject.toJSONString(good));
            //虚拟商品发货
            if (1 == good.getVirtualType()) { //优惠券
                VirtualGoodDto virtualGoodDto = JSONObject.parseObject(good.getSnapshotData(), VirtualGoodDto.class);
                List<Integer> couponIds = Stream.of(virtualGoodDto.getCouponTemplateids()).collect(Collectors.toList());
                for (int i = 0; i < good.getGoodNum() - 1; i++) {
                    couponIds.addAll(couponIds);
                }
                UserCouponBatchVo userCouponBatchVo = new UserCouponBatchVo();
                userCouponBatchVo.setUserId(orderPackage.getCreaterId());
                userCouponBatchVo.setCouponSource("SYSTEM");
                userCouponBatchVo.setCouponIds(couponIds);
                log.info("=> 虚拟商品发货，发放优惠券请求参数:{}", JSONObject.toJSONString(userCouponBatchVo));
                ReturnData returnData = userFeignClient.batchReceive(userCouponBatchVo);
                log.info("=> 虚拟商品发货，发放优惠券返回数据:{}", JSONObject.toJSONString(returnData));
                if (returnData.getCode() == 1) { //发放成功
                    virtualToBeDelivered(orderPackage);
                    orderInfoService.updateOrderStatus(OrderStatus.PENDING_RECEIPT.getStatus(), orderPackage.getOrderNo());
                    List<String> packageNos = Lists.newArrayListWithCapacity(1);
                    packageNos.add(orderPackage.getPackageNo());
//                            batchUpdateOrderPackageStatusByPackageNo(packageNos, OrderStatus.COMPLETED.getStatus());
                    updateUploadErpStatus(packageNos);
                }
            } else if (2 == good.getVirtualType()) {
                VirtualGoodDto virtualGoodDto = JSONObject.parseObject(good.getSnapshotData(), VirtualGoodDto.class);
                Map<String, Object> params = Maps.newHashMapWithExpectedSize(4);
                params.put("orderNo", orderPackage.getOrderNo());
                params.put("isGiveBy", "0");
                params.put("kingNum", virtualGoodDto.getNumber() * good.getGoodNum());
                params.put("userId", orderPackage.getCreaterId());
                log.info("=> 虚拟商品发货，发放买买金请求参数:{}", params);
                int n = userFeignClient.orderKingProc(params);
                log.info("=> 虚拟商品发货，发放买买金返回数据:{}", n);
                virtualToBeDelivered(orderPackage);
                orderInfoService.updateOrderStatus(OrderStatus.PENDING_RECEIPT.getStatus(), orderPackage.getOrderNo());
                List<String> packageNos = Lists.newArrayListWithCapacity(1);
                packageNos.add(orderPackage.getPackageNo());
//                        batchUpdateOrderPackageStatusByPackageNo(packageNos, OrderStatus.COMPLETED.getStatus());
                updateUploadErpStatus(packageNos);
            } else if (3 == good.getVirtualType()) {
                log.info("=> 虚拟商品发货，话费商品 orderNo:{},packageNo:{}", orderPackage.getOrderNo(), orderPackage.getPackageNo());
                if (!auto) {
                    virtualToBeDelivered(orderPackage);
                    orderInfoService.updateOrderStatus(OrderStatus.PENDING_RECEIPT.getStatus(), orderPackage.getOrderNo());
                    List<String> packageNos = Lists.newArrayListWithCapacity(1);
                    packageNos.add(orderPackage.getPackageNo());
                    updateUploadErpStatus(packageNos);
                }
            } else if (4 == good.getVirtualType()) {
                RechargeVo rechargeVo = new RechargeVo();
                rechargeVo.setOrderNo(orderPackage.getOrderNo());
                log.info("=> 虚拟商品发货，直冲话费请求参数:{}", JSONObject.toJSONString(rechargeVo));
                ReturnData returnData = activeFeignClient.recharge(rechargeVo);
                log.info("=> 虚拟商品发货，直冲话费返回数据:{}", JSONObject.toJSONString(returnData));
                if (returnData.getCode() == 1) { //发放成功
                    virtualToBeDelivered(orderPackage);
                    orderInfoService.updateOrderStatus(OrderStatus.COMPLETED.getStatus(), orderPackage.getOrderNo());
                    List<String> packageNos = Lists.newArrayListWithCapacity(1);
                    packageNos.add(orderPackage.getPackageNo());
                    updateUploadErpStatus(packageNos);
                }
            } else {
                log.warn("=> 处理发货订单，虚拟商品发货验证错误 orderNo:{},packageNo:{},saleId:{}", orderPackage.getOrderNo(), orderPackage.getPackageNo(), good.getSaleId());
            }
        });
    }

    /**
     * 虚拟包裹发货
     *
     * @param orderPackage
     */
    private void virtualToBeDelivered(OrderPackage orderPackage) {
        List<OrderLogistics> orderLogisticsList = orderLogisticsService.getOrderLogistics(orderPackage.getOrderNo(), orderPackage.getCreaterId());
        if (orderLogisticsList != null && orderLogisticsList.size() > 0) {
            OrderLogisticsVo orderLogisticsVo = new OrderLogisticsVo();
            orderLogisticsVo.setLogisticsNo("虚拟发货");
            orderLogisticsVo.setCompanyName("买买家");
            orderLogisticsVo.setCompanyCode("买买家");
            orderLogisticsVo.setPackageNo(orderPackage.getPackageNo());
            orderLogisticsVo.setSendTime(new Date());
            orderLogisticsVo.setUserId(orderPackage.getCreaterId());
            orderLogisticsService.updateLogistics(orderLogisticsVo);
        }
    }

    /**
     * 发货
     *
     * @param orderPackageList
     */
    @Override
    public void toBeDelivered(List<OrderPackage> orderPackageList) {
        String orderNo = orderPackageList.get(0).getOrderNo();
        try {
            Object shardingKey = BaseContextHandler.get(SecurityConstants.SHARDING_KEY);
            OrderInfo orderInfo = orderInfoService.getByOrderNo(orderNo);
            if (Objects.isNull(orderInfo)) {
                log.error("=> 发货 shardingKey:{},orderNo：{}，未查询到订单信息", shardingKey, orderNo);
                return;
            }
            List<OrderGood> orderGoodList = orderGoodService.selectByOrderNo(orderNo);
            if (Objects.isNull(orderGoodList) || orderGoodList.size() == 0) {
                log.error("=> 发货 shardingKey:{},orderNo：{}，未查询到订单商品信息", shardingKey, orderNo);
                return;
            }
            OrderLogistics orderLogistics = orderLogisticsService.selectOneByOrderNo(orderNo);
            if (Objects.isNull(orderLogistics)) {
                log.error("=> 发货 shardingKey:{},orderNo：{}，未查询到收件人信息", shardingKey, orderNo);
            }
            OrderPayment orderPayment = orderPaymentService.selectOneByOrderNo(orderNo);
            if (Objects.isNull(orderPayment)) {
                log.error("=> 发货 shardingKey:{},orderNo：{}，未查询到支付信息", shardingKey, orderNo);
                return;
            }
            log.info("=> 处理发货订单 orderNo:{},上传包裹数量:{}", orderInfo.getOrderNo(), orderPackageList.size());
            List<OrdersUploadRequest> ordersUploadRequests = Lists.newArrayListWithCapacity(orderPackageList.size());
            orderPackageList.forEach(orderPackage -> {
                log.info("=> 处理发货订单，包裹发货处理 orderPackage:{}", JSONObject.toJSONString(orderPackage));
                if (orderPackage.getOrderStatus() > OrderStatus.TO_BE_DELIVERED.getStatus()) {
                    log.warn("=> 处理发货订单，订单重复发货 orderNo:{},packageNo:{},packageStatus:{}", orderPackage.getOrderNo(), orderPackage.getPackageNo(), orderPackage.getOrderStatus());
                } else if (Objects.nonNull(orderPackage.getVirtualGood()) && 1 == orderPackage.getVirtualGood()) {//虚拟商品
                    List<OrderGood> orderGoods = orderGoodList.stream().filter(orderGood -> orderGood.getPackageNo().equals(orderPackage.getPackageNo())).collect(Collectors.toList());
                    virtualGoodToBeDelivered(orderPackage, orderGoods, true);
                } else {
                    //上传聚水潭
                    OrdersUploadRequest request = new OrdersUploadRequest();
                    request.setSoId(orderPackage.getPackageNo());
                    request.setOrderDate(orderPackage.getCreaterTime());
                    request.setShopStatus("WAIT_SELLER_SEND_GOODS");
                    request.setShopBuyerId(String.valueOf(orderPackage.getCreaterId()));
                    request.setReceiverPhone(orderLogistics.getConsumerMobile());
                    request.setReceiverAddress(orderLogistics.getConsumerAddr());
                    request.setReceiverCity(orderLogistics.getCity());
                    request.setReceiverState(orderLogistics.getProvince());
                    request.setReceiverDistrict(orderLogistics.getArea());
                    request.setReceiverName(orderLogistics.getConsumerName());

                    request.setPayAmount(PriceConversion.intToString(orderPackage.getOrderAmount()));
                    request.setOrderFrom(orderInfo.getOrderSource());
                    request.setFreight(PriceConversion.intToString(orderPackage.getLogisticsAmount()));
                    request.setRemark("初始化上传订单");
                    request.setBuyerMessage(" ");
                    request.setShopModified(new Date());
                    String payment;
                    if ("1".equals(orderPayment.getPayType())) {
                        payment = "微信支付";
                    } else {
                        payment = "买买家支付";
                    }
                    OrdersUploadRequest.Pay pay = new OrdersUploadRequest.Pay();
                    pay.setOuterPayId(orderPayment.getPayNo());
                    pay.setPayDate(orderPayment.getPayTime());
                    pay.setAmount(PriceConversion.intToString(orderPackage.getOrderAmount()));
                    pay.setPayment(payment);
                    pay.setSellerAccount(orderPayment.getCreaterId().toString());
                    pay.setBuyerAccount(orderPayment.getCreaterId().toString());
                    request.setPay(pay);

                    List<OrderGood> orderGoods = orderGoodList.stream().filter(orderGood -> orderGood.getPackageNo().equals(orderPackage.getPackageNo())).collect(Collectors.toList());
                    List<OrdersUploadRequest.Item> items = new ArrayList<>();
                    orderGoods.forEach(good -> {
                        OrdersUploadRequest.Item item = new OrdersUploadRequest.Item();
                        item.setName(good.getGoodName());
                        item.setSkuId(good.getGoodSku());
                        item.setShopSkuId(String.valueOf(good.getSaleId()));
                        item.setPic(good.getGoodImage());
                        item.setBasePrice(PriceConversion.intToString(good.getGoodAmount()));
                        item.setAmount(PriceConversion.intToString(good.getGoodPrice() * good.getGoodNum()));
                        item.setPropertiesValue(good.getModelName());
                        item.setQty(good.getGoodNum());
                        item.setOuterOiId(orderPackage.getPackageNo() + good.getOgId());
                        items.add(item);
                    });
                    request.setItems(items);
                    ordersUploadRequests.add(request);
                }
            });
            log.info("=> 处理发货订单 orderNo:{},上传包裹数量:{},封装订单数:{}", orderInfo.getOrderNo(), orderPackageList.size(), ordersUploadRequests.size());
            if (ordersUploadRequests.size() > 0) {
                mqProducer.sendOrderToJstUpload(ordersUploadRequests);
            }
        } catch (Exception e) {
            log.error("=> 订单发货失败 orderNo:{},error:{}", orderNo, e.getMessage());
        }
    }
}
