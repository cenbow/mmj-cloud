package com.mmj.pay.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmj.common.model.active.ActiveGoodStoreResult;
import com.mmj.common.properties.SecurityConstants;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.OrderType;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.Details;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.PriceConversion;
import com.mmj.pay.common.feign.ActiveFeignClient;
import com.mmj.pay.common.feign.GoodFeignClient;
import com.mmj.pay.common.feign.UserFeignClient;
import com.mmj.pay.common.model.dto.GoodInfo;
import com.mmj.pay.common.model.dto.GoodSale;
import com.mmj.pay.common.model.dto.UserCouponDto;
import com.mmj.pay.common.model.vo.ActiveGoodStore;
import com.mmj.pay.common.model.vo.CouponClass;
import com.mmj.pay.common.model.vo.CouponGood;
import com.mmj.pay.common.model.vo.PayIsBuyGiveVo;
import com.mmj.pay.dto.CouponConstants;
import com.mmj.pay.dto.OrderPriceDetailsInfo;
import com.mmj.pay.model.vo.CartOrderCouponParam;
import com.mmj.pay.model.vo.CartOrderGoodsDetails;
import com.mmj.pay.service.CalcOrderPriceService;
import com.mmj.pay.utils.CouponUtil;
import org.springframework.util.Assert;

