package com.mmj.active.homeManagement.service.impl;

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
import com.mmj.active.homeManagement.mapper.WebMaketingMapper;
import com.mmj.active.homeManagement.mapper.WebShowMapper;
import com.mmj.active.homeManagement.model.WebMaketing;
import com.mmj.active.homeManagement.model.WebMaketingEx;
import com.mmj.active.homeManagement.model.WebShow;
import com.mmj.active.homeManagement.model.vo.HomeManagement;
import com.mmj.active.homeManagement.service.WebMaketingService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * <p>
 * 营销配置表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Slf4j
@Service
public class WebMaketingServiceImpl extends ServiceImpl<WebMaketingMapper, WebMaketing> implements WebMaketingService {
    
	private static final String WEB_MAKETING = "webMaketing";
	private static final String MAKET_ID = "MAKET_ID";
	private static final String SHOW_MEMBER = "SHOW_MEMBER";
	private static final String SHOW_OLD = "SHOW_OLD";
	private static final String SHOW_NEW = "SHOW_NEW";
	private static final String USER_ID = "userId";
	private static final String CLASS_CODE = "CLASS_CODE";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String GOOD_CLASS = "GOOD_CLASS";
	
    @Autowired
    private WebMaketingMapper webMaketingMapper;
    @Autowired
    private WebShowMapper webShowMapper;
    @Autowired
    private UserMemberFeignClient userMemberFeignClient;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisUtils redisUtils;
    
    @Override
    public Object save(WebMaketing webMaketing) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if(webMaketing.getMaketId() == null){
            webMaketing.setCreaterId(userDetails.getUserId());
            webMaketing.setCreaterTime(DateUtil.date());
            webMaketingMapper.insert(webMaketing);
        }else{
            webMaketingMapper.updateById(webMaketing);
        }
        
        deleteReids(webMaketing);
        
        JSONObject map = new JSONObject();
        map.put("maketId", webMaketing.getMaketId());
        return map;
    }

    @Override
    public WebMaketingEx query(String classCode) {
        WebMaketingEx webMaketingEx = new WebMaketingEx();
        EntityWrapper<WebMaketing> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq(GOOD_CLASS, classCode);
        entityWrapper.orderBy(ORDER_ID);
        List<WebMaketing> list = webMaketingMapper.selectList(entityWrapper);

        EntityWrapper<WebShow> showentityWrapper = new EntityWrapper<>();
        showentityWrapper.eq(CLASS_CODE, classCode);
        WebShow webShow = webShowMapper.selectList(showentityWrapper).get(0);

        webMaketingEx.setList(list);
        webMaketingEx.setMaketingShow(webShow.getMaketingShow());
        webMaketingEx.setShowId(webShow.getShowId());

        return webMaketingEx;
    }

    @Override
    public WebMaketing selectByMaketId(Integer maketId) {
        return webMaketingMapper.selectById(maketId);
    }

    @Override
    public boolean deleteByMaketId(Integer maketId) {
        WebMaketing webMaketing = webMaketingMapper.selectById(maketId);
        webMaketingMapper.deleteById(maketId);
        deleteReids(webMaketing);   //清除缓存
        log.info("营销管理模块,删除,清楚缓存成功");
        return true;
    }

    /**
     * limit 0：新用户  1：老用户非会员  2：老用户会员
     * @param classCode
     * @param userId
     * @return
     */
    @Override
    public WebMaketingEx selectWebMaketing(String classCode,Long userId) {
        int userIdentity = 1;  //老用户非会员
        //Boolean isNewUser = orderFeignClient.checkNewUser(userid).getData();  //判断新老用户
        Map<String,Object> userMap = new HashMap<String,Object>();
        userMap.put(USER_ID, userId);
        boolean isNewUser = orderFeignClient.checkNewUser(userMap).getData();//false:否（新用户），true:是(老用户)
        log.info("-->首页弹窗,用户id:{},用户是否为新用户:{}",userId,isNewUser);
        UserMember userMember = userMemberFeignClient.queryUserMemberInfoByUserId(userId).getData();  //判断用户是否是会员
        log.info("-->首页弹窗,用户id:{},用户会员信息:{}",userId, JSON.toJSONString(userMember));
        if(!isNewUser){  //新用户
            userIdentity = 0;
        }
        if(isNewUser && userMember != null && userMember.getActive()){  //老用户会员
           userIdentity = 2;
        }

        //判断缓存是否存在
        String key = RedisKey.WEB_MAKETING_key + CommonConstant.Symbol.UNDERLINE + classCode + CommonConstant.Symbol.UNDERLINE + userIdentity;
        Boolean hasKey = redisTemplate.opsForHash().hasKey(RedisKey.WEB_MAKETING_key, key);
        if(hasKey){
            return (WebMaketingEx) redisTemplate.opsForHash().get(RedisKey.WEB_MAKETING_key,key);
        }else{
            WebMaketingEx webMaketingEx = new WebMaketingEx();
            EntityWrapper<WebShow> showentityWrapper = new EntityWrapper<>();
            showentityWrapper.eq(CLASS_CODE, classCode);
            WebShow webShow = webShowMapper.selectList(showentityWrapper).get(0);
            if(webShow.getMaketingShow() != null && webShow.getMaketingShow() == 0){  //关
                webMaketingEx.setShowId(0);
                webMaketingEx.setShowId(webShow.getShowId());
            }

            if(webShow.getMaketingShow() != null && webShow.getMaketingShow() == 1){  //开
                EntityWrapper<WebMaketing> entityWrapper = new EntityWrapper<WebMaketing>();
                entityWrapper.eq(GOOD_CLASS,classCode);
                switch (userIdentity){
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
                entityWrapper.orderBy(ORDER_ID);
                entityWrapper.orderBy(MAKET_ID);
                List<WebMaketing> list = webMaketingMapper.selectList(entityWrapper);
                webMaketingEx.setList(list);
                webMaketingEx.setMaketingShow(1);
                webMaketingEx.setShowId(webShow.getShowId());
            }

            // 添加缓存
            HomeManagement homeManagement = redisUtils.createHomeManagement(WEB_MAKETING, classCode, userIdentity);
            redisUtils.addReids(homeManagement,webMaketingEx);
            return webMaketingEx;
        }
    }

    @Override
    public void deleteReids(WebMaketing webMaketing) {
        // 清除缓存
        HomeManagement homeManagement = redisUtils.createHomeManagement(WEB_MAKETING, webMaketing.getGoodClass(), null);
        redisUtils.deleteRedis(homeManagement);
    }
    
}
