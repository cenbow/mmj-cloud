package com.mmj.active.topic.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.constants.CouponConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.model.*;
import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.common.service.ActiveSortService;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.CouponNumDto;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.active.topic.controller.TopicInfoController;
import com.mmj.active.topic.mapper.TopicInfoMapper;
import com.mmj.active.topic.model.TopicComponent;
import com.mmj.active.topic.model.TopicCoupon;
import com.mmj.active.topic.model.TopicInfo;
import com.mmj.active.topic.model.dto.ActiveSortDto;
import com.mmj.active.topic.model.dto.TopicComponentEx;
import com.mmj.active.topic.model.dto.TopicEx;
import com.mmj.active.topic.model.dto.TopicInfoDto;
import com.mmj.active.topic.service.TopicComponentService;
import com.mmj.active.topic.service.TopicCouponService;
import com.mmj.active.topic.service.TopicInfoService;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 专题表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-25
 */
@Service
public class TopicInfoServiceImpl extends ServiceImpl<TopicInfoMapper, TopicInfo> implements TopicInfoService {

    private Logger logger = LoggerFactory.getLogger(TopicInfoController.class);

    @Autowired
    private TopicInfoMapper topicInfoMapper;
    @Autowired
    private ActiveGoodService activeGoodService;
    @Autowired
    private TopicCouponService topicCouponService;
    @Autowired
    private ActiveSortService activeSortService;
    @Autowired
    private GoodFeignClient goodFeignClient;
    @Autowired
    private CouponInfoService couponInfoService;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private TopicComponentService topicComponentService;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据id查询专题
     *
     * @param topicId
     * @return
     */
    @Override
    public TopicInfoDto getTopicById(Integer topicId) {
        TopicInfo topic = topicInfoMapper.selectById(topicId);
        if (topic.getTopicTemplate() == null) {
            topic.setTopicTemplate(1);
        }
        TopicInfoDto dto = new TopicInfoDto();
        BeanUtils.copyProperties(topic, dto);

        //返回优惠券
        dto.setCoupons(getCoupons(topicId));

        dto.setActiveSort(getActiveSort(dto.getTopicId()));

        //查询商品id
        ActiveGood activeGood = new ActiveGood();
        activeGood.setBusinessId(topic.getTopicId());
        activeGood.setActiveType(ActiveGoodsConstants.ActiveType.TOPIC);
        activeGood.setCurrentPage(1);
        activeGood.setPageSize(Integer.MAX_VALUE);
        activeGood.setArg1("0");//非置顶商品
        Page<ActiveGood> activeGoodPage = activeGoodService.queryBaseList(activeGood);
        if (activeGoodPage != null && activeGoodPage.getSize() > 0) {
            List<ActiveGood> activeGoods = activeGoodPage.getRecords();
            String goodIds = "";
            for (ActiveGood a : activeGoods) {
                goodIds += a.getGoodId() + ",";
            }
            dto.setGoodIds(goodIds.length() > 0 ? goodIds.substring(0, goodIds.length() - 1) : "");
        }
        return dto;
    }


