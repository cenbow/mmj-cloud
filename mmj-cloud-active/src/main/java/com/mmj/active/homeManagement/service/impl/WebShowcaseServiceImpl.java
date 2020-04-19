package com.mmj.active.homeManagement.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.feigin.UserMemberFeignClient;
import com.mmj.active.common.model.GoodInfoBaseQueryEx;
import com.mmj.active.common.model.UserMember;
import com.mmj.active.homeManagement.common.RedisUtils;
import com.mmj.active.homeManagement.constant.RedisKey;
import com.mmj.active.homeManagement.constant.WebShowcaseTemplate;
import com.mmj.active.homeManagement.mapper.ShowcaseGoodMapper;
import com.mmj.active.homeManagement.mapper.WebShowMapper;
import com.mmj.active.homeManagement.mapper.WebShowcaseFileMapper;
import com.mmj.active.homeManagement.mapper.WebShowcaseMapper;
import com.mmj.active.homeManagement.model.ShowcaseGood;
import com.mmj.active.homeManagement.model.WebShow;
import com.mmj.active.homeManagement.model.WebShowcase;
import com.mmj.active.homeManagement.model.WebShowcaseEx;
import com.mmj.active.homeManagement.model.WebShowcaseFile;
import com.mmj.active.homeManagement.model.vo.HomeManagement;
import com.mmj.active.homeManagement.service.WebShowcaseService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * <p>
 * 橱窗配置表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-09
 */
@Slf4j
@Service
public class WebShowcaseServiceImpl extends ServiceImpl<WebShowcaseMapper, WebShowcase> implements WebShowcaseService {
    
    private static final String _1 = "1";
	private static final String WEB_SHOWCASE = "webShowcase";
	private static final String SHOWECASE_ORDER = "SHOWECASE_ORDER";
	private static final String SHOW_MEMBER = "SHOW_MEMBER";
	private static final String SHOW_OLD = "SHOW_OLD";
	private static final String SHOW_NEW = "SHOW_NEW";
	private static final String ACTIVE_FLAG = "ACTIVE_FLAG";
	private static final String USER_ID = "userId";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String SHOWECASE_ID = "SHOWECASE_ID";
	private static final String GOOD_ORDER = "GOOD_ORDER";
	private static final String SHOWCASE_ID = "SHOWCASE_ID";
	private static final String CLASS_CODE = "CLASS_CODE";
	private static final String GOOD_CLASS = "GOOD_CLASS";
    private static final String GOOD_INFO = "GOOD_INFO";
	private static final String GOOD_INFO_SHOWCASEGOOD = "SHOWCASEGOOD:";
	@Autowired
    private WebShowcaseMapper webShowcaseMapper;
    @Autowired
    private WebShowcaseFileMapper webShowcaseFileMapper;
    @Autowired
    private ShowcaseGoodMapper showcaseGoodMapper;
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
    @Autowired
    private GoodFeignClient goodFeignClient;


    @Override
    public ReturnData<Object>  save(WebShowcaseEx webShowcaseEx) {
        ReturnData<Object> rd = new ReturnData<Object>();
        List<ShowcaseGood> goodsList = webShowcaseEx.getShowcaseGood();
        if(CollectionUtil.isNotEmpty(goodsList)){
            List<String> goodnames = GoodIsUnshelve(goodsList); //查询商品是否上架
            StringBuilder message = new StringBuilder();
            if(CollectionUtil.isNotEmpty(goodnames)){
                goodnames.forEach(goodname -> {
                    message.append(","+goodname);
                });
                rd.setCode(SecurityConstants.EXCEPTION_CODE);
                rd.setDesc("商品未上架"+message);
                return rd;
            }
        }
        if(webShowcaseEx.getShowecaseId() == null){ //保存
            webShowcaseEx.setCreaterId(SecurityUserUtil.getUserDetails().getUserId());
            webShowcaseEx.setCreaterTime(DateUtil.date());
            webShowcaseMapper.insertAllColumn(webShowcaseEx);
            insertWebShowcase(webShowcaseEx);
        }else{ //修改
           webShowcaseMapper.updateById(webShowcaseEx);
           updateWebShowcase(webShowcaseEx);
        }

        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc(webShowcaseEx.getShowecaseId().toString());
        deleteRedis(webShowcaseEx);  //清除橱窗小程序缓存
        redisTemplate.opsForHash().delete(GOOD_INFO, GOOD_INFO_SHOWCASEGOOD + webShowcaseEx.getShowecaseId());//清除俊哥的缓存
        log.info("橱窗管理模块,保存, 删除缓存成功");
        return rd;
    }


