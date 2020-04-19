package com.mmj.active.homeManagement.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.homeManagement.mapper.WebShowMapper;
import com.mmj.active.homeManagement.mapper.WebWxshardMapper;
import com.mmj.active.homeManagement.model.WebShow;
import com.mmj.active.homeManagement.model.WebWxshard;
import com.mmj.active.homeManagement.model.WebWxshardEx;
import com.mmj.active.homeManagement.service.WebWxshardService;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.date.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 小程序分享配置 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Service
public class WebWxshardServiceImpl extends ServiceImpl<WebWxshardMapper, WebWxshard> implements WebWxshardService {
    private static final String WEB_WXSHARD2 = "WebWxshard";
	private static final String WEB_WXSHARD = "webWxshard";
	private static final String SHARD_ID = "shardId";
	private static final String GOOD_CLASS = "GOOD_CLASS";
	private static final String CLASS_CODE = "CLASS_CODE";
	private static final String WXSHARD_SHOW = "wxshardShow";
	private static final String SHOW_ID = "showId";
	@Autowired
    private WebWxshardMapper webWxshardMapper;
    @Autowired
    private WebShowMapper webShowMapper;

    @Override
    public Map<String, Object> selectWebWxshard(String classCode) {
        JSONObject map = new JSONObject();
        EntityWrapper<WebShow> webShowEntityWrapper = new EntityWrapper<WebShow>();
        webShowEntityWrapper.eq(CLASS_CODE, classCode);
        WebShow webShow = webShowMapper.selectList(webShowEntityWrapper).get(0);
        if(webShow.getWxshardShow() != null && webShow.getWxshardShow() == 0){
            map.put(SHOW_ID,webShow.getShowId());
            map.put(WXSHARD_SHOW, 0);
        }

        if(webShow.getWxshardShow() != null && webShow.getWxshardShow() == 1){
            EntityWrapper<WebWxshard> entityWrapper = new EntityWrapper<WebWxshard>();
            entityWrapper.eq(GOOD_CLASS, classCode);
            WebWxshard webWxshard = webWxshardMapper.selectList(entityWrapper).get(0);
            map.put(SHOW_ID, webShow.getShowId());
            map.put(WXSHARD_SHOW, 1);
            long newTime = new Date().getTime();
            if(newTime < webWxshard.getEndTime().getTime() ){
                map.put(WEB_WXSHARD2,webWxshard);
            }
        }
        return map;
    }

    @Override
    public Object save(WebWxshardEx entity) {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        if(entity.getShardId() == null ){
            entity.setCreaterId(userId);
            entity.setCreaterTime(DateUtil.date());
            webWxshardMapper.insert(entity);
            WebShow webShow = new WebShow();
            webShow.setShowId(entity.getShowId());
            webShow.setWxshardShow(entity.getWxshardShow());
            webShowMapper.updateById(webShow);
        }else{
            entity.setModifyId(userId);
            entity.setModifyTime(DateUtil.date());
            webWxshardMapper.updateById(entity);
            WebShow webShow = new WebShow();
            webShow.setShowId(entity.getShowId());
            webShow.setWxshardShow(entity.getWxshardShow());
            webShowMapper.updateById(webShow);
        }
        JSONObject map = new JSONObject();
        map.put(SHARD_ID, entity.getShardId());
        return map;
    }

    @Override
    public Map<String, Object> query(String classCode) {
        EntityWrapper<WebWxshard> entityWrapper = new EntityWrapper<WebWxshard>();
        entityWrapper.eq(GOOD_CLASS,classCode);
        WebWxshard webWxshard = webWxshardMapper.selectList(entityWrapper).get(0);

        WebShow webShow = new WebShow();
        webShow.setClassCode(classCode);
        WebShow entity = webShowMapper.selectOne(webShow);

        JSONObject map = new JSONObject();
        map.put(WEB_WXSHARD,webWxshard);
        map.put(SHOW_ID,entity.getShowId());
        map.put(WXSHARD_SHOW,entity.getWxshardShow());

        return map;
    }
}
