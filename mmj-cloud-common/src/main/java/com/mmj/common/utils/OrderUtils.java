package com.mmj.common.utils;

import com.google.common.collect.Lists;
import com.mmj.common.constants.ActiveGoodsConstants;
import com.mmj.common.constants.OrderClassify;
import com.mmj.common.constants.OrderSource;
import com.mmj.common.constants.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 订单相关工具类
 */
@Slf4j
public class OrderUtils {

    /**
     * 新团订单类型
     */
    private final static List<Integer> newGroupOrderTypes = Lists.newArrayList(
            OrderType.LOTTERY,
            OrderType.GROUP_BUY,
            OrderType.RELAY_LOTTERY,
            OrderType.TWO_GROUP,
            OrderType.NEWCOMERS);
    /**
     * 旧团订单类型
     */
    private final static List<Integer> oldGroupOrderTypes = Lists.newArrayList(2, 3, 8);

    /**
     * 根据订单号或售后单号判断新旧订单
     *
     * @param no
     * @return true：新订单，false：老订单
     */
    public static boolean isNewOrOld(String no) {
        return no.startsWith("3");
    }

    /**
     * 判断传入订单号是否为包裹号
     *
     * @param no
     * @return
     */
    public static boolean isPackageNo(String no) {
        if (isNewOrOld(no)) {
            int length = no.length();
            int index = Integer.parseInt(no.substring(length - 5, length));
            return index == OrderClassify.SON.ordinal();
        } else {
            return no.indexOf("-") != -1;
        }
    }

    /**
     * 通过订单号获取订单类型
     *
     * @param orderNo
     * @return
     */
    public static int getOrderType(String orderNo) {
        if (orderNo.startsWith("hy")) { //会员订单类型
            return -1;
        }
        if (isNewOrOld(orderNo)) {
            int length = orderNo.length();
            return Integer.parseInt(orderNo.substring(length - 2, length));
        } else {
            //TODO 老订单号类型未实现
            return 0;
        }
    }

    /**
     * 通过订单类型判断是否为订单团类型
     *
     * @param orderType
     * @return
     */
    public static boolean isGroupOrder(Integer orderType) {
        if (null == orderType)
            return false;
        return newGroupOrderTypes.contains(orderType);
    }

    /**
     * 通过订单号判断订单是否团订单
     *
     * @param orderNo
     * @return
     */
    public static boolean isGroupOrder(String orderNo) {
        return isGroupOrder(orderNo, null);
    }

    /**
     * 通过订单号判断订单是否是团订单
     *
     * @param orderNo
     * @return
     */
    public static boolean isGroupOrder(String orderNo, List<Integer> filterOrderTypes) {
        if (StringUtils.isEmpty(orderNo))
            return false;
        boolean bool = isNewOrOld(orderNo);
        boolean filterActive = Objects.nonNull(filterOrderTypes);
        List<Integer> orderTypes = Lists.newArrayListWithCapacity(5);
        orderTypes.addAll((bool ? newGroupOrderTypes : oldGroupOrderTypes));
        if (filterActive) {
            orderTypes.removeAll(filterOrderTypes);
        }
        int length = orderNo.length();
        int orderType;
        if (bool) { //新订单
            orderType = Integer.parseInt(orderNo.substring(length - 2, length));
            return orderTypes.contains(orderType);
        } else { //老订单
            orderType = Integer.parseInt(orderNo.substring(length - 3, length - 1));
        }
        return orderTypes.contains(orderType);
    }

    /**
     * 新订单号或者新售后单号获取取模值
     *
     * @param no
     * @return
     */
    public static long getNewDelivery(String no) {
        return Long.parseLong(no.substring(no.length() - 4, no.length() - 2));
    }

    /**
     * 生成订单号
     *
     * @param orderType
     * @param source
     * @return
     */
    public static String gainOrderNo(Long userId, Integer orderType, String source, OrderClassify orderClassify) {
        String userIdStr = userId.toString();
        int length = userIdStr.length();
        int delivery = Integer.parseInt(userIdStr.substring(length - 2, length));
        Random random = new Random();
        String result = "3";//3开头为新订单

        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }
        int sourceOrdinal = 0;
        try {
            sourceOrdinal = OrderSource.valueOf(source).ordinal();
        } catch (Exception e) {
            log.error("=> 生成订单号 传入参数错误 source:{},orderType:{},orderChileType:{},packageNum:{}", source, orderType);
        }
        int type = (Objects.isNull(orderType) ? OrderType.ORDINARY : orderType.intValue());
        StringBuffer sb = new StringBuffer();
        sb.append(result);//首位2和随机数
        sb.append(System.currentTimeMillis() + "")//时间戳
                .append(sourceOrdinal)//订单来源
                .append(orderClassify.ordinal())//订单分类
                .append(delivery < 10 ? "0" + delivery : delivery)//取模数
                .append(type < 10 ? "0" + type : type);//订单类型
        return sb.toString();
    }

    /**
     * 生成售后单号
     *
     * @param orderType
     * @return
     */
    public static String gainAfterNo(Integer orderType, String userId) {
        int delivery = Integer.parseInt(userId.substring(userId.length() - 2, userId.length()));
        Random random = new Random();
        String result = "3";//2开头为新订单

        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }
        int type = (Objects.isNull(orderType) ? OrderType.ORDINARY : orderType.intValue());
        StringBuffer sb = new StringBuffer();
        sb.append(result);//首位2和随机数
        sb.append(System.currentTimeMillis() + "")//时间戳
                .append(delivery)//取模数
                .append(type < 10 ? "0" + type : type);//订单类型
        return sb.toString();
    }

    /**
     * 订单类型转换为活动类型
     * 活动：
     * 二人团
     * 限时秒杀
     * 砍价
     * 抽奖
     * 接力购
     * 接力购抽奖
     * 零元购(TODO 未定义)
     * 买买金兑换
     *
     * @param orderType
     * @return 0错误类型 >0活动类型
     */
    public static int orderTypeToActiveType(Integer orderType) {
        Assert.notNull(orderType, "订单类型为空");
        switch (orderType) {
            case OrderType.BARGAIN:
                return ActiveGoodsConstants.ActiveType.CUT;
            case OrderType.TWO_GROUP:
                return ActiveGoodsConstants.ActiveType.TUAN;
            case OrderType.SPIKE:
                return ActiveGoodsConstants.ActiveType.SECKILL;
            case OrderType.LOTTERY:
                return ActiveGoodsConstants.ActiveType.GROUP_LOTTERY;
            case OrderType.NEWCOMERS:
                return ActiveGoodsConstants.ActiveType.GROUP_JIELIGOU;
            case OrderType.RELAY_LOTTERY:
                return ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY;
            case OrderType.MM_KING:
                return ActiveGoodsConstants.ActiveType.MMJ_GOOD;
            case OrderType.FREE_ORDER:
                return ActiveGoodsConstants.ActiveType.FREE_ORDER;
            default:
                return 0;
        }
    }
}
