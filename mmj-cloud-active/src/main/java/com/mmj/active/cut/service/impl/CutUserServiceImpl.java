package com.mmj.active.cut.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.mmj.active.common.MQProducer;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.constants.MMKingShareType;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.UserMember;
import com.mmj.active.common.model.dto.CutGoodDto;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.cut.model.CutInfo;
import com.mmj.active.cut.model.CutReward;
import com.mmj.active.cut.model.CutSponsor;
import com.mmj.active.cut.model.CutUser;
import com.mmj.active.cut.model.cache.MyCutCache;
import com.mmj.active.cut.model.dto.*;
import com.mmj.active.cut.model.vo.CutAssistVo;
import com.mmj.active.cut.model.vo.CutDetailsVo;
import com.mmj.active.cut.model.vo.CutUserVo;
import com.mmj.active.cut.service.*;
import com.mmj.active.cut.utils.CutFlag;
import com.mmj.active.cut.utils.CutType;
import com.mmj.active.cut.utils.PriceCalculationUtils;
import com.mmj.common.constants.OrderClassify;
import com.mmj.common.constants.OrderSource;
import com.mmj.common.constants.OrderStatus;
import com.mmj.common.constants.OrderType;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.ActiveGoodStoreResult;
import com.mmj.common.model.order.OrderProduceDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.OrderUtils;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户砍价表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@Slf4j
@Service
public class CutUserServiceImpl implements CutUserService {
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    /**
     * 砍价服务
     */
    @Autowired
    private CutInfoService cutInfoService;

    /**
     * 砍价任务服务
     */
    @Autowired
    private CutTaskService cutTaskService;

    /**
     * 砍价奖励服务
     */
    @Autowired
    private CutRewardService cutRewardService;

    /**
     * 发起砍价服务
     */
    @Autowired
    private CutSponsorService cutSponsorService;

    /**
     * 砍价商品服务
     */
    @Autowired
    private ActiveGoodService activeGoodService;

    /**
     * 用户模块服务
     */
    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private MQProducer mqProducer;

    @Autowired
    private CutBlackListService cutBlackListService;

    @Autowired
    private CutMessageSendService cutMessageSendService;

    private final static String CUT_BARGAIN_NO = "CUT:BARGAIN:";

    private final static String CUT_LIST_KEY = "CUT:LIST:";

    /**
     * 获取用户砍价金额的key
     *
     * @return
     */
    public String getUserCutRateAmountKey(String cutNo) {
        return CUT_BARGAIN_NO + "AMOUNT:" + cutNo;
    }

    /**
     * 获取帮砍人和发起砍价人的关系key
     *
     * @param cutNo 砍价号
     * @return
     */
    public String getCutSponsorAndAssistRelateKey(String cutNo) {
//        return CUT_BARGAIN_NO + "RELATE:" + cutNo + ":" + sponsorUserId + ":" + assistUserId;
        return CUT_BARGAIN_NO + "RELATE:" + cutNo;
    }

    @Override
    public List<CutFreeListDto> cutFreeList() {
        List<CutList> cutLists = getCutFreeList();
        List<CutFreeListDto> cutFreeListDtos = Lists.newArrayListWithCapacity(cutLists.size());
        cutLists.stream().forEach(cutList -> {
            BaseUser user = userFeignClient.getUserById(Long.parseLong(cutList.getUserId()));
            if (Objects.nonNull(user))
                cutFreeListDtos.add(new CutFreeListDto(user.getUserFullName(), user.getImagesUrl(), cutList.getScore().intValue()));
        });
        return cutFreeListDtos;
    }

    @Override
    public List<AssistCutListDto> assistCutList() {
        List<CutList> cutLists = getAssistCutList();
        List<AssistCutListDto> cutFreeListDtos = Lists.newArrayListWithCapacity(cutLists.size());
        cutLists.stream().forEach(cutList -> {
            BaseUser user = userFeignClient.getUserById(Long.parseLong(cutList.getUserId()));
            if (Objects.nonNull(user))
                cutFreeListDtos.add(new AssistCutListDto(user.getUserFullName(), user.getImagesUrl(), cutList.getScore().toString()));
        });
        return cutFreeListDtos;
    }

    @Override
    public List<PeopleCutListDto> peopleCutList() {
        List<CutList> cutLists = getPeopleCutList();
        List<PeopleCutListDto> cutFreeListDtos = Lists.newArrayListWithCapacity(cutLists.size());
        cutLists.stream().forEach(cutList -> {
            BaseUser user = userFeignClient.getUserById(Long.parseLong(cutList.getUserId()));
            if (Objects.nonNull(user))
                cutFreeListDtos.add(new PeopleCutListDto(user.getUserFullName(), user.getImagesUrl(), cutList.getScore().intValue()));
        });
        return cutFreeListDtos;
    }

    /**
     * 免费拿榜单(统计)
     *
     * @param sponsorUserId 发起人
     */
    @Override
    public void addCutFreeList(long sponsorUserId) {
        Double a = redisTemplate.opsForZSet().incrementScore(CUT_LIST_KEY + "FREE", sponsorUserId + "", 1);
        log.info("=> 免费拿榜单 userId:{},砍价成功数:{}", sponsorUserId, a);
    }

    /**
     * 砍价榜单（统计）
     */
    public Double assistCutList(long assistUserId, BigDecimal cutAmount) {
        return redisTemplate.opsForZSet().incrementScore(CUT_LIST_KEY + "ASSIST", assistUserId + "", cutAmount.doubleValue());
    }

    /**
     * 人脉榜单（统计）
     */
    public Double peopleCutList(long sponsorUserId) {
        return redisTemplate.opsForZSet().incrementScore(CUT_LIST_KEY + "PEOPLE", sponsorUserId + "", 1);
    }

