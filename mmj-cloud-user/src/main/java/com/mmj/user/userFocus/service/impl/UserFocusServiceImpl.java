package com.mmj.user.userFocus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.UserMerge;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.common.feigin.WxFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.user.manager.model.UserLogin;
import com.mmj.user.manager.service.UserLoginService;
import com.mmj.user.userFocus.constants.UserFocusConstants;
import com.mmj.user.userFocus.mapper.UserFocusMapper;
import com.mmj.user.userFocus.model.UserFocus;
import com.mmj.user.userFocus.service.UserFocusService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户关注公众号记录 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-16
 */
@Service
public class UserFocusServiceImpl extends ServiceImpl<UserFocusMapper, UserFocus> implements UserFocusService {

    private static final Logger logger = LoggerFactory.getLogger(UserFocusServiceImpl.class);

    @Autowired
    private UserFocusMapper userFocusMapper;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private WxFeignClient wxFeignClient;

    @Value("${weChatTmpId.officialAppid}")
    private String officialAppid;//公众号appid

    /**
     * 用户关注公众号
     * @param channel
     * @param openId
     *  channel格式：FP1902_wx77a4d1dccaab522c_1_1
     *
     * module
     * 		1:秒杀    1 关注前       1 关注后
     * 		2:砍价    1 关注前       1 关注后
     * 		3:抽奖    1 活动通知  2 开奖通知  3 领取优惠券     1 关注后活动通知  2 关注后开奖通知
     * 		4:签到    1 签到提醒  2 增加机会                  1 关注后签到提醒
     * 		5:十元三件 1 关注前
     * 		6:转盘    1 签到成功通知  2 转盘十元结果翻倍        1 关注后
     * 		7:店铺订单 1 关注前；								1 关注后
     */
    public void subscribe(String channel, String openId) {
        logger.info("-----------UserFocusServiceImpl-subscribe--" + channel);
        //FP1902_wxade5b7db33565f27_6_2
        String[] s = channel.split("_");
        if (s.length == 4 && openId != null && openId.length() > 0) {
            String appid = s[1];
            Integer module = Integer.valueOf(s[2]);
            Integer type = Integer.valueOf(s[3]);
            //根据openid获取userlogin信息
            UserLogin userLogin = userLoginService.getUserLoginInfoByUserName(openId);
            Long userId;
            if (userLogin != null) {
                userId = userLogin.getUserId();
                if (userId == null) {
                    logger.error("-----------UserFocusServiceImpl-subscribe--用户信息获取失败" + openId);
                    subscribe(openId);
                    return;
                }
            } else {
                logger.error("-----------UserFocusServiceImpl-subscribe--用户信息获取失败" + openId);
                subscribe(openId);
                return;
            }
            //记录关注信息
            EntityWrapper<UserFocus> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("USER_ID", userId);
            entityWrapper.eq("FORM", UserFocusConstants.FocusForm.OFFICIAL);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userId);
            List<UserFocus> userFoci = selectList(entityWrapper);
            UserFocus userFocus;
            if (userFoci != null && !userFoci.isEmpty()) {
                userFocus = userFoci.get(0);
                Integer status = userFocus.getStatus();
                if (status == UserFocusConstants.FocusStatus.NO_FOCUS) {
                    userFocus.setStatus(UserFocusConstants.FocusStatus.FOCUS);
                    userFocus.setReward(UserFocusConstants.FocusReward.GOT);
                } else {
                    userFocus.setStatus(UserFocusConstants.FocusStatus.RE_FOCUS);
                }
                userFocus.setAppId(appid);
                userFocus.setModule(module);
                userFocus.setType(type);
                userFocus.setModifyTime(new Date());
            } else {
                userFocus = new UserFocus();
                userFocus.setAppId(appid);
                userFocus.setModule(module);
                userFocus.setType(type);
                userFocus.setStatus(UserFocusConstants.FocusStatus.FOCUS);
                userFocus.setReward(UserFocusConstants.FocusReward.GOT);
            }
            userFocus.setForm(UserFocusConstants.FocusForm.OFFICIAL);
            userFocus.setUserId(userId);
            userFocus.setOpenId(openId);

            BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userId);
            insertOrUpdate(userFocus);

