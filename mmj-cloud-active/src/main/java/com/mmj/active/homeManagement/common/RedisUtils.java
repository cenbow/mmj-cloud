package com.mmj.active.homeManagement.common;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.homeManagement.constant.RedisKey;
import com.mmj.active.homeManagement.model.WebShow;
import com.mmj.active.homeManagement.model.vo.HomeManagement;
import com.mmj.active.homeManagement.service.WebShowService;
import com.mmj.common.utils.StringUtils;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private WebShowService webShowService;

    /**
     * 添加缓存
     * limit: 0：新用户  1：老用户非会员  2：老用户会员
     */
    public void addReids(HomeManagement homeManagement, Object object){
        String moduleKey = homeManagement.getModuleKey();
        String classCode = homeManagement.getClassCode();
        Integer limit = homeManagement.getLimit();
        String key = moduleKey+"_"+classCode+"_"+limit;
        //添加缓存
        redisTemplate.opsForHash().put(moduleKey,key,object);

        //添加版本号
        if(limit == 0){  //新用户
            String codeKey = homeManagement.getNewCodeKey()+"_"+classCode;
            redisTemplate.opsForHash().put(moduleKey,codeKey,CodeUtils.getCode());
        }else if(limit == 1){  //老用户非会员
            String codeKey = homeManagement.getOldCodeKey()+"_"+classCode;
            redisTemplate.opsForHash().put(moduleKey,codeKey,CodeUtils.getCode());
        }else if(limit == 2){  //老用户会员
            String codeKey = homeManagement.getMemberCodeKey()+"_"+classCode;
            redisTemplate.opsForHash().put(moduleKey,codeKey,CodeUtils.getCode());
        }
        redisTemplate.expire(moduleKey,2, TimeUnit.HOURS);
    }

    /**
     * 清楚缓存
     * limit: 0：新用户  1：老用户非会员  2：老用户会员
     */
    public void deleteRedis(HomeManagement homeManagement){
        String moduleKey = homeManagement.getModuleKey();
        String classCode = homeManagement.getClassCode();

        //删除用户缓存
        String newUserKey = moduleKey+"_"+classCode+"_"+0;
        redisTemplate.opsForHash().delete(moduleKey,newUserKey);

        String oldUserKey = moduleKey+"_"+classCode+"_"+1;
        redisTemplate.opsForHash().delete(moduleKey,oldUserKey);

        String memberUserKey = moduleKey+"_"+classCode+"_"+2;
        redisTemplate.opsForHash().delete(moduleKey,memberUserKey);

        //修改版本号
        String newKey = homeManagement.getNewCodeKey()+"_"+classCode;
        redisTemplate.opsForHash().put(moduleKey,newKey, CodeUtils.getCode());

        String oldKey = homeManagement.getOldCodeKey()+"_"+classCode;
        redisTemplate.opsForHash().put(moduleKey,oldKey,CodeUtils.getCode());

        String memberKey = homeManagement.getMemberCodeKey()+"_"+classCode;
        redisTemplate.opsForHash().put(moduleKey,memberKey,CodeUtils.getCode());

    }


    /**
     * 获取版本号
     */
    public Map<String, Object> getCodeRedis(HomeManagement homeManagement){
        String moduleKey = homeManagement.getModuleKey();
        String classCode = homeManagement.getClassCode();
        String newCode = (String) redisTemplate.opsForHash().get(moduleKey, homeManagement.getNewCodeKey() +"_" + classCode);
        String oldCode = (String) redisTemplate.opsForHash().get(moduleKey, homeManagement.getOldCodeKey()+"_" + classCode);
        String menberCode = (String) redisTemplate.opsForHash().get(moduleKey, homeManagement.getMemberCodeKey()+"_" + classCode);
        Map<String, Object>  map = new HashMap<>();
        map.put("newCode",newCode);
        map.put("oldCode",oldCode);
        map.put("menberCode",menberCode);
        return map;
    }


    public HomeManagement createHomeManagement(String type , String classCode, Integer limit) {
        HomeManagement homeManagement = new HomeManagement();
        switch (type){
            case "webMaketing":
                homeManagement.setModuleKey(RedisKey.WEB_MAKETING_key);
                homeManagement.setNewCodeKey(RedisKey.NEW_WEB_MAKETING_CODE);
                homeManagement.setOldCodeKey(RedisKey.OLD_WEB_MAKETING_CODE);
                homeManagement.setMemberCodeKey(RedisKey.MEMBER_WEB_MAKETING_CODE);
                break;

            case "webTop":
                homeManagement.setModuleKey(RedisKey.WEB_TOP_KEY);
                homeManagement.setNewCodeKey(RedisKey.NEW_WEB_TOP_CODE);
                homeManagement.setOldCodeKey(RedisKey.OLD_WEB_TOP_CODE);
                homeManagement.setMemberCodeKey(RedisKey.MEMBER_WEB_TOP_CODE);
                break;

            case "webShowcase":
                homeManagement.setModuleKey(RedisKey.WEB_SHOWCASE_KEY);
                homeManagement.setNewCodeKey(RedisKey.NEW_WEB_SHOWCASE_CODE);
                homeManagement.setOldCodeKey(RedisKey.OLD_WEB_SHOWCASE_CODE);
                homeManagement.setMemberCodeKey(RedisKey.MEMBER_WEB_SHOWCASE_CODE);
                break;
        }
        homeManagement.setClassCode(classCode);
        homeManagement.setLimit(limit);
        return homeManagement;
    }

    public void updateWebShowcaseCode() {
        List<WebShow> list = webShowService.selectList(new EntityWrapper<>());
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(webShow -> {
                String newKey = RedisKey.NEW_WEB_SHOWCASE_CODE + "_" + webShow.getClassCode();
                redisTemplate.opsForHash().put(RedisKey.WEB_SHOWCASE_KEY, newKey, CodeUtils.getCode());

                String oldKey = RedisKey.OLD_WEB_SHOWCASE_CODE + "_" + webShow.getClassCode();
                redisTemplate.opsForHash().put(RedisKey.WEB_SHOWCASE_KEY, oldKey, CodeUtils.getCode());

                String memberKey = RedisKey.MEMBER_WEB_SHOWCASE_CODE + "_" + webShow.getClassCode();
                redisTemplate.opsForHash().put(RedisKey.WEB_SHOWCASE_KEY, memberKey, CodeUtils.getCode());
            });
        }
    }


    public void updateUserCode(String userIdentity) {
        List<WebShow> list = webShowService.selectList(new EntityWrapper<>());
        if(CollectionUtil.isNotEmpty(list)){
           if(StringUtils.isNotEmpty(userIdentity) && userIdentity.equals("new")){
               list.forEach(webShow -> {
                   updateNewUserCode(webShow);
                   updateOldUserCode(webShow);
               });
           }

           if(StringUtils.isNotEmpty(userIdentity) && userIdentity.equals("old")){
               list.forEach(webShow -> {
                   updateOldUserCode(webShow);
                   updateMemberUserCode(webShow);
               });
           }

            if(StringUtils.isNotEmpty(userIdentity) && userIdentity.equals("member")){
                list.forEach(webShow -> {
                    updateMemberUserCode(webShow);
                });
            }
        }
    }

    private void updateMemberUserCode(WebShow webShow) {
        String memberWebTopCode = RedisKey.MEMBER_WEB_TOP_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_TOP_KEY, memberWebTopCode, CodeUtils.getCode());

        String memberWebMaketingCode = RedisKey.MEMBER_WEB_MAKETING_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_MAKETING_key, memberWebMaketingCode, CodeUtils.getCode());

        String memberberWebShowcaseCode = RedisKey.MEMBER_WEB_SHOWCASE_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_SHOWCASE_KEY, memberberWebShowcaseCode, CodeUtils.getCode());
    }

    private void updateOldUserCode(WebShow webShow) {
        String oldWebTopCode = RedisKey.OLD_WEB_TOP_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_TOP_KEY, oldWebTopCode, CodeUtils.getCode());

        String oldWebMaketingCode = RedisKey.OLD_WEB_MAKETING_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_MAKETING_key, oldWebMaketingCode, CodeUtils.getCode());

        String oldberWebShowcaseCode = RedisKey.OLD_WEB_SHOWCASE_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_SHOWCASE_KEY, oldberWebShowcaseCode, CodeUtils.getCode());
    }

    private void updateNewUserCode(WebShow webShow) {
        String newWebTopCode = RedisKey.NEW_WEB_TOP_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_TOP_KEY, newWebTopCode, CodeUtils.getCode());

        String newWebMaketingCode = RedisKey.NEW_WEB_MAKETING_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_MAKETING_key, newWebMaketingCode, CodeUtils.getCode());

        String newWebShowcaseCode = RedisKey.NEW_WEB_SHOWCASE_CODE + "_" + webShow.getClassCode();
        redisTemplate.opsForHash().put(RedisKey.WEB_SHOWCASE_KEY, newWebShowcaseCode, CodeUtils.getCode());
    }
}
