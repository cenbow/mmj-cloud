package com.mmj.good.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.good.mapper.GoodClassMapper;
import com.mmj.good.model.GoodClass;
import com.mmj.good.service.GoodClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisCacheUtil {

    /**
     * ---------------------------------------商品基础资料-----------------------------------------------------
     */
    public static String GOOD_INFO = "GOOD_INFO";

    /**
     * 根据id查询商品 goodId
     */
    public static String GOOD_INFO_QUERYBYGOODID = "QUERYBYGOODID:";

    /**
     * 根据id查询商品 goodId
     */
    public static String GOOD_INFO_GETBYID = "GETBYID:";

    /**
     * 根据sku查询商品 goodId
     */
    public static String GOOD_INFO_GETBYSKU = "GETBYSKU:";

    /**
     * 商品基础资料查询 goodId
     */
    public static String GOOD_INFO_QUERY = "QUERY:";

    /**
     * 商品基础资料列表查询 currentPage_md5
     */
    public static String GOOD_INFO_QUERYLIST = "QUERYLIST:";

    /**
     * 商品基础资料列表查询-简 currentPage_md5
     */
    public static String GOOD_INFO_QUERYBASELIST = "QUERYBASELIST:";

    /**
     * 橱窗商品查询 showecaseId
     */
    public static String GOOD_INFO_SHOWCASEGOOD = "SHOWCASEGOOD:";

    /**
     * 自定义排序查询(包含置顶) currentPage_md5
     */
    public static String GOOD_INFO_QUERYORDERALL = "QUERYORDERALL:";

    /**
     * 自定义排序查询 currentPage_md5
     */
    public static String GOOD_INFO_QUERYORDERLIST = "QUERYORDERLIST:";

    /**
     * 置顶商品查询 currentPage_md5
     */
    public static String GOOD_INFO_QUERYORDERTOPLIST = "QUERYORDERTOPLIST:";

    /**
     * 订单查询商品
     */
    public static String GOOD_INFO_QUERYGOODTT = "QUERYGOODTT:";

    /**
     * 首页橱窗查询
     */
    public static String WEB_SHOWCASE_KEY = "webShowcase";

    /**
     * 十元三件商品缓存
     */
    public static String THREESALETENNER_GOOD = "ThreeSaleTennerGood";


    public static List<String> getGoodInfoKeys;

    static {
        getGoodInfoKeys = new ArrayList<>();
        getGoodInfoKeys.add(WEB_SHOWCASE_KEY + "*");
        getGoodInfoKeys.add(THREESALETENNER_GOOD + "*");
    }

    public static void clearGoodInfoCache(RedisTemplate redisTemplate) {
        //商品缓存
        redisTemplate.delete(GOOD_INFO);
        //其他
        for (String key : getGoodInfoKeys) {
            Set keys = redisTemplate.keys(key);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        }
    }


    /**
     * ---------------------------------------商品详情资料-----------------------------------------------------
     */
    public static String GOOD_DETAIL = "GOOD_DETAIL";

    /**
     * 商品详情资料查询 goodId
     */
    public static String GOOD_DETAIL_QUERYINFO = "QUERYINFO:";

    public static String GOOD_DETAIL_QUERYBYGOODID = "QUERYBYGOODID:";

    public static List<String> getGoodFileKeys;

    static {
        getGoodFileKeys = new ArrayList<>();
        getGoodFileKeys.add(WEB_SHOWCASE_KEY + "*");
        getGoodFileKeys.add(THREESALETENNER_GOOD + "*");
    }

    public static void clearGoodFileCache(RedisTemplate redisTemplate) {
        //商品缓存
        redisTemplate.delete(GOOD_DETAIL);
        for (String key : getGoodFileKeys) {
            Set keys = redisTemplate.keys(key);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        }
    }

    /**
     * ---------------------------------------商品销售资料-----------------------------------------------------
     */
    public static String GOOD_SALE = "GOOD_SALE";

    /**
     * 商品销售资料查询 goodId
     */
    public static String GOOD_SALE_QUERY = "GOOD_SALE:QUERY:";

    /**
     * 商品销售资料分组查询 md5
     */
    public static String GOOD_SALE_QUERYGROUPBYINFO = "GOOD_SALE:QUERYGROUPBYINFO:";

    /**
     * 商品销售资料列表查询-简
     */
    public static String GOOD_SALE_QUERYLIST = "GOOD_SALE:QUERYLIST:";

    public static List<String> getGoodSaleKeys;

    static {
        getGoodSaleKeys = new ArrayList<>();
        getGoodSaleKeys.add(WEB_SHOWCASE_KEY + "*");
        getGoodSaleKeys.add(THREESALETENNER_GOOD + "*");
    }

    public static void clearGoodSaleCache(RedisTemplate redisTemplate) {
        //商品缓存
        redisTemplate.delete(GOOD_INFO);
        redisTemplate.delete(GOOD_SALE);
        for (String key : getGoodSaleKeys) {
            Set keys = redisTemplate.keys(key);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        }
    }

    public static String getKey(Object o) throws UnsupportedEncodingException {
        return DigestUtils.md5DigestAsHex(JSON.toJSONString(o).getBytes("UTF-8"));
    }

}

