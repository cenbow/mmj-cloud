package com.mmj.active.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.model.*;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.search.mapper.SearchConfigurationMapper;
import com.mmj.active.search.model.Custom;
import com.mmj.active.search.model.SearchConfiguration;
import com.mmj.active.search.model.SearchConfigurationEx;
import com.mmj.active.search.model.SearchVo;
import com.mmj.active.search.service.SearchConfigurationService;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 商品搜索配置表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
@Service
public class SearchConfigurationServiceImpl extends ServiceImpl<SearchConfigurationMapper, SearchConfiguration> implements SearchConfigurationService {

    Logger logger = LoggerFactory.getLogger(SearchConfigurationServiceImpl.class);

    @Autowired
    private SearchConfigurationMapper searchConfigurationMapper;

    @Autowired
    private ActiveGoodService activeGoodService;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public Long configuration(SearchConfigurationEx searchConfigurationEx) throws Exception {
        logger.info("开始配置搜索关键配置.....");
        Date date = new Date();
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if (searchConfigurationEx.getConfigurationId() != null) {
            logger.info("修改搜索boss后台配置.....");
            SearchConfiguration configuration = JSON.parseObject(JSON.toJSONString(searchConfigurationEx), SearchConfiguration.class);
            configuration.setKeyword(searchConfigurationEx.getDefaultKeyword());
            configuration.setModifyId(userDetails.getUserId());
            configuration.setModifyTime(date);
            EntityWrapper<SearchConfiguration> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("DEFAULT_FLAG", 1);
            entityWrapper.eq("CONFIGURATION_ID", searchConfigurationEx.getConfigurationId());
            searchConfigurationMapper.update(configuration, entityWrapper);

            EntityWrapper<SearchConfiguration> entityWrapper1 = new EntityWrapper<>();
            entityWrapper1.eq("DEFAULT_FLAG", 0);
            entityWrapper1.eq("CONFIGURATION_ID", searchConfigurationEx.getConfigurationId());
            List<SearchConfiguration> searchConfigurations = searchConfigurationMapper.selectList(entityWrapper1);
            searchConfigurationMapper.delete(entityWrapper1);

            if (searchConfigurationEx.getCustoms() != null && !searchConfigurationEx.getCustoms().isEmpty()) {
                if (searchConfigurationEx.getCustoms().size() > 3) {
                    throw new BusinessException("自定义词汇不能超过3条!");
                }
                List<Custom> customs = new ArrayList<>();
                int count = 0;
                for (int i = 0; i < searchConfigurationEx.getCustoms().size(); i++) {
                    Custom custom = searchConfigurationEx.getCustoms().get(i);
                    if (custom.getKeyword() != null && custom.getKeyword().length() != 0) {
                        customs.add(custom);
                    } else {
                        count++;
                    }
                }

                if (count < 3) {
                    boolean flag = dateflag(customs);
                    if (!flag) {
                        throw new BusinessException("自定义时间不合法!");
                    }
                }

                for (int i = 0; i < searchConfigurationEx.getCustoms().size(); i++) {
                    Custom custom = searchConfigurationEx.getCustoms().get(i);
                    if (custom.getKeyword() != null && custom.getKeyword().length() != 0) {
                        long ids = snowflakeIdWorker.nextId();
                        SearchConfiguration con = new SearchConfiguration();
                        con.setActive(true);
                        con.setKeyword(custom.getKeyword());
                        con.setGoodName(custom.getGoodName());
                        con.setGoodId(custom.getGoodId());
                        con.setDefaultFlag(false);
                        con.setCreaterId(userDetails.getUserId());
                        if (custom.getBeginDate() != null && custom.getEndDate() != null) {
                            con.setBeginDate(DateUtils.parse(custom.getBeginDate()));
                            con.setEndDate(DateUtils.parse(custom.getEndDate()));
                        }
                        con.setId(ids);
                        con.setSearchIndex(i + 1);
                        con.setConfigurationId(Long.valueOf(searchConfigurationEx.getConfigurationId()));
                        searchConfigurationMapper.insert(con);
                    }
                }
            }
            return searchConfigurationEx.getConfigurationId();
        } else {
            logger.info("新增搜索boss后台配置.....");
            EntityWrapper<SearchConfiguration> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("ACTIVE", 1);
            List<SearchConfiguration> configurationList = searchConfigurationMapper.selectList(entityWrapper);
            if (configurationList != null && configurationList.size() > 0) {
                throw new Exception("不要重复添加!");
            }
            SearchConfiguration configuration = new SearchConfiguration();
            long id = snowflakeIdWorker.nextId();
            long cid = snowflakeIdWorker.nextId();
            configuration.setActive(true);
            configuration.setKeyword(searchConfigurationEx.getDefaultKeyword());
            configuration.setGoodName(searchConfigurationEx.getGoodName());
            configuration.setGoodId(searchConfigurationEx.getGoodId());
            configuration.setDefaultFlag(true);
            configuration.setCreaterId(userDetails.getUserId());
            configuration.setId(id);
            configuration.setSearchIndex(0);    // 默认的索引就设置为0
            configuration.setConfigurationId(cid);
            searchConfigurationMapper.insert(configuration);

            if (searchConfigurationEx.getCustoms() == null) {
                return configuration.getId();
            }

            if (searchConfigurationEx.getCustoms().size() > 0 && searchConfigurationEx.getCustoms() != null) {
                if (searchConfigurationEx.getCustoms().size() > 3) {
                    throw new Exception("自定义词汇不能超过3条!");
                }

                List<Custom> customList = new ArrayList<>();
                int count = 0;
                for (int i = 0; i < searchConfigurationEx.getCustoms().size(); i++) {
                    Custom custom = searchConfigurationEx.getCustoms().get(i);
                    if (custom.getKeyword() != null && custom.getKeyword().length() != 0) {
                        customList.add(custom);
                    } else {
                        count++;
                    }
                }
                if (count < 3) {
                    boolean flag = dateflag(customList);
                    logger.info("当前时间是否合法,{}", flag);
                    if (!flag) {
                        throw new Exception("自定义时间不合法!");
                    }
                }

                for (int i = 0; i < searchConfigurationEx.getCustoms().size(); i++) {
                    if (searchConfigurationEx.getCustoms().get(i).getKeyword() != null && searchConfigurationEx.getCustoms().get(i).getKeyword().length() != 0) {
                        long ids = snowflakeIdWorker.nextId();
                        Custom custom = searchConfigurationEx.getCustoms().get(i);
                        SearchConfiguration config = new SearchConfiguration();
                        config.setActive(true);
                        config.setKeyword(custom.getKeyword());
                        config.setGoodName(custom.getGoodName());
                        config.setGoodId(custom.getGoodId());
                        config.setDefaultFlag(false);
                        config.setCreaterId(userDetails.getUserId());
                        if (custom.getBeginDate() != null && custom.getEndDate() != null) {
                            config.setBeginDate(DateUtils.parse(custom.getBeginDate()));
                            config.setEndDate(DateUtils.parse(custom.getEndDate()));
                        }
                        config.setId(ids);
                        config.setSearchIndex(i + 1);
                        config.setConfigurationId(cid);
                        searchConfigurationMapper.insert(config);
                    }
                }
            }
            return configuration.getId();
        }
    }


