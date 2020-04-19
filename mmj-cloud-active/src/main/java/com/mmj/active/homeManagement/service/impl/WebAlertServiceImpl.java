package com.mmj.active.homeManagement.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.mmj.active.common.feigin.*;
import com.mmj.active.homeManagement.model.WebAlertEX;
import com.mmj.common.model.*;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.model.UserMember;
import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.active.homeManagement.common.TimeUtils;
import com.mmj.active.homeManagement.constant.RedisKey;
import com.mmj.active.homeManagement.constant.WebAlertConstant;
import com.mmj.active.homeManagement.mapper.WebAlertMapper;
import com.mmj.active.homeManagement.model.WebAlert;
import com.mmj.active.homeManagement.service.WebAlertService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 弹窗管理 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Slf4j
@Service
public class WebAlertServiceImpl extends ServiceImpl<WebAlertMapper, WebAlert> implements WebAlertService {
	
    private static final String ACTIVITY_FINISHED = "该活动已结束";
	private static final String FAILED = "failed";
	private static final String MMJ_USER_NEWUSER_TOPIC = "mmj.user.newuser.topic";
	private static final String INDEX = "INDEX";
	private static final String RECIEVED_SUCCESS_DESC = "领取成功，可到【我的-我的优惠券】查看";
	private static final String SUCCESS = "success";
	private static final String ALL_COLLECTED_DESC = "该优惠券已被领完！";
	private static final String RECIEVED_DESC = "您已经领取过，快去使用吧！";
    private static final String COUPON_USER_DESC = "您已使用该优惠券";
    private static final String COUPON_UNCLAIMED = "未领取优惠券";
	private static final String OK = "ok";
	private static final String USER_ID = "userId";
	private static final String CREATER_TIME_DESC = "CREATER_TIME desc";
	private static final String APPALERT = "APPALERT";
	private static final String PAGE = "PAGE";
	private static final String END_TIME = "END_TIME";
	private static final String START_TIME = "START_TIME";
	private static final String ALERT_ORDER = "ALERT_ORDER";
	private static final String SHOW_MEMBER = "SHOW_MEMBER";
	private static final String SHOW_OLD = "SHOW_OLD";
	private static final String SHOW_NEW = "SHOW_NEW";
	private static final String ALERT = "ALERT";
	private static final String ALERT_STATUS = "ALERT_STATUS";
	private static final String ALERT_TYPE = "ALERT_TYPE";
    private static final String APP_CODE = "APP_CODE";
	