    /**
     * 砍价榜单|人脉榜单（统计）
     *
     * @param sponsorUserId 发起人
     * @param assistUserId  帮砍人
     * @param cutAmount     帮砍金额
     */
    public void addCutList(long sponsorUserId, long assistUserId, BigDecimal cutAmount) {
        log.info("=> 帮砍时榜单统计 发起砍价人:{},帮砍人:{},帮砍金额:{}", sponsorUserId, assistUserId, cutAmount);
        //砍价榜单
        Double b = assistCutList(assistUserId, cutAmount);
        log.info("=> 砍价榜单 assistUserId:{},cutAmount:{}", assistUserId, b);
        //人脉榜单
        Double c = peopleCutList(sponsorUserId);
        log.info("=> 人脉榜单 sponsorUserId:{},assistUserId:{},cutAmount:{}", sponsorUserId, assistUserId, c);
    }

    /**
     * 免费拿榜单
     *
     * @return
     */
    public List<CutList> getCutFreeList() {
        Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().reverseRangeWithScores(CUT_LIST_KEY + "FREE", 0, 10);
        return parseCutList(set);
    }

    /**
     * 砍价榜单
     *
     * @return
     */
    public List<CutList> getAssistCutList() {
        Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().reverseRangeWithScores(CUT_LIST_KEY + "ASSIST", 0, 10);
        return parseCutList(set);
    }

    /**
     * 人脉榜单
     *
     * @return
     */
    public List<CutList> getPeopleCutList() {
        Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().reverseRangeWithScores(CUT_LIST_KEY + "PEOPLE", 0, 10);
        return parseCutList(set);
    }

    /**
     * 解析排行榜数据
     *
     * @param set
     * @return
     */
    public List<CutList> parseCutList(Set<ZSetOperations.TypedTuple<String>> set) {
        List<CutList> cutLists = Lists.newArrayListWithCapacity(set.size());
        set.stream().forEach(s ->
                cutLists.add(new CutList(s.getValue(), BigDecimal.valueOf(s.getScore()).setScale(2, RoundingMode.HALF_UP).doubleValue()))
        );
        return cutLists;
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
    public CutOrderDto checkOrder(Integer cutId, String cutNo) {
        JwtUserDetails userDetails = getUserDetails();
        CutSponsor cutSponsor = cutSponsorService.getCutSponsorByCutNo(cutNo);
        Assert.notNull(cutSponsor, "砍价信息不存在");
        String orderNo = cutSponsor.getOrderNo();
        Assert.isTrue(StringUtils.isEmpty(orderNo), "砍价已下单，请处理");
        BigDecimal basePrice = cutSponsor.getBasePrice();
        CutUser cutUser = new CutUser();
        cutUser.setCutId(cutId);
        cutUser.setCutNo(cutNo);
        cutUser.setUserId(userDetails.getUserId());
        ReturnData<List<CutUser>> returnData = userFeignClient.cutUsers(cutUser);
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData().size() > 0, "获取砍价记录失败，请稍后重试");
        cutUser = returnData.getData().get(0);
        return new CutOrderDto(cutUser.getSurplusAmount().add(basePrice), cutSponsor.getGoodAmount());
    }