    /**
     * 专题列表查询
     *
     * @param topicInfo
     * @return
     */
    @Override
    public Page<TopicInfoDto> queryPage(TopicInfo topicInfo) {
        Page<TopicInfoDto> page = new Page<>(topicInfo.getCurrentPage(), topicInfo.getPageSize());
        List<TopicInfoDto> topicInfos = topicInfoMapper.queryTopicPage(page, topicInfo);
        //优惠券中文名称和分类中文名称
        for (TopicInfoDto topic : topicInfos) {
            topic.setCoupons(getCoupons(topic.getTopicId()));
            topic.setActiveSort(getActiveSort(topic.getTopicId()));

            //转换商品分类名称
            if (!StringUtils.isEmpty(topic.getTopicGoodClass()) && !"GOOD".equals(topic.getTopicGoodClass())) {
                GoodClassBase goodClassBase = new GoodClassBase();
                goodClassBase.setClassCodes(Arrays.asList(topic.getTopicGoodClass().split(",")));
                ReturnData<List<GoodClass>> goodClassBases = goodFeignClient.queryGoodClassDetail(goodClassBase);
                List<GoodClass> goodClassList = goodClassBases.getData();
                if (!goodClassList.isEmpty()) {
                    String topicGoodClassName = goodClassList.stream().map(GoodClass::getClassName).collect(Collectors.joining(","));
                    topic.setTopicGoodClass(topicGoodClassName);
                }
            } else {
                //查询商品id
                ActiveGood activeGood = new ActiveGood();
                activeGood.setBusinessId(topic.getTopicId());
                activeGood.setActiveType(ActiveGoodsConstants.ActiveType.TOPIC);
                activeGood.setCurrentPage(1);
                activeGood.setPageSize(Integer.MAX_VALUE);
                Page<ActiveGood> activeGoodPage = activeGoodService.queryBaseList(activeGood);
                if (activeGoodPage != null && activeGoodPage.getSize() > 0) {
                    List<ActiveGood> activeGoods = activeGoodPage.getRecords();
                    String goodIds = "";
                    for (ActiveGood a : activeGoods) {
                        goodIds += a.getGoodId() + ",";
                    }
                    topic.setGoodIds(goodIds.length() > 0 ? goodIds.substring(0, goodIds.length() - 1) : "");
                }
            }

        }
        page.setRecords(topicInfos);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveTopicById(TopicInfoDto topicInfoDto, Boolean bSave) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();

        Map<String, Object> map = new HashMap<>();
        try {
            String goodidsString = topicInfoDto.getGoodIds();

            if ("GOOD".equals(topicInfoDto.getTopicGoodClass()) && goodidsString == null) {
                map.put("msg", "商品不能为空");
                return map;
            }

            if (bSave) {
                topicInfoDto.setCreaterId(jwtUserDetails.getUserId());
                topicInfoDto.setCreaterTime(new Date());
                topicInfoMapper.insert(topicInfoDto);
            } else {
                topicInfoDto.setModifyId(jwtUserDetails.getUserId());
                topicInfoDto.setModifyTime(new Date());
                topicInfoMapper.updateById(topicInfoDto);

                activeSortService.deleteBusinessId(topicInfoDto.getTopicId());
                topicCouponService.deleteTopicId(topicInfoDto.getTopicId());
                activeGoodService.deleteBusinessId(topicInfoDto.getTopicId());
            }
            map.put("topicId", topicInfoDto.getTopicId());

            //写入商品
            if ("GOOD".equals(topicInfoDto.getTopicGoodClass())) {
                String[] goodIdsListA = goodidsString.split(",");
                List<String> goodIdsListB = Arrays.asList(goodIdsListA);
                List<Integer> goodIdsList = removeDuplicateWithOrder(goodIdsListB);
                List<ActiveGood> goodlist = new ArrayList<>();
                EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>();
                activeGoodEntityWrapper.eq("ACTIVE_TYPE", 12);
                activeGoodEntityWrapper.in("GOOD_ID", Lists.newArrayList(topicInfoDto.getGoodIds()));
                List<ActiveGood> activeGoods = activeGoodService.selectList(activeGoodEntityWrapper);
                for (int i = 0; i < goodIdsList.size(); i++) {
                    ActiveGood activeGood = new ActiveGood();
                    activeGood.setBusinessId(topicInfoDto.getTopicId());
                    activeGood.setActiveType(ActiveGoodsConstants.ActiveType.TOPIC);
                    activeGood.setGoodId(goodIdsList.get(i));
                    activeGood.setArg1("0");
                    List<ActiveGood> ag = activeGoods.stream().filter(a -> a.getGoodId().equals(activeGood.getGoodId())).collect(Collectors.toList());
                    activeGood.setGoodName(ag.size() > 0 ? ag.get(0).getGoodName() : "");
                    activeGood.setGoodImage(ag.size() > 0 ? ag.get(0).getGoodImage() : "");
                    goodlist.add(activeGood);
                }
                activeGoodService.insertBatch(goodlist);
            }

            //注意：排序和置顶商品由前端单独调俊哥接口统一写入

            List<TopicCoupon> topicCoupons = new ArrayList<>();
            if (!topicInfoDto.getCoupons().isEmpty()) {
                for (TopicInfoDto.Coupons coupons : topicInfoDto.getCoupons()) {
                    TopicCoupon topicCoupon = new TopicCoupon();
                    topicCoupon.setTopicId(topicInfoDto.getTopicId());
                    topicCoupon.setCouponId(coupons.getCouponId());
                    topicCoupon.setCouponTitle(coupons.getCouponName());
                    topicCoupons.add(topicCoupon);
                }
                topicCouponService.insertBatch(topicCoupons);
            }

        } catch (Exception e) {
            logger.error("-------saveTopicById:", e);
            map.put("msg", "新增失败");
            return map;
        }
        return map;
    }

