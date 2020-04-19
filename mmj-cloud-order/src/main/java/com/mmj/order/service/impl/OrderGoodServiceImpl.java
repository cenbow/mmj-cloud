package com.mmj.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.common.constants.ActiveGoodsConstants;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.ActiveGoodOrder;
import com.mmj.common.model.good.GoodModelEx;
import com.mmj.common.model.good.GoodOrder;
import com.mmj.common.model.good.GoodWarehouse;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.OrderUtils;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.StringUtils;
import com.mmj.order.common.feign.ActiveFeignClient;
import com.mmj.order.common.feign.GoodFeignClient;
import com.mmj.order.common.model.ActiveGood;
import com.mmj.order.common.model.dto.OrderCheckDto;
import com.mmj.order.constant.GoodStockStatus;
import com.mmj.order.constant.OrderType;
import com.mmj.order.mapper.OrderGoodMapper;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.dto.OrderGoodsDto;
import com.mmj.order.model.dto.VirtualGoodDto;
import com.mmj.order.model.vo.OrderGoodsVo;
import com.mmj.order.model.vo.OrderSaveVo;
import com.mmj.order.service.GoodsStockService;
import com.mmj.order.service.OrderGoodService;
import com.mmj.order.tools.OrderStockException;
import com.mmj.order.utils.MQProducer;
import com.mmj.order.utils.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单商品表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@Service
@Slf4j
public class OrderGoodServiceImpl extends ServiceImpl<OrderGoodMapper, OrderGood> implements OrderGoodService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OrderGoodMapper orderGoodMapper;

    @Autowired
    private OrderNoUtils orderNoUtils;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private ActiveFeignClient activeFeignClient;

    @Autowired
    private GoodsStockService goodsStockService;

    @Autowired
    private MQProducer mqProducer;

    /**
     * 获取商品价格类型
     *
     * @param orderType
     * @param memberOrder
     * @param groupOrderCheckDto
     * @return
     */
    public int getPriceType(Integer orderType, boolean memberOrder, OrderCheckDto groupOrderCheckDto) {
        //单价类型 0店铺价 1会员价 2原价 3活动价
        switch (orderType) {
            case OrderType.ORDINARY:
                return 2;
            case OrderType.TEN_YUAN_SHOP:
                return memberOrder ? 1 : 0;
            case OrderType.TWO_GROUP:
                return memberOrder ? 1 : (!groupOrderCheckDto.isLan() && groupOrderCheckDto.isLaunchIsMember() ? 1 : 0);
            default:
                return 3;
        }
    }

    @Override
    public List<OrderGood> checkOrderGoods(OrderSaveVo orderSaveVo, OrderCheckDto groupOrderCheckDto) {
        List<OrderGoodsVo> goodsVoList = orderSaveVo.getGood();
        boolean memberOrder = orderSaveVo.getMemberOrder();
        int priceType = getPriceType(orderSaveVo.getOrderType(), memberOrder, groupOrderCheckDto);
        List<OrderGood> orderGoodList = Lists.newArrayListWithCapacity(goodsVoList.size());
        Assert.isTrue(Objects.nonNull(goodsVoList) && goodsVoList.size() > 0, "请选择下单商品");
        List<String> goodSkuList = Lists.newArrayListWithCapacity(goodsVoList.size());
        goodsVoList.stream().forEach(g -> {
            goodSkuList.add(g.getGoodSku());
        });
        int activeType = OrderUtils.orderTypeToActiveType(orderSaveVo.getOrderType());
        if (activeType == 0) { //非活动商品
            ReturnData<List<GoodOrder>> returnData = goodFeignClient.queryOrderGood(goodSkuList);
            Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode().intValue(), returnData.getDesc());
            Assert.isTrue(Objects.nonNull(returnData.getData()) && returnData.getData().size() > 0, "商品已下架");
            List<GoodOrder> goodOrderList = returnData.getData();
            Assert.isTrue(goodSkuList.size() == goodOrderList.size(), "下单失败：部分商品已下架");
            goodOrderList.forEach(goodOrder -> {
                Assert.notNull(goodOrder.getGoodInfo(), "商品已下架");
                Assert.isTrue("1".equals(goodOrder.getGoodInfo().getGoodStatus()), "[" + goodOrder.getGoodInfo().getGoodName() + "]已下架");
            });
            goodsVoList.forEach(goods -> {
                //库存验证
                checkGoodStock(goods.getGoodTitle(), goods.getGoodSku(), goods.getGoodNum());
                GoodOrder goodOrder = goodOrderList.stream().filter(go -> goods.getGoodSku().equals(go.getGoodSku())).findFirst().orElse(null);
                Assert.notNull(goodOrder, goods.getGoodTitle() + "商品已不存在");
                Assert.notNull(goodOrder.getGoodInfo(), goods.getGoodTitle() + "商品主信息已不存在");
                //是否会员专属商品
                int memberFlag = Objects.nonNull(goodOrder.getGoodInfo().getMemberFlag()) ? goodOrder.getGoodInfo().getMemberFlag() : 0;
                if (!memberOrder) {
                    Assert.isTrue(memberFlag == 0, "[" + goods.getGoodTitle() + "]会员专属商品");
                }
                Assert.isTrue(Objects.nonNull(goodOrder.getGoodModels()) && goodOrder.getGoodModels().size() > 0, goods.getGoodTitle() + "商品规格信息不存在");
                Assert.isTrue(Objects.nonNull(goodOrder.getGoodWarehouses()) && goodOrder.getGoodWarehouses().size() > 0, goods.getGoodTitle() + "商品仓库信息不存在");
                OrderGood orderGood = new OrderGood();
                BeanUtils.copyProperties(goodOrder, orderGood);
                orderGood.setPriceType(priceType);
                orderGood.setGoodName(goods.getGoodTitle());
                orderGood.setGoodSpu(goodOrder.getGoodInfo().getGoodSpu());
                orderGood.setClassCode(goodOrder.getGoodInfo().getGoodClass());
                orderGood.setGoodNum(goods.getGoodNum());
                orderGood.setGoodPrice(goodOrder.getShopPrice());
                orderGood.setMemberPrice(goodOrder.getMemberPrice());
                orderGood.setGoodAmount(goodOrder.getBasePrice());
                StringBuilder goodModels = new StringBuilder();
                goodOrder.getGoodModels().forEach(goodModel -> {
                    goodModels.append(" ").append(goodModel.getModelValue());
                });
                List<GoodWarehouse> goodWarehouseList = goodOrder.getGoodWarehouses(); //商品仓库信息
                List<String> goodWarehouses = Lists.newArrayListWithCapacity(goodWarehouseList.size());
                goodWarehouseList.forEach(goodWarehouse -> {
                    goodWarehouses.add(goodWarehouse.getWarehouseName());
                });
                orderGood.setWarehouseId(String.join(",", goodWarehouses));
                orderGood.setModelName(goodModels.toString());
                orderGood.setVirtualFlag(goodOrder.getGoodInfo().getVirtualFlag().toString());//是否虚拟商品 1是 0否
                //虚拟商品类型 ,虚拟商品发货信息
                if (1 == goodOrder.getGoodInfo().getVirtualFlag()) { //虚拟商品
                    VirtualGoodDto virtualGoodDto = getVirtualGoods(goodOrder.getGoodInfo().getGoodId());
                    orderGood.setVirtualType(virtualGoodDto.getType());
                    orderGood.setSnapshotData(JSONObject.toJSONString(virtualGoodDto));
                }
                //goodOrder.getGoodInfo().getCombinaFlag(); //是否组合商品 1是 0否
                orderGood.setCombinaFlag(goodOrder.getGoodInfo().getCombinaFlag());
                orderGood.setGoodImage(goodOrder.getImage());
                orderGood.setLogisticsAmount(0);
                orderGood.setGoldPrice(0);
                orderGood.setCouponAmount(0);
                orderGood.setDiscountAmount(0);
                orderGoodList.add(orderGood);
            });
        } else { //活动商品
            ActiveGoodOrder activeGoodOrder = new ActiveGoodOrder();
            activeGoodOrder.setActiveType(activeType);
            activeGoodOrder.setGoodSkus(goodSkuList);
            activeGoodOrder.setBusinessId(orderSaveVo.getBusinessId());
            ReturnData<List<ActiveGood>> returnData = activeFeignClient.queryOrderGood(activeGoodOrder);
            Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode().intValue(), returnData.getDesc());
            Assert.isTrue(Objects.nonNull(returnData.getData()) && returnData.getData().size() > 0, "商品已下架");
            List<ActiveGood> activeGoodList = returnData.getData(); //活动商品信息

            ReturnData<List<GoodModelEx>> returnData1 = goodFeignClient.goodModelQueryList(goodSkuList);
            Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData1.getCode().intValue(), returnData.getDesc());
            Assert.isTrue(Objects.nonNull(returnData1.getData()) && returnData1.getData().size() > 0, "商品规格查询失败");
            List<GoodModelEx> goodModelExList = returnData1.getData(); //商品规格信息

            ReturnData<List<GoodWarehouse>> returnData2 = goodFeignClient.goodWarehouseQueryList(goodSkuList);
            Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData2.getCode().intValue(), returnData.getDesc());
            Assert.isTrue(Objects.nonNull(returnData2.getData()) && returnData2.getData().size() > 0, "商品仓库查询失败");
            List<GoodWarehouse> goodWarehouseList = returnData2.getData(); //商品仓库信息

            Assert.isTrue(goodSkuList.size() == activeGoodList.size(), "下单失败：部分商品已下架");
            activeGoodList.forEach(activeGood ->
                    Assert.isTrue("1".equals(activeGood.getGoodStatus()), "[" + activeGood.getGoodName() + "]已下架")
            );
            goodsVoList.forEach(goods -> {
                if (!orderSaveVo.getOrderType().equals(OrderType.SPIKE) && !orderSaveVo.getOrderType().equals(OrderType.LOTTERY))
                    checkGoodStock(goods.getGoodTitle(), goods.getGoodSku(), goods.getGoodNum());
                ActiveGood activeGood = activeGoodList.stream().filter(ag -> goods.getGoodSku().equals(ag.getGoodSku())).findFirst().orElse(null);
                Assert.notNull(activeGood, "[" + goods.getGoodTitle() + "]商品已不存在");
                //是否会员专属商品
                int memberFlag = Objects.nonNull(activeGood.getMemberFlag()) ? activeGood.getMemberFlag() : 0;
                if (!memberOrder) {
                    Assert.isTrue(memberFlag == 0, "[" + goods.getGoodTitle() + "]会员专属商品");
                }
                List<GoodModelEx> goodModelExes = goodModelExList.stream().filter(ag -> activeGood.getSaleId().equals(ag.getSaleId())).collect(Collectors.toList());
                Assert.isTrue(Objects.nonNull(goodModelExes) && goodModelExes.size() > 0, "[" + goods.getGoodTitle() + "]缺少规格");
                List<GoodWarehouse> goodWarehouses = goodWarehouseList.stream().filter(gw -> activeGood.getGoodSku().equals(gw.getGoodSku())).collect(Collectors.toList());
                Assert.isTrue(Objects.nonNull(goodWarehouses) && goodWarehouses.size() > 0, "[" + goods.getGoodTitle() + "]缺少归属仓库");

                StringBuilder goodModels = new StringBuilder();
                goodModelExes.forEach(goodModel -> {
                    goodModels.append(" ").append(goodModel.getModelValue());
                });
                OrderGood orderGood = new OrderGood();
                BeanUtils.copyProperties(activeGood, orderGood);
                if(orderSaveVo.getOrderType() == OrderType.BARGAIN){
                    orderGood.setGoodPrice(PriceConversion.stringToInt(goods.getUnitPrice()));
                }else{
                    orderGood.setGoodPrice(activeGood.getActivePrice());
                }
                orderGood.setPriceType(priceType);
                orderGood.setGoodName(goods.getGoodTitle());
                orderGood.setGoodSpu(activeGood.getGoodSpu());
                orderGood.setClassCode(activeGood.getGoodClass());
                orderGood.setGoodNum(goods.getGoodNum());
                orderGood.setMemberPrice(activeGood.getMemberPrice());
                orderGood.setGoodAmount(activeGood.getBasePrice());
                orderGood.setModelName(goodModels.toString());
                List<String> goodWarehouses1 = Lists.newArrayListWithCapacity(goodWarehouseList.size());//商品仓库信息
                goodWarehouses.forEach(goodWarehouse -> {
                    goodWarehouses1.add(goodWarehouse.getWarehouseName());
                });
                orderGood.setWarehouseId(String.join(",", goodWarehouses1));
                //活动商品虚拟商品标识
                orderGood.setVirtualFlag(activeGood.getVirtualFlag().toString());//是否虚拟商品 1是 0否
                //虚拟商品类型 ,虚拟商品发货信息
                if (1 == activeGood.getVirtualFlag()) { //虚拟商品
                    VirtualGoodDto virtualGoodDto = getVirtualGoods(activeGood.getGoodId());
                    orderGood.setVirtualType(virtualGoodDto.getType());
                    orderGood.setSnapshotData(JSONObject.toJSONString(virtualGoodDto));
                }
                orderGood.setCombinaFlag(activeGood.getCombinaFlag());
                orderGood.setGoodImage(goodModelExes.get(0).getImage());
                orderGood.setLogisticsAmount(0);
                orderGood.setGoldPrice(0);
                orderGood.setCouponAmount(0);
                orderGood.setDiscountAmount(0);
                orderGoodList.add(orderGood);
            });
        }
        return orderGoodList;
    }

    /**
     * 获取虚拟商品信息
     *
     * @return
     */
    public VirtualGoodDto getVirtualGoods(Integer goodId) {
        ActiveGoodOrder activeGoodOrder = new ActiveGoodOrder();
        activeGoodOrder.setActiveType(ActiveGoodsConstants.ActiveType.VIRTUAL_GOOD);
        activeGoodOrder.setBusinessId(goodId);
        activeGoodOrder.setGoodId(goodId);
        ReturnData<List<ActiveGood>> returnData = activeFeignClient.queryOrderGood(activeGoodOrder);
        Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode().intValue(), returnData.getDesc());
        Assert.isTrue(Objects.nonNull(returnData.getData()) && returnData.getData().size() > 0, "商品已下架");
        ActiveGood activeGood = returnData.getData().get(0);
        String virtualType = activeGood.getArg1(); //虚拟商品类型
        String virtualContent = activeGood.getArg2(); //虚拟商品发货信息
        Assert.isTrue(StringUtils.isNotEmpty(virtualType) && StringUtils.isNotEmpty(virtualContent), "商品资料未找到");
        VirtualGoodDto virtualGoodDto = new VirtualGoodDto();
        virtualGoodDto.setId(activeGood.getMapperyId().intValue());
        virtualGoodDto.setGoodsbaseid(goodId);
        virtualGoodDto.setType(Integer.parseInt(virtualType));
        //虚拟商品类型 1:优惠券,2:买买金,3:话费,4:直冲话费
        if ("1".equals(virtualType)) {
            String[] couponIds = virtualContent.split(",");
            Integer[] couponIdList = new Integer[couponIds.length];
            for (int i = 0; i < couponIds.length; i++) {
                couponIdList[i] = Integer.parseInt(couponIds[i]);
            }
            virtualGoodDto.setCouponTemplateids(couponIdList);
        } else if ("2".equals(virtualType)) {
            virtualGoodDto.setNumber(Integer.parseInt(virtualContent));
        }
        virtualGoodDto.setIsVirtualGoods(1);
        return virtualGoodDto;
    }

    /**
     * 商品库存验证
     *
     * @param goodSku
     * @param goodNum
     */
    private void checkGoodStock(String goodName, String goodSku, int goodNum) {
        /*Map<Object, Object> combGood = isCombGood(goodSku);
        if (combGood != null && !combGood.isEmpty()) {
            Integer num = sumCombNum(combGood);
            Assert.isTrue(goodNum <= num, "[" + goodName + "]库存不足");
        } else {
            String key = GoodStockStatus.SKU_STOCK + goodSku;
            Object obj = redisTemplate.opsForValue().get(key);
            log.info("当前商品sku:{},查询库存数量为:{}", goodSku, obj);
            Assert.notNull(obj, goodName + "库存不足");
            int stockNum = (Integer) obj;
            Assert.isTrue(goodNum <= stockNum, "[" + goodName + "]库存不足");
        }*/
    }

    /**
     * 占用SKU库存记录
     *
     * @param orderNo
     * @param orderType
     * @param createTime
     * @param orderGoodList
     */
    public void occupyStock(String orderNo, Integer orderType, Date createTime, List<OrderGood> orderGoodList) {
        if (orderType == OrderType.SPIKE || orderType == OrderType.LOTTERY || orderType == OrderType.RELAY_LOTTERY || orderType == OrderType.BARGAIN)
            return;
        List<GoodStock> goodStockList = Lists.newArrayListWithCapacity(orderGoodList.size());
        orderGoodList.forEach(orderGood -> {
            goodStockList.add(toGoodStock(orderNo, orderType, orderGood.getGoodSku(), -orderGood.getGoodNum(), 1, new Date(createTime.getTime() + 900000)));
        });
        log.info("=> 占用SKU库存记录请求参数：{}", JSON.toJSONString(goodStockList));
        ReturnData returnData = goodFeignClient.occupy(goodStockList);
        log.info("=> 占用SKU库存记录返回数据：{}", JSON.toJSONString(returnData));
        isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, "库存不足", orderGoodList);
    }

    private void isTrue(boolean expression, String message, List<OrderGood> orderGoodList) {
        if (!expression) {
            throw new OrderStockException(message, orderGoodList);
        }
    }

    /**
     * 校验是否占用库存
     *
     * @param orderNo
     * @return
     */
    @Override
    public boolean checkOccupyTime(String orderNo, Integer orderType) {
        if (orderType == OrderType.SPIKE || orderType == OrderType.LOTTERY || orderType == OrderType.RELAY_LOTTERY || orderType == OrderType.BARGAIN)
            return true;
        ReturnData<Boolean> returnData = goodFeignClient.checkOccupyTime(orderNo);
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, "校验库存失败");
        return Objects.nonNull(returnData.getData()) ? returnData.getData() : false;
    }

    /**
     * 扣减SKU库存记录
     *
     * @param orderNo
     * @param orderType
     * @param orderGoodList
     */
    public void deductStock(String orderNo, Integer orderType, List<OrderGood> orderGoodList) {
        List<GoodStock> goodStockList = Lists.newArrayListWithCapacity(orderGoodList.size());
        orderGoodList.forEach(orderGood -> {
            goodStockList.add(toGoodStock(orderNo, orderType, orderGood.getGoodSku(), -orderGood.getGoodNum(), 2, null));
        });
        mqProducer.synOrderStock(goodStockList);
    }

    /**
     * 释放SKU库存记录
     *
     * @param orderNo
     * @param orderType
     * @param orderGoodList
     */
    public void relieveStock(String orderNo, Integer orderType, List<OrderGood> orderGoodList) {
        if (orderType == OrderType.SPIKE || orderType == OrderType.LOTTERY || orderType == OrderType.RELAY_LOTTERY || orderType == OrderType.BARGAIN)
            return;
        List<GoodStock> goodStockList = Lists.newArrayListWithCapacity(orderGoodList.size());
        orderGoodList.forEach(orderGood -> {
            goodStockList.add(toGoodStock(orderNo, orderType, orderGood.getGoodSku(), orderGood.getGoodNum(), 3, null));
        });
//        ReturnData returnData = goodFeignClient.deduct(goodStockList);
//        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, "释放库存失败");
        mqProducer.synOrderStock(goodStockList);
    }

    /**
     * 回退SKU库存记录
     *
     * @param orderNo
     * @param orderType
     * @param orderGoodList
     */
    public void rollbackStock(String orderNo, Integer orderType, List<OrderGood> orderGoodList) {
        if (orderType == OrderType.SPIKE || orderType == OrderType.LOTTERY || orderType == OrderType.RELAY_LOTTERY || orderType == OrderType.BARGAIN)
            return;
        List<GoodStock> goodStockList = Lists.newArrayListWithCapacity(orderGoodList.size());
        orderGoodList.forEach(orderGood -> {
            goodStockList.add(toGoodStock(orderNo, orderType, orderGood.getGoodSku(), orderGood.getGoodNum(), 4, null));
        });
//        ReturnData returnData = goodFeignClient.deduct(goodStockList);
//        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, "回退库存失败");
        mqProducer.synOrderStock(goodStockList);
    }

    private GoodStock toGoodStock(String orderNo, Integer orderType, String goodSku, Integer goodNum, int status, Date expireTime) {
        GoodStock goodStock = new GoodStock();
        goodStock.setGoodSku(goodSku);
        goodStock.setGoodNum(goodNum);
        goodStock.setStatus(status);
        goodStock.setBusinessId(orderNo);
        goodStock.setBusinessType("order" + orderType);
        goodStock.setExpireTime(expireTime);
        return goodStock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderGoods(OrderInfo orderInfo, List<OrderGood> orderGoodList) {
        Assert.isTrue(orderGoodList.size() > 0, "无商品信息");
        boolean result = insertBatch(orderGoodList);
        Assert.isTrue(result, "保存订单商品信息失败");
        log.info("当前用户{}，完成订单商品保存信息,订单号为:{},订单ID为:{}", orderInfo.getCreaterId(), orderInfo.getOrderNo(), orderInfo.getOrderId());
    }

    /**
     * 通过订单号查询订单商品信息
     *
     * @param orderNo
     * @param userId
     * @return
     */
    @Override
    public List<OrderGoodsDto> selectByOrderNo(String orderNo, Long userId) {
        log.info("当前用户{}通过订单号{}查询订单商品", userId, orderNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderNo);
        List<OrderGood> orderGoodList = orderGoodMapper.selectByMap(map);
        log.info("当前用户{}通过订单号{}查询订单商品的结果集合:{}", userId, orderNo, orderGoodList);
        if (orderGoodList == null || orderGoodList.size() <= 0) {
            log.info("当前用户{}通过订单号{}查询订单商品的结果为空", userId, orderNo);
            return null;
        }
        List<OrderGoodsDto> list = new ArrayList<>();
        orderGoodList.stream().forEach(r -> {
            OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
            orderGoodsDto.setGoodId(r.getGoodId());
            orderGoodsDto.setGoodImage(r.getGoodImage());
            orderGoodsDto.setGoodTitle(r.getGoodName());
            orderGoodsDto.setGoodNum(r.getGoodNum());
            orderGoodsDto.setDiscountAmount(null == r.getDiscountAmount() ? "" : r.getDiscountAmount().toString());
            orderGoodsDto.setCouponAmount(null == r.getCouponAmount() ? "" : r.getCouponAmount().toString());
            orderGoodsDto.setGoodAmount(null == r.getGoodAmount() ? "" : r.getGoodAmount().toString());
            orderGoodsDto.setVirtualGoodFlag(r.getVirtualFlag());


            orderGoodsDto.setModelName(r.getModelName());
            orderGoodsDto.setCreateTime(r.getCreaterTime());
            list.add(orderGoodsDto);
        });
        log.info("当前用户{}通过订单号{}查询订单商品封装完毕!", userId, orderNo);
        return list;
    }

    @Override
    public List<OrderGood> selectByOrderNo(String orderNo) {
        return selectByPackageNo(orderNo, null);
    }

    @Override
    public List<OrderGood> selectByPackageNo(String orderNo, String packageNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderGood queryOrderGood = new OrderGood();
        queryOrderGood.setOrderNo(orderNo);
        queryOrderGood.setPackageNo(packageNo);
        EntityWrapper<OrderGood> orderGoodEntityWrapper = new EntityWrapper<>(queryOrderGood);
        return selectList(orderGoodEntityWrapper);
    }

    @Override
    public void unpickGoodsAmount(OrderInfo orderInfo, List<OrderGood> orderGoodList) {
        Integer goodAmount = orderInfo.getGoodAmount(); //商品金额
        /*********************************金额分摊**********************************/
        Integer couponAmount = orderInfo.getCouponAmount(); //优惠券优惠金额
        Integer discountAmount = orderInfo.getDiscountAmount(); //活动优惠金额
        Integer logisticsAmount = orderInfo.getExpressAmount(); //快递费
        Integer goldPrice = orderInfo.getGoldPrice(); //买买金兑换金额
        //根据单个商品金额排序
        orderGoodList = orderGoodList.stream().sorted(new Comparator<OrderGood>() {
            @Override
            public int compare(OrderGood o1, OrderGood o2) {
                Integer unitPrice1 = orderInfo.getMemberOrder() ? o1.getMemberPrice() : o1.getGoodPrice();
                Integer unitPrice2 = orderInfo.getMemberOrder() ? o2.getMemberPrice() : o2.getGoodPrice();
                Integer amount1 = unitPrice1 * o1.getGoodNum();
                Integer amount2 = unitPrice2 * o2.getGoodNum();
                return amount1.compareTo(amount2);
            }
        }).collect(Collectors.toList());
        //分摊金额至商品中
        OrderGood totalOrderGood = new OrderGood();
        totalOrderGood.setCouponAmount(0);
        totalOrderGood.setDiscountAmount(0);
        totalOrderGood.setLogisticsAmount(0);
        totalOrderGood.setGoldPrice(0);
        orderGoodList.forEach(orderGood -> {
            int unitPrice = orderInfo.getMemberOrder() ? orderGood.getMemberPrice() : orderGood.getGoodPrice();
            int amount = unitPrice * orderGood.getGoodNum();
            BigDecimal scale = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(goodAmount), 2, BigDecimal.ROUND_FLOOR);//权重不四舍五入
            log.debug("=> 订单拆单 orderNo:{},goodAmount:{},memberOrder:{},goodSku:{},unitPrice:{},goodNum:{},scale:{}",
                    orderInfo.getOrderNo(), goodAmount, orderInfo.getMemberOrder(),
                    orderGood.getGoodSku(), unitPrice, orderGood.getGoodNum(), scale);
            if (Objects.nonNull(couponAmount) && couponAmount > 0) {
                int goodCouponAmount = scale.multiply(BigDecimal.valueOf(couponAmount)).intValue();
                orderGood.setCouponAmount(goodCouponAmount);
                log.debug("=> 订单拆单,商品优惠券优惠金额分摊 orderNo:{},goodSku:{},goodCouponAmount:{}", orderInfo.getOrderNo(), orderGood.getGoodSku(), goodCouponAmount);
            } else {
                orderGood.setCouponAmount(0);
            }
            totalOrderGood.setCouponAmount(totalOrderGood.getCouponAmount() + orderGood.getCouponAmount());
            if (Objects.nonNull(discountAmount) && discountAmount > 0) {
                int goodDiscountAmount = scale.multiply(BigDecimal.valueOf(discountAmount)).intValue();
                orderGood.setDiscountAmount(goodDiscountAmount);
                log.debug("=> 订单拆单,商品活动优惠金额分摊 orderNo:{},goodSku:{},goodDiscountAmount:{}", orderInfo.getOrderNo(), orderGood.getGoodSku(), goodDiscountAmount);
            } else {
                orderGood.setDiscountAmount(0);
            }
            totalOrderGood.setDiscountAmount(totalOrderGood.getDiscountAmount() + orderGood.getDiscountAmount());
            if (Objects.nonNull(logisticsAmount) && logisticsAmount > 0) {
                int goodLogisticsAmount = scale.multiply(BigDecimal.valueOf(logisticsAmount)).intValue();
                orderGood.setLogisticsAmount(goodLogisticsAmount);
                log.debug("=> 订单拆单,商品快递费金额分摊 orderNo:{},goodSku:{},goodLogisticsAmount:{}", orderInfo.getOrderNo(), orderGood.getGoodSku(), goodLogisticsAmount);
            } else {
                orderGood.setLogisticsAmount(0);
            }
            totalOrderGood.setLogisticsAmount(totalOrderGood.getLogisticsAmount() + orderGood.getLogisticsAmount());
            if (Objects.nonNull(goldPrice) && goldPrice > 0) {
                int goodGoldPrice = scale.multiply(BigDecimal.valueOf(goldPrice)).intValue();
                orderGood.setGoldPrice(goodGoldPrice);
                log.debug("=> 订单拆单,商品买买金兑换金额分摊 orderNo:{},goodSku:{},goodGoldPrice:{}", orderInfo.getOrderNo(), orderGood.getGoodSku(), goodGoldPrice);
            } else {
                orderGood.setGoldPrice(0);
            }
            totalOrderGood.setGoldPrice(totalOrderGood.getGoldPrice() + orderGood.getGoldPrice());
        });
        OrderGood maxAmountGood = orderGoodList.get(orderGoodList.size() - 1);
        int couponAmountRemainder = couponAmount - totalOrderGood.getCouponAmount();
        if (couponAmountRemainder > 0) {
            maxAmountGood.setCouponAmount(maxAmountGood.getCouponAmount() + couponAmountRemainder);
        }
        int discountAmountRemainder = discountAmount - totalOrderGood.getDiscountAmount();
        if (discountAmountRemainder > 0) {
            maxAmountGood.setDiscountAmount(maxAmountGood.getDiscountAmount() + discountAmountRemainder);
        }
        int logisticsAmountRemainder = logisticsAmount - totalOrderGood.getLogisticsAmount();
        if (logisticsAmountRemainder > 0) {
            maxAmountGood.setLogisticsAmount(maxAmountGood.getLogisticsAmount() + logisticsAmountRemainder);
        }
        int goldPriceRemainder = goldPrice - totalOrderGood.getGoldPrice();
        if (goldPriceRemainder > 0) {
            maxAmountGood.setGoldPrice(maxAmountGood.getGoldPrice() + goldPriceRemainder);
        }
    }

    public Map<Object, Object> isCombGood(String goodSku) {
        return redisTemplate.opsForHash().entries(ActiveGoodsConstants.SKU_STOCK_COMBINE + goodSku);
    }

    public Integer sumCombNum(Map<Object, Object> map) {
        Integer subStock = 0;
        if (map != null && !map.isEmpty()) {
            Iterator<Object> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String subGoodSku = String.valueOf(iterator.next());//子sku
                Integer num = (Integer) map.get(subGoodSku);//包裹数
                if (subGoodSku != null && !"".equals(subGoodSku)) {
                    Object stock = redisTemplate.opsForValue().get(ActiveGoodsConstants.SKU_STOCK + String.valueOf(subGoodSku));//单品库存
                    if (stock != null && !"".equals(stock)) {
                        Integer sub = ((Integer) stock) / num;
                        if (subStock == null || subStock == 0) {
                            subStock = sub;
                        } else if (sub.compareTo(subStock) < 0) {
                            subStock = sub;
                        }
                    }
                }
            }
        }

        return subStock;
    }
}