	@Autowired
    private WebAlertMapper webAlertMapper;
    @Autowired
    private CouponInfoService couponInfoService;
    @Autowired
    private CouponUserFeignClient couponUserFeignClient;
    @Autowired
    private UserMemberFeignClient userMemberFeignClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private NoticeFeignClient noticeFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public ReturnData<Object> save(WebAlert webAlert) {
        ReturnData<Object> rd = new ReturnData<>();
        if (StringUtils.isEmpty(webAlert.getAlertType())) {
            rd.setCode(SecurityConstants.FAIL_CODE);
            rd.setDesc("参数为空");
            return rd;
        }
        if (ALERT.equals(webAlert.getAlertType())) { //弹窗
            if (StringUtils.isEmpty(webAlert.getAlertName()) || StringUtils.isEmpty(webAlert.getShowNew().toString()) || StringUtils.isEmpty(webAlert.getShowOld().toString()) ||
                    StringUtils.isEmpty(webAlert.getShowMember().toString()) || StringUtils.isEmpty(webAlert.getAlertImage()) || StringUtils.isEmpty(webAlert.getAlertStatus().toString())) {
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc("参数为空");
                return rd;
            }
            ///新需求允许新老用户有多个弹窗 所以注释掉了
            /*if(1 == webAlert.getAlertStatus()){
                EntityWrapper<WebAlert> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("ALERT_STATUS",1);
                entityWrapper.eq("ALERT_TYPE","ALERT");
                List<WebAlert> list = webAlertMapper.selectList(entityWrapper);
                if(CollectionUtil.isNotEmpty(list)){
                    //生效配置,不管是新用户还是老用户只能有一个是生效的
                    rd.setCode(SecurityConstants.FAIL_CODE);
                    rd.setDesc("生效用户配置冲突");
                    return rd;
                }
            }*/

            if (WebAlertConstant.JUMP_TYPE_COUPON.equals(webAlert.getHrafType())) {
                if (StringUtils.isEmpty(webAlert.getCouponId())) {
                    rd.setCode(SecurityConstants.FAIL_CODE);
                    rd.setDesc("跳转类型，参数为空");
                    return rd;
                }
            }

            if (WebAlertConstant.JUMP_TYPE_LINK.equals(webAlert.getHrafType())) {
                if (StringUtils.isEmpty(webAlert.getHrafUrl())) {
                    rd.setCode(SecurityConstants.FAIL_CODE);
                    rd.setDesc("跳转类型，参数为空");
                    return rd;
                }
            }
        } else if (PAGE.equals(webAlert.getAlertType())) {  //浮层
            if (StringUtils.isEmpty(webAlert.getAlertName()) || StringUtils.isEmpty(webAlert.getStartTime().toString()) || StringUtils.isEmpty(webAlert.getEndTime().toString())
                    || StringUtils.isEmpty(webAlert.getTimeLong().toString()) || StringUtils.isEmpty(webAlert.getAlertDesc())) {
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc("参数为空");
                return rd;
            }
            Date startTime = webAlert.getStartTime();
            Date endTime = webAlert.getEndTime();
            if (startTime.getTime() > endTime.getTime()) {
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc("开始时间不能大于结束时间");
                return rd;
            }

            EntityWrapper<WebAlert> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq(ALERT_TYPE, PAGE);
            if (webAlert.getAlertId() != null) {
                entityWrapper.ne("ALERT_ID", webAlert.getAlertId());
            }
            List<WebAlert> list = webAlertMapper.selectList(entityWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                for (WebAlert alert : list) {
                    if (TimeUtils.havaSame(startTime, endTime, alert.getStartTime(), alert.getEndTime())) {
                        rd.setCode(SecurityConstants.FAIL_CODE);
                        rd.setDesc("时间配置冲突");
                        return rd;

                    }
                }
            }
        } else if (APPALERT.equals(webAlert.getAlertType())) {   //app弹窗
            if (StringUtils.isEmpty(webAlert.getAlertName()) || StringUtils.isEmpty(webAlert.getAlertImage()) || StringUtils.isEmpty(webAlert.getAlertStatus().toString())) {
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc("参数为空");
                return rd;
            }

            if (WebAlertConstant.JUMP_TYPE_COUPON.equals(webAlert.getHrafType())) {
                if (StringUtils.isEmpty(webAlert.getCouponId())) {
                    rd.setCode(SecurityConstants.FAIL_CODE);
                    rd.setDesc("跳转类型，参数为空");
                    return rd;
                }
            }

            if (StringUtil.isEmpty(webAlert.getCouponId())) {
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc("优惠券，参数为空");
                return rd;
            }
        }
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        webAlert.setCreaterId(userDetails.getUserId());
        if (webAlert.getAlertId() == null) {
            webAlertMapper.insert(webAlert);
        } else {
            webAlertMapper.updateById(webAlert);
        }
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc("保存成功");
        return rd;

    }


    @Override
    public Page<WebAlert> query(WebAlert webAlert) {
        Page<WebAlert> page = new Page<>(webAlert.getCurrentPage(), webAlert.getPageSize());
        EntityWrapper<WebAlert> wrapper = new EntityWrapper<>();
        wrapper.eq(ALERT_TYPE, webAlert.getAlertType());
        wrapper.orderBy(CREATER_TIME_DESC);
        List<WebAlert> list = webAlertMapper.selectPage(page, wrapper);
        page.setRecords(list);
        return page;
    }