    @Override
    public List<WebShowcaseEx> queryByGoodClass(String goodClass) {
        EntityWrapper<WebShowcase> webShowcaseWrapper = new EntityWrapper<WebShowcase>();
        webShowcaseWrapper.eq(GOOD_CLASS, goodClass);
        webShowcaseWrapper.orderBy(SHOWECASE_ORDER);
        List<WebShowcase> list = webShowcaseMapper.selectList(webShowcaseWrapper);
        List<WebShowcaseEx> result = new ArrayList<>();
        list.forEach(webShowcase->{
            WebShowcaseEx cloneWebShowcaseEx = JSON.parseObject(JSON.toJSONString(webShowcase), WebShowcaseEx.class);
            EntityWrapper<WebShow> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq(CLASS_CODE, goodClass);
            WebShow webShow = webShowMapper.selectList(entityWrapper).get(0);
            cloneWebShowcaseEx.setShowId(webShow.getShowId());
            cloneWebShowcaseEx.setShowcaseShow(webShow.getShowcaseShow());
            if (WebShowcaseTemplate.A.equals(webShowcase.getTemplateCode()) || WebShowcaseTemplate.F.equals(webShowcase.getTemplateCode()) || WebShowcaseTemplate.G.equals(webShowcase.getTemplateCode())){
                EntityWrapper<ShowcaseGood> showcaseGoodWrapper = new EntityWrapper<>();
                showcaseGoodWrapper.eq(SHOWCASE_ID,webShowcase.getShowecaseId());
                showcaseGoodWrapper.orderBy(GOOD_ORDER);
                List<ShowcaseGood> goodsList = showcaseGoodMapper.selectList(showcaseGoodWrapper);
                cloneWebShowcaseEx.setShowcaseGood(goodsList);
            }else{
                EntityWrapper<WebShowcaseFile> webShowcaseFileWrapper = new EntityWrapper<>();
                webShowcaseFileWrapper.eq(SHOWECASE_ID, webShowcase.getShowecaseId());
                webShowcaseFileWrapper.orderBy(ORDER_ID);
                List<WebShowcaseFile> fileList = webShowcaseFileMapper.selectList(webShowcaseFileWrapper);
                cloneWebShowcaseEx.setWebShowcaseFile(fileList);

            }
            result.add(cloneWebShowcaseEx);
        });
        return result;
    }

    @Override
    public List<Object> selectWebShowcase(String goodClass, Long userId) {
        int userIdentity = 1;  //老用户非会员
        //Boolean isNewUser = orderFeignClient.checkNewUser(userid).getData();  //判断新老用户
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put(USER_ID, userId);
        boolean isNewUser = orderFeignClient.checkNewUser(userMap).getData();//false:否（新用户），true:是(老用户)
        log.info("-->首页弹窗,用户id:{},用户是否为新用户:{}",userId, isNewUser);
        UserMember userMember = userMemberFeignClient.queryUserMemberInfoByUserId(userId).getData();  //判断用户是否是会员
        log.info("-->首页弹窗,用户id:{},用户会员信息:{}",userId, JSON.toJSONString(userMember));
        if(!isNewUser){  //新用户
            userIdentity = 0;
        }
        if(isNewUser && userMember != null && userMember.getActive()){  //老用户会员
            userIdentity = 2;
        }
        String key = RedisKey.WEB_SHOWCASE_KEY + CommonConstant.Symbol.UNDERLINE + goodClass + CommonConstant.Symbol.UNDERLINE + userIdentity;
        Boolean hasKey = redisTemplate.opsForHash().hasKey(RedisKey.WEB_SHOWCASE_KEY, key);
        if(hasKey){
            return (List<Object>) redisTemplate.opsForHash().get(RedisKey.WEB_SHOWCASE_KEY,key);
        }else{
            List<Object> result = new ArrayList<>();
            WebShow webShow = new WebShow();
            webShow.setClassCode(goodClass);
            WebShow show = webShowMapper.selectOne(webShow);
            if(show != null && show.getShowcaseShow() == 0){
                result.add(webShow);
                return result;
            }
            if(show != null && show.getShowcaseShow() == 1){
                EntityWrapper<WebShowcase> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq(ACTIVE_FLAG,1);
                entityWrapper.eq(GOOD_CLASS,goodClass);
                switch (userIdentity){
                    case 0:
                        entityWrapper.eq(SHOW_NEW,1);
                        break;
                    case 1:
                        entityWrapper.eq(SHOW_OLD,1);
                        break;
                    case 2:
                        entityWrapper.eq(SHOW_MEMBER,1);
                        break;
                }
                entityWrapper.orderBy(SHOWECASE_ORDER);
                List<WebShowcase> webShowcaseList = webShowcaseMapper.selectList(entityWrapper);
                webShowcaseList.forEach(webShowcase -> {
                    WebShowcaseEx clone = JSON.parseObject(JSON.toJSONString(webShowcase), WebShowcaseEx.class);
                    if(WebShowcaseTemplate.A.equals(webShowcase.getTemplateCode()) || WebShowcaseTemplate.F.equals(webShowcase.getTemplateCode()) || WebShowcaseTemplate.G.equals(webShowcase.getTemplateCode())){
                        //小程序商品信息调用俊哥的接口
                        clone.setShowcaseGood(null);
                    }else{
                        EntityWrapper<WebShowcaseFile> webShowcaseFileWrapper = new EntityWrapper<>();
                        webShowcaseFileWrapper.eq(SHOWECASE_ID ,webShowcase.getShowecaseId());
                        webShowcaseFileWrapper.orderBy(ORDER_ID);
                        List<WebShowcaseFile> fileList = webShowcaseFileMapper.selectList(webShowcaseFileWrapper);
                        clone.setWebShowcaseFile(fileList);
                    }
                    result.add(clone);
                });
            }
            //添加缓存
            HomeManagement homeManagement = redisUtils.createHomeManagement(WEB_SHOWCASE, goodClass, userIdentity);
            redisUtils.addReids(homeManagement, result);
            return result;
        }
    }

