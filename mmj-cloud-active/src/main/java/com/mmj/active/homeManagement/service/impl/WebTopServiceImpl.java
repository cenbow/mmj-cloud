package com.mmj.active.homeManagement.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.feigin.UserMemberFeignClient;
import com.mmj.active.common.model.UserMember;
import com.mmj.active.homeManagement.common.RedisUtils;
import com.mmj.active.homeManagement.constant.RedisKey;
import com.mmj.active.homeManagement.mapper.WebShowMapper;
import com.mmj.active.homeManagement.mapper.WebTopMapper;
import com.mmj.active.homeManagement.model.WebShow;
import com.mmj.active.homeManagement.model.WebTop;
import com.mmj.active.homeManagement.model.WebTopEx;
import com.mmj.active.homeManagement.model.vo.HomeManagement;
import com.mmj.active.homeManagement.service.WebTopService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * <p>
 * 顶部配置表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Slf4j
@Service
public class WebTopServiceImpl extends ServiceImpl<WebTopMapper, WebTop> implements WebTopService {
    private static final String WEB_TOP = "webTop";
	private static final String TOP_ID2 = "TOP_ID";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String SHOW_MEMBER = "SHOW_MEMBER";
	private static final String SHOW_OLD = "SHOW_OLD";
	private static final String SHOW_NEW = "SHOW_NEW";
	private static final String GOOD_CLASS = "GOOD_CLASS";
	private static final String CLASS_CODE = "CLASS_CODE";
	private static final String TOP_ID = "topId";
	@Autowired
    private WebTopMapper webTopMapper;
    @Autowired
    private WebShowMapper webShowMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private UserMemberFeignClient userMemberFeignClient;

    @Override
    public Object save(WebTop entity) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if(entity.getTopId() == null){
            entity.setCreaterId(userDetails.getUserId());
            entity.setCreaterTime(DateUtil.date());
            webTopMapper.insert(entity);
        }else{
            webTopMapper.updateById(entity);
        }
        JSONObject map = new JSONObject();
        map.put(TOP_ID, entity.getTopId());

        deleteReids(entity);  //清除缓存
        log.info("顶部大图,保存,删除缓存成功");
        return map;
    }

    @Override
    public WebTopEx selectWebTop(String classCode,Long userid) {
        int userIdentity = 1;  //老用户,非会员
        //Boolean isNewUser = orderFeignClient.checkNewUser(userid).getData();  //判断新老用户
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("userId",userid);
        boolean isNewUser = orderFeignClient.checkNewUser(userMap).getData();//false:否（新用户），true:是(老用户)
        log.info("-->首页弹窗,用户id:{},用户是否为新用户:{}",userid,isNewUser);
        UserMember userMember = userMemberFeignClient.queryUserMemberInfoByUserId(userid).getData();  //判断用户是否是会员
        log.info("-->首页弹窗,用户id:{},用户会员信息:{}",userid, JSON.toJSONString(userMember));
        if(!isNewUser){  //新用户
            userIdentity = 0;
        }
        if(isNewUser && userMember != null && userMember.getActive()){  //老用户,会员
            userIdentity = 2;
        }
        //判断缓存中是否存在keys
        String key = RedisKey.WEB_TOP_KEY + CommonConstant.Symbol.UNDERLINE + classCode + CommonConstant.Symbol.UNDERLINE + userIdentity;
        Boolean hasKey = redisTemplate.opsForHash().hasKey(RedisKey.WEB_TOP_KEY, key);
        if(hasKey){
            return (WebTopEx) redisTemplate.opsForHash().get(RedisKey.WEB_TOP_KEY, key);
        }else{
            WebTopEx webTopEx = new WebTopEx();
            EntityWrapper<WebShow> webShowEntityWrapper = new EntityWrapper<>();
            webShowEntityWrapper.eq(CLASS_CODE, classCode);
            WebShow webShow = webShowMapper.selectList(webShowEntityWrapper).get(0);
            if(webShow.getTopShow() != null && webShow.getTopShow() == 0){
                webTopEx.setTopShow(0);
                webTopEx.setShowId(webShow.getShowId());
            }
            if(webShow.getTopShow() != null && webShow.getTopShow() == 1){
                EntityWrapper<WebTop> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq(GOOD_CLASS, classCode);
                switch (userIdentity){
                    case 0:
                        entityWrapper.eq(SHOW_NEW, 1);
                        break;
                    case 1:
                        entityWrapper.eq(SHOW_OLD, 1);
                        break;
                    case 2:
                        entityWrapper.eq(SHOW_MEMBER,1);
                        break;
                }
                entityWrapper.orderBy(ORDER_ID);
                entityWrapper.orderBy(TOP_ID2);
                List<WebTop> list = webTopMapper.selectList(entityWrapper);
                if(CollectionUtil.isNotEmpty(list)){
                    for (int i = 0; i < list.size(); i++) {
                        WebTop webTop = list.get(i);
                        Integer foreverFlag = webTop.getForeverFlag();
                        Date startTime = webTop.getStartTime();
                        Date endTime = webTop.getEndTime();
                        Date newTime = new Date();
                        if(foreverFlag == 0 && startTime != null && endTime != null){
                            if((startTime.getTime() > newTime.getTime()) || (endTime.getTime() < newTime.getTime())){
                                list.remove(i);
                                i--;
                            }
                        }
                    }
                }
                webTopEx.setTopShow(1);
                webTopEx.setShowId(webShow.getShowId());
                webTopEx.setWebTopExList(list);
            }
            //添加缓存
            HomeManagement homeManagement = redisUtils.createHomeManagement(WEB_TOP, classCode, userIdentity);
            redisUtils.addReids(homeManagement,webTopEx);
            return webTopEx;
        }
    }


    @Override
    public WebTopEx query(String classCode) {
        WebTopEx webTopEx = new WebTopEx();
        EntityWrapper<WebTop> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq(GOOD_CLASS,classCode);
        entityWrapper.orderBy(ORDER_ID);
        List<WebTop> list = webTopMapper.selectList(entityWrapper);

        EntityWrapper<WebShow> webShowEntityWrapper = new EntityWrapper<>();
        webShowEntityWrapper.eq(CLASS_CODE,classCode);
        WebShow webShow = webShowMapper.selectList(webShowEntityWrapper).get(0);

        webTopEx.setTopShow(webShow.getTopShow());
        webTopEx.setShowId(webShow.getShowId());
        webTopEx.setWebTopExList(list);
        return webTopEx;
    }

    @Override
    public boolean deleteByTopId(Integer topId) {
        WebTop webTop = webTopMapper.selectById(topId);
        webTopMapper.deleteById(topId);
        deleteReids(webTop); //清楚缓存
        log.info("顶部大图,删除,删除缓存成功");
        return true;
    }


    public void deleteReids(WebTop entity) {
        HomeManagement homeManagement = redisUtils.createHomeManagement(WEB_TOP, entity.getGoodClass(), null);
        redisUtils.deleteRedis(homeManagement);
    }

}