    /**
     * 搜索配置--获取列表
     *
     * @param configurationId
     * @return
     */
    @Override
    public SearchConfigurationEx getConfigurationList(Long configurationId) {
        Date date = new Date();
        SearchConfigurationEx searchConfigurationEx = new SearchConfigurationEx();
        if (configurationId == null) {
            EntityWrapper<SearchConfiguration> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("CONFIGURATION_ID", 0);
            entityWrapper.eq("ACTIVE", 0);
            List<SearchConfiguration> conlist = searchConfigurationMapper.selectList(entityWrapper);
            if (conlist != null && conlist.size() > 0) {
                SearchConfiguration configuration = conlist.get(0);
                if (configuration.getSearchIndex() == -1) {
                    searchConfigurationEx.setSwitchs(true);
                } else {
                    searchConfigurationEx.setSwitchs(false);
                }
                return searchConfigurationEx;
            }
            return searchConfigurationEx;
        }
        searchConfigurationEx.setSwitchs(true);

        EntityWrapper<SearchConfiguration> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("CONFIGURATION_ID", configurationId);
        entityWrapper.eq("ACTIVE", 1);
        entityWrapper.eq("DEFAULT_FLAG", 1);
        List<SearchConfiguration> configurationList = searchConfigurationMapper.selectList(entityWrapper);
        // 封装默认的关键词
        if (configurationList.size() > 0 && configurationList != null) {
            if (configurationList.get(0).getKeyword() != null && configurationList.get(0).getKeyword().length() != 0) {
                searchConfigurationEx.setDefaultKeyword(configurationList.get(0).getKeyword());
                searchConfigurationEx.setGoodName(configurationList.get(0).getGoodName());
                searchConfigurationEx.setGoodId(configurationList.get(0).getGoodId());
                searchConfigurationEx.setConfigurationId(configurationList.get(0).getConfigurationId());
                //查询商品
                GoodInfo goodInfo = goodFeignClient.getById(configurationList.get(0).getGoodId());
                if (goodInfo != null) {
                    searchConfigurationEx.setSpu(goodInfo.getGoodSpu());
                }
                String image = goodFeignClient.queryGoodImgUrl(configurationList.get(0).getGoodId());
                if (image != null && image.length() != 0) {
                    searchConfigurationEx.setImage(image);
                }
            }
        }

        EntityWrapper<SearchConfiguration> example1 = new EntityWrapper<>();
        example1.eq("CONFIGURATION_ID", configurationId);
        example1.eq("ACTIVE", 1);
        example1.eq("DEFAULT_FLAG", 0);
        example1.orderBy("SEARCH_INDEX");
        List<SearchConfiguration> configList = searchConfigurationMapper.selectList(example1);
        List<Custom> list = new ArrayList<>();
        // 自定义词汇
        if (configList != null && configList.size() > 0) {
            for (int i = 0; i < configList.size(); i++) {
                Custom Custom = new Custom();
                if (configList.get(i).getKeyword() != null && configList.get(i).getKeyword().length() != 0) {
                    Custom.setKeyword(configList.get(i).getKeyword());
                    Custom.setGoodName(configList.get(i).getGoodName());
                    Custom.setGoodId(configList.get(i).getGoodId());
                    Custom.setBeginDate(DateUtils.getDate(configList.get(i).getBeginDate(), "yyyy-MM-dd HH:mm:ss"));
                    Custom.setEndDate(DateUtils.getDate(configList.get(i).getEndDate(), "yyyy-MM-dd HH:mm:ss"));
                    //查询商品
                    GoodInfo goodInfo = goodFeignClient.getById(configList.get(i).getGoodId());
                    if (goodInfo != null) {
                        Custom.setGoodSpu(goodInfo.getGoodSpu());
                    }
                    String image = goodFeignClient.queryGoodImgUrl(configList.get(i).getGoodId());
                    if (image != null && image.length() != 0) {
                        Custom.setImage(image);
                    }
                    list.add(Custom);
                }
            }
            searchConfigurationEx.setCustoms(list);
        }
        return searchConfigurationEx;
    }

