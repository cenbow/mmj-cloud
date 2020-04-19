package com.mmj.aftersale.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.aftersale.common.feigin.OrderFeignClient;
import com.mmj.aftersale.common.feigin.PayFeignClient;
import com.mmj.aftersale.common.feigin.UserFeignClient;
import com.mmj.aftersale.common.model.*;
import com.mmj.aftersale.constant.*;
import com.mmj.aftersale.constant.HttpURLConnectionUtil;
import com.mmj.aftersale.mapper.*;
import com.mmj.aftersale.model.*;
import com.mmj.aftersale.model.dto.*;
import com.mmj.aftersale.model.dto.AfterSaleDto;
import com.mmj.aftersale.model.vo.*;
import com.mmj.aftersale.service.AfterSalesService;
import com.mmj.aftersale.service.ReturnAddressService;
import com.mmj.aftersale.utils.AfterNoUtils;
import com.mmj.aftersale.utils.MQProducer;
import com.mmj.aftersale.utils.MessageUtils;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 售后信息表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Service
public class AfterSalesServiceImpl extends ServiceImpl<AfterSalesMapper, AfterSales> implements AfterSalesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AfterSalesMapper afterSalesMapper;
    @Autowired
    private AfterPersonMapper afterPersonMapper;
    @Autowired
    private ReturnAddressService returnAddressService;
    @Autowired
    private AfterCustomMapper afterCustomMapper;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private AfterGoodMapper afterGoodMapper;
    @Autowired
    private AfterJstMapper afterJstMapper;
    @Autowired
    private PayFeignClient payFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private MQProducer mqProducer;
    @Autowired
    private UserMergeProcessor userMergeProcessor;
    @Autowired
    private AfterNoUtils afterNoUtils;
    @Autowired
    private MessageUtils messageUtils;


    /**
     * 审核详情
     *
     * @param moenyVo
     * @return
     */
    @Override
    public AfterSalesDetailDto getAfterSalesDetail(MoenyVo moenyVo) {
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("AFTER_SALE_NO", moenyVo.getAfterSaleNo());
        afterSalesEntityWrapper.ge("DEL_FLAG", 1);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, moenyVo.getCreaterId());
        List<AfterSales> afterSales = afterSalesMapper.selectList(afterSalesEntityWrapper);
        Assert.isTrue((afterSales.size() > 0 && afterSales != null), "售后单号异常");

        AfterSalesDetailDto afterSalesDetailDto = new AfterSalesDetailDto();
        afterSalesDetailDto.setAfterSaleDate(afterSales.get(0).getCreaterTime());
        afterSalesDetailDto.setOrderNo(afterSales.get(0).getOrderNo());

        EntityWrapper<AfterPerson> afterPersonEntityWrapper = new EntityWrapper<>();
        afterPersonEntityWrapper.eq("AFTER_SALE_NO", moenyVo.getAfterSaleNo());
        afterPersonEntityWrapper.eq("DEL_FLAG", 1);
        List<AfterPerson> afterPeople = afterPersonMapper.selectList(afterPersonEntityWrapper);
        if (afterPeople.size() > 0 && afterPeople != null) {
            afterSalesDetailDto.setUserRemarks(afterPeople.get(0).getUserRemark());
            // 图片
            String images = afterPeople.get(0).getAfterImage();
            List<String> imagesList = JSONObject.parseArray(images, String.class);
            afterSalesDetailDto.setUserImages(imagesList);
        }
        return afterSalesDetailDto;
    }


    /**
     * 审核通过
     *
     * @param afterSalesResultVo
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void auditResult(AfterSalesResultVo afterSalesResultVo) {
        boolean bool = afterSalesResultVo.isAuditStatus();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, afterSalesResultVo.getCreaterId());

        // 审核表（防止多次调用接口）
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("AFTER_SALE_NO", afterSalesResultVo.getAfterSaleNo());
        List<AfterSales> afterSalesList = afterSalesMapper.selectList(afterSalesEntityWrapper);
        if (afterSalesList != null && afterSalesList.size() > 0) {
            if (AfterSalesStatus.RETURN_GOODS_PASS.getStatus().equals(afterSalesList.get(0).getAfterStatus()) || AfterSalesStatus.RETRUN_GOODS_REFUSE.getStatus().equals(afterSalesList.get(0).getAfterStatus())) {
                Assert.isTrue(false, "不能多次操作");
            }
        }

        // 审核通过
        if (bool) {
            Integer addressId = afterSalesResultVo.getAddressId();
            Assert.isTrue(!(addressId == null), "仓库地址不能空!");
            AfterSaleAddressDto afterSaleAddressDto = returnAddressService.getAfterSaleAddressDto(afterSalesResultVo.getAddressId());

            AfterPerson afterPerson = new AfterPerson();
            afterPerson.setWarehousePerson(afterSaleAddressDto.getWarehousePerson());
            afterPerson.setWarehouseAddr(afterSaleAddressDto.getWarehouseAddr());
            afterPerson.setWarehouseMobile(afterSaleAddressDto.getWarehouseMobile());
            afterPerson.setCreaterId(Long.valueOf(afterSalesResultVo.getCreaterId()));

            //修改用户提交表
            EntityWrapper<AfterPerson> afterPersonEntityWrapper = new EntityWrapper<>();
            afterPersonEntityWrapper.eq("AFTER_SALE_NO", afterSalesResultVo.getAfterSaleNo());
            afterPersonMapper.update(afterPerson, afterPersonEntityWrapper);

            // 修改申请表
            AfterSales afterSales = new AfterSales();
            afterSales.setAfterStatus(AfterSalesStatus.RETURN_GOODS_PASS.getStatus());
            afterSalesMapper.update(afterSales, afterSalesEntityWrapper);
            // 修改备注表
            if (afterSalesResultVo.getRemarks() != null) {
                List<AfterSales> list = afterSalesMapper.selectList(afterSalesEntityWrapper);

                AfterCustom afterCustom = new AfterCustom();
                afterCustom.setUserRemark(afterSalesResultVo.getRemarks());
                afterCustom.setAfterSaleNo(afterSalesResultVo.getAfterSaleNo());
                afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_CONSUMER_TYPE.getStatus());
                afterCustom.setOrderNo(list.get(0).getOrderNo());
                afterCustom.setCreaterId(Long.valueOf(afterSalesResultVo.getCreaterId()));
                afterCustomMapper.insert(afterCustom);

                //模板消息
                messageUtils.afterSaleReturn(Long.valueOf(list.get(0).getCreaterId()), list.get(0).getOrderNo(), true);
            }
        } else {
            Assert.isTrue(!(afterSalesResultVo.getRemarks() == null), "拒绝原因不能为空!");

            AfterSales afterSales = new AfterSales();
            afterSales.setAfterStatus(AfterSalesStatus.RETRUN_GOODS_REFUSE.getStatus());
            // 修改申请表
            afterSalesMapper.update(afterSales, afterSalesEntityWrapper);
            mqProducer.updateAfterStatus(afterSalesList.get(0).getOrderNo(), Long.valueOf(afterSalesResultVo.getCreaterId()), afterSalesResultVo.getAfterSaleNo());

            AfterPerson afterPerson = new AfterPerson();
            afterPerson.setUserRemark(afterSalesResultVo.getRemarks());
            afterPerson.setCreaterId(Long.valueOf(afterSalesResultVo.getCreaterId()));

            // 修改审核表
            EntityWrapper<AfterPerson> afterPersonEntityWrapper = new EntityWrapper<>();
            afterPersonEntityWrapper.eq("AFTER_SALE_NO", afterSalesResultVo.getAfterSaleNo());
            afterPersonMapper.update(afterPerson, afterPersonEntityWrapper);

            // 备注表
            List<AfterSales> list = afterSalesMapper.selectList(afterSalesEntityWrapper);

            AfterCustom afterCustom = new AfterCustom();
            afterCustom.setUserRemark(afterSalesResultVo.getRemarks());
            afterCustom.setAfterSaleNo(afterSalesResultVo.getAfterSaleNo());
            afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_AUDIT_TYPE.getStatus());
            afterCustom.setOrderNo(list.get(0).getOrderNo());
            afterCustom.setCreaterId(Long.valueOf(afterSalesResultVo.getCreaterId()));
            afterCustomMapper.insert(afterCustom);

            //模板消息
            messageUtils.afterSaleReturn(list.get(0).getCreaterId(), list.get(0).getOrderNo(), false);

            //拒绝退款把买买金修改正常
            mqProducer.updateMMKing(list.get(0).getOrderNo(), afterCustom.getCreaterId(), OrderKingStatus.NORMAL.getStatus());
        }

    }


    /**
     * 质检接口
     *
     * @param afterSalesTestVo
     */
    @Override
    public void afterSalesTest(AfterSalesTestVo afterSalesTestVo) {
        boolean bool = afterSalesTestVo.isAuditStatus();

        // 防止多次调用接口
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("AFTER_SALE_NO", afterSalesTestVo.getAfterSaleNo());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, afterSalesTestVo.getCreaterId());
        List<AfterSales> afterSalesList = afterSalesMapper.selectList(afterSalesEntityWrapper);
        if (!afterSalesList.isEmpty() && afterSalesList.size() > 0) {
            if (AfterSalesStatus.QUALITY_TEST_PASS.getStatus().equals(afterSalesList.get(0).getAfterStatus()) || AfterSalesStatus.QUALITY_TEST_REFISE.getStatus().equals(afterSalesList.get(0).getAfterStatus())) {
                Assert.isTrue(false, "不能多次审核或质检");
            }
        }

        if (bool) {
            //  修改申请表
            AfterSales afterSales = new AfterSales();
            afterSales.setAfterStatus(AfterSalesStatus.QUALITY_TEST_PASS.getStatus());
            afterSalesMapper.update(afterSales, afterSalesEntityWrapper);

            // 备注表，当备注不为空时
            if (StringUtils.isNotBlank(afterSalesTestVo.getRemarks())) {
                afterSalesEntityWrapper.eq("DEL_FLAG", 1);
                List<AfterSales> list = afterSalesMapper.selectList(afterSalesEntityWrapper);

                AfterCustom afterCustom = new AfterCustom();
                afterCustom.setAfterSaleNo(afterSalesTestVo.getAfterSaleNo());
                afterCustom.setUserRemark(afterSalesTestVo.getRemarks());
                afterCustom.setOrderNo(list.get(0).getOrderNo());
                afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_TEST_TYPE.getStatus());
                afterCustom.setCreaterId(Long.valueOf(afterSalesTestVo.getCreaterId()));
                afterCustomMapper.insert(afterCustom);

                //模板消息
                messageUtils.afterSaleAccept(list.get(0).getCreaterId(), list.get(0).getOrderNo(), true);
                logger.info("质检通过修改订单买买金状态,orderNo:{}", list.get(0).getOrderNo());
                mqProducer.updateMMKing(list.get(0).getOrderNo(), afterCustom.getCreaterId(), OrderKingStatus.DELETE.getStatus());
            }
        } else {
            Assert.isTrue(!(StringUtils.isBlank(afterSalesTestVo.getRemarks())), "拒绝原因不能为空");
            //  修改申请表
            AfterSales afterSales = new AfterSales();
            afterSales.setAfterStatus(AfterSalesStatus.QUALITY_TEST_REFISE.getStatus());
            EntityWrapper<AfterSales> afterSalesEntityWrapper1 = new EntityWrapper<>();
            afterSalesEntityWrapper1.eq("AFTER_SALE_NO", afterSalesTestVo.getAfterSaleNo());
            afterSalesMapper.update(afterSales, afterSalesEntityWrapper1);
            //售后状态变更同步
            mqProducer.updateAfterStatus(afterSalesList.get(0).getOrderNo(), afterSalesList.get(0).getCreaterId(), afterSalesTestVo.getAfterSaleNo());

            // 审核记录
            AfterPerson afterPerson = new AfterPerson();
            afterPerson.setUserRemark(afterSalesTestVo.getRemarks());
            afterPerson.setCreaterId(Long.valueOf(afterSalesTestVo.getCreaterId()));

            EntityWrapper<AfterPerson> afterPersonEntityWrapper = new EntityWrapper<>();
            afterPersonEntityWrapper.eq("AFTER_SALE_NO", afterSalesTestVo.getAfterSaleNo());
            afterPersonMapper.update(afterPerson, afterPersonEntityWrapper);

            // 备注表
            AfterCustom afterCustom = new AfterCustom();
            afterCustom.setAfterSaleNo(afterSalesTestVo.getAfterSaleNo());
            afterCustom.setUserRemark(afterSalesTestVo.getRemarks());
            afterCustom.setOrderNo(!afterSalesList.isEmpty() ? afterSalesList.get(0).getOrderNo() : "");
            afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_TEST_TYPE.getStatus());
            afterCustom.setCreaterId(Long.valueOf(afterSalesTestVo.getCreaterId()));
            afterCustomMapper.insert(afterCustom);

            //模板消息
            messageUtils.afterSaleAccept(afterSalesList.get(0).getCreaterId(), afterSalesList.get(0).getOrderNo(), false);

            logger.info("质检不通过修改订单买买金状态为可用,orderNo:{}", afterSalesList.get(0).getOrderNo());
            mqProducer.updateMMKing(afterSalesList.get(0).getOrderNo(), afterCustom.getCreaterId(), OrderKingStatus.NORMAL.getStatus());
        }
    }

    /**
     * 退款详情页
     *
     * @param moenyVo
     * @return
     */
    @Override
    public MoneyDetailVo getMoneyDetail(MoenyVo moenyVo) {

        MoneyDetailVo moneyDetailVo = new MoneyDetailVo();
        logger.info("进入退款详情也查询{},{}", moenyVo.getCreaterId(), moenyVo.getAfterSaleNo());

        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("AFTER_SALE_NO", moenyVo.getAfterSaleNo());
        afterSalesEntityWrapper.eq("DEL_FLAG", 1);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, moenyVo.getCreaterId());
        List<AfterSales> afterSalesList = afterSalesMapper.selectList(afterSalesEntityWrapper);

        Assert.isTrue((afterSalesList.size() > 0 && afterSalesList != null), "售后单号异常");
        AfterSales afterSales = afterSalesList.get(0);

        OrderInfo orders = getOrderInfo(afterSales.getOrderNo(), afterSales.getCreaterId());
        Assert.isTrue((orders != null), "查询订单信息为空");
        moneyDetailVo.setOrderNo(afterSales.getOrderNo());
        moneyDetailVo.setFreight(PriceConversion.intToString(orders.getExpressAmount().intValue()));  // 运费 orders.getFreight()
        moneyDetailVo.setOrderAmount(PriceConversion.intToString(orders.getOrderAmount().intValue()));  //  订单金额 orders.getOrderAmount()
        moneyDetailVo.setCouponAmount(PriceConversion.intToString(orders.getCouponAmount().intValue())); // 优惠金额  orders.getCouponPrice()

        //TODO 文浩还没做 返现金额