@Slf4j
@Service
public class CalcOrderPriceServiceImpl implements CalcOrderPriceService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private ActiveFeignClient activeFeignClient;


    @Override
    public OrderPriceDetailsInfo calcOrderPrice() {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean isMember(long userId) {
        // 是否会员
        boolean isMember = false;
        try {
            ReturnData<Boolean> returnData = userFeignClient.isMember(userId);
            if (returnData != null && returnData.getData() != null) {
                isMember = returnData.getData();
            }
            return isMember;
        } catch (Exception e) {
            log.error("判断是否会员发生异常:", e);
            throw new CustomException("计算价格发生异常，请联系客服");
        }
    }

    private double checkOrder(ActiveGoodStore activeGoodStore) {
        ReturnData<ActiveGoodStoreResult> returnData = activeFeignClient.orderCheck(activeGoodStore);
        log.info("-->活动商品下单验证接口返回结果为: {}", returnData);
        Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode(), returnData.getDesc());
        Assert.isTrue(returnData.getData().isResultStatus(), "活动价格验证成功，价格有误");
        return returnData.getData().getDiscountAmount();
    }

    /**
     * 订单类型转换未活动类型
     *
     * @param orderType
     * @return
     */
    private Integer conversionActiveType(Integer orderType) {
        switch (orderType) {
            case OrderType.SPIKE:
                return 5;
            case OrderType.BARGAIN:
                return 7;
            default:
                return null;
        }
    }

    /**
     * 获取价格计算
     *
     * @param checkOrder 是否下单动作
     * @return
     */
    @Override
    public OrderPriceDetailsInfo calcOrderPrice(CartOrderCouponParam param, Boolean checkOrder) {

        long userId = param.getUserId();

        log.info("-->计算用户{}的订单价格，参数:{}", param.getUserId(), JSONObject.toJSONString(param));

        // 是否会员
        boolean isMember = this.isMember(userId);

        // 要返回的对象
        OrderPriceDetailsInfo orderPriceDetailsInfo = new OrderPriceDetailsInfo();

        // 商品信息
        Details[] details = param.getDetails();
        
        // 校验活动订单
        double activityPreferentialMoney = 0;
        if (param.getOrderType() == OrderType.BARGAIN || param.getOrderType() == OrderType.SPIKE) {
            // TODO 检查其它活动订单
            ActiveGoodStore activeGoodStore = new ActiveGoodStore();
            activeGoodStore.setActiveType(conversionActiveType(param.getOrderType()));
            activeGoodStore.setPassingData(param.getPassingData());
            activeGoodStore.setOrderCheck(checkOrder);
            activeGoodStore.setOrderNo(param.getOrderNo());
            activeGoodStore.setUserId(userId);
            List<ActiveGoodStore.GoodSales> goodSalesList = new ArrayList<>();
            for (int i = 0; i < details.length; i++) {
                ActiveGoodStore.GoodSales goodSales = new ActiveGoodStore.GoodSales();
                goodSales.setGoodId(details[i].getGoodId());
                goodSales.setGoodNum(details[i].getCount());
                goodSales.setSaleId(Integer.valueOf(details[i].getSaleId()));
                goodSales.setSpu(details[i].getGoodSpu());
                goodSales.setSku(details[i].getGoodSku());
                goodSales.setUnitPrice(PriceConversion.intToString(PriceConversion.doubleToInt(details[i].getUnitPrice())));
                goodSales.setMemberPrice(PriceConversion.intToString(PriceConversion.doubleToInt(details[i].getMemberPrice())));
                goodSalesList.add(goodSales);
            }
            activeGoodStore.setGoodSales(goodSalesList);
            activityPreferentialMoney = this.checkOrder(activeGoodStore);
        }

        log.info("-->计算价格取得当前用户{}是否是会员{}", userId, isMember);

        // 商品总金额
        double allGoodsTotalPrice = param.getGoodTotalPrice(isMember);
        param.setGoodsTotalPrice(allGoodsTotalPrice);

        // 商品总件数
        int totalCount = param.getGoodTotalCount();
        param.setTotalCount(totalCount);

        //  处理买买金
        setMMKingInfo(param, orderPriceDetailsInfo);

        // 运费
        double freight = getFreight(param.getUserId(), param.getOrderType(), allGoodsTotalPrice, param.getDetails());

        // 订单金额，包含运费，不含优惠
        double orderTotalPrice = DoubleUtil.add(allGoodsTotalPrice, freight);

        // 支付金额
        double payPrice = 0d;

        // 优惠券的优惠金额
        double preferentialMoney = 0.0d;
        double discountAmount = 0.0d;
        if (param.getOrderType() == OrderType.BARGAIN) {
            discountAmount = activityPreferentialMoney;
            payPrice = DoubleUtil.sub(orderTotalPrice, discountAmount); // 砍价的支付金额=商品原价-砍掉的金额
        } else if (param.getOrderType() == OrderType.TEN_FOR_THREE_PIECE) {
            // 2019-03-22 0元试用即10元3件功能调整，运费为0，3件10元，6件20元...依此类推，即价格=件数/3*10，最高30件
            payPrice = (double) (param.getTotalCount() / 3 * 10); // 此步不考虑买买金抵扣金额，因为要依据此值先计算出活动优惠金额
//            preferentialMoney = DoubleUtil.sub(orderTotalPrice, payPrice); // 优惠金额
            discountAmount = DoubleUtil.sub(orderTotalPrice, payPrice);
        } else if (param.getOrderType() == OrderType.NEW_CUSTOMER_FREE_POST) {
            orderPriceDetailsInfo.setFreePost(freight); // 免邮特权，只针对新人免邮，先获取运费为10，此处理设置freePost为运费的值，到时候二者扣减，相当于免邮
            payPrice = DoubleUtil.sub(orderTotalPrice, freight);
        } else {
            String couponCode = param.getCouponCode();
            // 如果有使用优惠券，并且订单类型也符合使用的条件，则支付金额=订单金额-优惠券优惠金额
            if (StringUtils.isNotEmpty(couponCode) && canUseCoupon(param.getOrderType())) {
                try {
                    ReturnData<UserCouponDto> returnData = userFeignClient.myCouponInfo(Integer.valueOf(couponCode));
                    if (returnData != null && returnData.getData() != null) {
                        UserCouponDto coupon = returnData.getData();
                        preferentialMoney = Double.valueOf(coupon.getCouponInfo().getCouponValue());
                        log.info("-->优惠券{}的优惠金额为：{}元", coupon.getCouponCode(), preferentialMoney);
                    } else {
                        log.info("-->根据couponcode：{}没有查询到对应的数据, userId：{}", couponCode, param.getUserId());
                    }
                } catch (Exception e) {
                    log.info("获取优惠劵信息异常:" + e);
                }
                payPrice = DoubleUtil.sub(orderTotalPrice, preferentialMoney);
            } else {
                //对已生成的订单计算价格，但没有传来couponCode，表示没有可使用的优惠券, 支付金额  = 订单金额
                payPrice = orderTotalPrice;
            }
        }

        if (param.isKingSelected() && param.getUseKingNum() > 0 && param.getExchangeMoney() > 0) {
            // 如果有选择使用买买金，则最终支付价格需要减去抵扣的金额，但有种情况是优惠金额加上买买金抵扣金额会大于商品金额，此时需要对买买金进行特殊处理
            if (param.getExchangeMoney() > payPrice) {
                // 如果抵扣金额大于此时的支付金额，则最多只允许抵扣到0元，多的不进行抵扣
                double exchangeMoney = payPrice;
                int useKingNum = (int) (exchangeMoney * 1000);
                param.setExchangeMoney(exchangeMoney);
                param.setUseKingNum(useKingNum);
                orderPriceDetailsInfo.setExchangeMoney(exchangeMoney);
                orderPriceDetailsInfo.setUseKingNum(useKingNum);
            }
            payPrice = DoubleUtil.sub(payPrice, param.getExchangeMoney());
        }
        payPrice = payPrice >= 0 ? payPrice : 0;

        // 如果当单符合参与买送活动的条件，则计算本单可以获得的买买金数量

        try {
            //  买买金
            PayIsBuyGiveVo payIsBuyGiveVo = new PayIsBuyGiveVo();
            payIsBuyGiveVo.setUserid(param.getUserId());
            payIsBuyGiveVo.setOrderType(param.getOrderType());
            payIsBuyGiveVo.setOrderNo("");
            payIsBuyGiveVo.setGoodsAmount(allGoodsTotalPrice);
            payIsBuyGiveVo.setPayAmount(payPrice);
            log.info("调用用户享受买送接口入参,用户id为:{},订单类型:{},商品总额:{},支付金额:{}", param.getUserId().intValue(), param.getOrderType(), allGoodsTotalPrice, payPrice);
            ReturnData<Boolean> returnData = userFeignClient.getPayIsBuyGive(payIsBuyGiveVo);
            if (returnData != null && returnData.getData() != null) {
                boolean enjoyBuyGiveActivity = returnData.getData();
                log.info("-->用户{}当单享受买送结果:{}", param.getUserId(), enjoyBuyGiveActivity);
                if (enjoyBuyGiveActivity) {
                    orderPriceDetailsInfo.setBuyGiveKingCount((int) (payPrice * 1000));
                }
            }

        } catch (Exception e) {
            log.info("调用用户享受买送结果接口异常:" + e);
        }
        orderPriceDetailsInfo.setDiscountAmount(discountAmount);
        orderPriceDetailsInfo.setFreightFreeDesc(getFreightFreeDesc(freight, param.getOrderType(), isMember));
        orderPriceDetailsInfo.setFreight(freight);
        orderPriceDetailsInfo.setPayPrice(payPrice);
        orderPriceDetailsInfo.setOrderTotalPrice(orderTotalPrice);
        orderPriceDetailsInfo.setGoodTotalPrice(allGoodsTotalPrice);
        orderPriceDetailsInfo.setPreferentialMoney(preferentialMoney);
        log.info("-->给用户{}计算订单价格结果：{}", userId, JSONObject.toJSONString(orderPriceDetailsInfo));
        return orderPriceDetailsInfo;

    }

    /**
     * 对生成的订单进行价格计算，并将优惠和运费按金额比例分摊到各个商品
     */
    @Override
    public CartOrderGoodsDetails calcFinalPrice(CartOrderGoodsDetails cogd) throws Exception {
        log.info("-->calcFinalPrice-->生单后再次进行计算价格，参数：{}", JSONObject.toJSONString(cogd));

        if (cogd.getGoodsTotalPrice() < 0) {
            throw new IllegalArgumentException("价格非法");
        }
        /***先从CartOrderGoodsDetails取出数据封装到CartOrderCouponParam调用getCartOrderCouponResponse计算价格***/
        CartOrderCouponParam param = new CartOrderCouponParam();
        param.setUserId(cogd.getUserid());
        param.setCouponCode(cogd.getCouponCode());
        param.setGoodsTotalPrice(cogd.getGoodsTotalPrice());
        param.setOrderType(cogd.getOrderType());
        param.setKingSelected(cogd.isKingSelected());
        param.setUseKingNum(cogd.getUseKingNum());
        param.setExchangeMoney(cogd.getExchangeMoney());
        param.setPassingData(cogd.getPassingData());
        param.setBusinessId(cogd.getBusinessId());
        param.setOrderNo(cogd.getOrderNo());
        Details[] detailsArr = cogd.getDetails();
        int totalCount = 0;

        for (Details gd : detailsArr) {
            //此处传来的SKU是经过分组的，一个SKU的数量可能是多件
            totalCount = totalCount + gd.getCount();
        }
        param.setTotalCount(totalCount);
        param.setDetails(detailsArr);
        if (cogd.getOrderType() == OrderType.TEN_FOR_THREE_PIECE) {
            if (totalCount > 30) {
                throw new IllegalArgumentException("最多只能购买30件");
            }
            if (totalCount % 3 != 0) {
                int count = 3 - (totalCount % 3);
                throw new IllegalArgumentException("请再挑选" + count + "件结算");
            }
        }

        // 判断是否是会员
        boolean isMember = this.isMember(cogd.getUserid());
        cogd.setMember(isMember);

        // 调用方法计算价格
        OrderPriceDetailsInfo response = calcOrderPrice(param, false);

        // 重新设置买买金的抵扣信息，防止前端未选择使用买买金，但是传了买买金抵扣数据
        cogd.setKingSelected(response.isKingSelected());
        cogd.setUseKingNum(response.getUseKingNum());
        cogd.setExchangeMoney(response.getExchangeMoney());

        /*************************同一SKU可能有多件，此处进行拆分，同一SKU如果有3件，则拆分成三条********************/
        // 用户存储拆分后的所有SKU商品
        List<Details> splitedSKUDetailsList = new ArrayList<Details>();
        for (Details detail : detailsArr) {
            // 如果订单运费为0， 则每件商品对应的运费也是0
            if (response.getFreight() < 1) {
                detail.setFreight(0.0);
            }
            if (detail.getCount() > 1) {
                // 如果同一SKU有多件，则拆成多条
                for (int x = 0; x < detail.getCount(); x++) {
                    Details e = detail.clone();
                    e.setCount(1);
                    splitedSKUDetailsList.add(e);
                }
            } else {
                // 如果同一SKU只有一件，则直接放到list
                splitedSKUDetailsList.add(detail);
            }
        }

        if (cogd.getOrderType() == OrderType.TEN_FOR_THREE_PIECE) {
            double remainder = response.getPreferentialMoney();
            boolean isLastGoodsSKU = false;
            double apportionMoney = 0d;
            int i = 0;
            for (Details e : splitedSKUDetailsList) {
                i++;
                if (i == splitedSKUDetailsList.size()) {
                    isLastGoodsSKU = true;
                }
                apportionMoney = getApportionMoney(e.getFinalUnitPrice(isMember, param.getOrderType()), response.getGoodTotalPrice(),
                        response.getPreferentialMoney());
                log.info("-->calcFinalPrice-->TEN_FOR_THREE_PIECE====" + remainder + "-" + apportionMoney);
                remainder = DoubleUtil.sub(remainder, apportionMoney);
                log.info("-->calcFinalPrice-->TEN_FOR_THREE_PIECE   =" + remainder);
                if (isLastGoodsSKU) {
                    apportionMoney = DoubleUtil.add(apportionMoney, remainder);
                }
                log.info("-->calcFinalPrice-->TEN_FOR_THREE_PIECE-->apportionMoney: " + apportionMoney);
                e.setPreferentialMoney(apportionMoney);
            }
        }

        /**********************************************End**************************************************/
        // 如果有运费, 则需要根据商品比例分摊到每件商品上
        if (response.getFreight() > 0) {
            calcFreightApportionMoney(splitedSKUDetailsList, cogd.getGoodsTotalPrice(), response.getFreight(), totalCount, param.getOrderType(), isMember);
        }


        // 如果出现每件商品价格都是免费的，则不用再计算优惠
        if (StringUtils.isNotEmpty(param.getCouponCode()) && canUseCoupon(param.getOrderType())) {
            /* CouponDetailsInfo coupon = this.loadByCouponCode(param.getCouponCode());*/
            ReturnData<UserCouponDto> returnData = null;
            try {
                returnData = userFeignClient.myCouponInfo(Integer.valueOf(param.getCouponCode()));
            } catch (Exception e) {
                log.error("通过优惠券编码获取优惠券信息异常:", e);
                throw new CustomException("获取优惠券异常");
            }
            if (returnData != null && returnData.getData() != null) {
                UserCouponDto userCouponDto = returnData.getData();
                // 查询订单使用的优惠券应用到哪些商品SKU上（参数detailsArr中的sku还是分组的，并不是拆分后的）
                Set<String> skuSet = getCouponApplyGoodsSkuSet(userCouponDto, detailsArr);
                // 统计应用到优惠券的sku有多少个
                int applySKUCount = 0;
                Double applySKUTotalPrice = 0.0;
                for (Details e : splitedSKUDetailsList) {
                    if (skuSet.contains(e.getSaleId())) {
                        applySKUCount++;
                        applySKUTotalPrice = DoubleUtil.add(applySKUTotalPrice, e.getFinalUnitPrice(isMember, param.getOrderType()));
                    }
                }

                /**************************** 处理优惠券的优惠金额，分摊到对应的SKU***************************************/

                // 使用优惠的SKU根据商品金额比例计算
                Double remainder = 0.0d;
                // 遍历使用优惠的SKU，是否为最后一个，如果是，则需要加上余数
                boolean isLastGoodsSKU = false;
                // 对应SKU分摊的优惠金额
                Double apportionMoney = null;
                int i = 1;
                for (Details e : splitedSKUDetailsList) {
                    if (skuSet.contains(e.getSaleId())) {
                        if (i == applySKUCount) {
                            isLastGoodsSKU = true;
                        }

                        apportionMoney = getApportionMoney(e.getFinalUnitPrice(isMember, param.getOrderType()), applySKUTotalPrice,
                                response.getPreferentialMoney());
                        if (i == 1) {
                            remainder = response.getPreferentialMoney();
                        }
                        log.info("-->calcFinalPrice====" + remainder + "-" + apportionMoney);
                        remainder = DoubleUtil.sub(remainder, apportionMoney);
                        log.info("-->calcFinalPrice           =" + remainder);
                        if (isLastGoodsSKU) {
                            apportionMoney = DoubleUtil.add(apportionMoney, remainder);
                        }
                        log.info("-->calcFinalPrice-->apportionMoney: " + apportionMoney);
                        e.setPreferentialMoney(apportionMoney);
                        i++;
                    }

                    /**********************************************End**************************************************/
                }
            }
        }


        // 如果有使用买买金，则将抵扣的金额进行分摊
        if (response.isKingSelected() && response.getExchangeMoney() != null) {
            log.info("-->计算价格，用户{}有选择使用买买金，使用个数：{}，抵扣金额：{}", param.getUserId(), response.getUseKingNum(), response.getExchangeMoney());
            double remainder = response.getExchangeMoney();
            boolean isLastGoodsSKU = false;
            double apportionMoney = 0d;
            int i = 0;
            for (Details e : splitedSKUDetailsList) {
                i++;
                if (i == splitedSKUDetailsList.size()) {
                    isLastGoodsSKU = true;
                }
                apportionMoney = getApportionMoney(e.getFinalUnitPrice(isMember, param.getOrderType()), response.getGoodTotalPrice(),
                        response.getExchangeMoney());
                log.info("-->calcFinalPrice-->买买金分摊====" + remainder + "-" + apportionMoney);
                remainder = DoubleUtil.sub(remainder, apportionMoney);
                log.info("-->calcFinalPrice-->买买金分摊   =" + remainder);
                if (isLastGoodsSKU) {
                    apportionMoney = DoubleUtil.add(apportionMoney, remainder);
                }
                log.info("-->calcFinalPrice-->买买金分摊-->apportionMoney: " + apportionMoney);
                e.setExchangeMoney(apportionMoney);
            }
        }

        if (response.getDiscountAmount() > 0) {
            log.info("-->计算价格，用户{}有在优惠券之外的优惠金额：{}元", param.getUserId(), response.getDiscountAmount());
            double remainder = response.getDiscountAmount();
            boolean isLastGoodsSKU = false;
            double apportionMoney = 0d;
            int i = 0;
            for (Details e : splitedSKUDetailsList) {
                i++;
                if (i == splitedSKUDetailsList.size()) {
                    isLastGoodsSKU = true;
                }
                apportionMoney = getApportionMoney(e.getFinalUnitPrice(isMember, param.getOrderType()), response.getGoodTotalPrice(),
                        response.getDiscountAmount());
                log.info("-->calcFinalPrice-->优惠券金额分摊====" + remainder + "-" + apportionMoney);
                remainder = DoubleUtil.sub(remainder, apportionMoney);
                log.info("-->calcFinalPrice-->优惠券金额分摊   =" + remainder);
                if (isLastGoodsSKU) {
                    apportionMoney = DoubleUtil.add(apportionMoney, remainder);
                }
                log.info("-->calcFinalPrice-->优惠券金额分摊-->apportionMoney: " + apportionMoney);
                e.setDiscountAmount(apportionMoney);
            }
        }

        // 20181207迭代运费暂不分摊
        cogd.setFreight(response.getFreight());
        if (cogd.getOrderType() == OrderType.NEW_CUSTOMER_FREE_POST) {
            cogd.setFreight(0d); // 此处得新将运费设置为0
        }
        // 设置免邮描述，只有免邮的时候才给描述
        cogd.setFreightFreeDesc(getFreightFreeDesc(response.getFreight(), cogd.getOrderType(), isMember));
        // 商品总金额
        cogd.setGoodsTotalPrice(response.getGoodTotalPrice());
        // 原总价(不含优惠券的优惠)，包含运费
        cogd.setOrderTotalPrice(response.getOrderTotalPrice());
        // 支付金额
        cogd.setPayPrice(response.getPayPrice());
        // 优惠券的优惠金额
        cogd.setPreferentialMoney(response.getPreferentialMoney());
        // 非优惠券的优惠金额
        cogd.setDiscountAmount(response.getDiscountAmount());
        // 拆分后的商品SKU详情，已进行了优惠价格处理
        cogd.setDetails(splitedSKUDetailsList.toArray(new Details[]{}));

        return cogd;
    }


    private void calcFreightApportionMoney(List<Details> splitedSKUDetailsList, Double goodsTotalPrice,
                                           Double freight, int totalCount, int orderType, boolean isMember) {

        if (orderType == OrderType.NEW_CUSTOMER_FREE_POST) {
            log.info("新客免邮不参与价格分摊...");
            return;
        }

        // 使用优惠的SKU根据商品金额比例计算
        Double remainder = 0.0d;
        // 遍历使用优惠的SKU，是否为最后一个，如果是，则需要加上余数
        boolean isLastGoodsSKU = false;
        // 对应SKU分摊的优惠金额
        Double apportionMoney = null;
        int size = splitedSKUDetailsList.size();

        // 0元试用订单,orderType = 5，此类型订单所有商品单价均为0，件数为3件，商品总金额自然也为0，但要收运费10元，此时运费需根据件数来平摊
        if (orderType == OrderType.TEN_FOR_THREE_PIECE || orderType == OrderType.ZERO_SHOPPING) {
            // 运费根据件数来平摊
            for (int i = 0; i < size; i++) {
                Details e = splitedSKUDetailsList.get(i);
                if (i == size - 1) {
                    isLastGoodsSKU = true;
                }
                apportionMoney = DoubleUtil.divide(freight, Double.valueOf(totalCount), DoubleUtil.SCALE_3);
                if (i == 0) {
                    remainder = freight;
                }
                log.info("-->calcFreightApportionMoney====" + remainder + "-" + apportionMoney);
                remainder = DoubleUtil.sub(remainder, apportionMoney);
                log.info("-->calcFreightApportionMoney           =" + remainder);
                if (isLastGoodsSKU) {
                    apportionMoney = DoubleUtil.add(apportionMoney, remainder);
                }
                log.info("-->calcFreightApportionMoney-->apportionMoney: " + apportionMoney);
                e.setFreight(apportionMoney);
            }
        } else {
            // 运费根据商品金额比例分摊
            for (int i = 0; i < size; i++) {
                Details e = splitedSKUDetailsList.get(i);
                if (i == size - 1) {
                    isLastGoodsSKU = true;
                }
                apportionMoney = getApportionMoney(e.getFinalUnitPrice(isMember, orderType), goodsTotalPrice, freight);
                if (i == 0) {
                    remainder = freight;
                }
                log.info("-->calcFreightApportionMoney====" + remainder + "-" + apportionMoney);
                remainder = DoubleUtil.sub(remainder, apportionMoney);
                log.info("-->calcFreightApportionMoney           =" + remainder);
                if (isLastGoodsSKU) {
                    apportionMoney = DoubleUtil.add(apportionMoney, remainder);
                }
                log.info("-->calcFreightApportionMoney-->apportionMoney: " + apportionMoney);
                e.setFreight(apportionMoney);
            }
        }
    }


    /**
     * 查询订单使用的优惠券应用到哪些商品SKU上
     *
     * @param coupon     优惠券
     * @param detailsArr 其中的sku还是分组的，并不是拆分后的，如果传拆分后的则会增加查询goodsbaseid的次数，影响性能
     * @return
     */
    private Set<String> getCouponApplyGoodsSkuSet(UserCouponDto coupon, Details[] detailsArr) {

        /*********根据订单下的SKU查询出对应的goodsbaseid，以及goodsbaseid和SKU的映射（一对多）*********/
        String goodsBaseId = null;
        GoodSale goodSale = null;

        // 存放订单中的商品ID
        Set<String> orderGoodsBaseidSet = new HashSet<String>();
        // 存放商品ID和SKU集合的映射，一对多
        Map<String, Set<String>> goodsToSkuMap = new HashMap<String, Set<String>>();
        for (Details detail : detailsArr) {
            GoodSale sale = new GoodSale();
            sale.setSaleId(Integer.valueOf(detail.getSaleId()));
            try {
                ReturnData<Object> returnData = goodFeignClient.queryList(sale);
                if (returnData != null && returnData.getData() != null) {
                    List<GoodSale> goodSaleList = JSONArray.parseArray(JSON.toJSONString(returnData.getData()), GoodSale.class);
                    goodSale = goodSaleList.get(0);
                }
            } catch (Exception e) {
                log.info("价格计算--调用商品服务查询商品的SKU方法异常:" + e);
            }
            if (goodSale == null) {
                log.error("-->getCouponApplyGoodsSkuSet-->根据skuId:{}查询GoodsSku对象为空", detail.getSaleId());
                continue;
            }
            goodsBaseId = String.valueOf(goodSale.getGoodId());
            orderGoodsBaseidSet.add(goodsBaseId);
            if (goodsToSkuMap.get(goodsBaseId) != null) {
                Set<String> skuSet = goodsToSkuMap.get(goodsBaseId);
                skuSet.add(detail.getSaleId());
            } else {
                Set<String> skuSet = new HashSet<String>();
                skuSet.add(detail.getSaleId());
                goodsToSkuMap.put(goodsBaseId, skuSet);
            }
        }
        /*********************************************END**************************************/

        /****由于传来的优惠券是可使用的优惠券，所以此处只需取出优惠券在订单中能匹配到的商品，不作其它校验****/
        if (CouponConstants.UseRange.PART_GOODS_CAN_USE.equals(coupon.getCouponInfo().getCouponScope())
                || CouponConstants.UseRange.PART_GOODS_CANNOT_USE.equals(coupon.getCouponInfo().getCouponScope())) {

            try {
                ReturnData<List<CouponGood>> returnData = activeFeignClient.getCouponGoods(coupon.getCouponId());
                if (returnData != null && returnData.getData() != null) {
                    List<CouponGood> couponGoods = returnData.getData();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < couponGoods.size(); i++) {
                        sb.append(couponGoods.get(i).getGoodId()).append(",");
                    }
                    // 优惠券部分商品可用 || 部分商品不可用
                    // 取出优惠券配置的商品集合
                    //  商品id  coupon.getGoodsBaseid()
                    Set<String> goodsBaseidSet = CouponUtil.getGoodsBaseidSet(sb.toString());
                    if (CouponConstants.RangeType.CAN_USE.equals(coupon.getCouponInfo().getCouponScope())) {
                        // 如果优惠券配置的是可使用的商品，则用订单中的商品与配置的商品取交集
                        orderGoodsBaseidSet.retainAll(goodsBaseidSet);
                    } else {
                        // 如果优惠券配置的是不可使用的商品，则用订单中的商品与配置的商品取差集
                        orderGoodsBaseidSet.removeAll(goodsBaseidSet);
                    }
                }
            } catch (Exception e) {
                log.info("调用优惠券接口异常:" + e);
            }


        } else if (CouponConstants.UseRange.SPECIFY_CATEGORY_CAN_USE.equals(coupon.getCouponInfo().getCouponScope())) {
            try {
                ReturnData<List<CouponClass>> returnData = activeFeignClient.getCouponGoodsClass(coupon.getCouponId());
                if (returnData != null && returnData.getData() != null) {
                    List<CouponClass> list = returnData.getData();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        sb.append(list.get(i).getGoodClass()).append(",");
                    }
                    // 优惠券指定分类可用

                    // 遍历取出订单中的商品，再获取商品对应的categoryId，与优惠券配置的指定分类进行匹配

                    // 取出优惠券配置的商品分类集合
                    //   分类id coupon.getCategoryids()
                    Set<String> categoryIdSet = CouponUtil.getCategoryIdSet(sb.toString());
                    Long categoryId = null;
                    Iterator<String> iter = orderGoodsBaseidSet.iterator();
                    while (iter.hasNext()) {
                        goodsBaseId = iter.next();
                        try {
                            GoodInfo goodInfo = goodFeignClient.getById(Integer.valueOf(goodsBaseId));
                            categoryId = goodInfo.getCreaterId();
                        } catch (Exception e) {
                            log.info("根据商品Id查询商品基本信息报错:" + e);
                        }
                        log.info("-->goodsBaseId：{}顶层父节点:{}", goodsBaseId, categoryId);
                        if (categoryId != null && !categoryIdSet.contains(categoryId)) {
                            // 如果订单中的商品对应的分类不在优惠券配置的指定分类中，则将该商品从orderGoodsBaseidSet移除掉
                            iter.remove();
                        }
                    }
                }

            } catch (Exception e) {
                log.info("通过优惠券Id获取商品分类信息异常:" + e);
            }
        }
        // else
        // if(CouponConstants.UseRange.UNLIMITED.equals(coupon.getCouponRange()))
        // {
        // // 所有商品可用，既然总价达到了使用条件，那应该平摊到每个商品上面，orderGoodsBaseidSet原封不动，不作处理
        // }


        /******************此时的orderGoodsBaseidSet里存放的商品即是优惠券应用到的商品*******************/
        Set<String> finalSkuSet = new HashSet<String>();

        // 可使用优惠券的商品goodsbaseid集合，然后根据goodsbaseid取出对应的SKU集合返回
        for (String goodsId : orderGoodsBaseidSet) {
            finalSkuSet.addAll(goodsToSkuMap.get(goodsId));
        }
        return finalSkuSet;
    }


    /**
     * 获取每个SKU商品的优惠价格，根据商品单价的比例来分摊
     *
     * @param goodsUnitPrice  商品单价
     * @param goodsTotalPrice 商品总价
     * @return
     */
    private Double getApportionMoney(Double goodsUnitPrice, Double goodsTotalPrice, Double money) {
        Double proportion = DoubleUtil.divide(goodsUnitPrice, goodsTotalPrice, DoubleUtil.SCALE_3);
        return DoubleUtil.mul(proportion, money);
    }


    private String getFreightFreeDesc(Double freight, int orderType, boolean isMember) {
        if (freight != null && freight < 1) {
            if (isMember) {
                return "(会员权益包邮)";
            } else {
                if (orderType == OrderType.TEN_YUAN_SHOP) {
                    return "(全场满30元包邮)";
                } else if (orderType == OrderType.BARGAIN) {
                    return "砍价包邮";
                }
            }
        }
        return null;
    }


    /**
     * 获取运费
     *
     * @param userid
     * @param orderType
     * @param allGoodsTotalPrice
     * @return
     */
    private Double getFreight(Long userid, int orderType, Double allGoodsTotalPrice, Details[] details) {

        // 邮费规则：消费满30元包邮
        double freight = allGoodsTotalPrice < CommonConstant.FREIGHT_TOTAL_PRICE_LIMIT ? CommonConstant.FREIGHT_PRICE : 0.0d;

        // 获取免邮的订单类型
        Integer[] freeFreightOrderTypeArr = CommonConstant.FREIGHT_FREE_ORDERTYPE;
        if (Arrays.asList(freeFreightOrderTypeArr).contains(orderType)) {
            return 0d;
        }

        // 会员体系迭代：会员免邮，不管多少件
        try {
            ReturnData<Boolean> returnData = userFeignClient.isMember(userid);
            if (returnData != null && returnData.getData() != null) {
                boolean member = returnData.getData();
                if (member) {
                    log.info("-->用户{}的会员未过期，此次免邮", userid);
                    return 0d;
                }

            }
        } catch (Exception e) {
            log.info("价格计算----调用判断是否是会员异常:" + e);
        }

        /***************************************处理虚拟商品的运费，是则运费为0*******************************************/
        GoodInfo goodInfo = null;
        boolean orderContainsVirtualGoods = false;
        Details cartGood = null;
        for (int i = 0; i < details.length; i++) {
            cartGood = details[i];
            goodInfo = goodFeignClient.getById(cartGood.getGoodId());
            if (goodInfo != null && goodInfo.getVirtualFlag() != null && goodInfo.getVirtualFlag() == 1) {
                orderContainsVirtualGoods = true;
                break;
            }
        }
        if ((orderType == OrderType.TEN_YUAN_SHOP || orderType == OrderType.ZERO_SHOPPING) && orderContainsVirtualGoods) {
            // 虚拟商品运费为0
            return 0d;
        }
        return freight;
    }


    private void setMMKingInfo(CartOrderCouponParam param, OrderPriceDetailsInfo response) {
        int orderType = param.getOrderType();
        // 如果订单类型不满足使用条件，但传过来的却是用户选择使用了买买金，则需要强制设置成false
        boolean useKing = false;
        // 当前只有普通订单、十元店订单、拼团订单（二人团）可进行买买金抵扣
        if (orderType == OrderType.ORDINARY || orderType == OrderType.TEN_YUAN_SHOP || (OrderType.TWO_GROUP == orderType)) {
            // 如果订单类型匹配，则前端传的isKingSelected是合法的，此处承认可使用买买金
            useKing = param.isKingSelected();
        } else {
            log.info("-->setMMKingInfo-->用户{}当前订单类型不可使用买买金进行抵扣，orderType:{}, orderChildType:{}",
                    param.getUserId(), orderType);
        }
        response.setKingSelected(useKing); // 重新设置是否使用买买金的状态，用户可以选择使用，也可以选择不使用，如果选择使用，但订单类型不匹配，此处也可以强制改为未选择使用
        param.setKingSelected(useKing); // 重新设置是否使用买买金的状态
        if (useKing) {
            // 如果用户选择使用买买金
            response.setExchangeMoney(param.getExchangeMoney() != null ? param.getExchangeMoney() : 0); // 买买金抵扣的金额
            response.setUseKingNum(param.getUseKingNum() != null ? param.getUseKingNum() : 0); // 买买金使用的数量
        } else {
            response.setExchangeMoney(0d);
            response.setUseKingNum(0);
        }
        log.info("-->用户{}是否使用买买金:{}", param.getUserId(), useKing);
    }


    /**
     * 根据订单类型判断是否可使用优惠券
     *
     * @param orderType
     * @param orderChildType
     * @return
     */
    private boolean canUseCoupon(int orderType) {
        if (orderType == OrderType.LOTTERY) {
            return false;
        } else if (orderType == OrderType.TWO_GROUP) {
            return false;
        } else if (orderType == OrderType.FREE_ORDER) {
            return false;
        } else if (orderType == OrderType.TEN_FOR_THREE_PIECE) {
            return false;
        } else if (orderType == OrderType.BARGAIN) {
            return false;
        } else if (orderType == OrderType.ZERO_SHOPPING) {
            return false;
        } else if (orderType == OrderType.RELAY_LOTTERY) {
            return false;
        } else if (orderType == OrderType.NEW_CUSTOMER_FREE_POST) {
            return false;
        } else if (orderType == OrderType.FREE_ORDER) {
            return false;
        } else if (orderType == OrderType.MM_KING) {
            return false;
        } else if (orderType == OrderType.GROUP_BUY) {
            return false;
        } else if (orderType == OrderType.NEWCOMERS) {
            return false;
        } else if (orderType == OrderType.SPIKE) {
            return false;
        }
        return true;
    }


}