    @Override
    public Map<String, Object> updateTopicById(TopicInfoDto topicInfoDto) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (topicInfoDto.getTopicId() != null) {
                return this.saveTopicById(topicInfoDto, false);
            }
        } catch (Exception e) {
            logger.error("-------updateTopicById:", e);
            map.put("msg", "编辑失败");
            return map;
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteTopicById(Integer topicId) {
        try {
            TopicInfo topicInfo = this.selectById(topicId);
            Assert.isTrue(topicInfo != null, "不存在该主题");
            logger.info("当前主题为:{},{}", topicInfo, null == topicInfo.getTopicTemplate());
            if (null == topicInfo.getTopicTemplate() || topicInfo.getTopicTemplate() == 1) {
                this.deleteById(topicId);
                topicCouponService.deleteTopicId(topicId);
                activeGoodService.deleteBusinessId(topicId);
                return "删除成功";
            } else {
                return "免邮拼团不可随意删除";
            }
        } catch (Exception e) {
            return "删除失败";
        }
    }

    /**
     * 修改专题组件(包含修改和新增)
     *
     * @param topicEx
     */
    @Override
    @Transactional
    public void updateTopicComponent(TopicEx topicEx) {
        Assert.notNull(topicEx.getTopicComponentExes(), "商品不能为空");
        Assert.isTrue(!topicEx.getTopicComponentExes().isEmpty(), "商品不能为空.");

        //修改专题组件 先删除
        EntityWrapper<TopicComponent> topicComponentEntityWrapper = new EntityWrapper<>();
        topicComponentEntityWrapper.eq("TOPIC_ID", topicEx.getTopicId());
        topicComponentService.delete(topicComponentEntityWrapper);

        List<TopicComponentEx> topicComponents = topicEx.getTopicComponentExes();
        //保存专题组件
        //此专题有组件(橱窗)
        topicComponents.forEach(topicComponent -> {
            if ("good".equals(topicComponent.getJumpType())) { //如果是商品类型的跳转 那么就要校验spu是否正确
                String jumpUrl1 = topicComponent.getJumpUrl1();
                GoodInfoBaseQueryEx goodInfo = null;
                if (StringUtils.isNotEmpty(jumpUrl1)) {
                    GoodInfoBaseQueryEx gbqe = new GoodInfoBaseQueryEx();
                    gbqe.setGoodSpu(jumpUrl1);
                    Object data = goodFeignClient.queryBaseList(gbqe).getData();
                    Assert.notNull(data, jumpUrl1 + "不存在!");
                    Page page = JSON.parseObject(JSON.toJSONString(data), Page.class);
                    List listPage = page.getRecords();
                    if (listPage != null && !listPage.isEmpty()) {
                        List<GoodInfoBaseQueryEx> goods = JSON.parseArray(JSON.toJSONString(listPage), GoodInfoBaseQueryEx.class);
                        if (!goods.isEmpty()) {
                            goodInfo = goods.get(0);
                        }
                    }
                    Assert.notNull(goodInfo, jumpUrl1 + "不存在");
                    Assert.isTrue("1".equals(goodInfo.getGoodStatus()), jumpUrl1 + "未上架");
                }
                String jumpUrl2 = topicComponent.getJumpUrl2();
                if (StringUtils.isNotEmpty(jumpUrl2)) {
                    GoodInfoBaseQueryEx gbqe = new GoodInfoBaseQueryEx();
                    gbqe.setGoodSpu(jumpUrl2);
                    Object data = goodFeignClient.queryBaseList(gbqe).getData();
                    Assert.notNull(data, jumpUrl2 + "不存在!");
                    Page page = JSON.parseObject(JSON.toJSONString(data), Page.class);
                    List listPage = page.getRecords();
                    if (listPage != null && !listPage.isEmpty()) {
                        List<GoodInfoBaseQueryEx> goods = JSON.parseArray(JSON.toJSONString(listPage), GoodInfoBaseQueryEx.class);
                        if (!goods.isEmpty()) {
                            goodInfo = goods.get(0);
                        }
                    }
                    Assert.notNull(goodInfo, jumpUrl2 + "不存在");
                    Assert.isTrue("1".equals(goodInfo.getGoodStatus()), jumpUrl2 + "未上架");
                }
                String jumpUrl3 = topicComponent.getJumpUrl3();
                if (StringUtils.isNotEmpty(jumpUrl3)) {
                    GoodInfoBaseQueryEx gbqe = new GoodInfoBaseQueryEx();
                    gbqe.setGoodSpu(jumpUrl3);
                    Object data = goodFeignClient.queryBaseList(gbqe).getData();
                    Assert.notNull(data, jumpUrl3 + "不存在!");
                    Page page = JSON.parseObject(JSON.toJSONString(data), Page.class);
                    List listPage = page.getRecords();
                    if (listPage != null && !listPage.isEmpty()) {
                        List<GoodInfoBaseQueryEx> goods = JSON.parseArray(JSON.toJSONString(listPage), GoodInfoBaseQueryEx.class);
                        if (!goods.isEmpty()) {
                            goodInfo = goods.get(0);
                        }
                    }
                    Assert.notNull(goodInfo, jumpUrl3 + "不存在");
                    Assert.isTrue("1".equals(goodInfo.getGoodStatus()), jumpUrl3 + "未上架");
                }
            }
            topicComponent.setCreateTime(new Date());
            topicComponent.setTopicId(topicEx.getTopicId());
            topicComponentService.insert(topicComponent);
        });
    }

    /**
     * 根据专题查询专题组件(橱窗)
     *
     * @param topic
     * @param appType
     * @return
     */
    @Override
    public Object queryTopicComponent(TopicInfo topic, String appType) {
        EntityWrapper<TopicComponent> topicComponentEntityWrapper = new EntityWrapper<>();
        topicComponentEntityWrapper.orderBy("sort_num");
        topicComponentEntityWrapper.eq("TOPIC_ID", topic.getTopicId());
        if (!"BOSS".equalsIgnoreCase(appType)) { //如果不是boss端 那么就考虑新老用户返回
            JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
            //获取是否会员
            UserMember userMember = userFeignClient.queryUserMemberInfoByUserId(jwtUserDetails.getUserId()).getData();
            //获取是否新用户
            Map<String, Object> map = new HashMap<>();
            map.put("userId", jwtUserDetails.getUserId());
            boolean newUser = orderFeignClient.checkNewUser(map).getData();
            String userType = (!Objects.isNull(userMember) && userMember.getActive()) ? "member" : (newUser ? "old" : "new");
            topicComponentEntityWrapper.and("find_in_set('" + userType + "', user_type)");
        }

        List<TopicComponent> topicComponents = topicComponentService.selectList(topicComponentEntityWrapper);
        List<TopicComponentEx> topicComponentExes = new ArrayList<>();
        topicComponents.forEach(topicComponent -> {
            TopicComponentEx topicComponentEx = new TopicComponentEx();
            BeanUtils.copyProperties(topicComponent, topicComponentEx);
            if (3 == topicComponent.getType()) {//优惠券组件 显示优惠券名称
                CouponInfo couponInfo = couponInfoService.selectById(topicComponent.getCouponId1());
                if (null != couponInfo) {
                    topicComponentEx.setCouponName1(couponInfo.getCouponTitle());
                    topicComponentEx.setCouponMoney1(Double.valueOf(PriceConversion.intToString(couponInfo.getCouponValue())));
                    topicComponentEx.setBusinessRemark1(couponInfo.getMaketingDesc());
                    topicComponentEx.setStatus1(getStatus(couponInfo, !"BOSS".equalsIgnoreCase(appType) ? SecurityUserUtil.getUserDetails().getUserId() : 0));
                }
                couponInfo = couponInfoService.selectById(topicComponent.getCouponId2());
                if (null != couponInfo) {
                    topicComponentEx.setCouponName2(couponInfo.getCouponTitle());
                    topicComponentEx.setCouponMoney2(Double.valueOf(PriceConversion.intToString(couponInfo.getCouponValue())));
                    topicComponentEx.setBusinessRemark2(couponInfo.getMaketingDesc());
                    topicComponentEx.setStatus2(getStatus(couponInfo, !"BOSS".equalsIgnoreCase(appType) ? SecurityUserUtil.getUserDetails().getUserId() : 0));
                }
                couponInfo = couponInfoService.selectById(topicComponent.getCouponId3());
                if (null != couponInfo) {
                    topicComponentEx.setCouponName3(couponInfo.getCouponTitle());
                    topicComponentEx.setCouponMoney3(Double.valueOf(PriceConversion.intToString(couponInfo.getCouponValue())));
                    topicComponentEx.setBusinessRemark3(couponInfo.getMaketingDesc());
                    topicComponentEx.setStatus3(getStatus(couponInfo, !"BOSS".equalsIgnoreCase(appType) ? SecurityUserUtil.getUserDetails().getUserId() : 0));
                }
            }
            if (StringUtils.isNotEmpty(topicComponent.getJumpType()) && "good".equals(topicComponent.getJumpType())) {//如果该组件是商品跳转类型
                GoodInfo goodInfo = new GoodInfo();
                GoodInfoBaseQueryEx gbqe = new GoodInfoBaseQueryEx();
                gbqe.setGoodSpu(topicComponentEx.getJumpUrl1());
                Object data = goodFeignClient.queryBaseList(gbqe).getData();
                if (data != null) {
                    Page page = JSON.parseObject(JSON.toJSONString(data), Page.class);
                    List listPage = page.getRecords();
                    if (listPage != null && !listPage.isEmpty()) {
                        List<GoodInfoBaseQueryEx> goods = JSON.parseArray(JSON.toJSONString(listPage), GoodInfoBaseQueryEx.class);
                        if (!goods.isEmpty()) {
                            goodInfo = goods.get(0);
                        }
                    }
                }

                if (null != goodInfo) {
                    topicComponentEx.setGoodsbaseid1(goodInfo.getGoodId());
                    List<GoodNum> goodNums = goodFeignClient.queryGoodNum(Lists.newArrayList(goodInfo.getGoodId())).getData();
                    if (!goodNums.isEmpty()) {
                        topicComponentEx.setStocknum1(goodNums.get(0).getGoodNumTotal());//库存
                    }
                    topicComponentEx.setShelves1("1".equals(goodInfo.getGoodStatus()));//上架状态
                }

                GoodInfo goodInfo2 = new GoodInfo();
                gbqe.setGoodSpu(topicComponentEx.getJumpUrl2());
                Object data2 = goodFeignClient.queryBaseList(gbqe).getData();
                if (data2 != null) {
                    Page page = JSON.parseObject(JSON.toJSONString(data2), Page.class);
                    List listPage = page.getRecords();
                    if (listPage != null && !listPage.isEmpty()) {
                        List<GoodInfoBaseQueryEx> goods = JSON.parseArray(JSON.toJSONString(listPage), GoodInfoBaseQueryEx.class);
                        if (!goods.isEmpty()) {
                            goodInfo2 = goods.get(0);
                        }
                    }
                }
                if (null != goodInfo2) {
                    topicComponentEx.setGoodsbaseid2(goodInfo2.getGoodId());
                    List<GoodNum> goodNums = goodFeignClient.queryGoodNum(Lists.newArrayList(goodInfo2.getGoodId())).getData();
                    if (!goodNums.isEmpty()) {
                        topicComponentEx.setStocknum1(goodNums.get(0).getGoodNumTotal());//库存
                    }
                    topicComponentEx.setShelves2("1".equals(goodInfo2.getGoodStatus()));
                }

                GoodInfo goodInfo3 = new GoodInfo();
                gbqe.setGoodSpu(topicComponentEx.getJumpUrl3());
                Object data3 = goodFeignClient.queryBaseList(gbqe).getData();
                if (data3 != null) {
                    Page page = JSON.parseObject(JSON.toJSONString(data3), Page.class);
                    List listPage = page.getRecords();
                    if (listPage != null && !listPage.isEmpty()) {
                        List<GoodInfoBaseQueryEx> goods = JSON.parseArray(JSON.toJSONString(listPage), GoodInfoBaseQueryEx.class);
                        if (!goods.isEmpty()) {
                            goodInfo3 = goods.get(0);
                        }
                    }
                }
                if (null != goodInfo3) {
                    topicComponentEx.setGoodsbaseid3(goodInfo3.getGoodId());
                    List<GoodNum> goodNums = goodFeignClient.queryGoodNum(Lists.newArrayList(goodInfo3.getGoodId())).getData();
                    if (!goodNums.isEmpty()) {
                        topicComponentEx.setStocknum1(goodNums.get(0).getGoodNumTotal());//库存
                    }
                    topicComponentEx.setShelves3("1".equals(goodInfo3.getGoodStatus()));
                }
            }
            topicComponentExes.add(topicComponentEx);
        });
        return topicComponentExes;
    }

    private int getStatus(CouponInfo couponInfo, long userId) {
        UserCouponVo userCouponVo = new UserCouponVo();
        userCouponVo.setCouponId(couponInfo.getCouponId());
        userCouponVo.setUserId(userId);
        userCouponVo.setCouponSource(CouponConstants.CouponSource.TOPIC_SEND);
        boolean receive = userFeignClient.hasReceive(userCouponVo).getData();
        if (receive) { //已领取
            return 2;
        } else {//返回1是可以领取
            if (couponInfo.getEveryDayNum() == -1) {
                return 1;
            } else {
                CouponNumDto couponNumDto = couponInfoService.toDayNum(couponInfo.getCouponId());
                if (couponNumDto.getNum() < couponInfo.getEveryDayNum()) {//当天发放量小于每天限定量可以发放
                    return 1;
                }
            }
        }
        if (couponInfo.getCountNum() == -1) {
            return 1;
        } else {
            if (couponInfo.getTotalSendNumber() <= couponInfo.getCountNum()) {
                return 1;
            }
        }
        return 3;
    }


    @Override
    public List<Map<String, Object>> getTopicsCoupon(int topicId) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        EntityWrapper<TopicCoupon> topicCouponEntityWrapper = new EntityWrapper<>();
        topicCouponEntityWrapper.eq("TOPIC_ID", topicId);
        List<TopicCoupon> topicCoupons = topicCouponService.selectList(topicCouponEntityWrapper);
        List<Map<String, Object>> listMap = new ArrayList<>();
        if (!topicCoupons.isEmpty()) {
            Map<String, String> typeMap = new HashMap<>();
            typeMap.put("1", "所有商品可用");
            typeMap.put("2", "部分商品可用");
            typeMap.put("3", "所有商品不可用");
            typeMap.put("4", "指定分类可用");
            Map<Double, Object> resultMap = new HashMap<>();
            List<Double> array = new ArrayList<>();

            List<Integer> couponInfoIds = topicCoupons.stream().map(TopicCoupon::getCouponId).collect(Collectors.toList());
            List<CouponInfo> couponInfos = couponInfoService.batchCouponInfos(couponInfoIds);
            for (TopicCoupon topicCoupon : topicCoupons) {
                List<CouponInfo> couponInfoList = couponInfos.stream().filter(p -> topicCoupon.getCouponId().equals(p.getCouponId())).collect(Collectors.toList());
                CouponInfo couponInfo = couponInfoList.size() > 0 ? couponInfoList.get(0) : null;
                if (couponInfo == null) {
                    continue;
                }

                Map<String, Object> map = new HashMap<>();
                boolean isShow = true;
                int countNum = couponInfo.getCountNum() != null ? Integer.valueOf(couponInfo.getCountNum()) : -1;//发送总量

                int everyDayNum = couponInfo.getEveryDayNum() != null ? Integer.valueOf(couponInfo.getEveryDayNum()) : -1;//每天发放数量
                int totalSendNum = couponInfo.getTotalSendNumber() != null ? Integer.valueOf(couponInfo.getTotalSendNumber()) : 0;//剩余数量
                int couponId = couponInfo.getCouponId();//优惠券id
                //当天发送的优惠券数量
                List<CouponNumDto> couponNumDtoList = couponInfoService.batchTodayNums(couponInfoIds);
                List<CouponNumDto> couponNumDtos = couponNumDtoList.stream().filter(c -> topicCoupon.getCouponId().equals(c.getCouponId())).collect(Collectors.toList());
                int currentDayRecievedCouponsCount = couponNumDtos.size() > 0 ? couponNumDtos.get(0).getNum() : 0;
                if (CouponConstants.UNLIMITED.equals(everyDayNum)) {
                    //如果总量有限量，要判断余量是否还够
                    if (!CouponConstants.UNLIMITED.equals(countNum)) {
                        if (countNum <= totalSendNum) {
                            isShow = false;
                        }
                    }
                    //如果总量也不限量，直前台直接可领取
                } else {
                    //每天限量，则要判断当天领取的数量是否已达到配置的数量
                    if (currentDayRecievedCouponsCount >= everyDayNum) {
                        isShow = false;
                    }
                }
                //要判断用户是否已经领取过
                UserCouponVo userCouponVo = new UserCouponVo();
                userCouponVo.setUserId(jwtUserDetails.getUserId());
                userCouponVo.setCouponId(couponInfo.getCouponId());
                userCouponVo.setCouponSource(CouponConstants.CouponSource.TOPIC_SEND);
                Boolean received = userFeignClient.hasReceive(userCouponVo).getData();
                map.put("received", received);
                if (isShow) {
                    isShow = !received;
                }

                map.put("couponId", couponId);
                map.put("couponName", couponInfo.getCouponTitle());//优惠券标题
                map.put("maketingDesc", couponInfo.getMaketingDesc());//运营备注
                map.put("countNum", countNum);
                map.put("isShow", isShow);

                map.put("couponRange", typeMap.get(couponInfo.getCouponScope()));//使用范围
                map.put("couponMoney", PriceConversion.intToString(couponInfo.getCouponValue()));//优惠值

                array.add(Double.parseDouble(couponInfo.getCouponValue().toString()));
                resultMap.put(Double.parseDouble(couponInfo.getCouponValue().toString()), map);

            }
            Collections.sort(array);
            for (int i = 0; i < array.size(); i++) {
                listMap.add((Map<String, Object>) resultMap.get(array.get(i)));
            }
        }
        return listMap;
    }


    public static List<Integer> removeDuplicateWithOrder(List<String> list) {
        Set set = new HashSet();
        List<Integer> newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(Integer.valueOf((String) element));
        }
        return newList;
    }

    /**
     * 获取优惠券列表
     *
     * @param topicId
     * @return
     */
    public List<TopicInfoDto.Coupons> getCoupons(int topicId) {
        List<TopicCoupon> topicCoupons = topicCouponService.selectTopicList(topicId);
        List<TopicInfoDto.Coupons> coupons = new ArrayList<>();
        for (TopicCoupon topicCoupon : topicCoupons) {
            TopicInfoDto.Coupons coupon = new TopicInfoDto.Coupons(topicCoupon.getCouponId(), topicCoupon.getCouponTitle());
            coupons.add(coupon);
        }
        return coupons;
    }

    /**
     * 获取排序信息
     *
     * @param topicId
     * @return
     */
    public ActiveSortDto getActiveSort(int topicId) {
        List<ActiveSort> activeSorts = activeSortService.selectBusinessList(topicId);
        ActiveSortDto activeSortDto = new ActiveSortDto();
        if (!activeSorts.isEmpty()) {
            BeanUtils.copyProperties(activeSorts.get(0), activeSortDto);
        }
        return activeSortDto;
    }
}