    @Override
    public Map<String, Object> selectWebAlert(long userId, String source) {
        Map<String, Object> map = new HashMap<String, Object>();
        int userIdentity = 1;  //老用户非会员
        Map<String, Object> userMap = new HashMap<String, Object>(1);
        userMap.put(USER_ID, userId);
        boolean isOldUser = orderFeignClient.checkNewUser(userMap).getData();//false:否（新用户），true:是(老用户)
        log.info("-->首页弹窗,用户id:{},用户是否为新用户:{}", userId, isOldUser);
        UserMember userMember = userMemberFeignClient.queryUserMemberInfoByUserId(userId).getData();  // 判断用户是否是会员
        boolean isMember = userMember != null && userMember.getActive();
        log.info("-->首页弹窗,用户id:{},用户是否会员:{}", userId, isMember);
        if (!isOldUser) {  //新用户
            userIdentity = 0;
        }
        if (isOldUser && isMember) {  //老用户会员
            userIdentity = 2;
        }
        EntityWrapper<WebAlert> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq(ALERT_TYPE, ALERT);
        entityWrapper.eq(ALERT_STATUS, 1);
        switch (userIdentity) {
            case 0:
                entityWrapper.eq(SHOW_NEW, 1);
                break;
            case 1:
                entityWrapper.eq(SHOW_OLD, 1);
                break;
            case 2:
                entityWrapper.eq(SHOW_MEMBER, 1);
                break;
        }
        entityWrapper.orderBy(ALERT_ORDER);
        List<WebAlert> webAlertList = webAlertMapper.selectList(entityWrapper);

        List<WebAlert> alertList = new ArrayList<WebAlert>();
        webAlertList.forEach(webAlert -> {
            Boolean hasKey = redisTemplate.opsForHash().hasKey(RedisKey.WEBALERT_REDIS_KEY, userId + CommonConstant.Symbol.UNDERLINE + webAlert.getAlertId());
            if (!hasKey) {
                alertList.add(webAlert);
            }
        });

        map.put(ALERT, alertList);
        EntityWrapper<WebAlert> wrapper = new EntityWrapper<>();
        wrapper.eq(ALERT_TYPE, PAGE);
        wrapper.le(START_TIME, new Date());
        wrapper.ge(END_TIME, new Date());
        wrapper.orderBy(ALERT_ORDER);
        List<WebAlert> pageList = webAlertMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(pageList)) {
            map.put(PAGE, pageList.get(0));  //浮层只会有一个有效的
        }
        return map;
    }


    @Override
    public ReturnData<Object> clickWebAlert(Integer alertId, Long userId) {
        ReturnData<Object> rd = new ReturnData<>();
        WebAlert webAlert = webAlertMapper.selectById(alertId);
        if (webAlert == null || webAlert.getAlertStatus() == 0) {
            rd.setCode(SecurityConstants.FAIL_CODE);
            rd.setDesc(ACTIVITY_FINISHED);
            return rd;
        }
        if (WebAlertConstant.JUMP_TYPE_LINK.equals(webAlert.getHrafType())) {  //跳转链接
            redisTemplate.opsForHash().put(RedisKey.WEBALERT_REDIS_KEY, userId + CommonConstant.Symbol.UNDERLINE + alertId, "1");
            rd.setCode(SecurityConstants.SUCCESS_CODE);
            rd.setDesc(OK);
            return rd;   //点击跳转链接成功
        } else {  //优惠券
            Boolean hasKey = redisTemplate.opsForHash().hasKey(RedisKey.WEBALERT_REDIS_KEY, userId + CommonConstant.Symbol.UNDERLINE + alertId);
            if (hasKey) {  //已领取
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc(RECIEVED_DESC);
                return rd;
            }

            List<Integer> couponIds = new ArrayList<>();
            String couponId = webAlert.getCouponId();
            if (StringUtils.isNotEmpty(couponId)) {
                String[] split = couponId.split(CommonConstant.Symbol.COMMA);
                for (String id : split) {
                    couponIds.add(Integer.parseInt(id));
                }
            }
            List<CouponInfo> couponList = couponInfoService.batchCouponInfos(couponIds);  // 根据优惠券id，查询剩余数量
            if (CollectionUtil.isNotEmpty(couponList)) {
                for (CouponInfo couponInfo : couponList) {  //遍历优惠券list
                    int countNum = Objects.isNull(couponInfo.getCountNum()) ? 0 : couponInfo.getCountNum();
                    int totalSendNum = Objects.isNull(couponInfo.getTotalSendNumber()) ? 0 : couponInfo.getTotalSendNumber();
                    if (countNum >= 0 && countNum <= totalSendNum) {
                        rd.setCode(SecurityConstants.FAIL_CODE);
                        rd.setDesc(ALL_COLLECTED_DESC);
                        webAlert.setAlertStatus(0);
                        webAlertMapper.updateById(webAlert);   //优惠券已过期， 修改该弹窗为禁用
                        return rd;
                    }
                }
            }
            String result = sendCoupon(userId, webAlert);
            if (SUCCESS.equals(result)) {
                rd.setCode(SecurityConstants.SUCCESS_CODE);
                rd.setDesc(RECIEVED_SUCCESS_DESC);
            } else {
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc(RECIEVED_DESC);
            }
        }
        return rd;
    }


    @Transactional
    public String sendCoupon(Long userId, WebAlert webAlert) {
        List<String> list = new ArrayList<>();
        String couponId = webAlert.getCouponId();
        String[] ids = couponId.split(CommonConstant.Symbol.COMMA);
        for (String id : ids) {
            UserCouponVo userCouponVo = new UserCouponVo();
            userCouponVo.setCouponId(Integer.parseInt(id));
            userCouponVo.setUserId(userId);
            userCouponVo.setCouponSource(INDEX);
            Boolean hasCollected = couponUserFeignClient.hasReceive(userCouponVo).getData();  // 判断用户是否已经领取过该优惠券，在别处领取
            if (!hasCollected) {   //没领取优惠券
                userCouponVo.setCouponSource(INDEX);
                couponUserFeignClient.receive(userCouponVo);  // 发用优惠券
                list.add(id);
            }
        }
        //发送成功，添加到缓存中 表示领取成功(如果有多张优惠券，部分是已领取过，此次只领取了其中一张，也是领取成功)
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(id -> {
                redisTemplate.opsForHash().put(RedisKey.WEBALERT_REDIS_KEY, userId + CommonConstant.Symbol.UNDERLINE + webAlert.getAlertId(), id);
            });
            return SUCCESS;
        }
        return FAILED;
    }

    @Override
    public WebAlert selectByAlertId(Integer alertId) {
        return webAlertMapper.selectById(alertId);
    }

    @Override
    public BaseDict selectNewUsreTopic() {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put(USER_ID, userId);
        log.info("-->新用户专题落地页,用户id:{}", userId);
        boolean isNewUser = orderFeignClient.checkNewUser(userMap).getData();//false:否（新用户），true:是(老用户)
        log.info("-->新用户专题落地页,用户是否为新用户,用户id:{},用户身份:{}",userId, isNewUser);
        return !isNewUser ? noticeFeignClient.queryGlobalConfigByDictCode(MMJ_USER_NEWUSER_TOPIC).getData() : null;
    }


    @Override
    public BaseDict queryNewUserTopic(String dictCode) {
        return noticeFeignClient.queryGlobalConfigByDictCode(dictCode).getData();
    }

    @Override
    public List<WebAlertEX> selectWebAlertByApp(HttpServletRequest request) {
        EntityWrapper<WebAlert> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq(ALERT_TYPE, APPALERT);
        entityWrapper.eq(ALERT_STATUS, 1);
        entityWrapper.orderBy(ALERT_ORDER);
        List<WebAlert> appAlerList = webAlertMapper.selectList(entityWrapper);
        String userid = request.getHeader(SecurityConstants.USER_ID);
        List<WebAlertEX> resultList = new ArrayList<>();
        log.info("app弹窗header打印:{}", userid);
        if (StringUtils.isEmpty(userid)) {
            appAlerList.forEach(webAlert -> {
                WebAlertEX webAlertEX = JSON.parseObject(JSON.toJSONString(webAlert), WebAlertEX.class);
                webAlertEX.setCouponStatus(0);
                webAlertEX.setCouponDesc(COUPON_UNCLAIMED);
                resultList.add(webAlertEX);
            });
            return resultList;
        }
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("--> app弹窗查询,用户id:{}",userId);
        if(CollectionUtil.isNotEmpty(appAlerList)){  //判断优惠券是否已经领取过
            appAlerList.forEach(webAlert -> {
                WebAlertEX webAlertEX = JSON.parseObject(JSON.toJSONString(webAlert), WebAlertEX.class);
                if (webAlertEX == null || webAlertEX.getAlertStatus() == 0) {
                    webAlertEX.setCouponStatus(4);
                    webAlertEX.setCouponDesc(ACTIVITY_FINISHED);
                }
                List<Integer> couponIds = new ArrayList<>();
                String couponId = webAlert.getCouponId();
                if (StringUtils.isNotEmpty(couponId)) {
                    String[] split = couponId.split(CommonConstant.Symbol.COMMA);
                    for (String id : split) {
                        couponIds.add(Integer.parseInt(id));
                    }
                }
                List<CouponInfo> couponList = couponInfoService.batchCouponInfos(couponIds);  // 根据优惠券id，查询剩余数量
                if (CollectionUtil.isNotEmpty(couponList)) {
                    for (CouponInfo couponInfo : couponList) {  //遍历优惠券list
                        int countNum = Objects.isNull(couponInfo.getCountNum()) ? 0 : couponInfo.getCountNum();
                        int totalSendNum = Objects.isNull(couponInfo.getTotalSendNumber()) ? 0 : couponInfo.getTotalSendNumber();
                        if (countNum >= 0 && countNum <= totalSendNum) {
                            webAlertEX.setCouponStatus(3);
                            webAlertEX.setCouponDesc(ALL_COLLECTED_DESC);
                            webAlert.setAlertStatus(0);
                            webAlertMapper.updateById(webAlert);   //优惠券已过期， 修改该弹窗为禁用
                        }
                        UserCouponVo userCouponVo = new UserCouponVo();
                        userCouponVo.setCouponId(couponInfo.getCouponId());
                        userCouponVo.setUserId(userId);
                        userCouponVo.setCouponSource(APP_CODE);
                        Boolean hasCollected = couponUserFeignClient.hasReceive(userCouponVo).getData();  // 判断用户是否已经领取过该优惠券，在别处领取
                        if(hasCollected){   //已经领取优惠券
                            List<UserCouponDto> userCouponList = userFeignClient.myCouponInfoByCouponId(couponInfo.getCouponId()).getData();  //判断优惠券是否已经使用
                            log.info("app弹窗优惠券状态查询结果{}", JSON.toJSONString(userCouponList));
                            if(CollectionUtil.isNotEmpty(userCouponList)){
                                UserCouponDto userCouponDto = userCouponList.get(0);
                                Integer usedFlag = userCouponDto.getUsedFlag();
                                if(usedFlag == 0){  //未使用
                                    webAlertEX.setCouponStatus(1);
                                    webAlertEX.setCouponDesc(RECIEVED_DESC);
                                }else { //已经使用
                                    webAlertEX.setCouponStatus(2);
                                    webAlertEX.setCouponDesc(COUPON_USER_DESC);
                                }
                            }
                        }else{
                            webAlertEX.setCouponStatus(0);
                            webAlertEX.setCouponDesc(COUPON_UNCLAIMED);
                        }
                    }
                }
                resultList.add(webAlertEX);
            });
        }
        return resultList;
    }

    @Override
    public ReturnData<Object> clickWebAlertByApp(Integer alertId, Long userId) {
        ReturnData<Object> rd = new ReturnData<>();
        WebAlert webAlert = webAlertMapper.selectById(alertId);
        String couponId = webAlert.getCouponId();
        if (StringUtils.isNotEmpty(couponId)) {
            String[] split = couponId.split(CommonConstant.Symbol.COMMA);
            for (String id : split) {
                UserCouponVo userCouponVo = new UserCouponVo();
                userCouponVo.setCouponId(Integer.parseInt(id));
                userCouponVo.setUserId(userId);
                userCouponVo.setCouponSource(APP_CODE);
                couponUserFeignClient.receive(userCouponVo);
                rd.setCode(SecurityConstants.SUCCESS_CODE);
                rd.setDesc(RECIEVED_SUCCESS_DESC);
            }
        }
        return rd;
    }
}
