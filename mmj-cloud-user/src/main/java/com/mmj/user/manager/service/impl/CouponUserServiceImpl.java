package com.mmj.user.manager.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.common.constants.OrderType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.good.GoodInfo;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.UserMergeProcessor;
import com.mmj.user.common.feigin.ActiveFeignClient;
import com.mmj.user.common.feigin.GoodFeignClient;
import com.mmj.user.common.feigin.NoticeFeignClient;
import com.mmj.user.common.model.dto.CouponInfoDto;
import com.mmj.user.common.model.dto.CouponNumDto;
import com.mmj.user.manager.dto.*;
import com.mmj.user.manager.mapper.CouponUserMapper;
import com.mmj.user.manager.model.CouponUser;
import com.mmj.user.manager.service.CouponUserService;
import com.mmj.user.manager.vo.*;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.service.UserMemberService;
import com.mmj.user.utils.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户关联优惠券表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-07-04
 */
@Slf4j
@Service
public class CouponUserServiceImpl extends ServiceImpl<CouponUserMapper, CouponUser> implements CouponUserService {
    @Autowired
    private ActiveFeignClient activeFeignClient;
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private NoticeFeignClient noticeFeignClient;
    @Autowired
    private GoodFeignClient goodFeignClient;
    @Autowired
    private MQProducer mqProducer;
    @Autowired
    private UserMergeProcessor userMergeProcessor;

    /**
     * 获取合并后的用户
     *
     * @param userId
     * @return
     */
    public Map<Long, List<Long>> getMergeUserListMap(Long userId) {
        return userMergeProcessor.getAllToMoldMap(userId, 10);
    }

    /**
     * 判断用户是否会员
     *
     * @param userId
     * @return
     */
    private boolean isMemberUser(Long userId) {
        //当前用户是否会员
        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(userId);
        return Objects.nonNull(userMember) && userMember.getActive();
    }

    @Override
    public ProduceOrderCouponDto produceOrderCoupon(ProduceOrderCouponVo produceOrderCouponVo) {
        JwtUserDetails userDetails = getUserDetails();
        //当前用户是否会员
        boolean userIsMember = isMemberUser(userDetails.getUserId());
        //订单类型
        int orderType = produceOrderCouponVo.getOrderType();
        //计算商品总额
        final List<ProduceOrderCouponVo.Goods> goodsList = produceOrderCouponVo.getGoods();
        int joinTotalGoodsAmount = 0; //商品总价
        int joinTotalGoodsNum = 0; //商品购买总件数
        final List<Integer> goodIdList = Lists.newArrayListWithCapacity(goodsList.size());
        for (ProduceOrderCouponVo.Goods goods : goodsList) {
            joinTotalGoodsAmount += PriceConversion.stringToInt(userIsMember ? goods.getMemberPrice() : goods.getUnitPrice()) * goods.getGoodNum();
            joinTotalGoodsNum += goods.getGoodNum();
            goodIdList.add(goods.getGoodId());
        }
        GoodInfo queryGoodInfo = new GoodInfo();
        queryGoodInfo.setGoodIds(goodIdList);
        ReturnData<List<GoodInfo>> returnData = goodFeignClient.queryGoodTT(queryGoodInfo);
        Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode() && returnData.getData().size() > 0, "未获取到商品信息");
        List<GoodInfo> goodInfoList = returnData.getData();
        goodsList.forEach(goods -> {
            GoodInfo goodInfo = goodInfoList.stream().filter(gi -> goods.getGoodId().intValue() == gi.getGoodId().intValue()).findFirst().orElse(null);
            if (Objects.nonNull(goodInfo) && StringUtils.isNotEmpty(goodInfo.getGoodClass())) {
                if (goodInfo.getGoodClass().length() > 4) {
                    goods.setGoodClass(goodInfo.getGoodClass().substring(0, 4));
                } else {
                    goods.setGoodClass(goodInfo.getGoodClass());
                }
            }
        });

