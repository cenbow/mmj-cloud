package com.mmj.user.recommend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.OrderType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.UserMerge;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.OrderUtils;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.common.feigin.OrderGoodFeignClient;
import com.mmj.user.common.model.OrderGood;
import com.mmj.user.common.model.OrderInfo;
import com.mmj.user.common.model.dto.OrderGoodsDto;
import com.mmj.user.common.model.vo.OrderFinishGoodVo;
import com.mmj.user.common.model.vo.OrderInfoGoodVo;
import com.mmj.user.common.model.vo.UserOrderVo;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.service.BaseUserService;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.service.MemberConfigService;
import com.mmj.user.member.service.UserKingLogService;
import com.mmj.user.member.service.UserMemberService;
import com.mmj.user.recommend.mapper.UserRecommendFileMapper;
import com.mmj.user.recommend.mapper.UserRecommendMapper;
import com.mmj.user.recommend.model.UserRecommend;
import com.mmj.user.recommend.model.UserRecommendEx;
import com.mmj.user.recommend.model.UserRecommendFile;
import com.mmj.user.recommend.model.vo.UserRecommendOrder;
import com.mmj.user.recommend.model.vo.UserRecommendVo;
import com.mmj.user.recommend.service.UserRecommendService;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import com.xiaoleilu.hutool.date.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户推荐表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
@Service
public class UserRecommendServiceImpl extends ServiceImpl<UserRecommendMapper, UserRecommend> implements UserRecommendService {
    Logger logger = LoggerFactory.getLogger(UserShardServiceImpl.class);
    @Autowired
    private UserRecommendMapper userRecommendMapper;
    @Autowired
    private UserRecommendFileMapper userRecommendFileMapper;
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private UserKingLogService userKingLogService;
    @Autowired
    private MemberConfigService memberConfigService;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private OrderGoodFeignClient orderGoodFeignClient;
    @Autowired
    private BaseUserService baseUserService;