    @Override
    public boolean deleteWebShowcase(Integer showecaseId) {
        WebShowcase webShowcase = webShowcaseMapper.selectById(showecaseId);
        webShowcaseMapper.deleteById(showecaseId);
        EntityWrapper<ShowcaseGood> goods = new EntityWrapper<>();
        goods.eq(SHOWCASE_ID, showecaseId);
        showcaseGoodMapper.delete(goods);
        EntityWrapper<WebShowcaseFile> file = new EntityWrapper<>();
        file.eq(SHOWECASE_ID, showecaseId);
        webShowcaseFileMapper.delete(file);
        WebShowcaseEx webShowcaseEx = JSON.parseObject(JSON.toJSONString(webShowcase), WebShowcaseEx.class);
        deleteRedis(webShowcaseEx);  //删除小程序缓存
        redisTemplate.delete(RedisKey.SHOWCASE_GOOD + showecaseId);
        redisTemplate.opsForHash().delete(GOOD_INFO, GOOD_INFO_SHOWCASEGOOD + webShowcaseEx.getShowecaseId());//清除俊哥的缓存
        log.info("橱窗管理模块,删除, 删除缓存成功");
        return true;
    }


    private void insertWebShowcase(WebShowcaseEx webShowcaseEx) {
        if(WebShowcaseTemplate.A.equals(webShowcaseEx.getTemplateCode()) || WebShowcaseTemplate.F.equals(webShowcaseEx.getTemplateCode()) || WebShowcaseTemplate.G.equals(webShowcaseEx.getTemplateCode())){ //模板A,F,G :商品关联
            List<ShowcaseGood> goodsList = webShowcaseEx.getShowcaseGood();
            if(CollectionUtils.isNotEmpty(goodsList)){
                goodsList.forEach(showcaseGood -> {
                    showcaseGood.setShowcaseId(webShowcaseEx.getShowecaseId());
                    showcaseGoodMapper.insert(showcaseGood);
                    addGoodsRedis(webShowcaseEx, showcaseGood); //将商品id添加到缓存中，给俊哥调用， 用户橱窗小程序获取商品信息
                });
            }
        }else{ //图片关联
            List<WebShowcaseFile> fileList = webShowcaseEx.getWebShowcaseFile();
            if(CollectionUtils.isNotEmpty(fileList)){
                fileList.forEach(webShowcaseFile -> {
                    webShowcaseFile.setShowecaseId(webShowcaseEx.getShowecaseId());
                    webShowcaseFileMapper.insert(webShowcaseFile);
                });
            }
        }
    }


