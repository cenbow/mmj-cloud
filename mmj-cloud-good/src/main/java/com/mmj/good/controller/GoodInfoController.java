package com.mmj.good.controller;


import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.controller.BaseController;
import com.mmj.common.exception.BaseException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.ThreeSaleTennerOrder;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.feigin.ActiveFeignClient;
import com.mmj.good.feigin.dto.*;
import com.mmj.good.model.*;
import com.mmj.good.service.GoodClassService;
import com.mmj.good.service.GoodFileService;
import com.mmj.good.service.GoodInfoService;
import com.mmj.good.service.GoodSaleService;
import com.mmj.good.util.GoodUtil;
import com.mmj.good.util.MQProduceGood;
import com.mmj.good.util.RedisCacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-03
 */
@RestController
@RequestMapping("/goodInfo")
@Api(value = "商品管理")
public class GoodInfoController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodInfoController.class);

    @Autowired
    GoodInfoService goodInfoService;

    @Autowired
    GoodFileService goodFileService;

    @Autowired
    private ActiveFeignClient activeFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private GoodSaleService goodSaleService;

    @Autowired
    private GoodClassService goodClassService;

    @Value("${good.config.fileServer}")
    private String fileServer;

    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/queryGood", method = RequestMethod.POST)
    public ReturnData<List<GoodInfo>> queryGood(@RequestBody GoodInfoBaseEx goodInfo) {
        EntityWrapper<GoodInfo> entityWrapper = new EntityWrapper<>(JSON.parseObject(JSON.toJSONString(goodInfo), GoodInfo.class));
        entityWrapper.like(goodInfo.getGoodClassLike() != null && goodInfo.getGoodClassLike().length() > 0, "GOOD_CLASS", goodInfo.getGoodClassLike() + "%", SqlLike.CUSTOM);
        return initSuccessObjectResult(goodInfoService.selectList(entityWrapper));
    }

    @ApiOperation(value = "根据id查询商品")
    @RequestMapping(value = "/queryByGoodId/{goodId}", method = RequestMethod.POST)
    public ReturnData<Object> queryByGoodId(@PathVariable("goodId") Integer goodId) {
        String key = RedisCacheUtil.GOOD_INFO_QUERYBYGOODID + goodId;
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseObject(String.valueOf(o)));
        }
        GoodInfo goodInfo = goodInfoService.selectById(goodId);
        if (goodInfo != null) {
            String jsonString = JSON.toJSONString(goodInfo);
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, jsonString);
            return initSuccessObjectResult(JSON.parseObject(jsonString));
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "新增或更新商品基本信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData<Map> save(@RequestBody GoodInfoBaseEx entityEx) {
        RedisCacheUtil.clearGoodInfoCache(redisTemplate);
        EntityWrapper<GoodInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_SPU", entityEx.getGoodSpu());
        entityWrapper.ne("GOOD_STATUS", GoodConstants.InfoStatus.DELETED);
        entityWrapper.ne(entityEx.getGoodId() != null, "GOOD_ID", entityEx.getGoodId());
        List<GoodInfo> goodInfos = goodInfoService.selectList(entityWrapper);
        if (goodInfos != null && !goodInfos.isEmpty()) {
            if (entityEx.getGoodId() == null) {
                return initExcetionObjectResult("该spu已重复！");
            }
        }
        try {
            Integer goodId = goodInfoService.saveInfo(entityEx);
            Map<String, Integer> map = new HashMap<>();
            map.put("goodId", goodId);
            return initSuccessObjectResult(map);
        } catch (BaseException e) {
            return initExcetionObjectResult(e.getMessage());
        }
    }

    @ApiOperation(value = "新增或更新商品详情")
    @RequestMapping(value = "/saveDetail", method = RequestMethod.POST)
    public ReturnData saveDetail(@RequestBody GoodInfoBaseEx entityEx) throws Exception {
        RedisCacheUtil.clearGoodFileCache(redisTemplate);
        goodInfoService.saveDetailInfo(entityEx);
        return initSuccessResult();
    }


    @ApiOperation(value = "商品基础资料查询")
    @RequestMapping(value = "/query/{goodId}", method = RequestMethod.POST)
    public ReturnData<Object> query(@PathVariable Integer goodId) {
        String key = RedisCacheUtil.GOOD_INFO_QUERY + goodId;
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseObject(String.valueOf(o)));
        }
        List<String> fileTypes = new ArrayList<>();
        fileTypes.add(GoodConstants.FileType.SELLING_POINT);
        fileTypes.add(GoodConstants.FileType.IMAGE);
        fileTypes.add(GoodConstants.FileType.MAINVIDEO);
        fileTypes.add(GoodConstants.FileType.VIDEOTITLE);
        fileTypes.add(GoodConstants.FileType.WECHAT);
        fileTypes.add(GoodConstants.FileType.H5);
        GoodInfo goodInfo = goodInfoService.selectById(goodId);
        List<GoodFile> goodFiles = goodFileService.queryByGoodId(goodId, GoodConstants.ActiveType.SHOP_GOOD, fileTypes);
        Map<String, Object> map = new HashMap<>();
        map.put("goodInfo", goodInfo);
        map.put("goodFiles", goodFiles);
        if (goodInfo != null && goodFiles != null) {
            String jsonString = JSON.toJSONString(map);
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, jsonString);
            return initSuccessObjectResult(JSON.parseObject(jsonString));
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "商品基础资料列表查询")
    @RequestMapping(value = "/queryListBoss", method = RequestMethod.POST)
    public ReturnData<Page<GoodInfoBaseQueryEx>> queryListBoss(@RequestBody GoodInfoBaseQueryEx entityEx) {
        Page<GoodInfoBaseQueryEx> goodInfoBaseQueryExPage = goodInfoService.queryList(entityEx);
        if (goodInfoBaseQueryExPage != null && goodInfoBaseQueryExPage.getRecords() != null && !goodInfoBaseQueryExPage.getRecords().isEmpty()) {
            List<GoodInfoBaseQueryEx> records = goodInfoBaseQueryExPage.getRecords();
            for (GoodInfoBaseQueryEx ex : records) {
                if (ex.getCombinaFlag() == 1) {
                    ex.setGoodNum(goodSaleService.sumCombNum(ex.getGoodId()));
                } else {
                    ex.setGoodNum(goodSaleService.sumNum(ex.getGoodId()));
                }
            }
        }
        return initSuccessObjectResult(goodInfoBaseQueryExPage);
    }

    @ApiOperation(value = "商品基础资料列表查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<Object> queryList(@RequestBody GoodInfoBaseQueryEx entityEx) throws Exception {
        String key = RedisCacheUtil.GOOD_INFO_QUERYLIST + entityEx.getCurrentPage() + RedisCacheUtil.getKey(entityEx);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseObject(String.valueOf(o)));
        }
        Page<GoodInfoBaseQueryEx> goodInfoBaseQueryExPage = goodInfoService.queryList(entityEx);
        if (goodInfoBaseQueryExPage != null && goodInfoBaseQueryExPage.getRecords() != null && !goodInfoBaseQueryExPage.getRecords().isEmpty()) {
            String jsonString = JSON.toJSONString(goodInfoBaseQueryExPage);
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, jsonString);
            return initSuccessObjectResult(JSON.parseObject(jsonString));
        } else {
            return initSuccessObjectResult(new Page<GoodInfoBaseQueryEx>());
        }
    }

    @ApiOperation(value = "商品基础资料列表查询-简")
    @RequestMapping(value = "/queryBaseList", method = RequestMethod.POST)
    public ReturnData<Object> queryBaseList(@RequestBody GoodInfoBaseQueryEx entityEx) throws Exception {
        String key = RedisCacheUtil.GOOD_INFO_QUERYBASELIST + entityEx.getCurrentPage() + RedisCacheUtil.getKey(entityEx);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseObject(String.valueOf(o)));
        }
        Page<GoodInfoBaseQueryEx> goodInfoBaseQueryExPage = goodInfoService.queryBaseList(entityEx);
        if (goodInfoBaseQueryExPage != null && goodInfoBaseQueryExPage.getRecords() != null && !goodInfoBaseQueryExPage.getRecords().isEmpty()) {
            String jsonString = JSON.toJSONString(goodInfoBaseQueryExPage);
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, jsonString);
            return initSuccessObjectResult(JSON.parseObject(jsonString));
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "商品批量下架")
    @RequestMapping(value = "/unshelve", method = RequestMethod.POST)
    public ReturnData unshelve(@RequestBody List<Integer> goodIds) {
        if (goodIds != null && !goodIds.isEmpty()) {
            RedisCacheUtil.clearGoodInfoCache(redisTemplate);
            goodInfoService.unshelve(goodIds);
            JSONObject o = new JSONObject();
            o.put("goodStatus", GoodConstants.InfoStatus.WAIT_ON);
            o.put("goodIds", goodIds);
            MQProduceGood.goodStatusUpdate(kafkaTemplate, o.toJSONString());
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "商品批量上架")
    @RequestMapping(value = "/onshelve", method = RequestMethod.POST)
    public ReturnData onshelve(@RequestBody List<Integer> goodIds) {
        if (goodIds != null && !goodIds.isEmpty()) {
            //查询
            GoodSaleEx goodSaleEx = new GoodSaleEx();
            goodSaleEx.setGoodIds(goodIds);
            List<GoodSaleEx> goodSaleExes = goodSaleService.queryGroupByInfo(goodSaleEx);
            if (goodSaleExes == null || goodSaleExes.size() != goodIds.size()) {
//                List<Integer> collect = goodSaleExes.stream().map(GoodSaleEx::getGoodId).collect(Collectors.toList());
//                goodIds.removeAll(collect);
                return initExcetionObjectResult("上架失败，商品信息不完整!");
            } else {
                for (GoodSaleEx goodSaleEx1 : goodSaleExes) {
                    if (goodSaleEx1.getGoodNum() <= 0) {
                        return initExcetionObjectResult("上架失败，商品信息不完整!");
                    }
                }
            }
            RedisCacheUtil.clearGoodInfoCache(redisTemplate);
            goodInfoService.onshelve(goodIds);
            JSONObject o = new JSONObject();
            o.put("goodStatus", GoodConstants.InfoStatus.PUT_ON);
            o.put("goodIds", goodIds);
            MQProduceGood.goodStatusUpdate(kafkaTemplate, o.toJSONString());
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "商品删除")
    @RequestMapping(value = "/delete/{goodId}", method = RequestMethod.POST)
    public ReturnData delete(@PathVariable Integer goodId) {
        RedisCacheUtil.clearGoodInfoCache(redisTemplate);
        GoodInfo goodInfo = new GoodInfo();
        goodInfo.setGoodStatus(GoodConstants.InfoStatus.DELETED);
        goodInfo.setDelFlag(GoodConstants.InfoDelFlag.YES);
        EntityWrapper<GoodInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("GOOD_ID", goodId);
        goodInfoService.update(goodInfo, wrapper);
        return initSuccessResult();
    }

    @ApiOperation(value = "批量验证商品编号是否存在 - 橱窗管理使用 - dashu")
    @GetMapping("/batchVerifyGoodSpu")
    public ReturnData batchVerifyGoodSpu(@RequestParam("spuList") List<String> spuList) {
        return initSuccessObjectResult(goodInfoService.batchVerifyGoodSpu(spuList));
    }

    @ApiOperation(value = "通过商品分类查询商品数量")
    @RequestMapping(value = "/queryCountByClassCode/{classCode}", method = RequestMethod.POST)
    public ReturnData<Integer> queryCountByClassCode(@PathVariable String classCode) {
        EntityWrapper<GoodInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_CLASS", classCode);
        int num = goodInfoService.selectCount(entityWrapper);
        return initSuccessObjectResult(num);
    }

    /**
     * 橱窗管理关联商品缓存
     */
    String SHOWCASE_GOOD = "showcaseGOOD:";

    @ApiOperation(value = "橱窗商品查询")
    @RequestMapping(value = "/showcaseGood/{showecaseId}", method = RequestMethod.POST)
    public ReturnData<Object> showcaseGood(@PathVariable Integer showecaseId) {
        logger.info("-->/goodInfo/showcaseGood/-->商品管理:橱窗商品查询，橱窗id：{}", showecaseId);
        String key = RedisCacheUtil.GOOD_INFO_SHOWCASEGOOD + showecaseId;
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseArray(String.valueOf(o)));
        }
        List<Object> goodList = redisTemplate.opsForList().range(SHOWCASE_GOOD + String.valueOf(showecaseId), 0, -1);
        logger.info("-->/goodInfo/showcaseGood/-->商品管理:从缓存中获取橱窗对应的商品ids，橱窗id：{},商品ids:{},", showecaseId, goodList);
        List<Integer> goodIds = new ArrayList<>();
        List<String> goodIdsJoin = new ArrayList<>();
        if (goodList != null && !goodList.isEmpty()) {
            CollectionUtils.collect(goodList,
                    new Transformer() {
                        public Object transform(Object input) {
                            return input;
                        }
                    }, goodIds);

            CollectionUtils.collect(goodList,
                    new Transformer() {
                        public Object transform(Object input) {
                            return String.valueOf(input);
                        }
                    }, goodIdsJoin);

        } else {
            ReturnData<WebShowcaseEx> webShowcaseExReturnData = activeFeignClient.selectByShowecaseId(showecaseId);
            if (webShowcaseExReturnData != null && webShowcaseExReturnData.getCode() == SecurityConstants.SUCCESS_CODE && webShowcaseExReturnData.getData() != null) {
                WebShowcaseEx data = webShowcaseExReturnData.getData();
                List<ShowcaseGood> showcaseGood = data.getShowcaseGood();
                if (showcaseGood != null && !showcaseGood.isEmpty()) {
                    goodIds = showcaseGood.stream().map(ShowcaseGood::getGoodId).collect(Collectors.toList());
                    logger.info("-->/goodInfo/showcaseGood/-->商品管理:从数据库中获取橱窗对应的商品ids，橱窗id：{},商品ids:{},", showecaseId, goodIds);
                    CollectionUtils.collect(goodIds,
                            new Transformer() {
                                public Object transform(Object input) {
                                    return String.valueOf(input);
                                }
                            }, goodIdsJoin);
                } else {
                    return initSuccessResult();
                }
            } else {
                return initSuccessResult();
            }
        }
        //查询
        GoodInfoEx entityEx = new GoodInfoEx();
        entityEx.setPageSize(Integer.MAX_VALUE);
        entityEx.setDelFlag(0);
        entityEx.setFileServer(fileServer);
        entityEx.setGoodIds(goodIds);
        entityEx.setGoodStatus(GoodConstants.InfoStatus.PUT_ON);
        //entityEx.setVirtualFlag(GoodConstants.InfoVirtualFlag.NO);
        //entityEx.setAutoShow(GoodConstants.InfoAutoShow.YES);
        entityEx.setOrderType(String.format(" FIELD(GOOD_ID, %s) ", String.join(",", goodIdsJoin)));
        Page<GoodInfoEx> goodInfoExPage = goodInfoService.queryOrderList(entityEx);
        logger.info("-->/goodInfo/showcaseGood/-->商品管理:根据橱窗id获取对应的商品信息,橱窗id：{},商品信息:{},", showecaseId, JSON.toJSONString(goodInfoExPage));
        if (goodInfoExPage != null && goodInfoExPage.getRecords() != null && !goodInfoExPage.getRecords().isEmpty()) {
            //返回json
            String jsonString = JSON.toJSONString(goodInfoExPage.getRecords());
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, jsonString);
            return initSuccessObjectResult(JSON.parseArray(jsonString));
        }
        return initSuccessResult();
    }

    /**
     * @param entityEx activeType   活动类型  8 主题  9 猜你喜欢  10 免邮热卖  11 分类商品  14 转盘6个十元店商品
     *                 goodStatus   商品状态        -1：删除 0：暂不发布 1：立即上架 2：自动上架
     *                 virtualFlag  是否虚拟商品      0：否 1：是
     *                 memberFlag   是否会员商品      0：否 1：是
     *                 combinaFlag  是否组合商品      0：否 1：是
     *                 --delFlag    是否删除          0：否 1：是
     *                 autoShow     是否自动展示      0：否 1：是
     *                 fileServer   文件服务商       ALIYUN TENGXUN （目前默认传 TENGXUN）
     *                 showFlag     分类是否展示      0：否 1：是
     *                 classCodes   商品分类集合
     *                 --arg1         1：置顶商品  2：例外商品
     *                 <p>
     *                 goodIds      商品id集合
     *                 noGoodIds    例外商品id
     *                 topGoodIds   置顶商品id
     *                 orderBy      排序规则
     * @return
     */
    @ApiOperation(value = "自定义排序查询(包含置顶)")
    @RequestMapping(value = "/queryOrderAll", method = RequestMethod.POST)
    public ReturnData<Object> queryOrderAll(@RequestBody GoodInfoEx entityEx) throws Exception {
        //从缓存获取
        String key = RedisCacheUtil.GOOD_INFO_QUERYORDERALL + entityEx.getActiveType() + RedisCacheUtil.getKey(entityEx);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            logger.info("从缓存中查询到商品信息,直接返回:{}", o);
            return initSuccessObjectResult(JSON.parseObject(String.valueOf(o)));
        }
        List<GoodInfoEx> goodInfoExes = new ArrayList<>();
        //置顶商品
        if (entityEx.getCurrentPage() == 1) {
            GoodInfoEx goodInfoExTop = new GoodInfoEx();
            goodInfoExTop.setCurrentPage(1);
            goodInfoExTop.setPageSize(Integer.MAX_VALUE);
            goodInfoExTop.setActiveType(entityEx.getActiveType());
            goodInfoExTop.setGoodStatus(entityEx.getGoodStatus());
            goodInfoExTop.setVirtualFlag(entityEx.getVirtualFlag());
            goodInfoExTop.setMemberFlag(entityEx.getMemberFlag());
            goodInfoExTop.setCombinaFlag(entityEx.getCombinaFlag());
            goodInfoExTop.setNoGoodIds(entityEx.getNoGoodIds());
            goodInfoExTop.setBusinessId(entityEx.getBusinessId());
            ReturnData<Page<GoodInfoEx>> pageReturnData1 = queryAllTopList(goodInfoExTop);
            if (pageReturnData1 != null && pageReturnData1.getData() != null && pageReturnData1.getData().getRecords() != null && pageReturnData1.getData().getRecords().size() != 0) {
                List<GoodInfoEx> records = pageReturnData1.getData().getRecords();
                goodInfoExes.addAll(records);
                entityEx.setNoGoodIds(records.stream().map(GoodInfoEx::getGoodId).collect(Collectors.toList()));
            }
        }
        //查询
        entityEx.setAutoShow(1);
        ReturnData<Page<GoodInfoEx>> pageReturnData = queryAllList(entityEx);
        if (pageReturnData != null && pageReturnData.getData() != null && pageReturnData.getData().getRecords() != null && pageReturnData.getData().getRecords().size() != 0) {
            goodInfoExes.addAll(pageReturnData.getData().getRecords());
        }
        //结果少于30个 查询
        if (entityEx.getActiveType() == GoodConstants.ActiveType.GUESS_LIKE && entityEx.getGoodClass() != null && !"".equals(entityEx.getGoodClass())) {//猜你喜欢本类商品不足补充
            String goodClassOld = entityEx.getGoodClass();
            if (goodInfoExes.size() < 30) {
                entityEx.setPageSize(entityEx.getPageSize() - goodInfoExes.size());
                if (goodClassOld.length() == 8) {
                    entityEx.setClassCodeLike(goodClassOld.substring(0, 6));
                }
                if (goodClassOld.length() == 6) {
                    entityEx.setClassCodeLike(goodClassOld.substring(0, 4));
                }
                if (goodClassOld.length() == 4) {
                    entityEx.setClassCodeLike(null);
                }
                entityEx.setNoClassCodes(Arrays.asList(goodClassOld));
                entityEx.setGoodClass(null);
                entityEx.setPageSize(30 - goodInfoExes.size());
                //结果少于30个 再查询
                Page<GoodInfoEx> pageReturnData2 = goodInfoService.queryOrderList(entityEx);
                entityEx.setGoodClass(entityEx.getClassCodeLike());
                if (pageReturnData2 != null && pageReturnData2.getRecords() != null && pageReturnData2.getRecords().size() != 0) {
                    goodInfoExes.addAll(pageReturnData2.getRecords());
                }
                if (goodInfoExes.size() < 30 && goodClassOld.length() > 4) {
                    entityEx.setPageSize(30 - goodInfoExes.size());
                    if (goodClassOld.length() == 8) {
                        EntityWrapper<GoodClass> entityWrapper = new EntityWrapper<>();
                        entityWrapper.like("CLASS_CODE", entityEx.getClassCodeLike() + "%", SqlLike.CUSTOM);
                        entityWrapper.eq("DEL_FLAG", 0);
                        List<GoodClass> goodClasses = goodClassService.selectList(entityWrapper);
                        if (goodClasses != null && !goodClasses.isEmpty()) {
                            List<String> collect = goodClasses.stream().map(GoodClass::getClassCode).collect(Collectors.toList());
                            collect.add(entityEx.getGoodClass());
                            entityEx.setNoClassCodes(collect);
                        }
                        entityEx.setClassCodeLike(goodClassOld.substring(0, 4));
                    }
                    if (goodClassOld.length() == 6) {
                        entityEx.setClassCodeLike(null);
                        entityEx.setNoClassCodes(Arrays.asList(entityEx.getGoodClass()));
                    }
                    entityEx.setGoodClass(null);
                    Page<GoodInfoEx> pageReturnData3 = goodInfoService.queryOrderList(entityEx);
                    if (pageReturnData3 != null && pageReturnData3.getRecords() != null && pageReturnData3.getRecords().size() != 0) {
                        goodInfoExes.addAll(pageReturnData3.getRecords());
                    }
                }
            }
        }
        if (goodInfoExes != null && !goodInfoExes.isEmpty()) {
            //返回json
            pageReturnData.getData().setRecords(goodInfoExes);
            String jsonString = JSON.toJSONString(pageReturnData.getData());
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, jsonString);
            return initSuccessObjectResult(JSON.parseObject(jsonString));
        }
        return initSuccessResult();
    }

    public ReturnData<Page<GoodInfoEx>> queryAllList(@RequestBody GoodInfoEx entityEx) {
        getOrderListEntity(entityEx);
        return initSuccessObjectResult(goodInfoService.queryOrderList(entityEx));
    }

    /**
     * @param entityEx activeType   活动类型  8 主题  9 猜你喜欢  10 免邮热卖  11 分类商品  14 转盘26个十元店商品 18 销量前十
     *                 goodStatus   商品状态        -1：删除 0：暂不发布 1：立即上架 2：自动上架
     *                 virtualFlag  是否虚拟商品      0：否 1：是
     *                 memberFlag   是否会员商品      0：否 1：是
     *                 combinaFlag  是否组合商品      0：否 1：是
     *                 --delFlag    是否删除        0：否 1：是
     *                 autoShow     是否自动展示      0：否 1：是
     *                 fileServer   文件服务商       ALIYUN TENGXUN （目前默认传 TENGXUN）
     *                 showFlag     分类是否展示      0：否 1：是
     *                 classCodes   商品分类集合
     *                 --arg1         1：置顶商品  2：例外商品
     *                 <p>
     *                 goodIds      商品id集合
     *                 noGoodIds    例外商品id
     *                 topGoodIds   置顶商品id
     *                 orderBy      排序规则
     * @return
     */
    @ApiOperation(value = "自定义排序查询")
    @RequestMapping(value = "/queryOrderList", method = RequestMethod.POST)
    public ReturnData<Object> queryOrderList(@RequestBody GoodInfoEx entityEx) throws Exception {
        //从缓存获取
        String key = RedisCacheUtil.GOOD_INFO_QUERYORDERLIST + entityEx.getActiveType() + RedisCacheUtil.getKey(entityEx);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseObject(String.valueOf(o)));
        }
        //查询
        getOrderListEntity(entityEx);
        entityEx.setAutoShow(1);
        Page<GoodInfoEx> goodInfoExPage = goodInfoService.queryOrderList(entityEx);
        List<GoodInfoEx> goodInfoExes = new ArrayList<>();
        if (goodInfoExPage != null && goodInfoExPage.getRecords() != null && !goodInfoExPage.getRecords().isEmpty()) {
            goodInfoExes.addAll(goodInfoExPage.getRecords());
        }
        //结果少于24个 查询
        if (GoodConstants.ActiveType.HOT_SALE == entityEx.getActiveType() && entityEx.getClassCodes() != null && !"".equals(entityEx.getClassCodes())) {
            if (goodInfoExes.size() < 24) {
                List<String> classCodesOld = entityEx.getClassCodes();
                List<String> classCodes = new ArrayList<>();
                boolean flag = false;
                for (String c : classCodesOld) {
                    EntityWrapper<GoodClass> entityWrapper = new EntityWrapper<>();
                    if (c.length() == 8) {
                        entityWrapper.like("CLASS_CODE", c.substring(0, 6) + "%", SqlLike.CUSTOM);
                    }
                    if (c.length() == 6) {
                        entityWrapper.like("CLASS_CODE", c.substring(0, 4) + "%", SqlLike.CUSTOM);
                    }
                    if (c.length() == 4) {
                        flag = true;
                        break;
                    }
                    entityWrapper.eq("DEL_FLAG", 0);
                    List<GoodClass> goodClasses = goodClassService.selectList(entityWrapper);
                    if (goodClasses != null && !goodClasses.isEmpty()) {
                        classCodes.addAll(goodClasses.stream().map(GoodClass::getClassCode).collect(Collectors.toList()));
                    }
                }
                if (classCodes != null && !classCodes.isEmpty()) {
                    classCodes.removeAll(classCodesOld);
                    if (!flag) {
                        entityEx.setClassCodes(classCodes);
                    }
                    entityEx.setGoodIds(null);
                    entityEx.setPageSize(24 - goodInfoExes.size());
                    entityEx.setCurrentPage(1);
                    //结果少于24个 再查询
                    Page<GoodInfoEx> pageReturnData2 = goodInfoService.queryOrderList(entityEx);
                    if (pageReturnData2 != null && pageReturnData2.getRecords() != null && pageReturnData2.getRecords().size() != 0) {
                        goodInfoExes.addAll(pageReturnData2.getRecords());
                    }
                    if (goodInfoExes.size() < 24) {
                        List<String> classCodes1 = new ArrayList<>();
                        int breakFlag = 0;
                        for (String c : classCodesOld) {
                            EntityWrapper<GoodClass> entityWrapper = new EntityWrapper<>();
                            if (c.length() == 8) {
                                entityWrapper.like("CLASS_CODE", c.substring(0, 4) + "%", SqlLike.CUSTOM);
                            }
                            if (c.length() == 6) {
                                breakFlag = 2;
                                break;
                            }
                            if (c.length() == 4) {
                                breakFlag = 1;
                                break;
                            }
                            entityWrapper.eq("DEL_FLAG", 0);
                            List<GoodClass> goodClasses = goodClassService.selectList(entityWrapper);
                            if (goodClasses != null && !goodClasses.isEmpty()) {
                                classCodes1.addAll(goodClasses.stream().map(GoodClass::getClassCode).collect(Collectors.toList()));
                            }
                        }
                        if (classCodes1 != null && !classCodes1.isEmpty() && breakFlag != 1) {
                            classCodes1.removeAll(classCodes);
                            classCodes1.removeAll(classCodesOld);
                            if (breakFlag == 0) {
                                entityEx.setClassCodes(classCodes1);
                            }
                            entityEx.setPageSize(24 - goodInfoExes.size());
                            Page<GoodInfoEx> pageReturnData3 = goodInfoService.queryOrderList(entityEx);
                            if (pageReturnData3 != null && pageReturnData3.getRecords() != null && pageReturnData3.getRecords().size() != 0) {
                                goodInfoExes.addAll(pageReturnData3.getRecords());
                            }
                        }
                    }
                }
            }
        }

        if (goodInfoExes != null && !goodInfoExes.isEmpty()) {
            goodInfoExPage.setRecords(goodInfoExes);
            //返回json
            String jsonString = JSON.toJSONString(goodInfoExPage);
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, jsonString);
            return initSuccessObjectResult(JSON.parseObject(jsonString));
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "查询id排序-十元三件")
    @RequestMapping(value = "/threeSaleTennerOrder", method = RequestMethod.POST)
    public ReturnData<List<Integer>> threeSaleTennerOrder(@RequestBody ThreeSaleTennerOrder threeSaleTennerOrder) {
        logger.info("-->/goodInfo/threeSaleTennerOrder/-->商品管理:十元三件商品查询，查询参数:{}", JSON.toJSONString(threeSaleTennerOrder));
        ActiveSort activeOrder = new ActiveSort();
        activeOrder.setOrderType(threeSaleTennerOrder.getOrderType());
        activeOrder.setFilterRule(threeSaleTennerOrder.getFilterRule());
        activeOrder.setOrderBy(threeSaleTennerOrder.getOrderBy());
        String orderSql = GoodUtil.getOrderSql(activeOrder);
        if (orderSql.indexOf("FIELD") != -1) {
            List<String> goodList = new ArrayList<>();
            CollectionUtils.collect(goodList,
                    new Transformer() {
                        public Object transform(Object input) {
                            return input;
                        }
                    }, threeSaleTennerOrder.getGoodIdList());
            orderSql = String.format(" FIELD(GOOD_ID, %s) ", String.join(",", goodList));
        }
        GoodInfoEx entityEx = new GoodInfoEx();
        entityEx.setGoodIds(threeSaleTennerOrder.getGoodIdList());
        entityEx.setCurrentPage(threeSaleTennerOrder.getCurrentPage());
        entityEx.setPageSize(threeSaleTennerOrder.getPageSize());
        entityEx.setOrderType(orderSql);
        Page<GoodInfoEx> goodInfoExPage = goodInfoService.queryOrderList(entityEx);
        logger.info("-->/goodInfo/threeSaleTennerOrder/-->商品管理:十元三件商品查询，查询结果:{}", JSON.toJSONString(goodInfoExPage));
        if (goodInfoExPage != null && goodInfoExPage.getRecords() != null && !goodInfoExPage.getRecords().isEmpty()) {
            List<Integer> goodIds = goodInfoExPage.getRecords().stream().map(GoodInfoEx::getGoodId).collect(Collectors.toList());
            return initSuccessObjectResult(goodIds);
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "查询id排序-专题拼团")
    @RequestMapping(value = "/queryGoodIdOrder", method = RequestMethod.POST)
    public ReturnData<List<Integer>> queryGoodIdOrder(@RequestBody GoodInfoEx entityEx) {
        ActiveSort activeOrder = new ActiveSort();
        activeOrder.setActiveType(entityEx.getActiveType());
        activeOrder.setBusinessId(entityEx.getBusinessId());
        //获取分类
        Integer topicId = entityEx.getBusinessId();
        ReturnData<TopicInfo> returnData = activeFeignClient.getTopicById(topicId);
        if (returnData != null && returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData() != null) {
            //获取排序规则
            ReturnData<List<ActiveSort>> returnDataOrder = activeFeignClient.queryList(activeOrder);
            String orderSql = GoodUtil.getOrderSql(returnDataOrder == null ? null : (returnDataOrder.getData() == null || returnDataOrder.getData().size() <= 0 ? null : returnDataOrder.getData().get(0)));
            //专题类型
            TopicInfo topicInfo = returnData.getData();
            if ("GOOD".equals(topicInfo.getTopicGoodClass())) {//关联商品
                //查询商品
                orderSql = getGoodIds(entityEx.getActiveType(), topicId, orderSql, entityEx);
                entityEx.setAutoShow(null);
            } else {//关联分类
                if (orderSql.indexOf("FIELD") != -1) {
                    orderSql = " CREATER_TIME desc, GOOD_ID ";
                }
                List<String> goodClasses = Arrays.asList(topicInfo.getTopicGoodClass().split(","));
                List<String> codes = new ArrayList<>();
                if (goodClasses != null && !goodClasses.isEmpty()) {
                    codes.addAll(goodClasses);
                    for (String classCode : goodClasses) {
                        EntityWrapper<GoodClass> goodClassEntityWrapper = new EntityWrapper<>();
                        goodClassEntityWrapper.like("CLASS_CODE", classCode + "%", SqlLike.CUSTOM);
                        List<GoodClass> goodClasses1 = goodClassService.selectList(goodClassEntityWrapper);
                        if (goodClasses1 != null && !goodClasses1.isEmpty()) {
                            codes.addAll(goodClasses1.stream().map(GoodClass::getClassCode).collect(Collectors.toList()));
                        }
                    }
                }
                //获取分类
                if (null != codes && !codes.isEmpty()) {
                    entityEx.setClassCodes(codes);
                }
                entityEx.setAutoShow(1);
                entityEx.setOrderType(orderSql);
            }
            entityEx.setAutoShow(1);
            Page<GoodInfoEx> goodInfoExPage = goodInfoService.queryOrderList(entityEx);
            if (goodInfoExPage != null && goodInfoExPage.getRecords() != null && !goodInfoExPage.getRecords().isEmpty()) {
                List<Integer> goodIds = goodInfoExPage.getRecords().stream().map(GoodInfoEx::getGoodId).collect(Collectors.toList());
                return initSuccessObjectResult(goodIds);
            }
        }
        return initSuccessResult();
    }

    /**
     * arg1         1：置顶商品
     *
     * @param entityEx
     * @return
     */
    @ApiOperation(value = "置顶商品查询")
    @RequestMapping(value = "/queryOrderTopList", method = RequestMethod.POST)
    public ReturnData<Object> queryOrderTopList(@RequestBody GoodInfoEx entityEx) throws Exception {
        //从缓存获取
        String key = RedisCacheUtil.GOOD_INFO_QUERYORDERTOPLIST + entityEx.getActiveType() + RedisCacheUtil.getKey(entityEx);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseObject(String.valueOf(o)));
        }
        //获取置顶商品
        List<Integer> goodIds = new ArrayList<>();
        ActiveGood activeGood = new ActiveGood();
        activeGood.setActiveType(entityEx.getActiveType());
        activeGood.setArg1("1");
        activeGood.setBusinessId(entityEx.getBusinessId());
        ReturnData<List<Integer>> listReturnData = activeFeignClient.queryGoodIds(activeGood);
        if (listReturnData != null && listReturnData.getCode() == SecurityConstants.SUCCESS_CODE && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
            goodIds = listReturnData.getData();
            //查询
            entityEx.setDelFlag(0);
            entityEx.setFileServer(fileServer);
            entityEx.setGoodIds(goodIds);
            //join商品
            List<String> goodIdsJoin = new ArrayList<>();
            CollectionUtils.collect(goodIds,
                    new Transformer() {
                        public Object transform(Object input) {
                            return String.valueOf(input);
                        }
                    }, goodIdsJoin);
            entityEx.setOrderType(String.format(" FIELD(GOOD_ID, %s) ", String.join(",", goodIdsJoin)));
            entityEx.setAutoShow(null);
            Page<GoodInfoEx> goodInfoExPage = goodInfoService.queryOrderList(entityEx);
            if (goodInfoExPage != null && goodInfoExPage.getRecords() != null && !goodInfoExPage.getRecords().isEmpty()) {
                //返回json
                String jsonString = JSON.toJSONString(goodInfoExPage);
                redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, jsonString);
                return initSuccessObjectResult(JSON.parseObject(jsonString));
            }
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "修改商品状态")
    @RequestMapping(value = "/updateGoodStatus/{goodId}/{goodStatus}", method = RequestMethod.POST)
    public ReturnData updateGoodStatus(@PathVariable(value = "goodId") Integer goodId, @PathVariable(value = "goodStatus") String goodStatus) {
        RedisCacheUtil.clearGoodInfoCache(redisTemplate);
        if (goodStatus == GoodConstants.InfoStatus.PUT_ON) {
            goodInfoService.onshelve(Arrays.asList(goodId));
        } else if (goodStatus == GoodConstants.InfoStatus.WAIT_ON) {
            goodInfoService.unshelve(Arrays.asList(goodId));
        } else {
            GoodInfo goodInfo = new GoodInfo();
            goodInfo.setGoodId(goodId);
            goodInfo.setGoodStatus(goodStatus);
            goodInfoService.updateById(goodInfo);
        }
        //发送状态变更消息
        JSONObject o = new JSONObject();
        o.put("goodStatus", goodStatus);
        o.put("goodIds", Arrays.asList(goodId));
        MQProduceGood.goodStatusUpdate(kafkaTemplate, o.toJSONString());
        return initSuccessResult();
    }

    @ApiOperation(value = "查询销量前十商品")
    @RequestMapping(value = "/queryTopGood", method = RequestMethod.POST)
    public ReturnData<List<Map<String, Object>>> queryTopGood() {
        return initSuccessObjectResult(goodInfoService.queryTopGood());
    }

    @ApiOperation(value = "搜索商品")
    @RequestMapping(value = "/searchGoods", method = RequestMethod.POST)
    public ReturnData<Page<GoodInfoEx>> searchGoods(@RequestBody String param) {
        return initSuccessObjectResult(goodInfoService.searchGoods(param));
    }

    @ApiOperation(value = "查询商品库存")
    @RequestMapping(value = "/queryGoodNum", method = RequestMethod.POST)
    public ReturnData<List<GoodNum>> queryGoodNum(@RequestBody List<Integer> goodIds) {
        // TODO 待优化-直接查询缓存
        return initSuccessObjectResult(goodInfoService.queryGoodNumTotal(goodIds));
    }

    @ApiOperation(value = "订单查询商品")
    @RequestMapping(value = "/queryGoodTT", method = RequestMethod.POST)
    public ReturnData<List<GoodInfo>> queryGoodTT(@RequestBody com.mmj.common.model.good.GoodInfo goodInfo) throws Exception {
        String key = RedisCacheUtil.GOOD_INFO_QUERYGOODTT + RedisCacheUtil.getKey(goodInfo);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseArray(String.valueOf(o), GoodInfo.class));
        }
        GoodInfo goodInfo1 = JSON.parseObject(JSON.toJSONString(goodInfo), GoodInfo.class);
        List<Integer> goodIds = goodInfo.getGoodIds();
        EntityWrapper<GoodInfo> entityWrapper = new EntityWrapper<>(goodInfo1);
        entityWrapper.in(goodIds != null && !goodIds.isEmpty(), "GOOD_ID", goodIds);
        List<GoodInfo> goodInfos = goodInfoService.selectList(entityWrapper);
        if (goodInfos != null && !goodInfos.isEmpty()) {
            //返回json
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, JSON.toJSONString(goodInfos));
            return initSuccessObjectResult(goodInfos);
        }
        return initSuccessResult();
    }

    public ReturnData<Page<GoodInfoEx>> queryAllTopList(@RequestBody GoodInfoEx entityEx) {
        //获取置顶商品
        List<Integer> goodIds = new ArrayList<>();
        ActiveGood activeGood = new ActiveGood();
        activeGood.setActiveType(entityEx.getActiveType());
        activeGood.setArg1("1");
        activeGood.setBusinessId(entityEx.getBusinessId());
        ReturnData<List<Integer>> listReturnData = activeFeignClient.queryGoodIds(activeGood);
        if (listReturnData != null && listReturnData.getCode() == SecurityConstants.SUCCESS_CODE && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
            goodIds = listReturnData.getData();
            //查询
            entityEx.setDelFlag(0);
            entityEx.setFileServer(fileServer);
            entityEx.setGoodIds(goodIds);
            //join商品
            List<String> goodIdsJoin = new ArrayList<>();
            CollectionUtils.collect(goodIds,
                    new Transformer() {
                        public Object transform(Object input) {
                            return String.valueOf(input);
                        }
                    }, goodIdsJoin);
            entityEx.setOrderType(String.format(" FIELD(GOOD_ID, %s) ", String.join(",", goodIdsJoin)));
            return initSuccessObjectResult(goodInfoService.queryOrderList(entityEx));
        }
        return initSuccessResult();
    }

    public String getGoodIds(Integer activeType, Integer businessId, String orderSql, GoodInfoEx entityEx) {
        List<Integer> goodIds = new ArrayList<>();
        ActiveGood activeGood = new ActiveGood();
        activeGood.setActiveType(activeType);
        activeGood.setBusinessId(businessId);
        activeGood.setArg1("0");
        ReturnData<List<Integer>> listReturnData = activeFeignClient.queryGoodIds(activeGood);
        if (listReturnData != null && listReturnData.getCode() == SecurityConstants.SUCCESS_CODE && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
            goodIds = listReturnData.getData();
            //排序规则
            if (orderSql.indexOf("FIELD") != -1) {
                List<String> out = new ArrayList<>();
                CollectionUtils.collect(goodIds,
                        new Transformer() {
                            public Object transform(Object input) {
                                return String.valueOf(input);
                            }
                        }, out);
                orderSql = String.format(orderSql, String.join(",", out));
            }
        } else {
            if (orderSql.indexOf("FIELD") != -1) {
                orderSql = " CREATER_TIME desc, GOOD_ID ";
            }
        }
        entityEx.setGoodIds(goodIds);
        entityEx.setOrderType(orderSql);
        return orderSql;
    }

    public void getOrderListEntity(GoodInfoEx entityEx) {
        String orderSql = null;
        ActiveSort activeOrder = new ActiveSort();
        activeOrder.setActiveType(entityEx.getActiveType());
        activeOrder.setBusinessId(entityEx.getBusinessId());
        if (entityEx.getActiveType() == GoodConstants.ActiveType.GUESS_LIKE || entityEx.getActiveType() == GoodConstants.ActiveType.CLASS_GOOD) {
            //获取排序规则
            ReturnData<List<ActiveSort>> returnData = activeFeignClient.queryList(activeOrder);
            orderSql = GoodUtil.getOrderSql(returnData == null || returnData.getCode() != SecurityConstants.SUCCESS_CODE ? null : (returnData.getData() == null || returnData.getData().size() <= 0 ? null : returnData.getData().get(0)));
            //获取例外商品
            List<Integer> goodIds = new ArrayList<>();
            ActiveGood activeGood = new ActiveGood();
            activeGood.setActiveType(entityEx.getActiveType());
            activeGood.setBusinessId(entityEx.getBusinessId());
            activeGood.setArg1("2");
            ReturnData<List<Integer>> listReturnData = activeFeignClient.queryGoodIds(activeGood);
            if (listReturnData != null && listReturnData.getCode() == SecurityConstants.SUCCESS_CODE && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
                goodIds = listReturnData.getData();
                entityEx.setNoGoodIds(goodIds);
            }
            if (orderSql.indexOf("FIELD") != -1) {
                orderSql = " CREATER_TIME desc, GOOD_ID ";
            }
        } else if (entityEx.getActiveType() == GoodConstants.ActiveType.TOPIC) {
            //获取分类
            Integer topicId = entityEx.getBusinessId();
            if (topicId == null) {
                return;
            }
            //查询专题信息
            ReturnData<TopicInfo> returnData = activeFeignClient.getTopicById(topicId);
            if (returnData != null && returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData() != null) {
                //获取排序规则
                ReturnData<List<ActiveSort>> returnDataOrder = activeFeignClient.queryList(activeOrder);
                orderSql = GoodUtil.getOrderSql(returnDataOrder == null ? null : (returnDataOrder.getData() == null || returnDataOrder.getData().size() <= 0 ? null : returnDataOrder.getData().get(0)));
                //专题类型
                TopicInfo topicInfo = returnData.getData();
                if ("GOOD".equals(topicInfo.getTopicGoodClass())) {//关联商品
                    //查询商品
                    orderSql = getGoodIds(entityEx.getActiveType(), topicId, orderSql, entityEx);
                    entityEx.setAutoShow(null);
                } else {//关联分类
                    if (orderSql.indexOf("FIELD") != -1) {
                        orderSql = " CREATER_TIME desc, GOOD_ID ";
                    }
                    //获取分类
                    String[] classList = topicInfo.getTopicGoodClass().split(",");
                    List<String> goodClasses = new ArrayList<>();
                    for (String classCode : classList) {
                        EntityWrapper<GoodClass> entityWrapper = new EntityWrapper<>();
                        entityWrapper.setSqlSelect("CLASS_CODE");
                        entityWrapper.like("CLASS_CODE", classCode + "%", SqlLike.CUSTOM);
                        List<GoodClass> classCodees = goodClassService.selectList(entityWrapper);
                        if (classCodees != null && !classCodees.isEmpty()) {
                            goodClasses.addAll(classCodees.stream().map(GoodClass::getClassCode).collect(Collectors.toList()));
                        }
                    }
                    entityEx.setClassCodes(goodClasses);
                }
            }

        } else if (entityEx.getActiveType() == GoodConstants.ActiveType.PRIZEWHEELS_6) {
            ActiveSort activeSort = new ActiveSort();
            activeSort.setOrderBy(GoodConstants.ActiveOrderType.RULE);
            activeOrder.setFilterRule(GoodConstants.ActiveOrderType.MODIFY);
            activeOrder.setOrderBy(GoodConstants.ActiveOrderType.DESC);
            orderSql = GoodUtil.getOrderSql(activeSort);
            entityEx.setCurrentPage(1);
            entityEx.setPageSize(26);
        } else if (entityEx.getActiveType() == GoodConstants.ActiveType.SEARCH_GOOD) {
            orderSql = " SALE_NUM DESC ";
            entityEx.setCurrentPage(1);
            entityEx.setPageSize(10);
        }
        //查询
        entityEx.setDelFlag(0);
        entityEx.setFileServer(fileServer);
        entityEx.setOrderType(orderSql);
        if (GoodConstants.ActiveType.HOT_SALE == entityEx.getActiveType()) {
            //免邮热卖没有分类自己查
            if (entityEx.getGoodIds() != null && !entityEx.getGoodIds().isEmpty()) {
                List list = goodInfoService.queryGoodClasses(entityEx.getGoodIds());
                entityEx.setClassCodes(list);
            }
            entityEx.setGoodIds(null);
            entityEx.setPageSize(24);
            entityEx.setCurrentPage(1);
        }
    }

    @ApiOperation(value = "查询商品封面图片")
    @RequestMapping(value = "/queryGoodImgUrl/{id}", method = RequestMethod.POST)
    public String queryGoodImgUrl(@PathVariable("id") Integer id) {
        return goodInfoService.queryGoodFile(id);
    }

    @ApiOperation(value = "根据id查询商品")
    @RequestMapping(value = "/getById/{id}", method = RequestMethod.POST)
    public GoodInfo getById(@PathVariable("id") Integer id) {
        return goodInfoService.getById(id);
    }


    /**
     *
     * @param request
     * @param response
     * @param entityEx
     *  GOOD_CLASS
     *  GOOD_STATUS
     *  GOOD_NAME
     *  GOOD_SPU
     *  VIRTUAL_FLAG
     *  MEMBER_FLAG
     *  COMBINA_FLAG
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "导出商品信息")
    @RequestMapping(value = "/exportGoods", method = RequestMethod.POST)
    public void exportGoods(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestBody GoodInfoBaseQueryEx entityEx){
        List<GoodInfoProperty> goodInfoProperties = goodInfoService.loadGoodInfo(entityEx);
        if (goodInfoProperties != null && !goodInfoProperties.isEmpty()) {
            List<ExportGoodsProperty> list = new ArrayList<>();
            String fileName = null;
            try {
                fileName = new String(("GoodInfo-" + DateUtils.SDF1.format(new Date()) + ".xlsx").getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            try (ServletOutputStream out = response.getOutputStream()){
                List<Integer> goodIds = goodInfoProperties.stream().map(GoodInfoProperty::getGoodId).collect(Collectors.toList());
                List<GoodSaleProperty> goodSaleProperties = goodInfoService.loadGoodSale(goodIds);
                List<GoodImageProperty> goodImageProperties = goodInfoService.loadGoodImage(goodIds);
                if (goodSaleProperties != null && !goodSaleProperties.isEmpty()) {
                    for (GoodSaleProperty goodSaleProperty : goodSaleProperties) {
                        try {
                            ExportGoodsProperty exportGoodsProperty = new ExportGoodsProperty();
                            exportGoodsProperty.setGoodSku(goodSaleProperty.getGoodSku() == null ? "" : goodSaleProperty.getGoodSku());
                            exportGoodsProperty.setModelName(goodSaleProperty.getModelName() == null ? "" : goodSaleProperty.getModelName());
                            exportGoodsProperty.setModelValue(goodSaleProperty.getModelValue() == null ? "" : goodSaleProperty.getModelValue());
                            exportGoodsProperty.setBasePrice(goodSaleProperty.getBasePrice() == null ? 0 : goodSaleProperty.getBasePrice());
                            exportGoodsProperty.setShopPrice(goodSaleProperty.getShopPrice() == null ? 0 : goodSaleProperty.getShopPrice());
                            exportGoodsProperty.setMemberPrice(goodSaleProperty.getMemberPrice() == null ? 0 : goodSaleProperty.getMemberPrice());
                            exportGoodsProperty.setSaleNum(goodSaleProperty.getSaleNum() == null ? 0 : goodSaleProperty.getSaleNum());
                            exportGoodsProperty.setWarehouseName(goodSaleProperty.getWarehouseName() == null ? "" : goodSaleProperty.getWarehouseName());
                            for (GoodInfoProperty goodInfoProperty : goodInfoProperties) {
                                if (goodSaleProperty.getGoodId().compareTo(goodInfoProperty.getGoodId()) == 0) {
                                    exportGoodsProperty.setGoodId(goodInfoProperty.getGoodId() == null ? 0 : goodInfoProperty.getGoodId());
                                    exportGoodsProperty.setGoodClass(goodInfoProperty.getGoodClass() == null ? "" : goodInfoProperty.getGoodClass());
                                    exportGoodsProperty.setGoodName(goodInfoProperty.getGoodName() == null ? "" : goodInfoProperty.getGoodName());
                                    exportGoodsProperty.setSellingPoint(goodInfoProperty.getSellingPoint() == null ? "" : goodInfoProperty.getSellingPoint());
                                    exportGoodsProperty.setShortName(goodInfoProperty.getShortName() == null ? "" : goodInfoProperty.getShortName());
                                    exportGoodsProperty.setGoodSpu(goodInfoProperty.getGoodSpu() == null ? "" : goodInfoProperty.getGoodSpu());
                                    exportGoodsProperty.setAutoShow(goodInfoProperty.getAutoShow() == null ? 0 : goodInfoProperty.getAutoShow());
                                    exportGoodsProperty.setVirtualFlag(goodInfoProperty.getVirtualFlag() == null ? 0 : goodInfoProperty.getVirtualFlag());
                                    exportGoodsProperty.setMemberFlag(goodInfoProperty.getMemberFlag() == null ? 0 : goodInfoProperty.getMemberFlag());
                                    exportGoodsProperty.setCombinaFlag(goodInfoProperty.getCombinaFlag() == null ? 0 : goodInfoProperty.getCombinaFlag());
                                    exportGoodsProperty.setUpTime(goodInfoProperty.getUpTime() == null ? "" : DateUtils.SDF1.format(goodInfoProperty.getUpTime()));
                                    exportGoodsProperty.setSaleDays(goodInfoProperty.getSaleDays() == null ? 0 : goodInfoProperty.getSaleDays());
                                    exportGoodsProperty.setGoodStatus(goodInfoProperty.getGoodStatus() == null ? "" : goodInfoProperty.getGoodStatus());
                                    break;
                                }
                            }
                            if (exportGoodsProperty.getGoodSku() != null && exportGoodsProperty.getGoodSku().length() > 0) {
                                if (exportGoodsProperty.getCombinaFlag() == 1) {
                                    exportGoodsProperty.setGoodNum(goodSaleService.queryCombNum(exportGoodsProperty.getGoodSku()));
                                } else {
                                    exportGoodsProperty.setGoodNum(goodSaleService.queryNum(exportGoodsProperty.getGoodSku()));
                                }
                            }
                            if (goodImageProperties != null && !goodImageProperties.isEmpty()) {
                                for (GoodImageProperty goodImageProperty : goodImageProperties) {
                                    if (goodSaleProperty.getGoodId().compareTo(goodImageProperty.getGoodId()) == 0) {
                                        exportGoodsProperty.setImage(goodImageProperty.getImage() == null ? "" : goodImageProperty.getImage());
                                        exportGoodsProperty.setSellingPointImage(goodImageProperty.getSellingPoint() == null ? "" : goodImageProperty.getSellingPoint());
                                        exportGoodsProperty.setWechat(goodImageProperty.getWechat() == null ? "" : goodImageProperty.getWechat());
                                        exportGoodsProperty.setH5(goodImageProperty.getH5() == null ? "" : goodImageProperty.getH5());
                                        break;
                                    }
                                }
                            }
                            list.add(exportGoodsProperty);
                        } catch (Exception e) {
                            logger.error("--------------------GOOD-EXPORTGOODS1:", new Throwable(e));
                        }
                    }
                } else {
                    for (GoodInfoProperty goodInfoProperty : goodInfoProperties) {
                        try {
                            ExportGoodsProperty exportGoodsProperty = new ExportGoodsProperty();
                            exportGoodsProperty.setGoodId(goodInfoProperty.getGoodId() == null ? 0 : goodInfoProperty.getGoodId());
                            exportGoodsProperty.setGoodClass(goodInfoProperty.getGoodClass() == null ? "" : goodInfoProperty.getGoodClass());
                            exportGoodsProperty.setGoodName(goodInfoProperty.getGoodName() == null ? "" : goodInfoProperty.getGoodName());
                            exportGoodsProperty.setSellingPoint(goodInfoProperty.getSellingPoint() == null ? "" : goodInfoProperty.getSellingPoint());
                            exportGoodsProperty.setShortName(goodInfoProperty.getShortName() == null ? "" : goodInfoProperty.getShortName());
                            exportGoodsProperty.setGoodSpu(goodInfoProperty.getGoodSpu() == null ? "" : goodInfoProperty.getGoodSpu());
                            exportGoodsProperty.setAutoShow(goodInfoProperty.getAutoShow() == null ? 0 : goodInfoProperty.getAutoShow());
                            exportGoodsProperty.setVirtualFlag(goodInfoProperty.getVirtualFlag() == null ? 0 : goodInfoProperty.getVirtualFlag());
                            exportGoodsProperty.setMemberFlag(goodInfoProperty.getMemberFlag() == null ? 0 : goodInfoProperty.getMemberFlag());
                            exportGoodsProperty.setCombinaFlag(goodInfoProperty.getCombinaFlag() == null ? 0 : goodInfoProperty.getCombinaFlag());
                            exportGoodsProperty.setUpTime(goodInfoProperty.getUpTime() == null ? "" : DateUtils.SDF1.format(goodInfoProperty.getUpTime()));
                            exportGoodsProperty.setSaleDays(goodInfoProperty.getSaleDays() == null ? 0 : goodInfoProperty.getSaleDays());
                            exportGoodsProperty.setGoodStatus(goodInfoProperty.getGoodStatus() == null ? "" : goodInfoProperty.getGoodStatus());
                            if (goodImageProperties != null && !goodImageProperties.isEmpty()) {
                                for (GoodImageProperty goodImageProperty : goodImageProperties) {
                                    if (goodInfoProperty.getGoodId().compareTo(goodImageProperty.getGoodId()) == 0) {
                                        exportGoodsProperty.setImage(goodImageProperty.getImage() == null ? "" : goodImageProperty.getImage());
                                        exportGoodsProperty.setSellingPointImage(goodImageProperty.getSellingPoint() == null ? "" : goodImageProperty.getSellingPoint());
                                        exportGoodsProperty.setWechat(goodImageProperty.getWechat() == null ? "" : goodImageProperty.getWechat());
                                        exportGoodsProperty.setH5(goodImageProperty.getH5() == null ? "" : goodImageProperty.getH5());
                                        break;
                                    }
                                }
                            }
                            list.add(exportGoodsProperty);
                        } catch (Exception e) {
                            logger.error("--------------------GOOD-EXPORTGOODS2:", new Throwable(e));
                        }
                    }
                }
                ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
                Sheet sheet1 = new Sheet(1, 0, ExportGoodsProperty.class);
                sheet1.setSheetName("商品信息");
                writer.write(list, sheet1);
                writer.finish();

                out.flush();
            } catch (Exception e) {
                logger.error("--------------------GOOD-EXPORTGOODS:", new Throwable(e));
            }
        }
    }
}