    @Override
    public Object save(UserRecommendVo userRecommendVo) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        EntityWrapper<UserRecommend> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_SKU", userRecommendVo.getGoodSku());
        entityWrapper.eq("GOOD_ID", userRecommendVo.getGoodId());
        entityWrapper.eq("CREATER_ID", userDetails.getUserId());
        entityWrapper.eq("ORDER_NO", userRecommendVo.getOrderNo());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        List<UserRecommend> userRecommends = userRecommendMapper.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(userRecommends)) {
            userRecommendVo.setShowStatus(0);
            userRecommendVo.setRecommendStatus(0);
            userRecommendVo.setCreaterId(userDetails.getUserId());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
            BaseUser baseUser = baseUserService.getById(userDetails.getUserId());
            userRecommendVo.setCreaterName(baseUser.getUserFullName());
            userRecommendVo.setCreaterHead(baseUser.getImagesUrl());
            userRecommendVo.setCreaterTime(DateUtil.date());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
            userRecommendMapper.insertAllColumn(userRecommendVo);

            List<UserRecommendFile> usrList = userRecommendVo.getUsrList(); //图片或视频
            if (CollectionUtils.isNotEmpty(usrList)) {
                UserRecommendFile userRecommendFile = new UserRecommendFile();
                userRecommendFile.setRecommendId(userRecommendVo.getRecommendId());
                userRecommendFile.setCreaterId(userDetails.getUserId());
                AtomicBoolean haveFile = new AtomicBoolean(false);
                usrList.forEach(u -> {
                    if (!"loading".equals(u.getFileUrl())) {
                        userRecommendFile.setCreaterTime(DateUtil.date());
                        userRecommendFile.setFileUrl(u.getFileUrl());
                        userRecommendFile.setSortOrder(u.getSortOrder());
                        userRecommendFile.setFileFormat(u.getFileFormat());
                        String coverUrl = u.getCoverUrl();
                        if (StringUtils.isNotEmpty(coverUrl)) {
                            userRecommendFile.setCoverUrl(coverUrl);
                        }
                        haveFile.set(true);
                        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
                        userRecommendFileMapper.insertAllColumn(userRecommendFile);
                    }
                });
                if (haveFile.get())
                    //增加买买金
                    userKingLogService.addMMKing(userDetails.getUserId(), userRecommendVo.getOrderNo());

            }
            JSONObject map = new JSONObject();
            map.put("recommendId", userRecommendVo.getRecommendId());
            return map;
        } else {
            return "请勿频繁操作!";
        }
    }

    @Override
    public Object updateUserRecommend(UserRecommend userRecommend) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        userRecommend.setModifyId(userDetails.getUserId());
        userRecommend.setModifyTime(DateUtil.date());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userRecommend.getCreaterId());
        userRecommendMapper.updateById(userRecommend);
        JSONObject map = new JSONObject();
        map.put("recommendId", userRecommend.getRecommendId());
        return map;
    }

    @Override
    public Page<UserRecommendEx> queryList(UserRecommend entity) {
        Page<UserRecommendEx> page = new Page<>(entity.getCurrentPage(), entity.getPageSize());
        List<UserRecommendEx> recommendList = userRecommendMapper.queryList(page, entity);
        if (CollectionUtils.isNotEmpty(recommendList)) {
            recommendList.forEach(userRecommendEx -> {
                List<String> fileList = userRecommendFileMapper.selectFileUrl(userRecommendEx.getRecommendId());
                userRecommendEx.setFileUrlList(fileList);
            });
        }
        page.setRecords(recommendList);
        return page;
    }


    @Override
    public Object selectRecommendList(UserRecommendVo userRecommendVo) {
        Page<UserRecommendVo> page = new Page<>(userRecommendVo.getCurrentPage(), userRecommendVo.getPageSize());
        userRecommendVo.setCreaterId(userRecommendVo.getCreaterId());
        userRecommendVo.setRecommendStatus(1);
        userRecommendVo.setShowStatus(1);
        List<UserRecommendVo> list = null;
        if (userRecommendVo.getFileFormat() == null) {
            list = userRecommendMapper.selectRecommendAllList(page, userRecommendVo);
        } else {
            list = userRecommendMapper.selectRecommendList(page, userRecommendVo);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(vo -> {
                List<UserRecommendFile> urlList = userRecommendFileMapper.selectRecommendFileUrl(vo.getRecommendId());
                vo.setUsrList(urlList);
            });
        }

        page.setRecords(list);

        Integer alltotal = userRecommendMapper.selectAlltotal(userRecommendVo); //全部数量
        userRecommendVo.setFileFormat(1);
        Integer pictureTotal = userRecommendMapper.selectPictureOrVideoTotal(userRecommendVo); //有图总数量
        userRecommendVo.setFileFormat(2);
        Integer videoTotal = userRecommendMapper.selectPictureOrVideoTotal(userRecommendVo); //有视频总数量

        JSONObject map = new JSONObject();
        map.put("list", page);
        map.put("alltotal", alltotal);
        map.put("pictureTotal", pictureTotal);
        map.put("videoTotal", videoTotal);
        return map;
    }

    /**
     * 给订单调用, 判断该订单是展示 "去写推荐" or "分享得返现"   1: 待评价  2:已评价待分享
     *
     * @param orderNoList
     * @return
     */
    @Override
    public List<UserRecommendOrder> selectByOrderNo(List<String> orderNoList, Long createrId) {
        logger.info("-->商品推荐,判断该订单是展示 去写推荐 or 分享得返现,用户id:{},订单号:{}", createrId, JSON.toJSONString(orderNoList));
        List<UserRecommendOrder> list = new ArrayList<>();
        //判断用户是否是会员
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, createrId);
        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(createrId);
        logger.info("-->商品推荐,会员信息：{}", JSONObject.toJSONString(userMember));
        if (userMember != null && userMember.getActive()) {  //会员
            for (String orderNo : orderNoList) {
                UserRecommendOrder userRecommendOrder = new UserRecommendOrder();
                userRecommendOrder.setOrderNo(orderNo);
                EntityWrapper<UserRecommend> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("CREATER_ID", createrId);
                entityWrapper.eq("ORDER_NO", orderNo);
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, createrId);
                List<UserRecommend> recommendList = userRecommendMapper.selectList(entityWrapper);
                if (CollectionUtil.isEmpty(recommendList)) { //用户未写评论
                    userRecommendOrder.setStatus(1);
                } else { //用户已写评论
                    OrderFinishGoodVo orderFinishGoodVo = new OrderFinishGoodVo(createrId);
                    orderFinishGoodVo.setOrderNo(orderNo);
                    orderFinishGoodVo.setUserId(createrId);
                    logger.info("-->调用订单服务，根据订单号，查询该订单号关联的商品信息，查询参数：{}", JSONObject.toJSONString(orderFinishGoodVo));
                    List<OrderGoodsDto> orderGoodList = orderGoodFeignClient.get(orderFinishGoodVo).getData();//调用订单服务查询数据,查询这个订单下关联的所有商品
                    logger.info("-->调用订单服务，根据订单号，查询该订单号关联的商品信息，查询结果：{}", JSONObject.toJSONString(orderGoodList));
                    //判断该订单的商品是否全部已经填写过评论
                    flag:
                    if (CollectionUtil.isNotEmpty(orderGoodList)) {
                        for (OrderGoodsDto orderGoodsDto : orderGoodList) {
                            EntityWrapper<UserRecommend> entity = new EntityWrapper<>();
                            entity.eq("CREATER_ID", createrId);
                            entity.eq("ORDER_NO", orderNo);
                            entity.eq("GOOD_ID", orderGoodsDto.getGoodId());
                            entity.eq("GOOD_SKU", orderGoodsDto.getSaleId());
                            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, createrId);
                            logger.info("-->根据条件查询商品推荐，查询参数：{}", JSONObject.toJSONString(entity));
                            List<UserRecommend> recommends = userRecommendMapper.selectList(entity);
                            logger.info("-->根据条件查询商品推荐，查询结果：{}", JSONObject.toJSONString(recommends));
                            if (CollectionUtil.isEmpty(recommends)) {
                                userRecommendOrder.setStatus(1);
                                break flag;
                            }
                            if (orderGoodList.size() == 1) { //只有一个商品,且已经推荐, 返回推荐id
                                userRecommendOrder.setRecommendId(recommends.get(0).getRecommendId());
                            }
                        }
                        userRecommendOrder.setStatus(2);
                    }
                }
                list.add(userRecommendOrder);
            }
        } else {  //不是会员
            for (String orderNo : orderNoList) {
                UserRecommendOrder recommendOrder = new UserRecommendOrder();
                recommendOrder.setOrderNo(orderNo);
                recommendOrder.setStatus(0);
                list.add(recommendOrder);
            }
        }


        return list;
    }


    /**
     * 给订单 调用,判断该商品是展示 "去写推荐" or "分享得返现"
     *
     * @param goodSkuList
     * @param orderNo
     * @param createrId
     * @return
     */
    @Override
    public List<UserRecommendOrder> selectByGoodSku(List<String> goodSkuList, String orderNo, Long createrId) {
        List<UserRecommendOrder> list = new ArrayList<>();
        goodSkuList.forEach(goodSku -> {
            UserRecommendOrder userRecommendOrder = new UserRecommendOrder();
            EntityWrapper<UserRecommend> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("CREATER_ID", createrId);
            entityWrapper.eq("ORDER_NO", orderNo);
            entityWrapper.eq("GOOD_SKU", goodSku);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, createrId);
            List<UserRecommend> recommendList = userRecommendMapper.selectList(entityWrapper);
            if (CollectionUtil.isEmpty(recommendList)) {
                userRecommendOrder.setStatus(1);
                userRecommendOrder.setGoodSku(goodSku);
                userRecommendOrder.setOrderNo(orderNo);
            } else {
                userRecommendOrder.setStatus(2);
                userRecommendOrder.setOrderNo(orderNo);
                userRecommendOrder.setGoodSku(goodSku);
                userRecommendOrder.setRecommendId(recommendList.get(0).getRecommendId());
            }
            list.add(userRecommendOrder);
        });
        return list;
    }

    /**
     * 分享中心， 未填写推荐的订单数量
     *
     * @param createrId
     * @return
     */
    @Override
    public Integer selectNORecommendOrderCont(Long createrId) {
        // 调用订单接口,根据userid,获取该用户的所有订单列表
        UserOrderVo userOrderVo = new UserOrderVo();
        userOrderVo.setUserId(createrId.toString());
        //订单类型为:普通订单, 团购订单, 十元订单, 试用订单, 砍价订单, 零元购, 新客免邮, 其他渠道, 免费送订单且订单状态为已完成的所有订单
        List<OrderInfo> orderInfoList = orderGoodFeignClient.getUserAllOrderNos(userOrderVo).getData();
        logger.info("-->查询订单数量，用户id参数：{},用户所有订单：{}",createrId,orderInfoList);
        List<String> orderList = new ArrayList<>();
        orderInfoList.forEach(orderInfo -> {
            if(orderInfo.getDelFlag() == 1){
                orderList.add(orderInfo.getOrderNo());
            }
        });
        //List<String> orderList = orderInfoList.stream().map(OrderInfo::getOrderNo).collect(Collectors.toList());
        int count = 0;
        List<UserRecommendOrder> list = selectByOrderNo(orderList, createrId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (UserRecommendOrder userRecommendOrder : list) {
                if (userRecommendOrder.getStatus() == 1) {
                    count += 1;
                }
            }
        }
        return count;
    }


    @Override
    public UserRecommendVo selectByRecommendId(Integer recommendId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, SecurityUserUtil.getUserDetails().getUserId());
        UserRecommend userRecommend = userRecommendMapper.selectById(recommendId);
        UserRecommendVo userRecommendVo = JSONObject.parseObject(JSONObject.toJSONString(userRecommend), UserRecommendVo.class);
        //掉订单接口，根据goodSku,订单号且,用户id根据创建时间倒序查询， 获取获取商品类型 和商品价格
        OrderInfoGoodVo orderInfoGoodVo = new OrderInfoGoodVo();
        orderInfoGoodVo.setUserId(userRecommend.getCreaterId());
        orderInfoGoodVo.setGoodId(userRecommend.getGoodId());
        orderInfoGoodVo.setSaleId(Integer.parseInt(userRecommendVo.getGoodSku()));
        orderInfoGoodVo.setOrderNo(userRecommend.getOrderNo());
        List<OrderGood> orderGoodList = orderGoodFeignClient.getOrderInfoByGood(orderInfoGoodVo).getData();
        if (CollectionUtil.isNotEmpty(orderGoodList)) {
            Integer price = orderGoodList.get(0).getGoodPrice().intValue();
            userRecommendVo.setPrice(price.toString());
        }
        Integer orderType = OrderUtils.getOrderType(userRecommend.getOrderNo());
        if (orderType != null && orderType != 0) {
            userRecommendVo.setGoodType(orderType.toString());
        } else {
            OrderInfo orderInfo = orderFeignClient.getOrderByOrderNo(userRecommend.getOrderNo()).getData();
            if (orderInfo != null) {
                userRecommendVo.setGoodType(orderInfo.getOrderType().toString());
            }
        }

        return userRecommendVo;
    }

    @Override
    public Object getRecommendByUserid(Long userId, Integer goodId) {
        JSONObject resultMap = new JSONObject();
        resultMap.put("goodId", goodId);
        resultMap.put("userId", userId);
        //调用订单, 根据商品id,和userid 查询用户是否购买过该商品
        OrderInfoGoodVo orderInfoGoodVo = new OrderInfoGoodVo();
        orderInfoGoodVo.setUserId(userId);
        orderInfoGoodVo.setGoodId(goodId);
        logger.info("-->查询订单商品信息，请求参数：{}", JSONObject.toJSONString(orderInfoGoodVo));
        List<OrderGood> orderGoodList = orderGoodFeignClient.getOrderInfoByGood(orderInfoGoodVo).getData();
        logger.info("-->查询订单商品信息，返回结果：{}", JSONObject.toJSONString(orderGoodList));
        if (CollectionUtil.isEmpty(orderGoodList)) {  //没有购买过该商品
            resultMap.put("isBuy", false);
        } else {  //购买过该商品
            resultMap.put("isBuy", true);
            //获取最新的订单, 判断用户是否评论过该商品
            String orderNo = orderGoodList.get(0).getOrderNo();
            EntityWrapper<UserRecommend> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("CREATER_ID", userId);
            entityWrapper.eq("GOOD_ID", goodId);
            entityWrapper.eq("ORDER_NO", orderNo);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            logger.info("-->查询推荐信息，请求参数：{}", JSONObject.toJSONString(entityWrapper));
            List<UserRecommend> recommends = userRecommendMapper.selectList(entityWrapper);
            logger.info("-->查询推荐信息，返回结果：{}", JSONObject.toJSONString(recommends));
            if (CollectionUtil.isNotEmpty(recommends)) {  //已写推荐
                resultMap.put("isRecommend", true);
            } else {   //未写推荐
                resultMap.put("isRecommend", false);
                //判断用户是否是会员
                UserMember userMember = userMemberService.queryUserMemberInfoByUserId(userId);
                logger.info("-->查询用户会员信息，返回结果：{}", JSONObject.toJSONString(userMember));
                if (userMember != null && userMember.getActive()) {  //会员
                    Integer goodsbaseCount = 0;
                    for (OrderGood orderGood : orderGoodList) {
                        //调用订单模块, 根据订单号,userid 查询订单类型
                        UserOrderVo userOrderVo = new UserOrderVo();
                        userOrderVo.setUserId(userId.toString());
                        userOrderVo.setOrderNo(orderNo);
                        logger.info("-->查询订单信息，请求参数：{}", JSONObject.toJSONString(userOrderVo));
                        OrderInfo orderInfo = orderGoodFeignClient.getOrderInfo(userOrderVo).getData();
                        logger.info("-->查询订单信息，返回结果：{}", JSONObject.toJSONString(orderInfo));
                        //订单类型不是： 抽奖订单， 接力购抽奖，买买金兑换订单 且状态是 已完成
                        int orderType = orderInfo.getOrderType();
                        Integer orderStatus = orderInfo.getOrderStatus();
                        if (orderType != OrderType.LOTTERY && orderType != OrderType.RELAY_LOTTERY && orderType != OrderType.MM_KING && orderStatus == 8) {
                            //查询未填写的商品数量,根据订单号和商品查询
                            EntityWrapper<UserRecommend> entity = new EntityWrapper<>();
                            entity.eq("CREATER_ID", userId);
                            entity.eq("ORDER_NO", orderGood.getOrderNo());
                            entity.eq("GOOD_ID", orderGood.getGoodId());
                            logger.info("-->查询推荐信息，请求参数：{}", JSONObject.toJSONString(entity));
                            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
                            List<UserRecommend> list = userRecommendMapper.selectList(entity);
                            logger.info("-->查询推荐信息，返回结果：{}", JSONObject.toJSONString(list));
                            if (CollectionUtils.isEmpty(list)) {
                                goodsbaseCount += 1;
                            }
                        }
                    }
                    resultMap.put("goodbaseCount", goodsbaseCount);
                    if (goodsbaseCount == 1) {
                        OrderGood orderGood = orderGoodList.get(0);
                        resultMap.put("orderNo", orderGood.getOrderNo());
                        resultMap.put("goodSku", orderGood.getGoodSku());
                    }
                } else {
                    double consumeMoney = orderFeignClient.getConsumeMoney(userId).getData();  //历史消费金额
                    int mmjMemberCumulativeConsumption = memberConfigService.getMmjMemberCumulativeConsumption();  //会员成为会员门槛
                    resultMap.put("money", mmjMemberCumulativeConsumption - consumeMoney);  //距离会员还差多少钱
                }
            }
        }
        return resultMap;
    }

    @Override
    public Object updateByRecommendId(List<UserRecommend> list) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        list.forEach(userRecommend -> {
            userRecommend.setModifyId(userDetails.getUserId());
            userRecommend.setModifyTime(DateUtil.date());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userRecommend.getCreaterId());
            userRecommendMapper.updateById(userRecommend);
        });
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserId(UserMerge userMerge) {
        Long oldUserId = userMerge.getOldUserId();
        Long newUserId = userMerge.getNewUserId();
        EntityWrapper<UserRecommend> userRecommendWrapper = new EntityWrapper<>();
        userRecommendWrapper.eq("CREATER_ID", newUserId);
        List<UserRecommend> recommends = userRecommendMapper.selectList(userRecommendWrapper);
        if (CollectionUtils.isNotEmpty(recommends)) {
            logger.error("-->商品推荐表合并-->用户{}的新ID {}在商品推荐表也有数据，请人工处理", userMerge.getOldUserId(), userMerge.getNewUserId());
            return;
        }

        EntityWrapper<UserRecommend> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("CREATER_ID", oldUserId);
        List<UserRecommend> list = userRecommendMapper.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(list)) {
            logger.info("-->商品推荐表合并-->根据被分享人oldUserId:{}未查到返现信息，不用合并", oldUserId);
        } else {
            userRecommendMapper.updateUserId(oldUserId, newUserId);
            logger.info("-->商品推荐表合并-->t_user_recommend表切换userId, {}改为{}", oldUserId, newUserId);

            userRecommendFileMapper.updateUserId(oldUserId, newUserId);
            logger.info("-->商品推荐表合并-->t_user_recommend_file表切换userId, {}改为{}", oldUserId, newUserId);
        }
    }
}