    @Override
    public BigDecimal getSurplusAmount(Integer cutId, String cutNo) {
        JwtUserDetails userDetails = getUserDetails();
        CutSponsor cutSponsor = cutSponsorService.getCutSponsorByCutNo(cutNo);
        Assert.notNull(cutSponsor, "砍价信息不存在");
        String orderNo = cutSponsor.getOrderNo();
        Assert.isTrue(StringUtils.isEmpty(orderNo), "砍价已下单，请处理");
        BigDecimal basePrice = cutSponsor.getBasePrice();
        CutUser cutUser = new CutUser();
        cutUser.setCutId(cutId);
        cutUser.setCutNo(cutNo);
        cutUser.setUserId(userDetails.getUserId());
        ReturnData<List<CutUser>> returnData = userFeignClient.cutUsers(cutUser);
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData().size() > 0, "获取砍价记录失败，请稍后重试");
        cutUser = returnData.getData().get(0);
        return cutUser.getSurplusAmount().add(basePrice);
    }

    @Override
    public List<MyCutListDto> myCutList() {
        JwtUserDetails userDetails = getUserDetails();
        CutUser queryCutUser = new CutUser();
        queryCutUser.setUserId(userDetails.getUserId());
        ReturnData<List<CutUser>> returnData = userFeignClient.myCutList(queryCutUser);
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, returnData.getDesc());
        List<CutUser> cutUsers = returnData.getData();
        List<Integer> sponsorIds = Lists.newArrayListWithCapacity(cutUsers.size());
        cutUsers.forEach(cutUser -> {
            if (!sponsorIds.contains(cutUser.getSponsorId())) {
                sponsorIds.add(cutUser.getSponsorId());
            }
        });
        List<CutSponsor> cutSponsors = cutSponsorService.batchGetCutSponsor(sponsorIds);

        List<MyCutListDto> myCutListDtos = Lists.newArrayListWithCapacity(cutUsers.size());
        cutUsers.stream().forEach(cu -> {
            CutSponsor cutSponsor = cutSponsors.stream().filter(sponsor -> sponsor.getSponsorId().equals(cu.getSponsorId())).findFirst().orElse(null);
            Assert.notNull(cutSponsor, "砍价不存在:" + cu.getSponsorId());
            MyCutListDto myCutListDto = new MyCutListDto();
            BeanUtils.copyProperties(cutSponsor, myCutListDto);
            myCutListDto.setActivePrice(cutSponsor.getGoodAmount());
            myCutListDto.setBaseUnitPrice(cutSponsor.getBasePrice());
            myCutListDto.setReadyBargainPrice(cu.getSurplusAmount());
            myCutListDto.setCutOrderNo(cutSponsor.getOrderNo());
            myCutListDto.setCutOrderStatus(cutSponsor.getOrderStatus());
            myCutListDto.setCurrentPrice(PriceCalculationUtils.add(cu.getSurplusAmount(), cutSponsor.getBasePrice()));
            //算出可以砍的总金额
            BigDecimal totalAmount = PriceCalculationUtils.subtract(myCutListDto.getActivePrice(), myCutListDto.getBaseUnitPrice());
            myCutListDto.setTotalBargainPrice(PriceCalculationUtils.subtract(totalAmount, myCutListDto.getReadyBargainPrice()));
            Integer cutNumberInt = getAssistCutNumber(cu.getCutNo(), userDetails.getUserId());
            myCutListDto.setCurrentUserCutNumber(cutNumberInt);
            myCutListDto.setCurrentUserCutFlag(myCutListDto.getCurrentUserCutNumber() < 2);
            if (myCutListDto.getCutFlag() == CutFlag.ONGOING) {
                Long expireTime = redisTemplate.getExpire(getUserCutRateAmountKey(cu.getCutNo()), TimeUnit.MILLISECONDS);
                if (Objects.isNull(expireTime) || expireTime <= 0) {
                    myCutListDto.setCutFlag(CutFlag.TIMEOUT);
                } else {
                    myCutListDto.setExpiredTime(expireTime);
                }
            }
            myCutListDtos.add(myCutListDto);
        });
        return myCutListDtos;
    }

    @Override
    public CutDetailsDto details(CutDetailsVo cutDetailsVo) {
        JwtUserDetails userDetails = getUserDetails();
        CutSponsor queryCutSponsor = new CutSponsor();
        queryCutSponsor.setCutNo(cutDetailsVo.getCutNo());
        EntityWrapper<CutSponsor> cutSponsorEntityWrapper = new EntityWrapper<>(queryCutSponsor);
        CutSponsor cutSponsor = cutSponsorService.selectOne(cutSponsorEntityWrapper);
        Assert.notNull(cutSponsor, "砍价不存在");
        CutUser cutUser = new CutUser();
        cutUser.setCutNo(cutDetailsVo.getCutNo());
        cutUser.setUserId(cutSponsor.getUserId());
        ReturnData<List<CutUser>> returnData = userFeignClient.cutUsers(cutUser);
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, returnData.getDesc());
        Assert.isTrue(returnData.getData().size() > 0, "砍价记录不存在");
        //获取最新的砍价信息
        cutUser = returnData.getData().get(0);
        CutDetailsDto cutDetailsDto = new CutDetailsDto();
        BeanUtils.copyProperties(cutSponsor, cutDetailsDto);
        BeanUtils.copyProperties(cutUser, cutDetailsDto);
        cutDetailsDto.setCutOrderNo(cutSponsor.getOrderNo());
        cutDetailsDto.setCutOrderStatus(cutSponsor.getOrderStatus());
        cutDetailsDto.setActivePrice(cutSponsor.getGoodAmount());
        cutDetailsDto.setBaseUnitPrice(cutSponsor.getBasePrice());
        cutDetailsDto.setReadyBargainPrice(cutUser.getSurplusAmount());
        cutDetailsDto.setCurrentPrice(PriceCalculationUtils.add(cutUser.getSurplusAmount(), cutSponsor.getBasePrice()));
        //算出可以砍的总金额
        BigDecimal totalAmount = PriceCalculationUtils.subtract(cutDetailsDto.getActivePrice(), cutDetailsDto.getBaseUnitPrice());
        cutDetailsDto.setTotalBargainPrice(PriceCalculationUtils.subtract(totalAmount, cutDetailsDto.getReadyBargainPrice()));
        cutDetailsDto.setProgress(PriceCalculationUtils.divide(cutDetailsDto.getTotalBargainPrice(), totalAmount));
        List<AssistBargainLogDto> assistBargainLogDtos = Lists.newArrayListWithCapacity(returnData.getData().size());
        returnData.getData().forEach(cu -> {
            AssistBargainLogDto assistBargainLogDto = new AssistBargainLogDto();
            assistBargainLogDto.setRewardAmount(cu.getRewardAmount());
            assistBargainLogDto.setCutAmount(cu.getCutAmount());
            assistBargainLogDto.setCutTime(cu.getCutTime());
            BaseUser user = userFeignClient.getUserById(cu.getCutMember());
            assistBargainLogDto.setUserId(user.getUserId());
            assistBargainLogDto.setNickname(user.getUserFullName());
            assistBargainLogDto.setPicUrl(user.getImagesUrl());
            assistBargainLogDto.setAssistPeople(userDetails.getUserId().longValue() == cu.getCutMember().longValue());
            assistBargainLogDtos.add(assistBargainLogDto);
        });
        cutDetailsDto.setAssistBargainLog(assistBargainLogDtos);
        cutDetailsDto.setCutFlag(cutUser.getCutFlag());
        if (cutDetailsDto.getCutFlag() == CutFlag.ONGOING) {
            Long expireTime = redisTemplate.getExpire(getUserCutRateAmountKey(cutUser.getCutNo()), TimeUnit.MILLISECONDS);
            if (Objects.isNull(expireTime) || expireTime <= 0) {
                cutDetailsDto.setCutFlag(CutFlag.TIMEOUT);
            } else {
                cutDetailsDto.setExpiredTime(expireTime);
            }
        }
        cutDetailsDto.setBargainPeople(userDetails.getUserId().longValue() == cutSponsor.getUserId().longValue());//当前用户是否为砍价发起人
        Integer cutNumberInt = getAssistCutNumber(cutUser.getCutNo(), userDetails.getUserId());
        cutDetailsDto.setCurrentUserCutNumber(cutNumberInt);
        cutDetailsDto.setCurrentUserCutFlag(cutDetailsDto.getBargainPeople() ? cutDetailsDto.getCurrentUserCutNumber() < 2 : cutDetailsDto.getCurrentUserCutNumber() < 1);
        return cutDetailsDto;
    }

    /**
     * 生成砍价号
     *
     * @param userId
     * @return
     */
    private String generateCutNo(Long userId) {
        String moldStr = String.valueOf(userId);
        int length = moldStr.length();
        String mold = moldStr.substring(length - 2, length);
        return String.valueOf(snowflakeIdWorker.nextId()) + mold;
    }

    @Override
    public CutAddressDto address(CutDetailsVo cutDetailsVo) {
        CutSponsor cutSponsor = cutSponsorService.getCutSponsorByCutNo(cutDetailsVo.getCutNo());
        Assert.notNull(cutSponsor, "地址缺失");
        CutAddressDto cutAddressDto = new CutAddressDto();
        cutAddressDto.setAddrCountry(cutSponsor.getCountry());
        cutAddressDto.setAddrProvince(cutSponsor.getProvince());
        cutAddressDto.setAddrCity(cutSponsor.getCity());
        cutAddressDto.setAddrArea(cutSponsor.getArea());
        cutAddressDto.setAddrDetail(cutSponsor.getConsumerAddr());
        cutAddressDto.setUserMobile(cutSponsor.getConsumerMobile());
        cutAddressDto.setCheckName(cutSponsor.getConsumerName());
        return cutAddressDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CutUserDto bargain(CutUserVo cutUserVo) {
        JwtUserDetails userDetails = getUserDetails();
        int blackNum = cutBlackListService.blackList(userDetails.getUserId(), userDetails.getOpenId());
        Assert.isTrue(blackNum == 0, "发起砍价失败:黑名单限制");
        RLock fairLock = redissonClient.getFairLock("CUT:BARGAIN:" + userDetails.getUserId());
        fairLock.lock(3, TimeUnit.SECONDS);
        try {
            //砍价信息
            CutInfo cutInfo = cutInfoService.selectById(cutUserVo.getCutId());
            Assert.notNull(cutInfo, "砍价信息已失效");
            //砍价商品信息
            ActiveGood activeGood = new ActiveGood();
            activeGood.setActiveType(ActiveGoodsConstants.ActiveType.CUT);
            activeGood.setBusinessId(cutInfo.getCutId());
            activeGood.setGoodId(cutUserVo.getGoodId());
            activeGood.setGoodSpu(cutUserVo.getGoodSpu());
            activeGood.setSaleId(cutUserVo.getSaleId());
            activeGood.setGoodSku(cutUserVo.getGoodSku());
            EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>(activeGood);
            activeGood = activeGoodService.selectOne(activeGoodEntityWrapper);
            Assert.notNull(activeGood, "砍价商品已失效");

            Integer unitPriceInt = activeGood.getActivePrice();//售卖单价
            BigDecimal unitPrice = PriceCalculationUtils.intToBigDecimal(unitPriceInt);
            BigDecimal basePrice = cutInfo.getBasePrice();//底价
            int basePriceInt = PriceCalculationUtils.bigDecimalToInt(basePrice);
            Assert.isTrue(unitPriceInt > basePriceInt, "底价超出售卖价");
            int cutStart = cutInfo.getFristCutStart(); //首砍起始值
            int cutEnd = cutInfo.getFristCutEnd(); //首砍最高值
            CutSponsor cutSponsor = new CutSponsor();
            cutSponsor.setGoodId(activeGood.getGoodId());
            cutSponsor.setGoodName(activeGood.getGoodName());
            cutSponsor.setGoodSpu(cutUserVo.getGoodSpu());
            cutSponsor.setSaleId(cutUserVo.getSaleId());
            cutSponsor.setGoodSku(cutUserVo.getGoodSku());
            cutSponsor.setGoodImage(cutUserVo.getGoodImage());
            cutSponsor.setGoodAmount(PriceCalculationUtils.intToBigDecimal(unitPriceInt));
            cutSponsor.setModelName(cutUserVo.getModelName());
            cutSponsor.setBasePrice(basePrice);
            cutSponsor.setCutNo(generateCutNo(userDetails.getUserId()));
            cutSponsor.setCutId(cutInfo.getCutId());
            cutSponsor.setUserId(userDetails.getUserId());
            cutSponsor.setCutFlag(CutFlag.ONGOING);
            int cutNumber = cutSponsorService.getSuccessCutNumberByUserId(userDetails.getUserId());
            cutSponsor.setNewUser(cutNumber > 0 ? 0 : 1);//0老用户 1新用户
            cutSponsor.setCountry(cutUserVo.getLogistics().getCountry());
            cutSponsor.setProvince(cutUserVo.getLogistics().getProvince());
            cutSponsor.setCity(cutUserVo.getLogistics().getCity());
            cutSponsor.setArea(cutUserVo.getLogistics().getArea());
            cutSponsor.setConsumerAddr(cutUserVo.getLogistics().getConsumerAddr());
            cutSponsor.setConsumerMobile(cutUserVo.getLogistics().getConsumerMobile());
            cutSponsor.setConsumerName(cutUserVo.getLogistics().getConsumerName());
            LocalDateTime midnight = LocalDateTime.now();
            cutSponsor.setStartTime(toDate(midnight));
            cutSponsor.setExpirtTime(toDate(midnight.plusDays(1)));
            cutSponsor.setSource(cutUserVo.getSource());
            cutSponsor.setChannel(cutUserVo.getChannel());
            cutSponsor.setOpenId(userDetails.getOpenId());
            cutSponsor.setAppId(userDetails.getAppId());
            boolean result = cutSponsorService.insert(cutSponsor);
            Assert.isTrue(result, "发起砍价失败");

            CutUser cutUser = new CutUser();
            cutUser.setUserId(cutSponsor.getUserId());
            cutUser.setSponsorId(cutSponsor.getSponsorId());
            cutUser.setCutNo(cutSponsor.getCutNo()); //砍价id
            cutUser.setCutId(cutSponsor.getCutId());
            cutUser.setCutMember(cutSponsor.getUserId());
            cutUser.setCutTime(cutSponsor.getStartTime());
            if (CutType.MONEY.equalsIgnoreCase(cutInfo.getFristCutType())) {//固定金额
                int startNumInt = cutStart * 100;
                int endNumInt = cutEnd * 100;
                Assert.isTrue(unitPriceInt >= startNumInt && unitPriceInt >= endNumInt, "首砍金额区间错误");
                int moneyNum = randomNumber(cutStart, cutEnd); //随机获取首砍金额
                cutUser.setCutAmount(BigDecimal.valueOf(moneyNum));
            } else { //比例
                BigDecimal cutStartRate = PriceCalculationUtils.intToBigDecimal(cutStart);
                BigDecimal cutEndRate = PriceCalculationUtils.intToBigDecimal(cutEnd);
                BigDecimal cutStartPrice = PriceCalculationUtils.multiply(unitPrice, cutStartRate);
                BigDecimal cutEndPrice = PriceCalculationUtils.multiply(unitPrice, cutEndRate);
                int cutStartPriceInt = PriceCalculationUtils.bigDecimalToInt(cutStartPrice);
                int cutEndPriceInt = PriceCalculationUtils.bigDecimalToInt(cutEndPrice);
                Assert.isTrue(unitPriceInt > cutStartPriceInt && unitPriceInt > cutEndPriceInt, "首砍比例区间错误");
                int moneyNum = randomNumber(cutStartPriceInt, cutEndPriceInt); //随机获取首砍金额
                cutUser.setCutAmount(PriceCalculationUtils.intToBigDecimal(moneyNum));
            }
            //最低砍价金额
            BigDecimal baseCutAmount = PriceCalculationUtils.subtract(cutSponsor.getGoodAmount(), cutSponsor.getBasePrice());
            //获取首砍奖励金额
            CutReward cutReward = cutRewardService.getMaxCutReward(cutUser.getUserId());
            if (Objects.nonNull(cutReward)) {
                if (1 == cutReward.getRewardValueType()) { //比例
                    BigDecimal rate = PriceCalculationUtils.divide(cutReward.getRewardValue(), PriceCalculationUtils.INIT_VAL);
                    cutUser.setRewardAmount(PriceCalculationUtils.multiply(rate, cutUser.getCutAmount()));
                } else {
                    cutUser.setRewardAmount(cutReward.getRewardValue());
                }
                cutUser.setCutAmount(cutUser.getCutAmount().add(cutUser.getRewardAmount()));
                log.info("=> 获取首砍奖励金额 cutNo:{},rewardType:{},rewardAmount:{}", cutUser.getCutNo(), cutReward.getRewardType(), cutUser.getRewardAmount());
                cutRewardService.editUseFlagById(cutReward.getRewardId(), cutUser.getCutNo());
            }
            //剩余砍价金额
            BigDecimal surplusAmount = PriceCalculationUtils.subtract(baseCutAmount, Objects.nonNull(cutUser.getRewardAmount()) ? cutUser.getCutAmount().add(cutUser.getRewardAmount()) : cutUser.getCutAmount());
            cutUser.setSurplusAmount(surplusAmount);
            cutUser.setCutFlag(CutFlag.ONGOING);
            cutUser.setStartTime(cutUser.getCutTime());
            ReturnData returnData = userFeignClient.addCutUser(cutUser);
            Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, returnData.getDesc());
            //预先计算出每阶段砍价金额
            bargainHelp(cutSponsor, cutInfo, cutUser.getSurplusAmount());

            log.info("发起砍价增加买买金,userId:{}", userDetails.getUserId());
            mqProducer.addMMKing(userDetails.getUserId(), MMKingShareType.BARGAIN);
            return new CutUserDto(cutUser.getCutNo());
        } finally {
            fairLock.unlock();
        }
    }

    // 砍价过期时间
    private long timeout = 24;
    // 砍价过期时间单位
    private TimeUnit timeUnit = TimeUnit.HOURS;

    /**
     * 发起砍价时，计算出每阶段金额
     *
     * @param cutSponsor
     * @param cutInfo
     * @param surplusAmount
     */
    private void bargainHelp(CutSponsor cutSponsor, CutInfo cutInfo, BigDecimal surplusAmount) {
        //计算每阶段砍价金额
        boolean oldUser = cutSponsor.getNewUser() == 0; //判断是否已发起过砍价并成功的用户
        BigDecimal firstRate = PriceCalculationUtils.divide(oldUser ? cutInfo.getOldFristRate() : cutInfo.getNewFristRate(), PriceCalculationUtils.INIT_VAL); //比例
        int firstTimes = oldUser ? cutInfo.getOldFristTimes() : cutInfo.getNewFristTimes(); //次数
        BigDecimal secondRate = PriceCalculationUtils.divide(oldUser ? cutInfo.getOldSecondRate() : cutInfo.getNewSecondRate(), PriceCalculationUtils.INIT_VAL);
        int secondTimes = oldUser ? cutInfo.getOldSecondTimes() : cutInfo.getNewSecondTimes();
        BigDecimal thirdRate = PriceCalculationUtils.divide(oldUser ? cutInfo.getOldThirdRate() : cutInfo.getNewThirdRate(), PriceCalculationUtils.INIT_VAL);
        int thirdTimes = oldUser ? cutInfo.getOldThirdTimes() : cutInfo.getNewThirdTimes();

        int firstRateAmount = PriceCalculationUtils.bigDecimalToInt(PriceCalculationUtils.multiply(firstRate, surplusAmount));
        int secondRateAmount = PriceCalculationUtils.bigDecimalToInt(PriceCalculationUtils.multiply(secondRate, surplusAmount));
        int thirdRateAmount = PriceCalculationUtils.bigDecimalToInt(PriceCalculationUtils.multiply(thirdRate, surplusAmount));
        //缓存阶段价格数据
        redisTemplate.opsForValue().set(getUserCutRateAmountKey(cutSponsor.getCutNo()),
                JSONObject.toJSONString(new MyCutCache(1, firstRateAmount, firstTimes, secondRateAmount, secondTimes, thirdRateAmount, thirdTimes)),
                timeout, timeUnit);
    }

    /**
     * 获取帮砍数量
     *
     * @param cutNo
     * @param assistUserId 帮砍次数
     * @return
     */
    public int getAssistCutNumber(String cutNo, Long assistUserId) {
        Object cutNumber = redisTemplate.opsForHash().get(getCutSponsorAndAssistRelateKey(cutNo), assistUserId.toString());
        return Objects.isNull(cutNumber) ? 0 : Integer.parseInt(cutNumber.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CutAssistDto assistBargain(CutAssistVo cutAssistVo) {
        JwtUserDetails userDetails = getUserDetails();
        CutAssistDto cutAssistDto = new CutAssistDto();
        boolean memberUser = isMemberUser(userDetails.getUserId());
        RLock fairLock = redissonClient.getFairLock("CUT:BARGAIN:ASSIST:" + cutAssistVo.getCutNo());
        fairLock.lock(3, TimeUnit.SECONDS);
        try {
            //发起人砍价信息
            CutSponsor cutSponsor = cutSponsorService.getCutSponsorByCutNo(cutAssistVo.getCutNo());
            Assert.notNull(cutSponsor, "砍价信息不存在");
            Assert.isNull(cutSponsor.getOrderNo(), "砍价已结束:已下单");
            int cutNumber = getAssistCutNumber(cutAssistVo.getCutNo(), userDetails.getUserId());
            if (cutSponsor.getUserId().longValue() == userDetails.getUserId().longValue()) { //发起砍价用户
                //自己可以帮自己砍2次
                Assert.isTrue(cutNumber < 2, "用户次数已超出:" + cutNumber);
            } else { //帮砍用户
                Assert.isTrue(cutNumber < 1, "帮砍次数已超出:" + cutNumber);
            }
            String myCutCacheStr = redisTemplate.opsForValue().get(getUserCutRateAmountKey(cutAssistVo.getCutNo()));
            Assert.notNull(myCutCacheStr, "砍价已结束");
            MyCutCache myCutCache = JSONObject.parseObject(myCutCacheStr, MyCutCache.class);
            Assert.notNull(myCutCacheStr, "砍价已结束！");
            BaseUser baseUser = userFeignClient.getUserById(cutSponsor.getUserId());
            Assert.notNull(baseUser, "未获取到用户信息");
            int cutAmount, cutStage;
            int[] cutAmounts;
            int cutFlag = 0; //0砍价进行中 1砍价结束
            if (myCutCache.getFirstWaitNum() > 0 && myCutCache.getFirstWaitAmount() > 0) {
                //第一阶段
                cutStage = 1;
                cutAmounts = randomAmount(myCutCache.getFirstWaitAmount(), myCutCache.getFirstWaitNum());
            } else if (myCutCache.getSecondWaitNum() > 0 && myCutCache.getSecondWaitAmount() > 0) {
                //第二阶段
                cutStage = 2;
                cutAmounts = randomAmount(myCutCache.getSecondWaitAmount(), myCutCache.getSecondWaitNum());
            } else if (myCutCache.getThirdWaitNum() > 0 && myCutCache.getThirdWaitAmount() > 0) {
                //第三阶段
                cutStage = 3;
                cutAmounts = randomAmount(myCutCache.getThirdWaitAmount(), myCutCache.getThirdWaitNum());
            } else {
                throw new CustomException("砍价已结束");
            }
            cutAmount = cutAmounts[randomNumber(0, cutAmounts.length)];
            Assert.isTrue(cutAmount > 0, "砍价失败");
            myCutCache.setCutStage(cutStage);
            boolean sendScheduleMessage = false;
            switch (cutStage) {
                case 1:
                    myCutCache.setFirstWaitAmount(myCutCache.getFirstWaitAmount() - cutAmount);
                    myCutCache.setFirstWaitNum(myCutCache.getFirstWaitNum() - 1);
                    sendScheduleMessage = myCutCache.getFirstWaitNum() == 0;
                    break;
                case 2:
                    myCutCache.setSecondWaitAmount(myCutCache.getSecondWaitAmount() - cutAmount);
                    myCutCache.setSecondWaitNum(myCutCache.getSecondWaitNum() - 1);
                    break;
                case 3:
                    myCutCache.setThirdWaitAmount(myCutCache.getThirdWaitAmount() - cutAmount);
                    myCutCache.setThirdWaitNum(myCutCache.getThirdWaitNum() - 1);
                    break;
                default:
                    throw new CustomException("系统异常");
            }
            CutUser cutUser = new CutUser();
            cutUser.setUserId(cutSponsor.getUserId());
            cutUser.setSponsorId(cutSponsor.getSponsorId());
            cutUser.setCutNo(cutSponsor.getCutNo());
            cutUser.setCutId(cutSponsor.getCutId());
            cutUser.setCutMember(userDetails.getUserId());
            cutUser.setCutTime(new Date());
            cutUser.setCutAmount(PriceCalculationUtils.intToBigDecimal(cutAmount));
            cutUser.setSurplusAmount(PriceCalculationUtils.intToBigDecimal(myCutCache.getFirstWaitAmount() + myCutCache.getSecondWaitAmount() + myCutCache.getThirdWaitAmount()));
            cutUser.setCutFlag(cutUser.getSurplusAmount().compareTo(BigDecimal.valueOf(0)) == 0 ? CutFlag.COMPLETED : cutFlag);
            cutTaskService.addAssistNumber(baseUser, cutSponsor.getUserId(), cutUser.getCutMember(), cutUser.getCutNo(), cutUser.getCutId());
            if (cutUser.getUserId().longValue() != cutUser.getCutMember().longValue()) {
                Double score = redisTemplate.opsForZSet().score(CUT_LIST_KEY + "ASSIST", cutUser.getCutMember().toString());
                if (Objects.isNull(score)) {
                    CutReward cutReward = cutRewardService.addFirstCutReward(cutUser.getCutMember(), cutUser.getCutId(), cutUser.getCutNo());
                    if (Objects.nonNull(cutReward)) {
                        cutAssistDto.setFirstCutRate(cutReward.getRewardValue());
                    }
                }
            }
            cutAssistDto.setCutAmount(cutUser.getCutAmount());
            ReturnData returnData = userFeignClient.addCutUser(cutUser);
            Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, returnData.getDesc());
            //记录发起砍价人与帮砍人关系记录
            redisTemplate.opsForHash().increment(getCutSponsorAndAssistRelateKey(cutAssistVo.getCutNo()), userDetails.getUserId().toString(), 1);
            if (cutUser.getCutFlag() == CutFlag.COMPLETED) { //砍价成功-记录免费拿记录
                addCutFreeList(cutSponsor.getUserId());
                //底价未零，砍价成功时，自动生成订单并发货
                if (cutSponsor.getBasePrice().compareTo(BigDecimal.valueOf(0)) == 0) {
                    produceOrder(cutSponsor, memberUser);
                } else {
                    cutSponsor.setCutFlag(CutFlag.COMPLETED);
                    cutSponsorService.editCutFlagBySponsorId(cutSponsor.getSponsorId(), cutSponsor.getCutFlag());
                    cutMessageSendService.sendCutSuccess(cutSponsor.getUserId(), cutSponsor.getCutNo(), null, cutSponsor.getGoodName(), cutSponsor.getGoodAmount().toString(), cutSponsor.getBasePrice().toString());
                }
            } else {
                String key = getUserCutRateAmountKey(cutAssistVo.getCutNo());
                long expire = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
                redisTemplate.opsForValue().set(key, JSONObject.toJSONString(myCutCache), expire, TimeUnit.MILLISECONDS);
            }
            addCutList(cutSponsor.getUserId(), cutUser.getCutMember(), cutUser.getCutAmount());
            log.info("帮助砍价增加买买金,userId:{}", userDetails.getUserId());
            mqProducer.addMMKing(userDetails.getUserId(), MMKingShareType.BARGAIN);
            /**************************************模板消息发送****************************************/
            if (cutSponsor.getUserId().longValue() != userDetails.getUserId().longValue()) {
                //判断是否首次有好友帮砍
                Set<Object> keys = redisTemplate.opsForHash().keys(getCutSponsorAndAssistRelateKey(cutAssistVo.getCutNo()));
                long count = keys.stream().filter(s -> !s.toString().equals(cutSponsor.getUserId().toString())).count();
                if (count == 0) {
                    cutMessageSendService.sendAssistBargain(cutSponsor.getUserId(), cutSponsor.getCutNo(), userDetails.getUserFullName(), cutUser.getCutAmount().toString(), cutUser.getSurplusAmount().toString());
                }
                //给帮砍用户发送模板消息
                cutMessageSendService.sendAssistBargain(userDetails.getUserId(), null, baseUser.getUserFullName(), cutUser.getCutAmount().toString(), cutUser.getSurplusAmount().toString());
            }
            Long expireTime = redisTemplate.getExpire(getUserCutRateAmountKey(cutUser.getCutNo()), TimeUnit.MILLISECONDS);
            expireTime = Objects.isNull(expireTime) ? 0 : expireTime; //过期时间
            //可砍总金额
            BigDecimal totalCutAmount = PriceCalculationUtils.subtract(cutSponsor.getGoodAmount(), cutSponsor.getBasePrice());
            //已砍总金额
            BigDecimal totalBargainPrice = PriceCalculationUtils.subtract(totalCutAmount, cutUser.getSurplusAmount());
            //砍价进度通知-第一阶段结束
            if (sendScheduleMessage) {
                cutMessageSendService.sendSchedule(1, cutSponsor.getUserId(), cutSponsor.getCutNo(), totalBargainPrice.toString(), cutUser.getSurplusAmount().toString(), cutSponsor.getGoodName(), cutSponsor.getGoodAmount().toString(), expireTime);
            }
            //砍价进度通知-砍价金额还剩10%时
            String key = CUT_BARGAIN_NO + "SCHEDULE:" + cutSponsor.getCutNo();
            String value = redisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(value)) {
                BigDecimal rate = PriceCalculationUtils.divide(cutUser.getSurplusAmount(), totalCutAmount); //剩余可砍金额占比
                if (rate.compareTo(BigDecimal.valueOf(0.1)) < 1) {
                    redisTemplate.opsForValue().set(key, "true", 24, TimeUnit.HOURS);
                    cutMessageSendService.sendSchedule(0, cutSponsor.getUserId(), cutSponsor.getCutNo(), totalBargainPrice.toString(), cutUser.getSurplusAmount().toString(), cutSponsor.getGoodName(), cutSponsor.getGoodAmount().toString(), expireTime);
                }
            }
        } finally {
            fairLock.unlock();
        }
        return cutAssistDto;
    }

    //生成砍价订单
    private void produceOrder(CutSponsor cutSponsor, boolean memberUser) {
        OrderProduceDto orderProduceDto = new OrderProduceDto();
        String orderNo = OrderUtils.gainOrderNo(cutSponsor.getUserId(), OrderType.BARGAIN, OrderSource.MIN.name(), OrderClassify.MAIN);
        orderProduceDto.setOrderNo(orderNo);
        orderProduceDto.setOrderType(OrderType.BARGAIN);
        orderProduceDto.setOrderStatus(OrderStatus.PAYMENTED.getStatus());
        orderProduceDto.setOrderAmount(0);
        orderProduceDto.setGoodAmount(PriceConversion.bigDecimalToInt(cutSponsor.getGoodAmount()));
        orderProduceDto.setDiscountAmount(orderProduceDto.getGoodAmount());
        orderProduceDto.setOrderSource(cutSponsor.getSource());
        orderProduceDto.setOrderChannel(cutSponsor.getChannel());
        orderProduceDto.setOpenId(cutSponsor.getOpenId());
        orderProduceDto.setAppId(cutSponsor.getAppId());
        orderProduceDto.setPassingData("{\"activeId\":\"" + cutSponsor.getCutId() + "\",\"cutNo\":\"" + cutSponsor.getCutNo() + "\"}");
        orderProduceDto.setCreaterId(cutSponsor.getUserId());
        orderProduceDto.setVirtualGood(0);
        orderProduceDto.setMemberOrder(memberUser);
        orderProduceDto.setBusinessId(cutSponsor.getCutId());
        OrderProduceDto.Goods goods = new OrderProduceDto.Goods();
        goods.setGoodId(cutSponsor.getGoodId());
        goods.setGoodSpu(cutSponsor.getGoodSpu());
        goods.setSaleId(cutSponsor.getSaleId());
        goods.setGoodSku(cutSponsor.getGoodSku());
        goods.setGoodName(cutSponsor.getGoodName());
        goods.setGoodImage(cutSponsor.getGoodImage());
        goods.setGoodNum(1);
        goods.setPriceType(3);
        goods.setGoodPrice(orderProduceDto.getGoodAmount());
        goods.setGoodAmount(orderProduceDto.getGoodAmount());
        goods.setMemberPrice(orderProduceDto.getGoodAmount());
        goods.setModelName(cutSponsor.getModelName());
        goods.setVirtualFlag("0");
        goods.setVirtualType(null);
        goods.setDiscountAmount(orderProduceDto.getGoodAmount());
        goods.setCouponAmount(0);
        orderProduceDto.setGoods(goods);
        OrderProduceDto.Consignee consignee = new OrderProduceDto.Consignee();
        consignee.setConsumerName(cutSponsor.getConsumerName());
        consignee.setConsumerMobile(cutSponsor.getConsumerMobile());
        consignee.setProvince(cutSponsor.getProvince());
        consignee.setCity(cutSponsor.getCity());
        consignee.setArea(cutSponsor.getArea());
        consignee.setConsumerAddr(cutSponsor.getConsumerAddr());
        orderProduceDto.setConsignee(consignee);
        OrderProduceDto.Payment payment = new OrderProduceDto.Payment();
        payment.setPayType("0");
        payment.setPayAmount(orderProduceDto.getOrderAmount());
        payment.setPayNo(cutSponsor.getCutNo());
        payment.setPayStatus(1);
        payment.setPayDesc("砍到底价零元后生单");
        orderProduceDto.setPayment(payment);
        mqProducer.produceOrder(orderProduceDto);
        cutSponsorService.addOrderInfoByCutNo(cutSponsor.getCutNo(), orderProduceDto.getOrderNo(), orderProduceDto.getOrderStatus(), orderProduceDto.getCreaterId());
        cutMessageSendService.sendCutSuccess(cutSponsor.getUserId(), cutSponsor.getCutNo(), orderProduceDto.getOrderNo(), cutSponsor.getGoodName(), cutSponsor.getGoodAmount().toString(), cutSponsor.getBasePrice().toString());
    }

    /**
     * 获取当前剩余毫秒数
     *
     * @return
     */
    public long getMillSeconds() {
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return ChronoUnit.MILLIS.between(LocalDateTime.now(), midnight);
    }

    /**
     * 转换时间
     *
     * @param midnight
     * @return
     */
    public Date toDate(LocalDateTime midnight) {
        Instant instant = midnight.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * 判断是否会员
     *
     * @param userId
     * @return
     */
    private boolean isMemberUser(Long userId) {
        //当前用户是否会员
        UserMember userMember = userFeignClient.queryUserMemberInfoByUserId(userId).getData();
        return Objects.nonNull(userMember) && userMember.getActive();
    }

    /**
     * 获取v1~v2区间的随机数
     *
     * @param min
     * @param max
     * @return
     */
    private int randomNumber(int min, int max) {
        if (max == 0)
            return 0;
        if (min == max) {
            return min;
        }
        return new Random().nextInt(max - min) + min;
    }


    /**
     * 随机获取totalAmount金额，共分为num份
     *
     * @param totalAmount
     * @param num
     * @return
     */
    private int[] randomAmount(int totalAmount, int num) {
        if (totalAmount <= 0 || num <= 1)
            return new int[]{totalAmount};
        int startAmount = (int) Math.round(Math.random() * (double) (totalAmount / num));
        int[] arr = new int[num];
        arr[0] = startAmount;
        int remAmount = totalAmount - startAmount;
        for (int i = 1; i < (arr.length - 1); i++) {
            arr[i] = randomNumber(1, remAmount + 1);
            remAmount = remAmount - arr[i];
        }
        arr[arr.length - 1] = remAmount;
        return arr;
    }

    /**
     * 砍价排行榜
     */
    class CutList {
        private String userId;
        private Double score;

        public CutList(String userId, Double score) {
            this.userId = userId;
            this.score = score;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }
    }

}