    /**
     * boss 后台 搜索关键词的开启与失败
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateConfiguration(String flag) {
        logger.info("开始进入boss后台搜索关键词轮播的开关按钮配置.......");
        logger.info("boss 后台用户{}操作的动作为{}", flag);
        Date date = new Date();

        List<SearchConfiguration> list = searchConfigurationMapper.selectList(new EntityWrapper<>());

        EntityWrapper<SearchConfiguration> configurationExample = new EntityWrapper<>();
        configurationExample.eq("CONFIGURATION_ID", 0);
        configurationExample.eq("ACTIVE", 0);
        List<SearchConfiguration> data = searchConfigurationMapper.selectList(configurationExample);
        if (list.size() > 0 && list != null) {
            if ("false".equals(flag)) {
                for (int i = 0; i < list.size(); i++) {
                    SearchConfiguration configuration = new SearchConfiguration();
                    configuration.setActive(false);
                    configuration.setBeginDate(null);
                    configuration.setEndDate(null);
                    configuration.setModifyTime(date);
                    configuration.setId(list.get(i).getId());
                    searchConfigurationMapper.updateById(configuration);
                }
                if (data.size() > 0 && data != null) {
                    SearchConfiguration con = new SearchConfiguration();
                    con.setActive(false);
                    con.setBeginDate(null);
                    con.setEndDate(null);
                    con.setSearchIndex(-2);
                    con.setId(38949382934894234L);
                    searchConfigurationMapper.updateById(con);
                }


            }
            if ("true".equals(flag)) {
                for (int i = 0; i < list.size(); i++) {
                    SearchConfiguration configuration = new SearchConfiguration();
                    list.get(i).getId();
                    configuration.setActive(true);
                    configuration.setBeginDate(null);
                    configuration.setEndDate(null);
                    configuration.setModifyTime(date);
                    configuration.setId(list.get(i).getId());
                    searchConfigurationMapper.updateById(configuration);
                }
                if (data.size() > 0 && data != null) {
                    SearchConfiguration ccc = new SearchConfiguration();
                    ccc.setActive(false);
                    ccc.setBeginDate(null);
                    ccc.setEndDate(null);
                    ccc.setSearchIndex(-1);
                    ccc.setId(38949382934894234L);
                    searchConfigurationMapper.updateById(ccc);
                }

            }
        }
        logger.info("boss后台搜索关键词轮播的开关按钮配置成功.......");
        return "success";
    }

    /**
     * 向上排序
     *
     * @param activeGood
     */
    @Transactional(rollbackFor = Exception.class)
    public void upSort(ActiveGood activeGood) {
        logger.info("搜索boss后台配置向上排序......");
        Integer index = activeGood.getGoodOrder();
        Integer goodId = activeGood.getGoodId();
        logger.info("当前索引值:{},当前的goodId为:{}", index, goodId);

        ActiveGood param = new ActiveGood();
        param.setGoodStatus(ActiveGoodsConstants.goodStatus.PUT_ON);
        param.setActiveType(ActiveGoodsConstants.ActiveType.SEARCH_GOOD);
        Page<ActiveGood> activeGoodPage = activeGoodService.queryBaseList(param);
        if (activeGoodPage != null && activeGoodPage.getRecords() != null && activeGoodPage.getRecords().size() > 0) {
            if (index == activeGoodPage.getRecords().get(0).getGoodOrder()) {
                throw new BusinessException("亲,已经置顶了!");
            }
            List<ActiveGood> list = activeGoodPage.getRecords();
            Integer lastId = null;
            Integer lastIndex = -1;
            ActiveGood g1 = null;
            ActiveGood g2 = null;
            for (int i = 0; i < list.size(); i++) {
                if (index == list.get(i).getGoodOrder()) {
                    g1 = list.get(i);
                }
                if (index == list.get(i).getGoodOrder() + 1) {
                    g2 = list.get(i);
                    lastId = list.get(i).getGoodId();
                    lastIndex = index;
                    break;
                }
            }
            logger.info("上一索引的id为:{},上一个索引的索引值为:", lastId, lastIndex);
            // 当前索引上移
            ActiveGood up = new ActiveGood();
            up.setGoodOrder(index - 1);
            up.setGoodStatus(ActiveGoodsConstants.goodStatus.PUT_ON);
            up.setArg2("1");
            logger.info("当前索引{}开始上移..,", index);
            EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("GOOD_ID", goodId);
            entityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SEARCH_GOOD);
            activeGoodService.update(up, entityWrapper);
            logger.info("当前索引{}上移成功..,", index);

            // 上一个索引下移
            ActiveGood upDown = new ActiveGood();
            upDown.setGoodOrder(lastIndex);
            upDown.setGoodStatus(ActiveGoodsConstants.goodStatus.PUT_ON);
            upDown.setArg2("1");
            logger.info("上一个索引{}开始上移..,", lastIndex);
            EntityWrapper<ActiveGood> entityWrapper2 = new EntityWrapper<>();
            entityWrapper2.eq("GOOD_ID", lastId);
            entityWrapper2.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SEARCH_GOOD);
            activeGoodService.update(upDown, entityWrapper2);
            logger.info("上一个索引{}上移成功..,", lastIndex);
        }
    }

    /**
     * 向下排序
     *
     * @param activeGood
     */
    public void downSort(ActiveGood activeGood) {
        logger.info("boss后台搜索向下排序......");
        Integer index = activeGood.getGoodOrder();
        Integer id = activeGood.getGoodId();
        logger.info("当前的索引值为:{},当前的id值为:{}", index, id);

        EntityWrapper<ActiveGood> Wrapper = new EntityWrapper<>();
        Wrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SEARCH_GOOD);
        Wrapper.orderBy("GOOD_ORDER");
        Page<ActiveGood> page = new Page<>(activeGood.getCurrentPage(), activeGood.getPageSize());
        Page<ActiveGood> activeGoodPage = activeGoodService.selectPage(page, Wrapper);
        if (activeGoodPage != null && activeGoodPage.getRecords() != null && activeGoodPage.getRecords().size() > 0) {
            if (index == activeGoodPage.getRecords().get(activeGoodPage.getRecords().size() - 1).getGoodOrder()) {
                throw new BusinessException("亲,已经置底了!");
            }
            Integer nextIndex = -1;
            String nextId = null;
            List<ActiveGood> list = activeGoodPage.getRecords();
            for (int i = 0; i < list.size(); i++) {
                if (index == list.get(i).getGoodOrder() - 1) {
                    nextIndex = index;
                    nextId = String.valueOf(list.get(i).getGoodId());
                    break;
                }
            }
            logger.info("下一个索引的id为:{},下一个索引的索引值为:", nextId, nextIndex);

            // 下移当前索引值
            ActiveGood down = new ActiveGood();
            down.setGoodOrder(index + 1);
            down.setGoodStatus(ActiveGoodsConstants.goodStatus.PUT_ON);
            down.setArg2("1");
            logger.info("当前索引{}开始下移......", index);
            EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("GOOD_ID", id);
            entityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SEARCH_GOOD);
            activeGoodService.update(down, entityWrapper);
            logger.info("当前索引{}下移成功..,", index);

            // 上一个索引值开始上移
            ActiveGood downUp = new ActiveGood();
            down.setGoodOrder(nextIndex);
            down.setGoodStatus(ActiveGoodsConstants.goodStatus.PUT_ON);
            down.setArg2("1");

            logger.info("下一个索引值{}开始上移.....", nextIndex);
            EntityWrapper<ActiveGood> entityWrapper2 = new EntityWrapper<>();
            entityWrapper2.eq("GOOD_ID", nextId);
            entityWrapper2.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SEARCH_GOOD);
            activeGoodService.update(down, entityWrapper2);
            logger.info("下一个索引值{}上移成功..", nextIndex);
        }

    }

    public Page<GoodInfoEx> getSearchResult(List<String> words, SearchVo searchVo) {
        StringBuilder sb = new StringBuilder();
        if (words.size() > 1 && words != null) {
            for (int i = 0; i < words.size(); i++) {
                if (i != words.size() - 1) {
                    sb.append(words.get(i)).append("|");
                } else {
                    sb.append(words.get(i));
                }
            }
        } else if (words.size() == 1) {
            sb.append(words.get(0));
        }
        searchVo.setContent(sb.toString());
        ReturnData<Page<GoodInfoEx>> pageReturnData = goodFeignClient.searchGoods(JSON.toJSONString(searchVo));
        if (pageReturnData != null && pageReturnData.getCode() == SecurityConstants.SUCCESS_CODE) {
            return pageReturnData.getData();
        }
        return null;
    }

    public Long getConfigurationId() {
        EntityWrapper<SearchConfiguration> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("ACTIVE", 1);
        List<SearchConfiguration> configurationList = searchConfigurationMapper.selectList(entityWrapper);
        if (configurationList != null && configurationList.size() > 0) {
            return configurationList.get(0).getConfigurationId();
        } else {
            return null;
        }
    }



    /**
     * 判断时间合法性
     *
     * @param list
     * @return
     */
    public boolean dateflag(List<Custom> list) {
        boolean flag = false;
        if (list.size() > 0 && list != null) {
            for (int i = 0; i < list.size(); i++) {
                Custom custom = list.get(i);
                for (int j = 0; j < list.size(); j++) {
                    if (list.size() > 1) {
                        if (i == j) {
                            continue;
                        }
                        if ((DateUtils.parse(custom.getBeginDate()).getTime() >= DateUtils.parse(custom.getEndDate()).getTime())) {
                            flag = false;
                            break;
                        }
                        if (custom.getBeginDate() != null && custom.getEndDate() != null && list.get(j).getBeginDate() != null) {
                            if ((DateUtils.parse(custom.getBeginDate()).getTime() <= DateUtils.parse(list.get(j).getBeginDate()).getTime()) && (DateUtils.parse(custom.getEndDate()).getTime() <= DateUtils.parse(list.get(j).getBeginDate()).getTime()) && (DateUtils.parse(custom.getEndDate()).getTime() < DateUtils.parse(list.get(j).getEndDate()).getTime())) {
                                flag = true;
                            }
                            if ((DateUtils.parse(custom.getEndDate()).getTime() > DateUtils.parse(list.get(j).getBeginDate()).getTime()) && (DateUtils.parse(custom.getEndDate()).getTime() < DateUtils.parse(list.get(j).getEndDate()).getTime())) {
                                flag = false;
                                break;
                            }
                            if (custom.getBeginDate().equals(list.get(j).getBeginDate())) {
                                flag = false;
                                break;
                            }
                        }
                    } else {
                        if (DateUtils.parse(custom.getBeginDate()).getTime() <= DateUtils.parse(custom.getEndDate()).getTime()) {
                            flag = true;
                        }
                    }
                }
            }
        }
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetHotSellGoods(){
        //查询当前热销前十
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("ACTIVE_TYPE", 18);
        List<ActiveGood> activeGoods = activeGoodService.selectList(entityWrapper);
        List<ActiveGood> need = new ArrayList<>();
        if (activeGoods != null && !activeGoods.isEmpty()) {
            //查询最新热销前十商品
            ReturnData<List<Map<String, Object>>> listReturnData = goodFeignClient.queryTopGood();
            if (listReturnData != null && listReturnData.getCode() == SecurityConstants.SUCCESS_CODE && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
                for (ActiveGood activeGood : activeGoods) {
                    for (int i = 0; i < listReturnData.getData().size(); i++) {
                        Map<String, Object> map = listReturnData.getData().get(i);
                        Integer goodId = (Integer) map.get("goodid");
                        if (activeGood.getGoodOrder().compareTo(i + 1) == 0) {
                            //修改标识(0:否 1:是)
                            if ("1".equals(activeGood.getArg2())) {
                                //修改过的商品替换缓存
                                String keyOld = ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP + activeGood.getGoodId();
                                if (redisTemplate.hasKey(keyOld)) {//修改
                                    String keyNew = ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP + goodId;
                                    redisTemplate.opsForHash().put(keyNew, "goodId", redisTemplate.opsForHash().get(keyOld, "goodId"));
                                    redisTemplate.opsForHash().put(keyNew, "goodOrder", redisTemplate.opsForHash().get(keyOld, "goodOrder"));
                                    redisTemplate.opsForHash().put(keyNew, "arg1", redisTemplate.opsForHash().get(keyOld, "arg1"));
                                } else {
                                    Object value = redisTemplate.opsForValue().get(ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP_ORDER + activeGood.getGoodId());
                                    if (value != null && !"".equals(value)) {
                                        redisTemplate.opsForValue().set(ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP_ORDER + goodId, value, 7, TimeUnit.DAYS);
                                    }
                                }
                            } else {
                                //未修改的商品直接替换
                                activeGood.setGoodId(goodId);
                                activeGood.setGoodName(String.valueOf(map.get("goodname")));
                                activeGood.setGoodSpu(String.valueOf(map.get("goodspu")));
                                activeGood.setGoodImage(String.valueOf(map.get("goodimage")));
                                activeGood.setArg1(String.valueOf(map.get("goodname")));
                                need.add(activeGood);
                                redisTemplate.opsForValue().set(ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP_ORDER + goodId, activeGood.getGoodOrder(), 7, TimeUnit.DAYS);
                            }
                        }
                    }
                }
                activeGoodService.updateBatchById(need);
            }
        } else {
            ReturnData<List<Map<String, Object>>> listReturnData = goodFeignClient.queryTopGood();
            if (listReturnData != null && listReturnData.getCode() == SecurityConstants.SUCCESS_CODE && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
                for (int i = 0; i < listReturnData.getData().size(); i++) {
                    Map<String, Object> map = listReturnData.getData().get(i);
                    //未修改的商品直接替换
                    Integer goodId = (Integer) map.get("goodid");
                    ActiveGood activeGood = new ActiveGood();
                    activeGood.setGoodId(goodId);
                    activeGood.setGoodName(String.valueOf(map.get("goodname")));
                    activeGood.setGoodSpu(String.valueOf(map.get("goodspu")));
                    activeGood.setGoodImage(String.valueOf(map.get("goodimage")));
                    activeGood.setArg1(String.valueOf(map.get("goodname")));
                    need.add(activeGood);
                    redisTemplate.opsForValue().set(ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP_ORDER + goodId, activeGood.getGoodOrder(), 7, TimeUnit.DAYS);
                }
                activeGoodService.insertBatch(need);
            }
        }
    }
}