        final int totalGoodsAmount = joinTotalGoodsAmount;
        final int totalGoodsNum = joinTotalGoodsNum;
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        ProduceOrderCouponDto produceOrderCouponDto = new ProduceOrderCouponDto();
        CouponUser queryCouponUser = new CouponUser();
        queryCouponUser.setUsedFlag(0);
        List<CouponUser> couponUsers = Lists.newArrayList();
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userDetails.getUserId());
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper<>(queryCouponUser);
            entityWrapper.in("USER_ID", v);
            entityWrapper.ge("END_TIME", new Date());
            entityWrapper.orderBy("MAPPER_ID", false);
            couponUsers.addAll(selectList(entityWrapper));
        });
        List<UserCouponDto> userCouponDtoList = toUserCouponDto(couponUsers);
        List<UserCouponDto> normals = Lists.newArrayList();
        List<UserCouponDto> invalids = Lists.newArrayList();
        userCouponDtoList.forEach(userCouponDto -> {
            int cpTotalGoodsAmount = totalGoodsAmount;
            int cpTotalGoodsNum = totalGoodsNum;
            boolean validStatus = false;
            boolean canUseCoupon = canUseCoupon(orderType) && userCouponDto.getValidStatus() == 0;
            if (canUseCoupon) {
                CouponInfoDataDto couponInfoDataDto = userCouponDto.getCouponInfo();
                //使用范围 - 1：所有商品可用；2：部分商品可用；3：部分商品不可用；4：指定分类可用
                if ("2".equals(couponInfoDataDto.getCouponScope())) {
                    cpTotalGoodsAmount = 0;
                    cpTotalGoodsNum = 0;
                    for (ProduceOrderCouponVo.Goods goods : goodsList) {
                        validStatus = couponInfoDataDto.getGoodIdList().contains(goods.getGoodId());
                        if (validStatus) {
                            cpTotalGoodsAmount += PriceConversion.stringToInt(userIsMember ? goods.getMemberPrice() : goods.getUnitPrice()) * goods.getGoodNum();
                            cpTotalGoodsNum += goods.getGoodNum();
                        }
                    }
                    validStatus = cpTotalGoodsAmount > 0;
                } else if ("3".equals(couponInfoDataDto.getCouponScope())) {
                    cpTotalGoodsAmount = 0;
                    cpTotalGoodsNum = 0;
                    for (ProduceOrderCouponVo.Goods goods : goodsList) {
                        validStatus = couponInfoDataDto.getGoodIdList().contains(goods.getGoodId());
                        if (!validStatus) {
                            cpTotalGoodsAmount += PriceConversion.stringToInt(userIsMember ? goods.getMemberPrice() : goods.getUnitPrice()) * goods.getGoodNum();
                            cpTotalGoodsNum += goods.getGoodNum();
                        }
                    }
                    validStatus = cpTotalGoodsAmount > 0;
                } else if ("4".equals(couponInfoDataDto.getCouponScope())) {
                    cpTotalGoodsAmount = 0;
                    cpTotalGoodsNum = 0;
                    for (ProduceOrderCouponVo.Goods goods : goodsList) {
                        validStatus = couponInfoDataDto.getGoodClassList().contains(goods.getGoodClass());
                        if (validStatus) {
                            cpTotalGoodsAmount += PriceConversion.stringToInt(userIsMember ? goods.getMemberPrice() : goods.getUnitPrice()) * goods.getGoodNum();
                            cpTotalGoodsNum += goods.getGoodNum();
                        }
                    }
                    validStatus = cpTotalGoodsAmount > 0;
                } else {
                    validStatus = true;
                }
                if (validStatus) {
                    if (couponInfoDataDto.getCouponMain() == 1) { //商品金额
                        if ("2".equals(couponInfoDataDto.getWhereType())) {
                            validStatus = cpTotalGoodsAmount >= PriceConversion.stringToInt(couponInfoDataDto.getWhereValue());
                        } else if ("3".equals(couponInfoDataDto.getWhereType())) {
                            validStatus = cpTotalGoodsNum >= Integer.parseInt(couponInfoDataDto.getWhereValue());
                        }
                        if (validStatus && "1".equals(couponInfoDataDto.getCouponAmount())) { //优惠类型  1 减X元; 2 打X拆
                            validStatus = cpTotalGoodsAmount - PriceConversion.stringToInt(couponInfoDataDto.getCouponValue()) > 0;
                        }
                    } else if (couponInfoDataDto.getCouponMain() == 3) { //运费金额
                        if (userIsMember || cpTotalGoodsAmount >= 3000) { //会员免邮|商品总金额满30元免邮
                            validStatus = false;
                        } else {
                            if ("2".equals(couponInfoDataDto.getWhereType())) { //运费写死10元
                                validStatus = 1000 >= PriceConversion.stringToInt(couponInfoDataDto.getWhereValue());
                            } else if ("3".equals(couponInfoDataDto.getWhereType())) {
                                validStatus = cpTotalGoodsNum >= Integer.parseInt(couponInfoDataDto.getWhereValue());
                            }
                        }
                    }
                }
            }
            if (validStatus) {//有效
                normals.add(userCouponDto);
            } else {//无效
                invalids.add(userCouponDto);
            }
        });
        produceOrderCouponDto.setNormals(normals);
        produceOrderCouponDto.setInvalids(invalids);
        return produceOrderCouponDto;
    }

    /**
     * 根据类型判断是否可用优惠券
     *
     * @param orderType
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

    @Override
    public PersonalCouponInfoDto personalCouponInfo() {
        JwtUserDetails jwtUserDetails = getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
        PersonalCouponInfoDto personalCouponInfoDto = new PersonalCouponInfoDto();
        boolean memberDay = isMemberDay();
        personalCouponInfoDto.setMemberDay(memberDay);
        CouponUser couponUser = new CouponUser();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(jwtUserDetails.getUserId());
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> couponUserEntityWrapper = new EntityWrapper<>(couponUser);
            couponUserEntityWrapper.ge("END_TIME", new Date());
            couponUserEntityWrapper.eq("USED_FLAG", 0);
            couponUserEntityWrapper.in("USER_ID", v);
            atomicInteger.addAndGet(selectCount(couponUserEntityWrapper));
        });
        personalCouponInfoDto.setCouponTotal(atomicInteger.get());
        ReturnData<BaseDict> returnData = noticeFeignClient.queryGlobalConfigByDictCode("mmj.memberday.coupon.totalmoney");
        if (memberDay) {
            if (isMemberUser(jwtUserDetails.getUserId())) { //当前用户是会员
                personalCouponInfoDto.setDesc("今日" + returnData.getData().getDictValue() + "元可领");
            } else {
                personalCouponInfoDto.setDesc("会员" + returnData.getData().getDictValue() + "元可领");
            }
        } else {
            personalCouponInfoDto.setDesc("每周三会员日领券");
        }
        return personalCouponInfoDto;
    }

    /**
     * 获取优惠券
     *
     * @param couponId
     * @return
     */
    private CouponInfoDto getCouponInfo(Integer couponId) {
        ReturnData<CouponInfoDto> returnData = activeFeignClient.getCouponInfo(couponId);
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && Objects.nonNull(returnData.getData()), "获取优惠券信息失败");
        return returnData.getData();
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    private JwtUserDetails getUserDetails() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(jwtUserDetails, "缺少用户信息");
        return jwtUserDetails;
    }

    @Override
    public void batchReceive(UserCouponBatchVo userCouponBatchVo) {
        Long userId;
        if (Objects.nonNull(userCouponBatchVo.getUserId())) {
            userId = userCouponBatchVo.getUserId();
        } else {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            userId = userDetails.getUserId();
        }
        Assert.notNull(userId, "发送优惠券缺少用户Id");
        ReturnData<List<CouponInfoDto>> returnData = activeFeignClient.batchCouponInfos(userCouponBatchVo.getCouponIds());
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && Objects.nonNull(returnData.getData()) && returnData.getData().size() > 0, returnData.getDesc());
        List<CouponUser> couponUserList = Lists.newArrayListWithCapacity(returnData.getData().size());
        returnData.getData().forEach(couponInfo -> {
            int countNum = Objects.isNull(couponInfo.getCountNum()) ? 0 : couponInfo.getCountNum();
            int toDaySendNumber = Objects.isNull(couponInfo.getToDaySendNumber()) ? 0 : couponInfo.getToDaySendNumber();
            Assert.isTrue(countNum == -1 || (countNum >= 0 && countNum <= toDaySendNumber), "总量限制:" + couponInfo.getCouponId());
            Integer everyDayNum = couponInfo.getEveryDayNum();
            everyDayNum = Objects.isNull(everyDayNum) ? 0 : everyDayNum;
            if (everyDayNum > 0) { //每日发放量限制
                Integer todayReceiveNum = couponInfo.getToDaySendNumber();
                todayReceiveNum = Objects.isNull(todayReceiveNum) ? 0 : todayReceiveNum;
                Assert.isTrue(todayReceiveNum >= everyDayNum, "每日限制:" + couponInfo.getCouponId());
            }

            Date startTime, endTime; //优惠券有效期范围
            String indateType = couponInfo.getIndateType();
            if ("2".equals(indateType)) { //2 领取后生效
                Integer afterDay = couponInfo.getAfterDay(); //几天后生效
                afterDay = Objects.isNull(afterDay) ? 0 : afterDay;
                Calendar calendar = Calendar.getInstance();
                if (afterDay > 0) {
                    calendar.add(Calendar.DAY_OF_MONTH, afterDay);
                }
                startTime = calendar.getTime();
                String afterUnit = couponInfo.getAfterUnit();
                if ("DATE".equals(afterUnit)) { //日期
                    endTime = couponInfo.getAfterDate(); //某日期前后效
                } else if ("HOURS".equals(afterUnit)) { //小时
                    calendar.add(Calendar.HOUR_OF_DAY, couponInfo.getAfterTime());
                    endTime = calendar.getTime();
                } else if ("MINUTES".equals(afterUnit)) { //分钟
                    calendar.add(Calendar.MINUTE, couponInfo.getAfterTime());
                    endTime = calendar.getTime();
                } else if ("DAYS".equals(afterUnit)) { //天
                    calendar.add(Calendar.DAY_OF_MONTH, couponInfo.getAfterTime());
                    endTime = calendar.getTime();
                } else {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    endTime = calendar.getTime();
                }
            } else {//1 时间区间
                startTime = couponInfo.getCouponStart();
                endTime = couponInfo.getCouponEnd();
            }

            CouponUser couponUser = new CouponUser();
            couponUser.setUserId(userId);
            couponUser.setCouponId(couponInfo.getCouponId());
            couponUser.setCouponSource(userCouponBatchVo.getCouponSource());
            couponUser.setStartTime(startTime);
            couponUser.setEndTime(endTime);
            couponUser.setCheckTime(new Date()); //领取时间
            couponUser.setUsedFlag(0); //未使用
            couponUser.setSendFlag(0);
            couponUser.setMissFlag(0); //未失效
            couponUser.setDistanceTime(new Date(couponUser.getEndTime().getTime() - (couponInfo.getDistanceTime() * 1000)));
            couponUserList.add(couponUser);
        });
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        boolean result = insertBatch(couponUserList);
        Assert.isTrue(result, "添加优惠券失败");
        couponUserList.forEach(couponUser -> {
            activeFeignClient.issued(couponUser.getCouponId());
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserReceiveCouponDto receive(UserCouponVo userCouponVo) {
        Long userId;
        if (Objects.nonNull(userCouponVo.getUserId())) {
            userId = userCouponVo.getUserId();
        } else {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            userId = userDetails.getUserId();
        }
        Assert.notNull(userId, "发送优惠券缺少用户Id");
        String couponSource = userCouponVo.getCouponSource();
        if (CouponSource.INDEX_SEND.equalsIgnoreCase(couponSource)
                || CouponSource.QR_RECIEVE.equalsIgnoreCase(couponSource)
                || CouponSource.TOPIC_SEND.equalsIgnoreCase(couponSource)
                || CouponSource.ADD_MINI_APPS.equalsIgnoreCase(couponSource)
                || CouponSource.BUY_GIVE.equalsIgnoreCase(couponSource)
                || CouponSource.GOODS_DETAILS.equalsIgnoreCase(couponSource)) {
            // 判断用户是否已领取过
            if (hasReceive(userId, userCouponVo.getCouponId())) {
                return new UserReceiveCouponDto(0);
            }
        }
        CouponInfoDto couponInfo = getCouponInfo(userCouponVo.getCouponId());
        if (CouponSource.MEMBER_DAY.equalsIgnoreCase(couponInfo.getActiveFlag())) {
            boolean memberCanReceive = isMemberDay() && isMemberUser(userId);
            if (!memberCanReceive) {
                throw new IllegalArgumentException("优惠券条件限制:会员日会员专享");
            }
            if (todayHasReceive(userId, userCouponVo.getCouponId())) {
                return new UserReceiveCouponDto(0);
            }
            Assert.isTrue(getTodayReceiveNum(userId, CouponSource.MEMBER_DAY) < 3, "优惠券条件限制:会员日只能领取3张");
        }
        int countNum = Objects.isNull(couponInfo.getCountNum()) ? 0 : couponInfo.getCountNum();
        int toDaySendNumber = Objects.isNull(couponInfo.getToDaySendNumber()) ? 0 : couponInfo.getToDaySendNumber();
        if (countNum >= 0 && countNum <= toDaySendNumber) { //总量限制
            return new UserReceiveCouponDto(2);
        }
        Integer everyDayNum = couponInfo.getEveryDayNum();
        everyDayNum = Objects.isNull(everyDayNum) ? 0 : everyDayNum;
        if (everyDayNum > 0) { //每日发放量限制
            Integer todayReceiveNum = couponInfo.getToDaySendNumber();
            todayReceiveNum = Objects.isNull(todayReceiveNum) ? 0 : todayReceiveNum;
            if (todayReceiveNum >= everyDayNum) {
                return new UserReceiveCouponDto(2);
            }
        }

        Date startTime, endTime; //优惠券有效期范围
        String indateType = couponInfo.getIndateType();
        if ("1".equals(indateType)) { //1 时间区间
            startTime = couponInfo.getCouponStart();
            endTime = couponInfo.getCouponEnd();
        } else if ("2".equals(indateType)) { //2 领取后生效
            Integer afterDay = couponInfo.getAfterDay(); //几天后生效
            afterDay = Objects.isNull(afterDay) ? 0 : afterDay;
            Calendar calendar = Calendar.getInstance();
            if (afterDay > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, afterDay);
            }
            startTime = calendar.getTime();
            String afterUnit = couponInfo.getAfterUnit();
            if ("DATE".equals(afterUnit)) { //日期
                endTime = couponInfo.getAfterDate(); //某日期前后效
            } else if ("HOURS".equals(afterUnit)) { //小时
                calendar.add(Calendar.HOUR_OF_DAY, couponInfo.getAfterTime());
                endTime = calendar.getTime();
            } else if ("MINUTES".equals(afterUnit)) { //分钟
                calendar.add(Calendar.MINUTE, couponInfo.getAfterTime());
                endTime = calendar.getTime();
            } else if ("DAYS".equals(afterUnit)) { //天
                calendar.add(Calendar.DAY_OF_MONTH, couponInfo.getAfterTime());
                endTime = calendar.getTime();
            } else {
                return new UserReceiveCouponDto(-1);
            }
        } else {
            return new UserReceiveCouponDto(-1); //未找到优惠券有效期类型
        }

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        CouponUser couponUser = new CouponUser();
        couponUser.setUserId(userId);
        couponUser.setCouponId(couponInfo.getCouponId());
        couponUser.setCouponSource(userCouponVo.getCouponSource());
        couponUser.setStartTime(startTime);
        couponUser.setEndTime(endTime);
        couponUser.setCheckTime(new Date()); //领取时间
        couponUser.setUsedFlag(0); //未使用
        couponUser.setSendFlag(0);
        couponUser.setMissFlag(0); //未失效
        couponUser.setDistanceTime(new Date(couponUser.getEndTime().getTime() - (couponInfo.getDistanceTime() * 1000)));
        boolean result = insert(couponUser);
        Assert.isTrue(result, "添加优惠券失败");
        ReturnData returnData = activeFeignClient.issued(userCouponVo.getCouponId());
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, "已发放优惠券计数失败");
        return new UserReceiveCouponDto(1, toUserCouponDto(couponUser, couponInfo));
    }

    public void addReceiveTask(UserCouponDto userCouponDto) {
        //TODO 优惠券模板消息未实现
    }

    @Override
    public boolean use(UseUserCouponVo useUserCouponVo) {
        Long userId;
        if (Objects.nonNull(useUserCouponVo.getUserId())) {
            userId = useUserCouponVo.getUserId();
        } else {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            userId = userDetails.getUserId();
        }
        log.info("==> 使用优惠券 userId:{},couponCode:{},orderNo:{},useStatus:{}", userId, useUserCouponVo.getCouponCode(), useUserCouponVo.getOrderNo(), useUserCouponVo.getUseStatus());
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userId);
        CouponUser couponUser = new CouponUser();
        mergeUserListMap.forEach((k, v) -> {
            if (Objects.nonNull(couponUser.getMapperId()))
                return;
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            CouponUser couponUserTemp = getUserCoupon(v, useUserCouponVo.getCouponCode(), useUserCouponVo.getOrderNo());
            if (Objects.nonNull(couponUserTemp)) {
                BeanUtils.copyProperties(couponUserTemp, couponUser);
                return;
            }
        });
        if (Objects.isNull(couponUser.getMapperId()))
            return false;
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, couponUser.getUserId());
        boolean useStatus = Objects.isNull(useUserCouponVo.getUseStatus()) ? true : useUserCouponVo.getUseStatus();
        CouponUser updateCouponUser = new CouponUser();
        if (useStatus) { //使用
            updateCouponUser.setUsedFlag(1);
            updateCouponUser.setOrderNo(useUserCouponVo.getOrderNo());
        } else { //未使用
            updateCouponUser.setUsedFlag(0);
        }
        CouponUser queryCouponUser = new CouponUser();
        queryCouponUser.setUserId(couponUser.getUserId());
        if (useStatus) {
            queryCouponUser.setMapperId(useUserCouponVo.getCouponCode());
            queryCouponUser.setUsedFlag(0);
        } else {
            queryCouponUser.setUsedFlag(1);
            queryCouponUser.setOrderNo(useUserCouponVo.getOrderNo());
        }
        EntityWrapper<CouponUser> entityWrapper = new EntityWrapper<>(queryCouponUser);
        return update(updateCouponUser, entityWrapper);
    }

    @Override
    public boolean hasReceive(UserCouponVo userCouponVo) {
        JwtUserDetails userDetails = getUserDetails();
        Long userId = Objects.isNull(userCouponVo.getUserId()) ? userDetails.getUserId() : userCouponVo.getUserId();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        return hasReceive(userId, userCouponVo.getCouponId());
    }

    @Override
    public List<UserCouponReceiveDto> batchHasReceive(BatchUserCouponVo batchUserCouponVo) {
        JwtUserDetails userDetails = getUserDetails();
        Long userId = Objects.isNull(batchUserCouponVo.getUserId()) ? userDetails.getUserId() : batchUserCouponVo.getUserId();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        CouponUser queryCouponUser = new CouponUser();
        List<CouponUser> couponUsers = Lists.newArrayList();
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userDetails.getUserId());
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper(queryCouponUser);
            entityWrapper.in("USER_ID", v);
            entityWrapper.in("COUPON_ID", batchUserCouponVo.getCouponIds());
            if (StringUtils.isNotBlank(batchUserCouponVo.getStartTime()) && StringUtils.isNotBlank(batchUserCouponVo.getEndTime())) {
                Date startTime = com.mmj.common.utils.DateUtils.parse(batchUserCouponVo.getStartTime());
                Date endTime = com.mmj.common.utils.DateUtils.parse(batchUserCouponVo.getEndTime());
                entityWrapper.between("CHECK_TIME", startTime, endTime);
            }
            couponUsers.addAll(selectList(entityWrapper));
        });
        List<UserCouponReceiveDto> userCouponReceiveDtos = Lists.newArrayListWithCapacity(batchUserCouponVo.getCouponIds().size());
        batchUserCouponVo.getCouponIds().forEach(couponId -> {
            long count = couponUsers.stream().filter(couponUser -> couponId.equals(couponUser.getCouponId())).count();
            userCouponReceiveDtos.add(new UserCouponReceiveDto(couponId, count > 0));
        });
        return userCouponReceiveDtos;
    }

    /**
     * 判断用户是否有领取该优惠券
     *
     * @param userId
     * @param couponId
     * @return true已领取 false未领取
     */
    public boolean hasReceive(Long userId, Integer couponId) {
        CouponUser couponUser = new CouponUser();
        couponUser.setCouponId(couponId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userId);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper(couponUser);
            entityWrapper.in("USER_ID", v);
            if (atomicInteger.addAndGet(selectCount(entityWrapper)) > 0)
                return;
        });
        return atomicInteger.get() > 0;
    }

    /**
     * 判断当天是否已领取
     *
     * @param userId
     * @param couponId
     * @return
     */
    public boolean todayHasReceive(Long userId, Integer couponId) {
        CouponUser couponUser = new CouponUser();
        couponUser.setCouponId(couponId);
        String today = todayYYYY_MM_DD();
        Date startDate = com.mmj.common.utils.DateUtils.parse(today + " 00:00:00");
        Date endDate = com.mmj.common.utils.DateUtils.parse(today + " 23:59:59");
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userId);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper(couponUser);
            entityWrapper.between("CHECK_TIME", startDate, endDate);
            entityWrapper.in("USER_ID", v);
            if (atomicInteger.addAndGet(selectCount(entityWrapper)) > 0)
                return;
        });
        return atomicInteger.get() > 0;
    }

    /**
     * 通过优惠券来源获取今天领取数量
     *
     * @param userId
     * @param couponSource
     * @return
     */
    public int getTodayReceiveNum(Long userId, String couponSource) {
        CouponUser couponUser = new CouponUser();
        couponUser.setCouponSource(couponSource);
        String today = todayYYYY_MM_DD();
        Date startDate = com.mmj.common.utils.DateUtils.parse(today + " 00:00:00");
        Date endDate = com.mmj.common.utils.DateUtils.parse(today + " 23:59:59");
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userId);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper(couponUser);
            entityWrapper.between("CHECK_TIME", startDate, endDate);
            entityWrapper.in("USER_ID", v);
            atomicInteger.addAndGet(selectCount(entityWrapper));
        });
        return atomicInteger.get();
    }

    /**
     * 获取当日优惠券发放数量
     *
     * @param couponId
     * @return
     */
    public Integer getTodayReceiveNum(Integer couponId) {
        ReturnData<CouponNumDto> returnData = activeFeignClient.todayNum(couponId);
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && Objects.nonNull(returnData), "获取当天优惠券发放数量失败");
        return returnData.getData().getNum();
    }

    @Override
    public List<UserCouponDto> myOrderCouponList(OrderCouponVo orderCouponVo) {
        Long userId = Objects.isNull(orderCouponVo.getUserId()) ? getUserDetails().getUserId() : orderCouponVo.getUserId();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        CouponUser queryCouponUser = new CouponUser();
        queryCouponUser.setOrderNo(orderCouponVo.getOrderNo());
        boolean useStatus = Objects.isNull(orderCouponVo.getUseStatus()) ? true : orderCouponVo.getUseStatus();
        queryCouponUser.setUsedFlag(useStatus ? 1 : 0);
        List<CouponUser> couponUsers = Lists.newArrayList();
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userId);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper(queryCouponUser);
            entityWrapper.in("USER_ID", v);
            couponUsers.addAll(selectList(entityWrapper));
        });
        if (couponUsers.size() == 0)
            return Lists.newArrayListWithCapacity(1);
        return toUserCouponDto(couponUsers);
    }

    @Override
    public List<UserCouponDto> myCouponList() {
        JwtUserDetails userDetails = getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        CouponUser queryCouponUser = new CouponUser();
//        queryCouponUser.setUsedFlag(0);
        List<CouponUser> couponUsers = Lists.newArrayList();
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userDetails.getUserId());
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper<>(queryCouponUser);
            entityWrapper.in("USER_ID", v);
            entityWrapper.orderBy("MAPPER_ID", false);
            couponUsers.addAll(selectList(entityWrapper));
        });
        if (couponUsers.size() == 0)
            return Lists.newArrayListWithCapacity(1);
        return toUserCouponDto(couponUsers);
    }

    @Override
    public UserCouponDto myCouponInfo(Integer couponCode) {
        JwtUserDetails userDetails = getUserDetails();
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userDetails.getUserId());
        CouponUser couponUser = new CouponUser();
        mergeUserListMap.forEach((k, v) -> {
            if (Objects.nonNull(couponUser.getMapperId()))
                return;
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            CouponUser couponUserTemp = getUserCoupon(v, couponCode, null);
            if (Objects.nonNull(couponUserTemp)) {
                BeanUtils.copyProperties(couponUserTemp, couponUser);
                return;
            }
        });
        if (Objects.isNull(couponUser.getMapperId()))
            return null;
        ReturnData<CouponInfoDto> returnData = activeFeignClient.getCouponInfo(couponUser.getCouponId());
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && Objects.nonNull(returnData.getData()), "获取优惠券信息失败");
        CouponInfoDto couponInfoDto = returnData.getData();
        UserCouponDto userCouponDto = toUserCouponDto(couponUser, couponInfoDto);
        return userCouponDto;
    }

    private CouponUser getUserCoupon(List<Long> userId, Integer couponCode, String orderNo) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId.get(0));
        CouponUser queryCouponUser = new CouponUser();
        queryCouponUser.setMapperId(couponCode);
        EntityWrapper<CouponUser> entityWrapper = new EntityWrapper<>(queryCouponUser);
        entityWrapper.in("USER_ID", userId);
        if (Objects.isNull(couponCode) && Objects.nonNull(orderNo)) {
            entityWrapper.eq("ORDER_NO", orderNo);
        }
        CouponUser couponUser = selectOne(entityWrapper);
        return couponUser;
    }

    @Override
    public List<UserCouponDto> myCouponInfoList(List<Integer> couponCodes) {
        JwtUserDetails userDetails = getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        CouponUser queryCouponUser = new CouponUser();
        queryCouponUser.setUsedFlag(0);
        List<CouponUser> couponUsers = Lists.newArrayList();
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userDetails.getUserId());
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper<>(queryCouponUser);
            entityWrapper.in("MAPPER_ID", couponCodes);
            entityWrapper.in("USER_ID", v);
            entityWrapper.orderBy("MAPPER_ID", false);
            couponUsers.addAll(selectList(entityWrapper));
        });
        if (couponUsers.size() == 0)
            return Lists.newArrayListWithCapacity(1);
        return toUserCouponDto(couponUsers);
    }

    @Override
    public List<UserCouponDto> myCouponInfoByCouponId(Integer couponId) {
        JwtUserDetails userDetails = getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        CouponUser queryCouponUser = new CouponUser();
        queryCouponUser.setCouponId(couponId);
        List<CouponUser> couponUsers = Lists.newArrayList();
        Map<Long, List<Long>> mergeUserListMap = getMergeUserListMap(userDetails.getUserId());
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<CouponUser> entityWrapper = new EntityWrapper<>(queryCouponUser);
            entityWrapper.in("USER_ID", v);
            entityWrapper.orderBy("MAPPER_ID", false);
            couponUsers.addAll(selectList(entityWrapper));
        });
        if (couponUsers.size() == 0)
            return Lists.newArrayListWithCapacity(1);
        return toUserCouponDto(couponUsers);
    }

    @Override
    public MemberCouponDto memberCouponInfoList() {
        ReturnData<List<CouponInfoDto>> returnData = activeFeignClient.getActiveCouponInfoList("MEMBER_DAY");
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && Objects.nonNull(returnData.getData()) && returnData.getData().size() > 0, "获取活动优惠券信息失败");
        List<CouponInfoDto> couponInfoDtoList = returnData.getData();
        BatchUserCouponVo batchUserCouponVo = new BatchUserCouponVo();
        batchUserCouponVo.setCouponSource("MEMBER_DAY");
        List<Integer> couponIds = Lists.newArrayListWithCapacity(couponInfoDtoList.size());
        couponInfoDtoList.forEach(couponInfoDto -> couponIds.add(couponInfoDto.getCouponId()));
        batchUserCouponVo.setCouponIds(couponIds);
        boolean memberDay = isMemberDay();
        if (memberDay) {
            Calendar calendar = Calendar.getInstance();
            String toDay = String.format("%d-%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            batchUserCouponVo.setStartTime(toDay + " 00:00:00");
            batchUserCouponVo.setEndTime(toDay + " 23:59:59");
        }
        List<UserCouponReceiveDto> userCouponReceiveDtos = batchHasReceive(batchUserCouponVo);
        MemberCouponDto memberCouponDto = new MemberCouponDto();
        memberCouponDto.setMemberDay(memberDay);
        if (!memberDay) { // 如果不是会员日，则计算距离下个会员日的毫秒数，前端需根据此值转换成还有XX天XX小时XX分XX秒
            memberCouponDto.setNextMemberDayIntervalMilliseconds(getNextMemberDayIntervalMilliseconds());
        }
        memberCouponDto.setTotalCouponMoney(BigDecimal.valueOf(0));
        List<MemberCouponDto.CouponStat> couponInfoList = Lists.newArrayListWithCapacity(couponInfoDtoList.size());
        couponInfoDtoList.forEach(couponInfo -> {
            if ("2".equals(couponInfo.getWhereType())) {
                memberCouponDto.setTotalCouponMoney(new BigDecimal(PriceConversion.intToString(couponInfo.getCouponValue())).add(memberCouponDto.getTotalCouponMoney()));
            }
            MemberCouponDto.CouponStat couponInfoData = new MemberCouponDto.CouponStat();
            couponInfoData.setCouponInfo(toCouponInfoDataDto(couponInfo));
            UserCouponReceiveDto userCouponReceiveDto = userCouponReceiveDtos.stream().filter(receiveDto -> couponInfo.getCouponId().equals(receiveDto.getCouponId())).findFirst().orElse(null);
            couponInfoData.setHasCollected(Objects.nonNull(userCouponReceiveDto) ? userCouponReceiveDto.getReceiveStatus() : false);
            //会员日优惠券领取量百分比计算
            int totalCount = Objects.isNull(couponInfo.getCountNum()) ? 0 : couponInfo.getCountNum();
            int sentCount = couponInfo.getToDaySendNumber(); // 已发送数量
            int result = 100;
            if (totalCount > 0 && totalCount > sentCount) {
                double percent = DoubleUtil.divide((double) sentCount, (double) totalCount);
                result = (int) Math.ceil(DoubleUtil.mul(percent, 100d));
            }
            couponInfoData.setPercent(result + "%");
            if (result >= 100) {
                couponInfoData.setPercentStr("已领完");
            } else {
                couponInfoData.setPercentStr("已领取" + result + "%");
            }
            couponInfoList.add(couponInfoData);
        });
        memberCouponDto.setCouponInfoList(couponInfoList.stream().sorted(new Comparator<MemberCouponDto.CouponStat>() {
            @Override
            public int compare(MemberCouponDto.CouponStat o1, MemberCouponDto.CouponStat o2) {
                Integer o1v = PriceConversion.stringToInt(o1.getCouponInfo().getCouponValue());
                Integer o2v = PriceConversion.stringToInt(o2.getCouponInfo().getCouponValue());
                return o2v.compareTo(o1v);
            }
        }).collect(Collectors.toList()));
        return memberCouponDto;
    }

    @Override
    public List<GoodsCouponDto> goodsCouponInfoList(GoodsCouponVo goodsCouponVo) {
        JwtUserDetails jwtUserDetails = getUserDetails();
        ReturnData<List<CouponInfoDto>> returnData = activeFeignClient.getGoodCouponList(goodsCouponVo.getGoodClass(), goodsCouponVo.getGoodId());
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && Objects.nonNull(returnData.getData()), returnData.getDesc());
        List<CouponInfoDto> couponInfoDtoList = returnData.getData();
        List<Integer> couponIds = Lists.newArrayListWithCapacity(couponInfoDtoList.size());
        BatchUserCouponVo batchUserCouponVo = new BatchUserCouponVo();
        couponInfoDtoList.forEach(couponInfoDto -> couponIds.add(couponInfoDto.getCouponId()));
        batchUserCouponVo.setCouponIds(couponIds);
        List<UserCouponReceiveDto> userCouponReceiveDtos = batchHasReceive(batchUserCouponVo);
        List<GoodsCouponDto> goodsCouponDtos = Lists.newArrayListWithCapacity(couponInfoDtoList.size());
        long currentTime = System.currentTimeMillis();
        couponInfoDtoList.forEach(couponInfo -> {
            boolean active = true;
            if (CouponSource.MEMBER_DAY.equalsIgnoreCase(couponInfo.getActiveFlag())) {
                active = isMemberDay() && isMemberUser(jwtUserDetails.getUserId());
            }
            String indateType = couponInfo.getIndateType();
            if ("1".equals(indateType)) { //1 时间区间
                active = couponInfo.getCouponEnd().getTime() > currentTime;
            }
            if (active) {
                GoodsCouponDto goodsCouponDto = new GoodsCouponDto();
                goodsCouponDto.setCouponInfo(toCouponInfoDataDto(couponInfo));
                UserCouponReceiveDto userCouponReceiveDto = userCouponReceiveDtos.stream().filter(receiveDto -> couponInfo.getCouponId().equals(receiveDto.getCouponId())).findFirst().orElse(null);
                goodsCouponDto.setHasCollected(Objects.nonNull(userCouponReceiveDto) ? userCouponReceiveDto.getReceiveStatus() : false);
                goodsCouponDtos.add(goodsCouponDto);
            }
        });
        return goodsCouponDtos.stream().sorted(new Comparator<GoodsCouponDto>() {
            @Override
            public int compare(GoodsCouponDto o1, GoodsCouponDto o2) {
                Integer o1v = PriceConversion.stringToInt(o1.getCouponInfo().getCouponValue());
                Integer o2v = PriceConversion.stringToInt(o2.getCouponInfo().getCouponValue());
                return o2v.compareTo(o1v);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 转换为接口对外实体对象
     *
     * @param couponUsers
     * @return
     */
    private List<UserCouponDto> toUserCouponDto(List<CouponUser> couponUsers) {
        if (couponUsers.size() == 0)
            return Lists.newArrayListWithCapacity(1);
        List<Integer> couponIds = Lists.newArrayListWithCapacity(couponUsers.size());
        couponUsers.forEach(couponUser -> {
            if (!couponIds.contains(couponUser.getCouponId())) {
                couponIds.add(couponUser.getCouponId());
            }
        });
        List<UserCouponDto> userCouponDtos = Lists.newArrayListWithCapacity(couponUsers.size());
        ReturnData<List<CouponInfoDto>> returnData = activeFeignClient.batchCouponInfos(couponIds);
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && Objects.nonNull(returnData.getData()) && returnData.getData().size() > 0, returnData.getDesc());
        List<CouponInfoDto> couponInfoDtos = returnData.getData();
        couponUsers.stream().forEach(couponUser -> {
            CouponInfoDto couponInfoDto = couponInfoDtos.stream().filter(cid -> couponUser.getCouponId().equals(cid.getCouponId())).findFirst().orElse(null);
            if (Objects.nonNull(couponInfoDto)) {
                UserCouponDto userCouponDto = toUserCouponDto(couponUser, couponInfoDto);
                userCouponDtos.add(userCouponDto);
            }
        });
        return userCouponDtos.stream().sorted(new Comparator<UserCouponDto>() {
            @Override
            public int compare(UserCouponDto o1, UserCouponDto o2) {
//                if (!o1.getValidStatus().equals(o2.getValidStatus())) {
//                    return o1.getValidStatus().compareTo(o2.getValidStatus());
//                }
                Integer o1v = PriceConversion.stringToInt(o1.getCouponInfo().getCouponValue());
                Integer o2v = PriceConversion.stringToInt(o2.getCouponInfo().getCouponValue());
                return o1v > o2v ? -1 : (o1v < o2v ? 1 : (o1.getEndTime().compareTo(o2.getEndTime())));
            }
        }).sorted(new Comparator<UserCouponDto>() {
            @Override
            public int compare(UserCouponDto o1, UserCouponDto o2) {
                return o1.getValidStatus().compareTo(o2.getValidStatus());
            }
        }).collect(Collectors.toList());
    }

    /**
     * 优惠券模板转换为接口对外实体对象
     *
     * @return
     */
    private CouponInfoDataDto toCouponInfoDataDto(CouponInfoDto couponInfoDto) {
        CouponInfoDataDto couponInfoData = new CouponInfoDataDto();
        BeanUtils.copyProperties(couponInfoDto, couponInfoData);
        //条件类型 1 无限制 ; 2 满X元; 3 满X件
        if ("3".equals(couponInfoDto.getWhereType())) {
            couponInfoData.setWhereValue(couponInfoDto.getWhereValue().toString());
        } else {
            couponInfoData.setWhereValue(PriceConversion.intToString(couponInfoDto.getWhereValue()));
        }
        //优惠类型  1 减X元; 2 打X拆
        if ("1".equals(couponInfoDto.getCouponAmount())) {
            couponInfoData.setCouponValue(PriceConversion.intToString(couponInfoDto.getCouponValue()));
        } else {
            couponInfoData.setCouponValue(couponInfoDto.getCouponValue().toString());
        }
        return couponInfoData;
    }

    /**
     * 转换为接口对外实体对象
     *
     * @param couponUser
     * @param couponInfoDto
     * @return
     */
    private UserCouponDto toUserCouponDto(CouponUser couponUser, CouponInfoDto couponInfoDto) {
        UserCouponDto userCouponDto = new UserCouponDto();
        BeanUtils.copyProperties(couponUser, userCouponDto);
        userCouponDto.setCouponCode(couponUser.getMapperId());
        userCouponDto.setCouponInfo(toCouponInfoDataDto(couponInfoDto));
        Integer validStatus;
        if (1 == couponUser.getUsedFlag()) {
            validStatus = 1;
        } else {
            long currentTime = System.currentTimeMillis();
            validStatus = couponUser.getEndTime().getTime() <= currentTime
                    ? 1 : (couponUser.getStartTime().getTime() > currentTime ? 2 : 0);
        }
        userCouponDto.setValidStatus(validStatus);
        userCouponDto.setStartTime(couponUser.getStartTime());
        userCouponDto.setEndTime(couponUser.getEndTime());
        return userCouponDto;
    }

    /**
     * 获取会员日
     *
     * @return
     */
    private int getMmjMemberDay() {
        ReturnData<BaseDict> returnData = noticeFeignClient.queryGlobalConfigByDictCode("mmj.member.day");
        BaseDict baseDict = returnData.getData();
        if (Objects.isNull(baseDict) || StringUtils.isEmpty(baseDict.getDictValue())) {
            return 4;
        }
        return Integer.parseInt(baseDict.getDictValue());
    }

    /**
     * 是否会员日
     *
     * @return
     */
    public boolean isMemberDay() {
        int memberDay = getMmjMemberDay();
        Calendar calendar = Calendar.getInstance();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == memberDay;
    }

    /**
     * 获取距离下一个会员日时间戳
     *
     * @return
     */
    public long getNextMemberDayIntervalMilliseconds() {
        Calendar calendar = Calendar.getInstance();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int memberDay = getMmjMemberDay();
        int addDay;
        if (weekDay < memberDay) {
            addDay = memberDay - weekDay;
        } else {
            addDay = 7 - weekDay + getMmjMemberDay();
        }
        Date now = new Date();
        Date date = DateUtils.addDays(now, addDay);
        String nextMemberDayStr = com.mmj.common.utils.DateUtils.SDF10.format(date) + " 00:00:00";
        Date nextMemberDay = com.mmj.common.utils.DateUtils.parse(nextMemberDayStr);
        return nextMemberDay.getTime() - now.getTime();
    }

    /**
     * 获取今天年月日
     *
     * @return
     */
    public String todayYYYY_MM_DD() {
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        int month = localDate.getMonth().getValue();
        int day = localDate.getDayOfMonth();
        return String.format("%s-%s-%s", year, month < 10 ? "0" + month : month, day < 10 ? "0" + day : day);
    }
}