//        RefOtherOrderVo refOtherOrderVo = new RefOtherOrderVo();
//        BuyBackQualificationEx buyBackQualificationEx = buyBackQualificationService.queryRefundByOrderNo(orderNo);

        OrderAfterVo orderAfterVo = new OrderAfterVo();
        orderAfterVo.setOrderNo(orders.getOrderNo());
        orderAfterVo.setUserId(String.valueOf(orders.getCreaterId()));
        OrderPayment orderPayment = orderFeignClient.selectByOrderPayment(orderAfterVo).getData();

        Integer returnAmount = orderPayment.getPayAmount().intValue();
//        if (buyBackQualificationEx != null) {
//            if (!"yes".equals(buyBackQualificationEx.getRefund())) {
//                moneyDetailVo.setResultAmount(PriceConversion.intToString(buyBackQualificationEx.getRedMoney()));  // 返现金额
//                if (StringUtils.isNotBlank(buyBackQualificationEx.getBackRefOrderNo())) {
//                    refOtherOrderVo.setOtherOrderNo(buyBackQualificationEx.getBackRefOrderNo());
//                }
//                moneyDetailVo.setRefOtherOrderVo(refOtherOrderVo);
//                if (buyBackQualificationEx.getRedMoney() != null) {
//                    returnAmount = returnAmount - buyBackQualificationEx.getRedMoney();
//                }
//            } else {
//                moneyDetailVo.setResultAmount("0");  // 返现金额
//            }
//        } else {
//            moneyDetailVo.setResultAmount("0");  // 返现金额
//        }

        //优惠券
        CouponDto couponDto = new CouponDto();
        OrderCouponVo orderCouponVo = new OrderCouponVo();
        orderCouponVo.setOrderNo(orders.getOrderNo());
        orderCouponVo.setUserId(orders.getCreaterId());
        ReturnData<List<UserCouponDto>> returnData = userFeignClient.myOrderCouponList(orderCouponVo);

        UserCouponDto userCouponDto = returnData.getData().size() > 0 ? returnData.getData().get(0) : new UserCouponDto();

        if (userCouponDto != null) {
            couponDto.setCouponCode(userCouponDto.getCouponCode() == null ? "" : userCouponDto.getCouponCode().toString());
            logger.info("查询优惠值===============");
            if (!Objects.isNull(userCouponDto.getCouponInfo()) && null != userCouponDto.getCouponInfo().getCouponValue()) {
                couponDto.setCouponMoney(PriceConversion.intToString(Integer.valueOf(userCouponDto.getCouponInfo().getCouponValue())));
            }
            couponDto.setCouponName(!Objects.isNull(userCouponDto.getCouponInfo()) && !"".equals(userCouponDto.getCouponInfo().getCouponTitle()) ? userCouponDto.getCouponInfo().getCouponTitle() : "");
        }
        moneyDetailVo.setCouponDto(couponDto);

        //获取推荐分享返现金额
        //开始更新分享金额接口
        logger.info("当前订单号{},开始更新分享金额接口", orders.getOrderNo());
        Map<String, Object> refMap = new HashMap<>();
        refMap.put("userId", afterSales.getCreaterId());
        refMap.put("orderNo", orders.getOrderNo());
        userFeignClient.queryRefundByOrderNo(refMap);
        logger.info("当前订单号{},更新分享金额接口接口完毕", orders.getOrderNo());


        //判断会员是否降级
        Map<String, Object> map = userFeignClient.getMemberThresholdAsConsumeBoss(afterSales.getCreaterId()).getData();
        boolean bool = false;
        logger.info("查询用户消费结果:{}", JSON.toJSONString(map));
        if (!map.isEmpty()) {
            double consumeMoney = (Double) map.get("consumeMoney");
            logger.info("累计消费金额金额(包含当单):{}", consumeMoney);
            consumeMoney = consumeMoney - (orders.getOrderAmount().divide(new BigDecimal(100), 2, BigDecimal.ROUND_DOWN)).doubleValue();
            int memberThreshold = (Integer) map.get("memberThreshold");
            logger.info("累计消费金额:{},阀值:{}", consumeMoney, memberThreshold);
            bool = consumeMoney < memberThreshold;
        }

        logger.info("会员是否降级:{}", bool);
        if (bool) {
            //会员降级，加一个提示使用了多少买买金
            /*
            1.  查询买送获得的买买金 A
                查询账户买买金总额 B
                若 B>=A(没用过买送的买买金，降级时:买买金总额减去买送活动获得的买买金 B-A。boss后台不用展示)
                若 A>B (买买金花掉了，降级时:boss 后台展示：用掉的买买金：A-B 。boss后台要展示)
                买买金明细也要写进去
             */
            int num = userFeignClient.getOweKingNum(orders.getOrderNo(), orders.getCreaterId());
            moneyDetailVo.setOweKingNum(num);
        }

        if (orders.getOrderType().equals("11")) {
            logger.info("计算免费送退款,订单号:{}", orders.getOrderNo());

            OrderGroup group = new OrderGroup();
            group.setLaunchOrderNo(orders.getOrderNo());
            group.setGroupType(6);
            group = orderFeignClient.getGroupInfo(group);
            if (group != null && group.getGroupStatus() == 1) {
                Double resultAmount = null == orders.getOrderAmount() ? 0 : orders.getOrderAmount().
                        divide(new BigDecimal(100), 2, BigDecimal.ROUND_DOWN).doubleValue();
                moneyDetailVo.setResultAmount(resultAmount + "元");
                logger.info("免费送退款信息,returnAmount:{},orderAmount:{}", returnAmount, orders.getOrderAmount().intValue());
                returnAmount -= orders.getOrderAmount().intValue();
            }
        }
        if (returnAmount <= 0 || returnAmount > orders.getOrderAmount().intValue()) {
            logger.info("可退金额:{}", returnAmount);
            moneyDetailVo.setCanRefundAmount(PriceConversion.intToString(0));
        } else {
            logger.info("可退金额:{}", returnAmount);
            moneyDetailVo.setCanRefundAmount(PriceConversion.intToString(returnAmount));
        }


        return moneyDetailVo;
    }

    /**
     * 退款
     *
     * @param moneyReturnVo
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRED)
    public String getMoneyReturn(MoneyReturnVo moneyReturnVo) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, moneyReturnVo.getCreaterId());

        // 防止多次调用
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("AFTER_SALE_NO", moneyReturnVo.getAfterSaleNo());
        afterSalesEntityWrapper.eq("DEL_FLAG", 1);
        List<AfterSales> afterSalesList = afterSalesMapper.selectList(afterSalesEntityWrapper);
        if (afterSalesList.isEmpty()) {
            Assert.isTrue(false, "退款售后单号异常");
        }

        String afterSaleNo = moneyReturnVo.getAfterSaleNo();
        AfterSales afterSales = afterSalesList.get(0);
        String orderNo = afterSales.getOrderNo();
        AfterGood afterGood = new AfterGood();
        OrderInfo orderInfo = getOrderInfo(orderNo, afterSales.getCreaterId());

        afterGood.setReturnAmount(moneyReturnVo.getRefundAmount());  // 退款金额
        afterGood.setOrderAmount(orderInfo.getOrderAmount());  // 订单金额
        afterGood.setLogisticsAmount("0");   // 运费
        afterGood.setCouponAmount(orderInfo.getCouponAmount()); // 优惠金额
        //TODO 返现没做 返现金额参数
        afterGood.setAfterDesc(moneyReturnVo.getRefundRemarks()); // 退款备注
        afterGood.setOrderNo(orderNo);
        afterGood.setAfterSaleNo(afterSaleNo);
        afterGood.setCreaterId(afterSales.getCreaterId());
        afterGoodMapper.insert(afterGood);
        // 备注同步到备注表
        AfterCustom afterCustom = new AfterCustom();
        afterCustom.setUserRemark(moneyReturnVo.getRefundRemarks());
        afterCustom.setAfterSaleNo(afterSaleNo);
        afterCustom.setOrderNo(orderNo);
        afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_CONSUMER_TYPE.getStatus());
        afterCustom.setCreaterId(afterSales.getCreaterId());
        afterCustomMapper.insert(afterCustom);
        //  修改申请表
        AfterSales as = new AfterSales();
        as.setAfterStatus(AfterSalesStatus.RETURN_MONEY_FINISH.getStatus());
        afterSalesMapper.update(as, afterSalesEntityWrapper);

        // 关闭订单
        orderFeignClient.close(Lists.newArrayList(orderNo));

        //开始更新分享金额接口
        logger.info("当前订单号{},开始更新分享金额接口", orderNo);
        Map<String, Object> refMap = new HashMap<>();
        refMap.put("userId", afterSales.getCreaterId());
        refMap.put("orderNo", orderNo);
        userFeignClient.queryRefundByOrderNo(refMap);
        logger.info("当前订单号{},更新分享金额接口接口完毕", orderNo);

        //判断会员是否降级
        Map<String, Object> map = userFeignClient.getMemberThresholdAsConsumeBoss(afterSales.getCreaterId()).getData();
        boolean bool = false;
        if (!map.isEmpty()) {
            double consumeMoney = (Double) map.get("consumeMoney");
            int memberThreshold = (Integer) map.get("memberThreshold");
            bool = consumeMoney < memberThreshold;
        }
        UserMember userMember = userFeignClient.queryUserMemberInfoByUserId(afterCustom.getCreaterId()).getData();
        boolean isMember = userMember != null ? userMember.getActive() : false;

        if (bool && isMember && !"BUY".equals(userMember.getBeMemberType())) {
            logger.info("申请售后导致会员降级，orderNo:{}", orderInfo.getOrderNo());
            //降级
            DegradeVo degradeVo = new DegradeVo();
            degradeVo.setRemark(String.format("订单%s售后成功后不满足会员条件后降级", orderInfo.getOrderNo()));
            degradeVo.setUserId(userMember.getUserId());
            boolean degrade = (Boolean) userFeignClient.degrade(degradeVo).getData();
            logger.info("userId:{}降级结果{}", afterSales.getCreaterId(), degrade);

            //取消买送资格
            boolean buyGive = userFeignClient.editBuyGice(afterSales.getCreaterId()).getData();
            logger.info("userId:{}取消买送资格结果{}，", afterSales.getCreaterId(), buyGive);

            userFeignClient.degradeProces(orderNo, orderInfo.getCreaterId());
        }

        //  退款金额为0的只改订单与售后的状态
        int refundAmount = moneyReturnVo.getRefundAmount().intValue();
        if (refundAmount <= 0) {
            logger.info("退款金额小于等于零的订单号{}", orderNo);
            // 模板消息
            messageUtils.afterSaleRefund(afterSales.getCreaterId(), orderNo, "0");
            return "success";
        } else {
            // 退款
            String result = orderRefund(orderNo, moneyReturnVo.getCreaterId(), moneyReturnVo.getRefundAmount(), moneyReturnVo.getRefundRemarks());
            if (result.equals("success")) {
                //模板消息推送
                messageUtils.afterSaleRefund(afterSales.getCreaterId(), orderNo, PriceConversion.intToString(refundAmount));
                return "success";
            } else if (result.equals("fail")) {
                return "fail";
            } else {
                return result;
            }
        }
    }


    /**
     * @param afterSalesListVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<AfterSalesListDto> getAfterSalesLists(AfterSalesListVo afterSalesListVo) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        if (StringUtils.isNotBlank(afterSalesListVo.getBeginOrderDate()) && StringUtils.isNotBlank(afterSalesListVo.getEndOrderDate())) {
            Date beginOrderDate = DateUtils.parse(afterSalesListVo.getBeginOrderDate());
            Date endOrderDate = DateUtils.parse(afterSalesListVo.getEndOrderDate());
            Assert.isTrue(beginOrderDate.getTime() <= endOrderDate.getTime(), "开始时间大于结束");
        }

        if (StringUtils.isNotBlank(afterSalesListVo.getBeginAfSaleDate()) && StringUtils.isNotBlank(afterSalesListVo.getEndOrderDate())) {
            Date beginSaleDate = DateUtils.parse(afterSalesListVo.getBeginAfSaleDate());
            Date endSaleDate = DateUtils.parse(afterSalesListVo.getEndOrderDate());
            Assert.isTrue(beginSaleDate.getTime() <= endSaleDate.getTime(), "开始时间大于结束");
        }

        Page<AfterSalesListDto> page = new Page<>(afterSalesListVo.getCurrentPage(), afterSalesListVo.getPageSize());
        List<AfterSales> relayInfos = afterSalesMapper.queryAfterSalesList(page, afterSalesListVo);
//        updateAfterSales(relayInfos);

        List<AfterSalesListDto> listDtos = new ArrayList<>();
        for (int i = 0; i < relayInfos.size(); i++) {
            AfterSales afterSales = relayInfos.get(i);
            AfterSalesListDto afterSalesListDto = new AfterSalesListDto();
            BeanUtils.copyProperties(afterSales, afterSalesListDto);
            afterSalesListDto.setAfterStatusDesc(AfterSalesStatus.toStatusMessage(afterSales.getAfterStatus()));

            OrderInfo orders = getOrderInfo(afterSales.getOrderNo(), afterSales.getCreaterId());
            if (orders != null) {
                afterSalesListDto.setOrderStatus(orders.getOrderStatus());
                afterSalesListDto.setOrderStatusDesc(OrderStatus.toStatusMessage(orders.getOrderStatus()));

                afterSalesListDto.setFreight(PriceConversion.intToString(orders.getExpressAmount().intValue()));//运费
                afterSalesListDto.setCouponAmount(PriceConversion.intToString(orders.getCouponAmount().intValue()));//优惠金额

                // 如果处于已完成状态
                if (orders.getOrderStatus() == OrderStatus.COMPLETED.getStatus()) {
                    //  聚水潭状态
                    afterSalesListDto.setJstStatus(1);
                } else {
                    afterSalesListDto.setJstStatus(afterSales.getJstCancel());
                }


                //快递
                EntityWrapper<AfterPerson> afterPersonEntityWrapper = new EntityWrapper<>();
                afterPersonEntityWrapper.eq("AFTER_SALE_NO", afterSalesListDto.getAfterSaleNo());
                afterPersonEntityWrapper.eq("DEL_FLAG", 1);
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, afterSales.getCreaterId());
                List<AfterPerson> afterPersonList = afterPersonMapper.selectList(afterPersonEntityWrapper);

                if (afterPersonList.size() > 0 && afterPersonList != null) {
                    afterSalesListDto.setLogisticsName(afterPersonList.get(0).getLogisticsName());
                    afterSalesListDto.setLogisticsNo(afterPersonList.get(0).getLogisticsNo());
                }
            }
            //订单提供商品 货物
            OrderGoodVo orderGoodVo = new OrderGoodVo();
            orderGoodVo.setOrderNo(afterSales.getOrderNo());
            orderGoodVo.setUserId(String.valueOf(afterSales.getCreaterId()));
            ReturnData<List<OrderGoodsDto>> returnData = orderFeignClient.getOrderGoodList(orderGoodVo);
            List<OrderGoodsDto> orderGoodsDtos = returnData.getData();
            afterSalesListDto.setGoodsList(orderGoodsDtos);

            listDtos.add(afterSalesListDto);
        }

        page.setRecords(listDtos);
        return page;
    }


    /**
     * 退款接口
     *
     * @param orderNo
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String orderRefund(String orderNo, String userId, BigDecimal refundAmount, String remarks) {
        logger.info("退款入参：{},{}", orderNo, userId);
        if (StringUtils.isBlank(userId))
            return "退款userId不能为空";
        OrderAfterVo orderAfterVo = new OrderAfterVo();
        orderAfterVo.setUserId(userId);
        orderAfterVo.setOrderNo(orderNo);
        OrderPayment orderPayment = orderFeignClient.selectByOrderPayment(orderAfterVo).getData();
        if (orderPayment == null) {
            return "查询订单支付信息为空";
        }

        logger.info("开始退款：{},{}", orderNo, userId);
        //退款地址
        WxpayRefund wxpayRefund = new WxpayRefund();
        wxpayRefund.setOutTradeNo(orderNo);
        wxpayRefund.setRefundFee(refundAmount.intValue());//退款金额
        wxpayRefund.setRefundDesc(remarks);//退款说明
        ReturnData<WxpayRefund> wr = payFeignClient.refund(wxpayRefund);
        logger.info("开始退款2：{},{}", orderNo, userId);
        if (wr.getCode() == 1) {
            logger.error("订单【订单号】 {}   退款成功", orderPayment.getOrderNo());
            return "success";
        } else if (wr.getCode() == -1) {
            logger.error("订单【订单号】 {}   退款失败:{}", orderPayment.getOrderNo(), wr.getDesc());
            return wr.getDesc();
        } else {
            logger.error("订单【订单号】 {}   退款失败:{}", orderPayment.getOrderNo(), wr.getDesc());
            return "fail";
        }
    }


    /**
     * 修改售后信息
     *
     * @param afterSalesListDtos
     * @throws Exception
     */
    @Transactional
    public void updateAfterSales(List<AfterSalesListDto> afterSalesListDtos) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();

        for (AfterSalesListDto afterSalesListDto : afterSalesListDtos) {
            MoneyRefundDto moneyRefundDto = queryRefund(afterSalesListDto.getOrderNo(), afterSalesListDto.getCreaterId());
            if (moneyRefundDto != null) {
                // 防止多次调用
                EntityWrapper<AfterGood> afterSalesEntityWrapper = new EntityWrapper<>();
                afterSalesEntityWrapper.eq("AFTER_SALE_NO", afterSalesListDto.getAfterSaleNo());
                List<AfterGood> afterGoods = afterGoodMapper.selectList(afterSalesEntityWrapper);
                if (afterGoods != null && afterGoods.size() > 0) {
                    continue;
                }

                //  修改申请表
                AfterSales afterSales = new AfterSales();
                BeanUtils.copyProperties(afterSalesListDto, afterSales);
                afterSales.setAfterStatus(AfterSalesStatus.RETURN_MONEY_FINISH.getStatus());
                afterSalesMapper.updateById(afterSales);

                // 修改订单状态
                OrderInfo or = getOrderInfo(afterSales.getOrderNo(), afterSales.getCreaterId());
                if (or != null) {
                    //关闭订单
                    orderFeignClient.close(Lists.newArrayList(afterSales.getOrderNo()));

                    // 解绑优惠券
//                    orderService.unBindCoupon(afterSales.getOrderNo(), jwtUserDetails.getUserId());

                    AfterGood afterGood = new AfterGood();
                    OrderInfo orders = getOrderInfo(afterSales.getOrderNo(), afterSales.getCreaterId());
//                    OrderPayment orderPayment = orderFeignClient.getOrderPay(afterSales.getOrderNo());

                    afterGood.setReturnAmount(new BigDecimal(moneyRefundDto.getRefundFee()));  // 退款金额
                    afterGood.setOrderAmount(orders.getOrderAmount());  // 订单金额
                    afterGood.setLogisticsAmount("0");   // 运费
                    afterGood.setCouponAmount(orders.getCouponAmount()); // 优惠金额

                    //  TODO  返现金额参数
//                    BuyBackQualificationEx buyBackQualificationEx = buyBackQualificationService.queryRefundByOrderNo(afterSales.getOrderNo());
//                    if (buyBackQualificationEx != null) {
//                        if (!"yes".equals(buyBackQualificationEx.getRefund())) {
//                            // 返现金额
////                            afterGood.setRebackAmount(buyBackQualificationEx.getRedMoney());
//                        } else {
//                            afterGood.setRebackAmount(new BigDecimal(0));  // 返现金额
//                        }
//                    } else {
//                        afterGood.setRebackAmount(new BigDecimal(0));  // 返现金额
//                    }
                    afterGood.setReturnDesc("直接发起微信退款!"); // 退款备注
                    afterGood.setOrderNo(afterSales.getOrderNo());
                    afterGood.setAfterSaleNo(afterSales.getAfterSaleNo());
                    afterGoodMapper.insert(afterGood);
                    /**
                     * TODO 店铺订单更新购物单
                     */
//                  groceryLlistUtils.updateOrder(afterSales.getOrderNo(), 5);
                    //模板消息推送
                    messageUtils.afterSaleRefund(jwtUserDetails.getUserId(), afterSales.getOrderNo(), PriceConversion.intToString(moneyRefundDto.getRefundFee()));
                }

            }
        }
    }

    /**
     * 查询退款详情
     *
     * @param orderNo
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MoneyRefundDto queryRefund(String orderNo, Long userid) {

        //退款详情地址
//        String refundUrl = ConfigUtil.getProperty("weixin.domain") + "/order/queryRefund";
        String refundUrl = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("outTradeNo", orderNo);
        jsonObject.put("userid", userid);
        jsonObject.put("appType", "MIN");
        JSONObject jsonObjectResult = JSONObject.parseObject(HttpURLConnectionUtil.doPost(refundUrl, jsonObject));
        if (jsonObjectResult != null) {
            Integer code = jsonObjectResult.getInteger("code");
            JSONObject object = jsonObjectResult.getJSONObject("data");
            MoneyRefundDto moneyRefundDto = new MoneyRefundDto();
            if (object != null && code == 200) {
                String id = object.getString("id");
                String openId = object.getString("openId");
                Date createTime = object.getDate("createTime");
                String outTradeNo = object.getString("outTradeNo");
                Integer refundFee = object.getInteger("refundFee");
                Integer totalFee = object.getInteger("totalFee");

                moneyRefundDto.setId(id);
                moneyRefundDto.setOpenId(openId);
                moneyRefundDto.setCreateTime(createTime);
                moneyRefundDto.setOutTradeNo(outTradeNo);
                moneyRefundDto.setRefundFee(refundFee);
                moneyRefundDto.setTotalFee(totalFee);
                return moneyRefundDto;
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afterSaleReturn(AfterSaleReturnVo afterSaleReturnVo) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
//        Assert.isTrue(!(StringUtils.isBlank(afterSaleReturnVo.getCreaterId())), "createrId不可为空");
        OrderInfo orderInfo = getOrderInfo(afterSaleReturnVo.getOrderNo(), Long.valueOf(jwtUserDetails.getUserId()));
        Assert.isTrue(orderInfo.getOrderStatus() == OrderStatus.COMPLETED.getStatus(), "不能申请售后");

        // 用户提供收件人姓名和电话
        UserOrderVo userOrderVo = new UserOrderVo();
        userOrderVo.setUserId(String.valueOf(jwtUserDetails.getUserId()));
        userOrderVo.setOrderNo(afterSaleReturnVo.getOrderNo());
        OrderLogistics orderLogistics = new OrderLogistics();
        try {
            List<OrderLogistics> returnData = orderFeignClient.getLogistics(userOrderVo).getData();
            if (!returnData.isEmpty()) {
                orderLogistics = returnData.get(0);
            }
        } catch (Exception e) {
            logger.info("当前异常为:" + e);
            Assert.isTrue(false, "未查询到订单寄件人信息");
        }
        Assert.notNull(orderLogistics, "未查询到订单寄件人信息");

        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("ORDER_NO", afterSaleReturnVo.getOrderNo());
        afterSalesEntityWrapper.eq("DEL_FLAG", 1);
        afterSalesEntityWrapper.orderBy("AFTER_ID", false);
        AfterSales afterSales = selectOne(afterSalesEntityWrapper);
        boolean addAfterSale = Objects.isNull(afterSales);
        String afterSaleNo;
        if (addAfterSale) {
            afterSales = new AfterSales();
            afterSaleNo = OrderUtils.gainAfterNo(Integer.valueOf(orderInfo.getOrderType()), jwtUserDetails.getUserId().toString());
            afterSales.setAfterSaleNo(afterSaleNo);
        } else {
            afterSaleNo = afterSales.getAfterSaleNo();
        }

        afterSales.setAfterType(afterSaleReturnVo.getAfterSaleReason());
        afterSales.setOrderNo(afterSaleReturnVo.getOrderNo());
        afterSales.setOrderTime(orderInfo.getCreaterTime());
        afterSales.setCheckName(orderLogistics.getConsumerName());//收件人姓名
        afterSales.setCheckPhone(orderLogistics.getConsumerMobile());//收件人电话
        afterSales.setJstCancel(0);
        afterSales.setDelFlag(1);
        afterSales.setCreaterId(Long.valueOf(jwtUserDetails.getUserId()));

        AfterPerson afterPerson = new AfterPerson();
        if (afterSaleReturnVo.getAfterSaleReason() != 3) { //非质量问题
            afterSales.setAfterStatus(AfterSalesStatus.RETURN_GOODS_PASS.getStatus());
            // 默认的售后地址
            List<ReturnAddress> afterSaleAddresses = returnAddressService.addressList();
            ReturnAddress defaultAddress = afterSaleAddresses.stream().filter(asa -> 1 == asa.getDefaultFlag()).findFirst().orElse(null);

            if (Objects.nonNull(defaultAddress)) {
                AfterSaleAddressDto afterSaleAddressDto = returnAddressService.getAfterSaleAddressDto(defaultAddress.getAddressId());
                afterPerson.setWarehouseMobile(afterSaleAddressDto.getWarehouseMobile());
                afterPerson.setWarehouseAddr(afterSaleAddressDto.getWarehouseAddr());
                afterPerson.setWarehousePerson(afterSaleAddressDto.getWarehousePerson());
            }

            // 模板消息,直接发送
            messageUtils.afterSaleReturn(jwtUserDetails.getUserId(), afterSaleReturnVo.getOrderNo(), true);
        } else {
            afterSales.setAfterStatus(AfterSalesStatus.RETURN_GOODS_APPLLY.getStatus());
        }
        if (addAfterSale) {
            afterSalesMapper.insert(afterSales);
        } else {
            afterSalesMapper.updateById(afterSales);
        }

        afterPerson.setAfterSaleNo(afterSales.getAfterSaleNo());
        afterPerson.setUserRemark(afterSaleReturnVo.getUserRemark());
        if (afterSaleReturnVo.getUserImages() != null && afterSaleReturnVo.getUserImages().size() > 0) {
            afterPerson.setAfterImage(JSONObject.toJSONString(afterSaleReturnVo.getUserImages()));
        }
        afterPerson.setCreaterId(Long.valueOf(jwtUserDetails.getUserId()));
        afterPerson.setDelFlag(1);
        afterPersonMapper.insert(afterPerson);
        // 备注信息
        AfterCustom afterCustom = new AfterCustom();
        afterCustom.setAfterSaleNo(afterSales.getAfterSaleNo());
        afterCustom.setOrderNo(afterSaleReturnVo.getOrderNo());
        afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_USER_TYPE.getStatus());
        afterCustom.setUserRemark(afterSaleReturnVo.getUserRemark());
        afterCustom.setDelFlag(1);
        afterCustomMapper.insert(afterCustom);

        // 订单提供接口更改订单状态为已售后
        orderFeignClient.updateAfterSaleFlag(userOrderVo);

        //包裹添加到售后聚水潭表中
        OrderGoodVo orderGoodVo = new OrderGoodVo();
        orderGoodVo.setOrderNo(afterSales.getOrderNo());
        orderGoodVo.setUserId(String.valueOf(afterSales.getCreaterId()));
        ReturnData<List<OrderPackageDto>> returnData = orderFeignClient.getOrderPackage(orderGoodVo);
        List<OrderPackageDto> packageList = returnData.getData();
        if (packageList != null && packageList.size() > 0) {
            for (int i = 0; i < packageList.size(); i++) {
                AfterJst afterJst = new AfterJst();
                afterJst.setOrderNo(afterSaleReturnVo.getOrderNo());
                afterJst.setAfterSaleNo(afterSaleNo);
                afterJst.setChildOrderNo(packageList.get(i).getPackageNo());//包裹号
                afterJst.setDelFlag(1);//是否删除
                afterJst.setJstCancel(0);//聚水潭是否取消
                afterJstMapper.insert(afterJst);
            }
        }
        mqProducer.updateMMKing(afterSaleReturnVo.getOrderNo(), jwtUserDetails.getUserId(), OrderKingStatus.FROZEN.getStatus());
        logger.info("申请退货后冻结买买金,orderNo:{}", afterSaleReturnVo.getOrderNo());
    }

    /**
     * 获取售后信息
     *
     * @param afterSaleNo
     * @return
     */
    public AfterSales getAfterSales(String afterSaleNo) {
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("DEL_FLAG", 1);
        afterSalesEntityWrapper.eq("AFTER_SALE_NO", afterSaleNo);
        List<AfterSales> afterSales = afterSalesMapper.selectList(afterSalesEntityWrapper);
        Assert.isTrue(afterSales.size() > 0, "售后信息不存在");
        return afterSales.get(0);
    }

    /**
     * 获取售后信息
     *
     * @param afterSaleNo
     * @return
     */
    public AfterPerson getAfterSaleAudit(String afterSaleNo) {
        EntityWrapper<AfterPerson> afterPersonEntityWrapper = new EntityWrapper<>();
        afterPersonEntityWrapper.eq("AFTER_SALE_NO", afterSaleNo);
        List<AfterPerson> afterPersonList = afterPersonMapper.selectList(afterPersonEntityWrapper);
        if (afterPersonList.size() > 0) {
            return afterPersonList.get(0);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afterSaleExpress(AfterSaleExpressVo afterSaleExpressVo) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
        AfterSales afterSales = getAfterSales(afterSaleExpressVo.getAfterSaleNo());
        if (AfterSalesStatus.RETURN_GOODS_PASS.getStatus().equals(afterSales.getAfterStatus()) || AfterSalesStatus.RETURN_BACK_GOODS.getStatus().equals(afterSales.getAfterStatus())) {
            AfterPerson afterPerson = new AfterPerson();

            afterPerson.setLogisticsNo(afterSaleExpressVo.getLogisticsNo());
            afterPerson.setLogisticsName(afterSaleExpressVo.getLogisticsName());
            afterPerson.setLogisticsCode(afterSaleExpressVo.getLogisticsCode());

            EntityWrapper<AfterPerson> afterPersonEntityWrapper = new EntityWrapper<>();
            afterPersonEntityWrapper.eq("AFTER_SALE_NO", afterSales.getAfterSaleNo());
            afterPersonEntityWrapper.eq("DEL_FLAG", 1);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
            long n = afterPersonMapper.update(afterPerson, afterPersonEntityWrapper);

            editStatusByAfterSaleNo(AfterSalesStatus.RETURN_BACK_GOODS, afterSales.getAfterSaleNo());
            Assert.isTrue(n > 0, "操作失败");
        } else {
            Assert.isTrue(false, "不能进行该操作");
        }
    }

    /**
     * 编辑售后状态
     *
     * @param afterSalesStatus
     * @param afterSaleNos
     */
    @Transactional(propagation = Propagation.NESTED)
    public void editStatusByAfterSaleNo(AfterSalesStatus afterSalesStatus, String... afterSaleNos) {
        AfterSales newAfterSales = new AfterSales();
        newAfterSales.setAfterStatus(afterSalesStatus.getStatus());
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("DEL_FLAG", 1);
        afterSalesEntityWrapper.in("AFTER_SALE_NO", Arrays.asList(afterSaleNos));
        afterSalesMapper.update(newAfterSales, afterSalesEntityWrapper);
    }

    @Override
    public List<RemarkDto> orderRemarks(ConsumerRemarksVo consumerRemarksVo) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, consumerRemarksVo.getCreaterId());
        //统计客服备注
        EntityWrapper<AfterCustom> afterCustomEntityWrapper = new EntityWrapper<>();
        afterCustomEntityWrapper.eq("DEL_FLAG", 1);
        if (StringUtils.isNotBlank(consumerRemarksVo.getAfterSaleNo())) {
            afterCustomEntityWrapper.eq("AFTER_SALE_NO", consumerRemarksVo.getAfterSaleNo());
        } else {
            afterCustomEntityWrapper.eq("ORDER_NO", consumerRemarksVo.getOrderNo());
        }
        afterCustomEntityWrapper.isNotNull("USER_REMARK");
        afterCustomEntityWrapper.ne("USER_REMARK", "");
        afterCustomEntityWrapper.orderDesc(Arrays.asList("CREATER_TIME"));
        List<AfterCustom> afterCustoms = afterCustomMapper.selectList(afterCustomEntityWrapper);


        List<RemarkDto> remarkDtos = Lists.newArrayListWithCapacity(afterCustoms.size() + 1);
        RemarkDto remarkDto1 = new RemarkDto();
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("DEL_FLAG", 1);
        if (StringUtils.isNotBlank(consumerRemarksVo.getAfterSaleNo())) {
            afterSalesEntityWrapper.eq("AFTER_SALE_NO", consumerRemarksVo.getAfterSaleNo());
        } else {
            afterSalesEntityWrapper.eq("ORDER_NO", consumerRemarksVo.getOrderNo());
        }

        for (int i = 0; i < afterCustoms.size(); i++) {
            RemarkDto remarkDto = new RemarkDto();
            if (StringUtils.isNotBlank(afterCustoms.get(i).getUserRemark())) {
                remarkDto.setUserRemark(afterCustoms.get(i).getUserRemark());
            }

            remarkDto.setCustomType(afterCustoms.get(i).getCustomType());
            remarkDto.setCreateTime(afterCustoms.get(i).getCreaterTime());
            remarkDto.setTypeDesc(AfterSalesRemarksStatus.toStatusMessage(afterCustoms.get(i).getCustomType()));

            remarkDto.setCreateId(consumerRemarksVo.getCreaterId());
            remarkDtos.add(remarkDto);
        }

        //统计用户提交售后类型备注
        List<AfterSales> afterSalesList = afterSalesMapper.selectList(afterSalesEntityWrapper);
        if (afterSalesList != null && afterSalesList.size() > 0) {
            AfterSales as = afterSalesList.get(0);
            if (AfterSaleReason.REMARKS_CONSUMER_TYPE.getStatus().equals(as.getAfterType()) || AfterSaleReason.REMARKS_AUDIT_TYPE.getStatus().equals(as.getAfterType())) {
                remarkDto1.setUserRemark(AfterSaleReason.toStatusMessage(as.getAfterType()));
                remarkDto1.setCustomType(AfterSalesRemarksStatus.REMARKS_USER_TYPE.getStatus());
                remarkDto1.setCreateId(String.valueOf(as.getCreaterId()));
                remarkDto1.setCreateTime(as.getCreaterTime());
                remarkDto1.setTypeDesc(AfterSalesRemarksStatus.REMARKS_USER_TYPE.getMessage());
                remarkDtos.add(remarkDto1);
            }
        }
        return remarkDtos;
    }

    /**
     * 拒绝申请退货
     *
     * @param orderNo
     * @param userid
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refuseGoods(String orderNo, Long userid, String context) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("ORDER_NO", orderNo);
        List<AfterSales> afterSalesList = afterSalesMapper.selectList(afterSalesEntityWrapper);

        if (afterSalesList.size() > 0 && afterSalesList != null) {
            AfterSales afterSales = afterSalesList.get(0);
            if (AfterSalesStatus.RETURN_MONEY_APPLY.getStatus().equals(afterSales.getAfterStatus())) {

                // 修改 after_sale 的状态
                AfterSales after = new AfterSales();
                Date date = new Date();
                after.setAfterStatus(AfterSalesStatus.RETURN_MONEY_REFUSE.getStatus());
                EntityWrapper<AfterSales> afterSalesEntityWrapper1 = new EntityWrapper<>();
                afterSalesEntityWrapper1.eq("ORDER_NO", orderNo);
                afterSalesMapper.update(after, afterSalesEntityWrapper);
                mqProducer.updateAfterStatus(orderNo, userid, afterSales.getAfterSaleNo());


                // 备注表
                AfterCustom afterCustom = new AfterCustom();
                afterCustom.setOrderNo(afterSales.getOrderNo());
                afterCustom.setAfterSaleNo(afterSales.getAfterSaleNo());
                if (StringUtils.isNotBlank(context)) {
                    afterCustom.setUserRemark(context);
                }
                afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_CONSUMER_TYPE.getStatus());
                afterCustom.setDelFlag(1);
                afterCustom.setCreaterTime(date);
                afterCustomMapper.insert(afterCustom);

                //TODO 更新订单状态
//                orderService.editOrderAfterSalesByOrderNo(false, afterSales.getOrderNo());
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 转退货
     *
     * @param trunReturnVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean turnReturn(TrunReturnVo trunReturnVo) {
        logger.info("进入转退货入参：{},{}", trunReturnVo.toString());
        String orderNo = trunReturnVo.getOrderNo();
        boolean flag = trunReturnVo.isStatus();

        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("ORDER_NO", orderNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, trunReturnVo.getCreaterId());
        List<AfterSales> afterSalesList = afterSalesMapper.selectList(afterSalesEntityWrapper);


        logger.info("当前售后单数量:{}", afterSalesList);
        Assert.isTrue(afterSalesList.size() > 0 && afterSalesList != null, "售后订单号不存在");
        AfterSales afterSales = afterSalesList.get(0);
        if (!AfterSalesStatus.RETURN_MONEY_APPLY.getStatus().equals(afterSales.getAfterStatus())) {
            throw new IllegalArgumentException("该售后状态不在范围内!");
        }

        updateTurnReturn(flag, afterSales, orderNo, Long.valueOf(trunReturnVo.getCreaterId()));
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateTurnReturn(boolean flag, AfterSales afterSales, String orderNo, Long userid) {
        logger.info("开始更新售后表.......");
        Date date = new Date();
        AfterSales sales = new AfterSales();
        sales.setAfterSaleNo(afterSales.getAfterSaleNo());
        //TODO 缺少转退货标识
//        sales.setTurnFlag(true);

        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("AFTER_ID", afterSales.getAfterId());

        // 备注表
        AfterCustom afterCustom = new AfterCustom();
        afterCustom.setOrderNo(afterSales.getOrderNo());
        afterCustom.setAfterSaleNo(afterSales.getAfterSaleNo());
        afterCustom.setCreaterTime(date);
        afterCustom.setCreaterId(userid);

        /**
         *  审核表
         */
        AfterPerson afterPerson = new AfterPerson();
        afterPerson.setAfterSaleNo(afterSales.getAfterSaleNo());
        afterPerson.setDelFlag(1);

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
        List<ReturnAddress> afterSaleAddresses = returnAddressService.addressList();
        ReturnAddress defaultAddress = afterSaleAddresses.stream().filter(asa -> 1 == asa.getDefaultFlag()).findFirst().orElse(null);
        if (Objects.nonNull(defaultAddress)) {
            AfterSaleAddressDto afterSaleAddressDto = returnAddressService.getAfterSaleAddressDto(defaultAddress.getAddressId());
            afterPerson.setWarehouseMobile(afterSaleAddressDto.getWarehouseMobile());
            afterPerson.setWarehouseAddr(afterSaleAddressDto.getWarehouseAddr());
            afterPerson.setWarehousePerson(afterSaleAddressDto.getWarehousePerson());
        }

        if (flag) {
//            List<OrderLogisticsDto> list = orderLogisticsService.logistics(orderNo);
//            if (list != null && list.size() > 0) {
//                OrderLogisticsDto orderLogisticsDto = list.get(0);
//                afterPerson.setLogisticsCode(orderLogisticsDto());//快递公司编码
            //缺少快递单号
//                afterPerson.setlId(orderLogisticsDto.getlId());//快递单号
//                afterPerson.setLogisticsName(());//快递公司名称
//            }

            sales.setAfterStatus(AfterSalesStatus.RETURN_BACK_GOODS.getStatus());
            afterCustom.setUserRemark("转退货流程--已寄回退货");
            afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_CONSUMER_TYPE.getStatus());
        } else {
            sales.setAfterStatus(AfterSalesStatus.RETURN_GOODS_PASS.getStatus());
            afterCustom.setUserRemark("转退货流程--退货申请通过");
            afterCustom.setCustomType(AfterSalesRemarksStatus.REMARKS_CONSUMER_TYPE.getStatus());
        }

        afterPersonMapper.insert(afterPerson);
        int a = afterSalesMapper.update(sales, afterSalesEntityWrapper);
        logger.info("转退货更新售后结果:{}", a);
        int b = afterCustomMapper.insert(afterCustom);
        logger.info("转退货插入备注结果:{}", b);
    }


    @Override
    public void updateStatus(String orderNo) {
        AfterSales sales = new AfterSales();
        ReturnData<OrderInfo> data = orderFeignClient.getOrderByOrderNo(orderNo);
        if (null == data || data.getCode() == -1 || null == data.getData())
            return;
        logger.info("###查询订结果是:{}", data.getData().toString());
        sales.setCreaterId(data.getData().getCreaterId());
        sales.setOrderNo(orderNo);
        sales.setAfterStatus(AfterSalesStatus.RETURN_MONEY_FINISH.getStatus());

        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("ORDER_NO", orderNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, data.getData().getCreaterId());
        boolean result = this.update(sales, afterSalesEntityWrapper);
        logger.info("微信退款后修改售后状态结果:{}", result);
    }


    /**
     * 获取售后信息
     *
     * @param orderAfterVo
     * @return
     */
    @Override
    public List<OrderAfterSaleDto> selectByOrderNo(OrderAfterVo orderAfterVo) {
        logger.info("当前用户{}已经进入获取售后信息", orderAfterVo.getUserId());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, orderAfterVo.getUserId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderAfterVo.getOrderNo());
        List<AfterSales> list = afterSalesMapper.selectByMap(map);
        logger.info("当前用户{}已经进入获取售后信息为:{}", orderAfterVo.getUserId(), list);
        List<OrderAfterSaleDto> data = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                // 售后基本信息
                OrderAfterSaleDto orderAfterSaleDto = new OrderAfterSaleDto();
                orderAfterSaleDto.setAfterId(list.get(i).getAfterId());
                orderAfterSaleDto.setAfterSlaeNo(list.get(i).getAfterSaleNo());
                orderAfterSaleDto.setAfterStatus(list.get(i).getAfterStatus());
                orderAfterSaleDto.setAfterType(list.get(i).getAfterType());
                orderAfterSaleDto.setOrderNo(list.get(i).getOrderNo());
                orderAfterSaleDto.setOrderTime(list.get(i).getOrderTime());
                orderAfterSaleDto.setCheckName(list.get(i).getCheckName());
                orderAfterSaleDto.setCheckPhone(list.get(i).getCheckPhone());
                orderAfterSaleDto.setJstCancel(list.get(i).getJstCancel());
                orderAfterSaleDto.setAfterDesc(list.get(i).getAfterDesc());
                orderAfterSaleDto.setDelFlag(list.get(i).getDelFlag());
                orderAfterSaleDto.setReturnFlag(list.get(i).getReturnFlag());
                orderAfterSaleDto.setCreaterId(list.get(i).getCreaterId());
                orderAfterSaleDto.setCreaterTime(list.get(i).getCreaterTime());
                orderAfterSaleDto.setModifyId(list.get(i).getModifyId());
                orderAfterSaleDto.setModifyTime(list.get(i).getModifyTime());

                // 售后邮寄回来的快递信息
                AfterSales afterSales = list.get(i);
                Map<String, Object> afterMap = new HashMap<>();
                afterMap.put("AFTER_SALE_NO", afterSales.getAfterSaleNo());
                List<AfterPerson> afterPersonList = afterPersonMapper.selectByMap(afterMap);
                if (afterPersonList != null && afterPersonList.size() > 0) {
                    orderAfterSaleDto.setLogisticsCode(afterPersonList.get(0).getLogisticsCode());
                    orderAfterSaleDto.setLogisticsName(afterPersonList.get(0).getLogisticsName());
                    orderAfterSaleDto.setLogisticsNo(afterPersonList.get(0).getLogisticsNo());
                }

                data.add(orderAfterSaleDto);
            }
        }
        return data;
    }

    /**
     * 获取当前用户的售后数量
     *
     * @return
     */
    @Override
    public Integer getAfterSaleCount() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
        Long userId = jwtUserDetails.getUserId();
        logger.info("当前用户获取用户{}的售后数量", userId);
        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllOrderToMoldMap(userId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper entityWrapper = new EntityWrapper();
            entityWrapper.in("CREATER_ID", v);
            // todo  del_flag 待统一规范，1 标识未删除，0：已经删除
            entityWrapper.eq("DEL_FLAG", 1);
            Integer count = afterSalesMapper.selectCount(entityWrapper);
            atomicInteger.addAndGet(count);
        });
        int count = atomicInteger.get();
        logger.info("当前用户获取用户{}的售后数量为:{}", userId, count);
        return count;
    }


    /**
     * 获取用户的订单售后列表
     *
     * @return
     */
    @Override
    public Page<OrderListDto> getUserOrderAfter(OrderListVo orderListVo) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        Long userId = jwtUserDetails.getUserId();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        Page<OrderListDto> page = new Page<>(orderListVo.getCurrentPage(), orderListVo.getPageSize());
        List<OrderListDto> orderList = new ArrayList<>();
        final List<AfterSales> afterSaleList = Lists.newArrayList();
        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllAfterSaleToMoldMap(userId);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper entityWrapper = new EntityWrapper();
            entityWrapper.in("CREATER_ID", v);
            // todo  del_flag 待统一规范，1 标识未删除，0：已经删除
            entityWrapper.eq("DEL_FLAG", 1);
            entityWrapper.orderBy("CREATER_TIME desc");
            afterSaleList.addAll(afterSalesMapper.selectPage(page, entityWrapper));
        });

        if (afterSaleList.isEmpty())
            return page;
        List<AfterSales> list = afterSaleList.stream().sorted(new Comparator<AfterSales>() {
            @Override
            public int compare(AfterSales o1, AfterSales o2) {
                Integer o1Status = o1.getAfterStatus();
                Integer o2Status = o2.getAfterStatus();
                return o1Status < o2Status ? -1 : o2.getCreaterTime().compareTo(o1.getCreaterTime());
            }
        }).limit(page.getSize()).collect(Collectors.toList());

        for (AfterSales anAfterSalesList : list) {
            List<String> data = new ArrayList<>();
            UserOrderVo userOrderVo = new UserOrderVo();
            userOrderVo.setOrderNo(anAfterSalesList.getOrderNo());
            userOrderVo.setUserId(String.valueOf(userId));
            OrderInfo info = orderFeignClient.getOrderInfo(userOrderVo).getData();
            OrderListDto orderListDto = new OrderListDto();
            orderListDto.setAfterSaleNo(anAfterSalesList.getAfterSaleNo());
            orderListDto.setAfterSaleStatus(anAfterSalesList.getAfterStatus());
            orderListDto.setAfterSaleStatusDesc(AfterSalesStatus.toStatusMessage(anAfterSalesList.getAfterStatus()));
            orderListDto.setOrderNo(info.getOrderNo());
            orderListDto.setOrderStatus(info.getOrderStatus());
            orderListDto.setOrderStatusDesc(com.mmj.common.constants.OrderStatus.toStatusMessage(info.getOrderStatus()));
            orderListDto.setOrderAmount(PriceConversion.intToString(info.getOrderAmount().intValue()));
            orderListDto.setOrderType(Integer.valueOf(info.getOrderType()));
            orderListDto.setOrderTypeDesc(OrderTypeStatus.OrderTypeStatus(Integer.valueOf(info.getOrderType())));
            orderListDto.setCreateDate(DateUtils.getDate(info.getCreaterTime(), "yyyy-MM-dd HH:mm:ss"));
            orderListDto.setExpireDate(DateUtils.getDate(info.getExpirtTime(), "yyyy-MM-dd HH:mm:ss"));

            data.add(anAfterSalesList.getOrderNo());
            List<OrderGoodsDto> goods = new ArrayList<>();

            OrderGoodVo orderGoodVo = new OrderGoodVo();
            orderGoodVo.setUserId(userId.toString());
            orderGoodVo.setOrderNo(info.getOrderNo());
            List<OrderGoodsDto> orderGoodsDtos = orderFeignClient.getOrderGoodList(orderGoodVo).getData();
            // 封装商品信息
            orderListDto.setGood(orderGoodsDtos);
            //  商品推荐
            if (info.getOrderStatus() == com.mmj.common.constants.OrderStatus.COMPLETED.getStatus()) {
                try {
                    HashMap<String, Object> maps = new HashMap<>();
                    maps.put("createrId", Long.valueOf(orderListVo.getUserId()));
                    maps.put("orderNoList", data);
                    logger.info("订单列表开始调用商品推荐接口,用户id为:{},订单号为:{}", orderListVo.getUserId(), data);
                    ReturnData<List<UserRecommendOrder>> listReturnData = userFeignClient.selectByOrderNo(maps);
                    if (listReturnData != null && listReturnData.getData() != null) {
                        List<UserRecommendOrder> userRecommendOrderList = listReturnData.getData();
                        UserRecommendOrder userRecommendOrder = userRecommendOrderList.get(0);
                        logger.info("调用商品推荐接口结果为:{}", userRecommendOrder);
                        orderListDto.setHasRecommend(userRecommendOrder.getStatus());
                        orderListDto.setRecommendId(userRecommendOrder.getRecommendId());
                    }
                } catch (Exception e) {
                    logger.info("调用订单是展示 去写推荐 or 分享得返现 方法异常:" + e);
                }
            }
            orderList.add(orderListDto);
        }
        page.setRecords(orderList);
        return page;
    }

    /**
     * 添加到售后
     *
     * @param addAfterSaleVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addAfterSale(AddAfterSaleVo addAfterSaleVo) {
        Long userId = Long.valueOf(addAfterSaleVo.getUserId());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("ORDER_NO", addAfterSaleVo.getOrderNo());
        List<AfterSales> afterSalesList = afterSalesMapper.selectList(afterSalesEntityWrapper);
        if (afterSalesList.size() > 0) {
            return "不可重复取消";
        }

        logger.info("当前用户{}:,开始取消订单进入售后部分，订单号为{}", userId, addAfterSaleVo.getOrderNo());

        String afterSaleNo = OrderUtils.gainAfterNo(Integer.valueOf(addAfterSaleVo.getOrderType()), addAfterSaleVo.getUserId());
        AfterSales afterSales = new AfterSales();
        afterSales.setAfterSaleNo(afterSaleNo);
        afterSales.setAfterStatus(AfterSalesStatus.RETURN_MONEY_APPLY.getStatus());
        afterSales.setOrderNo(addAfterSaleVo.getOrderNo());
        afterSales.setOrderTime(addAfterSaleVo.getCreaterTime());
        afterSales.setDelFlag(1);
        afterSales.setCheckName(addAfterSaleVo.getConsumerName());
        afterSales.setCheckPhone(addAfterSaleVo.getConsumerMobile());
        afterSales.setDelFlag(1);
        afterSales.setCreaterId(userId);
        afterSales.setCreaterTime(new Date());
        afterSalesMapper.insert(afterSales);
        addJst(addAfterSaleVo.getOrderNo(), afterSaleNo, userId);
        return "success";
    }

    /**
     * 获取售后信息
     *
     * @param addAfterSaleVo
     * @return
     */
    @Override
    public AfterSaleDto getAfterSaleInfo(AddAfterSaleVo addAfterSaleVo) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, addAfterSaleVo.getUserId());
        logger.info("当前用户{}的订单号为:{}，已经进入售后方法中", addAfterSaleVo.getUserId(), addAfterSaleVo.getOrderNo());
        AfterSaleDto afterSaleDto = new AfterSaleDto();
        afterSaleDto.setOrderNo(addAfterSaleVo.getOrderNo());

        AfterSales queryAfterSales = new AfterSales();
        queryAfterSales.setOrderNo(addAfterSaleVo.getOrderNo());
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>(queryAfterSales);
        afterSalesEntityWrapper.orderBy("AFTER_ID", false);
        List<AfterSales> list = selectList(afterSalesEntityWrapper);
        if (list != null && list.size() > 0) {
            AfterSales afterSales = list.get(0);
            afterSaleDto.setAfterSaleNo(afterSales.getAfterSaleNo());
            afterSaleDto.setAfterSaleStatus(afterSales.getAfterStatus());
            afterSaleDto.setAfterSaleStatusDesc(AfterSalesStatus.toStatusMessage(afterSales.getAfterStatus()));
            afterSaleDto.setHasAfterSale(true);
            afterSaleDto.setCloudApplyAfterSale(false);
            if (afterSales.getJstCancel() != null && afterSales.getJstCancel() == 1) {
                afterSaleDto.setJstStatus(true);
            } else {
                afterSaleDto.setJstStatus(false);
            }
            if (afterSales.getReturnFlag() != null && afterSales.getReturnFlag() == 1) {
                afterSaleDto.setRefuseFlag(true);
            } else {
                afterSaleDto.setRefuseFlag(false);
            }
            HashMap<String, Object> afterMap = new HashMap<>();
            afterMap.put("AFTER_SALE_NO", afterSales.getAfterSaleNo());
            List<AfterPerson> personList = afterPersonMapper.selectByMap(afterMap);
            if (personList != null && personList.size() > 0) {
                afterSaleDto.setRemarks(personList.get(0).getUserRemark());
                AfterSaleDto.Shipping shipping = new AfterSaleDto.Shipping();

                shipping.setLogisticsCode(personList.get(0).getLogisticsCode());
                shipping.setLogisticsName(personList.get(0).getLogisticsName());
                shipping.setLogisticsNo(personList.get(0).getLogisticsNo());

                afterSaleDto.setShipping(shipping);

                AfterSaleDto.Depot depot = new AfterSaleDto.Depot();
                depot.setDepotAddress(personList.get(0).getWarehouseAddr());
                depot.setDepotName(personList.get(0).getWarehousePerson());
                depot.setDepotTel(personList.get(0).getWarehouseMobile());
                afterSaleDto.setDepot(depot);
            }
        }
        return afterSaleDto;
    }

    /**
     * 取消订单，将订单添加到聚水潭取消表中去(防止多包裹，未全部取消)
     *
     * @param orderNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void addJst(String orderNo, String afterSaleNo, Long userId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);

        OrderGoodVo orderGoodVo = new OrderGoodVo();
        orderGoodVo.setOrderNo(orderNo);
        orderGoodVo.setUserId(String.valueOf(userId));

        logger.info("查询包裹信息开始：{}，{}", userId, orderNo);
        ReturnData<List<OrderPackageDto>> returnData = orderFeignClient.getOrderPackage(orderGoodVo);
        List<OrderPackageDto> list = returnData.getData();
        if (list == null) {
            logger.info("取消订单时，包裹号添加到聚水潭表中失败!,{}", orderNo);
            return;
        }
        list.stream().forEach(r -> {
            AfterJst afterJst = new AfterJst();
            afterJst.setAfterSaleNo(afterSaleNo);
            afterJst.setOrderNo(orderNo);
            afterJst.setChildOrderNo(r.getPackageNo());
            afterJst.setJstCancel(0);   // 0：未取消，1：取消
            afterJst.setDelFlag(1);   // 0：无效，1：有效
            afterJst.setCreaterTime(new Date());
            afterJstMapper.insert(afterJst);
        });
    }


    /**
     * 获取订单信息
     *
     * @param orderNo
     * @param userId
     * @return
     */
    public OrderInfo getOrderInfo(String orderNo, long userId) {
        UserOrderVo userOrderVo = new UserOrderVo();
        userOrderVo.setOrderNo(orderNo);
        userOrderVo.setUserId(String.valueOf(userId));
        ReturnData<OrderInfo> returnData = orderFeignClient.getOrderInfo(userOrderVo);
        return returnData.getData();
    }


    /**
     * 聚水潭取消订单同步
     *
     * @param map
     */
    @Override
    public void jstCancelAfterSales(Map<String, String> map) {
        if (map.size() <= 0 || map == null) {
            return;
        }
        String packageNo = map.get("packageNo");
        String afterDesc = map.get("remark");
        logger.info("聚水潭取消订单--当前包裹号为:{}", packageNo);

        // 有多个子订单，取消完所有子订单的状态才更新售后表聚水潭的状态
        EntityWrapper<AfterJst> afterJstEntityWrapper = new EntityWrapper<>();
        afterJstEntityWrapper.eq("DEL_FLAG", 1);
        afterJstEntityWrapper.eq("CHILD_ORDER_NO", packageNo);
        AfterJst afterJst = new AfterJst();
        afterJst.setJstCancel(1);
        afterNoUtils.shardingKey(packageNo);
        afterJstMapper.update(afterJst, afterJstEntityWrapper);

        EntityWrapper<AfterJst> afterJstEntityWrapper1 = new EntityWrapper<>();
        afterJstEntityWrapper1.eq("CHILD_ORDER_NO", packageNo);
        List<AfterJst> afterJsts = afterJstMapper.selectList(afterJstEntityWrapper1);

        boolean bool = false;
        String orderNo = "";
        if (afterJsts != null && afterJsts.size() > 0) {
            orderNo = afterJsts.get(0).getOrderNo();
            for (int x = 0; x < afterJsts.size(); x++) {
                if (1 == afterJsts.get(x).getJstCancel()) {
                    bool = true;
                } else {
                    bool = false;
                    break;
                }
            }
        }

        if (bool) {
            EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
            afterSalesEntityWrapper.eq("DEL_FLAG", 1);
            afterSalesEntityWrapper.eq("ORDER_NO", orderNo);

            AfterSales afterSales = new AfterSales();
            afterSales.setAfterDesc(afterDesc);
            afterSales.setJstCancel(1);
            long n = afterSalesMapper.update(afterSales, afterSalesEntityWrapper);
            Assert.isTrue(n > 0, "售后信息不存在");
        }
    }

    /**
     * 售后订单逻辑删除
     *
     * @param orderNo
     */
    @Override
    public void delStatusByAfterSaleNo(String orderNo) {
        AfterSales newAfterSales = new AfterSales();
        newAfterSales.setDelFlag(0);
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("DEL_FLAG", 1);
        afterSalesEntityWrapper.in("ORDER_NO", orderNo);

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, afterNoUtils.getDelivery(orderNo));
        afterSalesMapper.update(newAfterSales, afterSalesEntityWrapper);
    }


}
