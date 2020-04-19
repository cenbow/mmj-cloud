package com.mmj.active.search.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.GoodInfo;
import com.mmj.active.common.model.GoodInfoEx;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.search.model.SearchConfiguration;
import com.mmj.active.search.model.SearchConfigurationEx;
import com.mmj.active.search.model.SearchVo;
import com.mmj.active.search.service.SearchConfigurationService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 商品搜索配置表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
@RestController
@RequestMapping("/searchConfiguration")
public class SearchConfigurationController extends BaseController {

    @Autowired
    private SearchConfigurationService searchConfigurationService;

    @Autowired
    private ActiveGoodService activeGoodService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @ApiOperation(value = "搜索关键词后boss后台配置")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData insertConfiguration(@RequestBody SearchConfigurationEx configurationVo) {
        Long configurationId = searchConfigurationService.getConfigurationId();
        if (configurationId != null) {
            configurationVo.setConfigurationId(configurationId);
        }
        Long configuration = null;
        try {
            configuration = searchConfigurationService.configuration(configurationVo);
        } catch (Exception e) {
            return initExcetionObjectResult(e.getMessage());
        }
        return initSuccessObjectResult(String.valueOf(configuration));
    }

    @ApiOperation(value = "获取关键词boss 后台配置列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ReturnData<SearchConfigurationEx> getConfiguration() {
        Long configurationId = searchConfigurationService.getConfigurationId();
        SearchConfigurationEx searchConfigurationEx = searchConfigurationService.getConfigurationList(configurationId);
        return initSuccessObjectResult(searchConfigurationEx);
    }

    @ApiOperation(value = "获取关键词boss 后台配置 开启与关闭")
    @RequestMapping(value = "/switch/{flag}", method = RequestMethod.POST)
    public ReturnData configurationSwitch(@PathVariable String flag) {
        String result = searchConfigurationService.updateConfiguration(flag);
        Map<Object, Object> map = new HashMap<>();
        map.put("result", result);
        return initSuccessObjectResult(map);
    }

    @ApiOperation(value = "boss-后台配置-商品重置")
    @RequestMapping(value = "/hotSellDelete/{mapperyId}/{goodId}", method = RequestMethod.POST)
    public ReturnData<Page<ActiveGood>> hotSellDelete(@PathVariable("mapperyId") Long mapperyId, @PathVariable("goodId") Integer goodId) {
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
        ActiveGood activeGood = new ActiveGood();
        activeGood.setMapperyId(mapperyId);
        activeGood.setGoodStatus(ActiveGoodsConstants.goodStatus.PUT_ON);
        activeGood.setArg2("0");
        String key = ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP + goodId;
        if (redisTemplate.hasKey(key)) {
            //恢复默认商品
            Integer goodIdOld = Integer.valueOf(String.valueOf(redisTemplate.opsForHash().get(key, "goodId")));
            GoodInfo goodInfo = goodFeignClient.getById(goodIdOld);
            String image = goodFeignClient.queryGoodImgUrl(goodIdOld);
            activeGood.setGoodId(goodInfo.getGoodId());
            activeGood.setGoodSpu(goodInfo.getGoodSpu());
            activeGood.setGoodImage(image);
            activeGood.setGoodName(goodInfo.getGoodName());
            activeGood.setMemberFlag(goodInfo.getMemberFlag());
            activeGood.setArg1(String.valueOf(redisTemplate.opsForHash().get(key, "arg1")));
            Object value = redisTemplate.opsForValue().get(ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP_ORDER + goodIdOld);
            if (value != null && !"".equals(value)) {
                activeGood.setGoodOrder(Integer.valueOf(String.valueOf(value)));
            }
            redisTemplate.delete(key);
        }else{
            Object value = redisTemplate.opsForValue().get(ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP_ORDER + goodId);
            if (value != null && !"".equals(value)) {
                activeGood.setGoodOrder(Integer.valueOf(String.valueOf(value)));
            }
        }
        activeGoodService.updateById(activeGood);
        return initSuccessResult();
    }

    @ApiOperation(value = "boss--搜索热销榜产品排序功能")
    @RequestMapping(value = "/hotSellSort", method = RequestMethod.POST)
    public ReturnData hotSellSort(@RequestBody String params) {
        JSONObject o = JSON.parseObject(params);
        Integer sortFlag = o.getInteger("sortFlag");
        Integer goodId = o.getInteger("goodId");
        Integer goodOrder = o.getInteger("goodOrder");
        // 判断当前的索引值
        EntityWrapper<ActiveGood> Wrapper = new EntityWrapper<>();
        Wrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SEARCH_GOOD);
        Wrapper.eq("GOOD_ID", goodId);
        Wrapper.orderBy("GOOD_ORDER");
        Page<ActiveGood> page = new Page<>(1, 10);
        Page<ActiveGood> activeGoodPage = activeGoodService.selectPage(page, Wrapper);
        if (activeGoodPage != null && activeGoodPage.getRecords() != null && activeGoodPage.getRecords().size() > 0) {
            if (activeGoodPage.getRecords().size() == 1 && activeGoodPage.getRecords().get(0).getGoodOrder() != goodOrder) {
                return initExcetionObjectResult("当前的索引值不对!");
            }
            if (sortFlag == 1) {  // 向上排序
                searchConfigurationService.upSort(activeGoodPage.getRecords().get(0));
            } else if (sortFlag == 2) {   // 向下排序
                searchConfigurationService.downSort(activeGoodPage.getRecords().get(0));
            }
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "小程序-获取搜索栏的关键词")
    @RequestMapping(value = "/queryKeyword", method = RequestMethod.POST)
    public ReturnData<SearchConfiguration> queryKeyword() {
        EntityWrapper<SearchConfiguration> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("ACTIVE", 1);
        entityWrapper.eq("DEFAULT_FLAG", 0);
        entityWrapper.le("BEGIN_DATE", new Date());
        entityWrapper.ge("END_DATE", new Date());
        SearchConfiguration searchConfiguration = searchConfigurationService.selectOne(entityWrapper);
        if (searchConfiguration == null) {
            EntityWrapper<SearchConfiguration> entityWrapper1 = new EntityWrapper<>();
            entityWrapper1.eq("ACTIVE", 1);
            entityWrapper1.eq("DEFAULT_FLAG", 1);
            searchConfiguration = searchConfigurationService.selectOne(entityWrapper1);
        }
        return initSuccessObjectResult(searchConfiguration);
    }

    @ApiOperation(value = "小程序-搜索")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ReturnData<Page<GoodInfoEx>> search(@RequestBody SearchVo searchVo) {
        if (searchVo != null && searchVo.getContent() != null && searchVo.getContent().length() < 30) {
            if (!isChar(searchVo.getContent()) && !isEmoji(searchVo.getContent())) {
                // 分词
                List<Term> list = SpeedTokenizer.segment(searchVo.getContent());
                List<String> words = new ArrayList<>();
                list.stream().filter(r -> r.word.length() > 1).forEach(n -> {
                    words.add(n.word);
                });

                if (words.size() == 0) {
                    words.add(searchVo.getContent());
                }
                return initSuccessObjectResult(searchConfigurationService.getSearchResult(words, searchVo));
            }
        }
        return initSuccessResult();
    }

    public boolean isChar(String string) {
        String regEx = "[`~!@#$%^&*()+=|{}:;\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.matches();
    }

    public boolean isEmoji(String string) {
        Pattern p = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        return m.find();
    }
}