    private void updateWebShowcase(WebShowcaseEx webShowcaseEx) {
        if(WebShowcaseTemplate.A.equals(webShowcaseEx.getTemplateCode()) || WebShowcaseTemplate.F.equals(webShowcaseEx.getTemplateCode()) || WebShowcaseTemplate.G.equals(webShowcaseEx.getTemplateCode())){ //模板A,F,G :商品关联
            List<ShowcaseGood> goodsList = webShowcaseEx.getShowcaseGood();
            EntityWrapper<ShowcaseGood> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq(SHOWCASE_ID,webShowcaseEx.getShowecaseId());
            showcaseGoodMapper.delete(entityWrapper);  //先删除之前存的商品

            if(CollectionUtils.isNotEmpty(goodsList)){  //重新保存商品
                redisTemplate.delete(RedisKey.SHOWCASE_GOOD+webShowcaseEx.getShowecaseId());  //删除俊哥商品调用的缓存
                goodsList.forEach(showcaseGood -> {
                    showcaseGood.setShowcaseId(webShowcaseEx.getShowecaseId());
                    showcaseGoodMapper.insertAllColumn(showcaseGood);
                    addGoodsRedis(webShowcaseEx,showcaseGood);  //将商品id添加到缓存中，给俊哥调用， 用户橱窗小程序获取商品信息
                });
            }
        }else{ //图片关联
            EntityWrapper<WebShowcaseFile> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq(SHOWECASE_ID, webShowcaseEx.getShowecaseId());
            webShowcaseFileMapper.delete(entityWrapper);  //先删除之前存的图片
            List<WebShowcaseFile> fileList = webShowcaseEx.getWebShowcaseFile(); //重新保存图片
            if(CollectionUtils.isNotEmpty(fileList)){
                fileList.forEach(webShowcaseFile -> {
                    webShowcaseFile.setShowecaseId(webShowcaseEx.getShowecaseId());
                    webShowcaseFileMapper.insertAllColumn(webShowcaseFile);
                });
            }
        }
    }


    @Override
    public WebShowcaseEx selectByShowecaseId(Integer showecaseId) {
        WebShowcase webShowcase = webShowcaseMapper.selectById(showecaseId);
        WebShowcaseEx entity = JSON.parseObject(JSON.toJSONString(webShowcase), WebShowcaseEx.class);
        if (WebShowcaseTemplate.A.equals(webShowcase.getTemplateCode()) || WebShowcaseTemplate.F.equals(webShowcase.getTemplateCode()) || WebShowcaseTemplate.G.equals(webShowcase.getTemplateCode())){
            EntityWrapper<ShowcaseGood> showcaseGoodWrapper = new EntityWrapper<>();
            showcaseGoodWrapper.eq(SHOWCASE_ID, webShowcase.getShowecaseId());
            List<ShowcaseGood> goodsList = showcaseGoodMapper.selectList(showcaseGoodWrapper);
            entity.setShowcaseGood(goodsList);
        }else{
            EntityWrapper<WebShowcaseFile> webShowcaseFileWrapper = new EntityWrapper<>();
            webShowcaseFileWrapper.eq(SHOWECASE_ID ,webShowcase.getShowecaseId());
            List<WebShowcaseFile> fileList = webShowcaseFileMapper.selectList(webShowcaseFileWrapper);
            entity.setWebShowcaseFile(fileList);

        }
        return entity;
    }


    public void deleteRedis(WebShowcaseEx webShowcaseEx) {
        HomeManagement homeManagement = redisUtils.createHomeManagement(WEB_SHOWCASE, webShowcaseEx.getGoodClass(), null);
        redisUtils.deleteRedis(homeManagement);
    }

    /**
     * 给俊哥调用，橱窗管理获取商品信息
     * @param webShowcaseEx
     * @param showcaseGood
     */
    private void addGoodsRedis(WebShowcaseEx webShowcaseEx, ShowcaseGood showcaseGood) {
        redisTemplate.opsForList().rightPush(RedisKey.SHOWCASE_GOOD + webShowcaseEx.getShowecaseId(),showcaseGood.getGoodId());
    }


    /**
     * 查询商品是否下架
     * @param goodsList
     * @return
     */
    private List<String> GoodIsUnshelve(List<ShowcaseGood> goodsList) {
        List<Integer> goodIds = goodsList.stream().map(ShowcaseGood::getGoodId).collect(Collectors.toList());
        GoodInfoBaseQueryEx goodInfoBaseQueryEx = new GoodInfoBaseQueryEx();
        goodInfoBaseQueryEx.setGoodIds(goodIds);
        goodInfoBaseQueryEx.setPageSize(goodIds.size());
        ReturnData<Object> pageReturnData = goodFeignClient.queryBaseList(goodInfoBaseQueryEx);
        if (pageReturnData != null && pageReturnData.getCode() == SecurityConstants.SUCCESS_CODE && pageReturnData.getData() != null) {
            Object data = pageReturnData.getData();
            Page page = JSON.parseObject(JSON.toJSONString(data), Page.class);
            List listPage = page.getRecords();
            if (listPage != null && !listPage.isEmpty()) {
                List<GoodInfoBaseQueryEx> goods = JSON.parseArray(JSON.toJSONString(listPage), GoodInfoBaseQueryEx.class);
                List<GoodInfoBaseQueryEx> collect = goods.stream().filter(n -> !_1.equals(n.getGoodStatus())).collect(Collectors.toList());
                List<String> goodNames = collect.stream().map(GoodInfoBaseQueryEx::getGoodName).collect(Collectors.toList());
                return goodNames;
            }
        }
        return null;
    }
}
