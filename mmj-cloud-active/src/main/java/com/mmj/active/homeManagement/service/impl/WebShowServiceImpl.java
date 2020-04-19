package com.mmj.active.homeManagement.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.homeManagement.common.CodeUtils;
import com.mmj.active.homeManagement.common.RedisUtils;
import com.mmj.active.homeManagement.constant.RedisKey;
import com.mmj.active.homeManagement.mapper.WebShowMapper;
import com.mmj.active.homeManagement.model.GoodClassEx;
import com.mmj.active.homeManagement.model.WebShow;
import com.mmj.active.homeManagement.model.vo.HomeManagement;
import com.mmj.active.homeManagement.model.vo.WebMaketingVo;
import com.mmj.active.homeManagement.model.vo.WebShowcaseVo;
import com.mmj.active.homeManagement.model.vo.WebTopVo;
import com.mmj.active.homeManagement.service.WebShowService;

import com.mmj.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 页面展示表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Service
public class WebShowServiceImpl extends ServiceImpl<WebShowMapper, WebShow> implements WebShowService {
	
    private static final String CLASS_CODE = "CLASS_CODE";
	private static final String MENBER_CODE = "menberCode";
	private static final String OLD_CODE = "oldCode";
	private static final String NEW_CODE = "newCode";
	private static final String WEB_SHOWCASE = "webShowcase";
	private static final String WEB_MAKETING = "webMaketing";
	private static final String WEB_TOP = "webTop";
	
	@Autowired
    private WebShowMapper webShowMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<GoodClassEx> selectGoodClass() {
        return webShowMapper.selectGoodClass();
    }

    @Override
    public Object selectCode(String classCode) {
        //获取顶部大图code
        HomeManagement homeManagement = redisUtils.createHomeManagement(WEB_TOP, classCode, null);
        Map<String, Object> webTop = redisUtils.getCodeRedis(homeManagement);
        WebTopVo webTopVo = new WebTopVo(classCode, WEB_TOP,"顶部大图", (String)webTop.get(NEW_CODE), (String)webTop.get(OLD_CODE), (String)webTop.get(MENBER_CODE));

        //获取营销模块code
        HomeManagement homeManagement1 = redisUtils.createHomeManagement(WEB_MAKETING, classCode, null);
        Map<String, Object> webMaketing = redisUtils.getCodeRedis(homeManagement1);
        WebMaketingVo webMaketingVo = new WebMaketingVo(classCode, WEB_MAKETING,"营销模块", (String) webMaketing.get(NEW_CODE), (String)webMaketing.get(OLD_CODE),(String)webMaketing.get(MENBER_CODE));

        //获取橱窗code
        HomeManagement homeManagement2 = redisUtils.createHomeManagement(WEB_SHOWCASE, classCode, null);
        Map<String, Object> webShowcase = redisUtils.getCodeRedis(homeManagement2);
        WebShowcaseVo webShowcaseVo = new WebShowcaseVo(classCode ,WEB_SHOWCASE,"橱窗模块", (String) webShowcase.get(NEW_CODE),(String)webShowcase.get(OLD_CODE),(String)webShowcase.get(MENBER_CODE));

        List<Object> list = new ArrayList<Object>();
        list.add(webTopVo);
        list.add(webMaketingVo);
        list.add(webShowcaseVo);
        return list;
    }

    @Override
    public Object updateWebShow(WebShow webShow) {
        int flag = webShowMapper.updateById(webShow);
        HomeManagement homeManagement = null;
        if(null != webShow.getTopShow()){
            homeManagement = redisUtils.createHomeManagement(WEB_TOP, webShow.getClassCode(), null);
        }
        if(null != webShow.getShowcaseShow()){
            homeManagement = redisUtils.createHomeManagement(WEB_SHOWCASE, webShow.getClassCode(), null);
        }
        if(null != webShow.getMaketingShow()){
            homeManagement = redisUtils.createHomeManagement(WEB_MAKETING, webShow.getClassCode(), null);
        }
        redisUtils.deleteRedis(homeManagement);
        return flag;
    }

    @Override
    public Object delectByGoodClass(String goodClass) {
        EntityWrapper<WebShow> entity = new EntityWrapper<>();
        entity.eq(CLASS_CODE,goodClass);
        return webShowMapper.delete(entity);
    }

    @Override
    public void updateIndexCode(String userIdentity) {
        redisUtils.updateUserCode(userIdentity);
    }
}