            if (userFocus.getStatus() == 1) {
                String passingData = userFocus.getPassingData();
                JSONObject param;
                if (passingData == null || passingData.length() == 0) {
                    param = new JSONObject();
                } else {
                    param = JSONObject.parseObject(passingData);
                }
                param.put("userId", userId);
                param.put("openId", openId);
                param.put("module", module);
                param.put("type", type);
                //发送关注模板消息
                logger.info("-----FP1902_MSG-----" + param.toJSONString());
                kafkaTemplate.send(MQTopicConstant.FP1902_MSG, param.toJSONString());

                //打标签
                if (s.length == 4 && openId != null && openId.length() > 0) {
                    String[] tag = getTag(String.valueOf(module), String.valueOf(type));
                    if (tag != null && tag.length != 0) {
                        JSONObject o = new JSONObject();
                        o.put("appid", officialAppid);
                        o.put("openid", openId);
                        o.put("tagNames", tag);
                        try {
                            wxFeignClient.doTag(o.toJSONString());
                        } catch (Exception e) {
                            logger.error("打标签失败：", e);
                        }

                    }
                }
            }
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
        } else {
            logger.error("-----------UserFocusServiceImpl-subscribe--参数错误-----------------");
        }
    }


    /**
     * 取消关注公众号
     * @param openId
     */
    public void unsubscribe(String openId) {
        logger.info("-----------UserFocusServiceImpl-unsubscribe:" + openId);
        if (openId == null || openId.length() == 0) {
            logger.error("取消关注公众号openid为空！");
            return;
        }
        UserLogin userLogin = userLoginService.getUserLoginInfoByUserName(openId);
        Long userId;
        if (userLogin != null) {
            userId = userLogin.getUserId();
            if (userId == null) {
                logger.error("-----------UserFocusServiceImpl-subscribe--用户信息获取失败" + openId);
                subscribe(openId);
                return;
            }
        } else {
            logger.error("-----------UserFocusServiceImpl-subscribe--用户信息获取失败" + openId);
            subscribe(openId);
            return;
        }
        UserFocus userFocus = new UserFocus();
        userFocus.setStatus(UserFocusConstants.FocusStatus.CANCEL_FOCUS);
        EntityWrapper<UserFocus> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("OPEN_ID", openId);
        entityWrapper.eq("FORM", UserFocusConstants.FocusForm.OFFICIAL);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userId);
        update(userFocus, entityWrapper);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,null);
    }

    /**
     * 未知渠道用户关注公众号
     * @param openId
     */
    public void subscribe(String openId){
        logger.info("-----------UserFocusServiceImpl-subscribe--" + openId);
        if (openId == null || openId.length() == 0) {
            logger.error("未知渠道用户关注公众号openid为空！");
            return;
        }
        //根据openid获取userlogin信息
        UserLogin userLogin = userLoginService.getUserLoginInfoByUserName(openId);
        Long userId;
        String appid;
        if (userLogin != null) {
            userId = userLogin.getUserId();
            appid = userLogin.getAppId();
            if (userId == null || appid == null || appid.isEmpty()) {
                return;
            }
        } else {
            return;
        }
        EntityWrapper<UserFocus> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("OPEN_ID", openId);
        entityWrapper.eq("FORM", UserFocusConstants.FocusForm.OFFICIAL);
        List<UserFocus> userFoci = selectList(entityWrapper);
        UserFocus userFocus;
        if (userFoci != null && !userFoci.isEmpty()) {
            userFocus = userFoci.get(0);
            userFocus.setStatus(UserFocusConstants.FocusStatus.FOCUS);
            userFocus.setModule(99);
            userFocus.setType(0);
        } else {
            userFocus = new UserFocus();
            userFocus.setOpenId(openId);
            userFocus.setAppId(appid);
            userFocus.setModule(99);
            userFocus.setType(0);
            userFocus.setStatus(UserFocusConstants.FocusStatus.FOCUS);
            userFocus.setReward(UserFocusConstants.FocusReward.NOT_GET);
        }
        userFocus.setForm(UserFocusConstants.FocusForm.OFFICIAL);
        userFocus.setUserId(userId);
        insertOrUpdate(userFocus);
    }

    public void sync(Integer module, Integer type) {
        int pageSize = 50;
        int currentPage = 1;
        Page<UserFocus> page = new Page<>(currentPage, pageSize);
        List<UserFocus> userFoci = userFocusMapper.queryList(page, module, type);
        if (userFoci != null && !userFoci.isEmpty()) {
            kafkaTemplate.send(MQTopicConstant.SYNC_USER_FOCUS_INFO, JSON.toJSONString(userFoci));

            //分页处理
            int total = page.getTotal();
            if (total <= pageSize) {
                return;
            } else {
                int pageCount = total/pageSize;
                if (total > pageCount * pageSize) {
                    pageCount++;
                }
                for (int i = 2; i <= pageCount; i++) {
                    page = new Page<>(i, pageSize);
                    List<UserFocus> userFocusList = userFocusMapper.queryList(page, module, type);
                    kafkaTemplate.send(MQTopicConstant.SYNC_USER_FOCUS_INFO, JSON.toJSONString(userFocusList));
                }
            }
        }
    }

    public String[] getTag(String module, String type) {
        String tag = "";
        switch (module){
            case "1": tag="活动、秒杀提醒"; break;
            case "2": tag="活动、砍价进度"; break;
            case "3":
                if("1".equals(type)){
                    tag="活动、抽奖通知"; break;
                }else if("2".equals(type)){
                    tag="活动"; break;
                }
            case "4":
                if("1".equals(type)){
                    tag="活动、签到提醒"; break;
                }else if("2".equals(type)){
                    tag="活动、签到"; break;
                }
            case "5": tag="活动"; break;
            case "6":
                if("1".equals(type)){
                    tag="转盘签到、转盘"; break;
                }else if("2".equals(type)){
                    tag="转盘"; break;
                };
            case "7": tag="订单物流"; break;
        }
        if (tag != null && tag.length() != 0) {
            String[] split = tag.split("、");
            return split;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserID(UserMerge userMerge) {
        long oldUserId = userMerge.getOldUserId();
        long newUserId = userMerge.getNewUserId();
        logger.info("-->流量池表合并-->oldUserId:{}, newUserId:{}", oldUserId, newUserId);
        if (oldUserId == newUserId) {
            logger.info("-->流量池表合并-->新旧userId相等，不用合并");
            return;
        }

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        Wrapper<UserFocus> wrapper = new EntityWrapper<>();
        wrapper.eq("USER_ID", oldUserId);
        UserFocus um = this.selectOne(wrapper);
        if (um == null) {
            logger.info("-->流量池表合并-->根据oldUserId:{}未查到会员信息，不用合并", oldUserId);
            return;
        }
        // 判断是否需要切换表
        int oldTableIndex = (int) (oldUserId % 10);
        int newTableIndex = (int) (newUserId % 10);
        logger.info("-->流量池表合并-->oldUserId:{}所在表t_user_focus_{}，newUserId:{}所在表t_user_focus_{}", oldUserId, oldTableIndex, newUserId, newTableIndex);
        if (oldTableIndex != newTableIndex) {
            // 需要切换表
            // 1.插入到新表
            um.setUserId(newUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newUserId);
            this.insert(um);
            // 2.删除旧数据
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
            this.delete(wrapper);
        } else {
            // 两个userId在同一张表，直接修改userId
            logger.info("-->流量池表合并-->新旧ID都在同一张表：t_user_focus_{}，直接修改用户ID：{}为{}", oldTableIndex, oldUserId, newUserId);
            EntityWrapper<UserFocus> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("USER_ID", userMerge.getOldUserId());
            UserFocus userFocus = new UserFocus();
            userFocus.setUserId(userMerge.getNewUserId());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
            update(userFocus, entityWrapper);
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
    }

}
