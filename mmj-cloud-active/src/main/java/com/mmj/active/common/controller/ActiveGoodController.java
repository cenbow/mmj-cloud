package com.mmj.active.common.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.model.*;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.common.service.WatermarkConfigureService;
import com.mmj.active.cut.model.CutInfo;
import com.mmj.active.cut.service.CutInfoService;
import com.mmj.active.limit.model.ActiveLimitDetail;
import com.mmj.active.limit.model.ActiveLimitEx;
import com.mmj.active.limit.service.ActiveLimitService;
import com.mmj.active.seckill.constants.SeckillConstants;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.controller.BaseController;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.ActiveGoodStoreResult;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动商品关联表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-13
 */
@RestController
@RequestMapping("/activeGood")
public class ActiveGoodController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(ActiveGoodController.class);

    @Autowired
    private ActiveGoodService activeGoodService;

    @Autowired
    private WatermarkConfigureService watermarkConfigureService;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ActiveLimitService activeLimitService;

    @Autowired
    private CutInfoService cutInfoService;

    @ApiOperation(value = "活动商品销售信息查询")
    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    public ReturnData<List<ActiveGood>> queryDetail() {
        goodFeignClient.getById(11000004);
        return null;
    }

    @ApiOperation(value = "活动商品销售信息查询")
    @RequestMapping(value = "/querySale", method = RequestMethod.POST)
    public ReturnData<List<ActiveGood>> queryDetail(@RequestBody ActiveGood activeGood) {
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>(activeGood);
        List<ActiveGood> activeGoods = activeGoodService.selectList(entityWrapper);
        if (activeGoods != null && !activeGoods.isEmpty()) {
            if (activeGood.getActiveType() == ActiveGoodsConstants.ActiveType.SECKILL) {
                activeGoods.stream().forEach(a -> {
                    StringBuilder sb = new StringBuilder(SeckillConstants.SECKILL_STORE);
                    sb.append(a.getBusinessId()).append(":").append(a.getGoodId()).append(":").append(a.getGoodSku());
                    Object skuStock = redisTemplate.opsForValue().get(sb.toString());
                    Object skuSale = redisTemplate.opsForValue().get(ActiveGoodsConstants.SKU_SALE + a.getGoodId());
                    a.setSaleNum(skuSale == null ? 0 : (Integer) skuSale);
                    a.setActiveStore(skuStock == null ? 0 : (Integer) skuStock);
                });
            } else {
                activeGoods.stream().forEach(a -> {
                    if (ActiveGoodsConstants.ActiveType.CUT == a.getActiveType()) {
                        CutInfo cutInfo = cutInfoService.selectById(a.getBusinessId());
                        if (Objects.nonNull(cutInfo))
                            a.setMemberAmount(cutInfo.getBasePrice().doubleValue());
                    }
                    if (a.getCombinaFlag() == 1) {
                        Integer num = sumCombNum(a.getGoodSku());
                        if (num == null) {
                            num = 0;
                        }
                        a.setActiveStore(num == null ? 0 : num);
                    } else {
                        String goodSku = a.getGoodSku();
                        Object o1 = redisTemplate.opsForValue().get(ActiveGoodsConstants.SKU_STOCK + goodSku);
                        Object o1U = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY + goodSku);
                        if (o1 != null && !"".equals(o1)) {
                            if (o1U != null && !"".equals(o1U)) {
                                a.setActiveStore(Integer.valueOf(String.valueOf(o1)) - Integer.valueOf(String.valueOf(o1U)));
                            } else {
                                a.setActiveStore(Integer.valueOf(String.valueOf(o1)));
                            }
                        } else {
                            a.setActiveStore(0);
                        }
                    }

                    Object skuSale = redisTemplate.opsForValue().get(ActiveGoodsConstants.SKU_SALE + a.getGoodId());
                    a.setSaleNum(skuSale == null ? 0 : (Integer) skuSale);
                });
            }
        }
        return initSuccessObjectResult(activeGoods);
    }

    @ApiOperation(value = "活动商品列表查询-分组")
    @RequestMapping(value = "/queryBaseList", method = RequestMethod.POST)
    public ReturnData<Page<ActiveGood>> queryBaseList(@RequestBody ActiveGood activeGood) {
        return initSuccessObjectResult(activeGoodService.queryBaseList(activeGood));
    }

    @ApiOperation(value = "活动商品列表查询-不分组")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<Page<ActiveGood>> queryList(@RequestBody ActiveGood activeGood) {
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>(activeGood);
        entityWrapper.orderBy("GOOD_ORDER");
        Page<ActiveGood> page = new Page<>(activeGood.getCurrentPage(), activeGood.getPageSize());
        return initSuccessObjectResult(activeGoodService.selectPage(page, entityWrapper));
    }

    @ApiOperation(value = "活动商品列表查询-排序")
    @RequestMapping(value = "/queryBaseOrder", method = RequestMethod.POST)
    public ReturnData<Page<ActiveGood>> queryBaseOrder(@RequestBody ActiveGoodEx activeGoodEx) {
        return initSuccessObjectResult(activeGoodService.queryBaseOrder(activeGoodEx));
    }

    @ApiOperation(value = "活动商品列表查询-排序")
    @RequestMapping(value = "/queryTopicGood", method = RequestMethod.POST)
    public ReturnData<Page<ActiveGood>> queryTopicGood(@RequestBody ActiveGoodEx activeGoodEx) {
        //查询置顶商品
        List<ActiveGood> topGoods = null;
        GoodInfoEx entityEx = new GoodInfoEx();
        if (activeGoodEx.getCurrentPage() == 1) {
            EntityWrapper<ActiveGood> entityWrapperTop = new EntityWrapper<>();
            entityWrapperTop.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.TOPIC);
            entityWrapperTop.eq("BUSINESS_ID", activeGoodEx.getBusinessId());
            entityWrapperTop.eq("ARG_1", 1);
            List<ActiveGood> topGoodIds = activeGoodService.selectList(entityWrapperTop);
            if (topGoodIds != null && !topGoodIds.isEmpty()) {
                List<Integer> goodIds = topGoodIds.stream().map(ActiveGood::getGoodId).collect(Collectors.toList());
                EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.TUAN);
                entityWrapper.in("GOOD_ID", goodIds);
                entityWrapper.groupBy("GOOD_ID");
                activeGoodEx.setNoGoodIds(goodIds);
                entityEx.setNoGoodIds(goodIds);
                topGoods = activeGoodService.selectList(entityWrapper);
            }
        }
        //查询专题商品
        entityEx.setActiveType(ActiveGoodsConstants.ActiveType.TOPIC);
        entityEx.setBusinessId(activeGoodEx.getBusinessId());
//        entityEx.setVirtualFlag(0);
        entityEx.setGoodStatus("1");
        entityEx.setPageSize(activeGoodEx.getPageSize());
        entityEx.setCurrentPage(activeGoodEx.getCurrentPage());
        ReturnData<List<Integer>> listReturnData = goodFeignClient.queryGoodIdOrder(entityEx);
        if (listReturnData != null && listReturnData.getCode() == SecurityConstants.SUCCESS_CODE && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
            List<String> goodIdsJoin = new ArrayList<>();
            CollectionUtils.collect(listReturnData.getData(),
                    new Transformer() {
                        public Object transform(Object input) {
                            return String.valueOf(input);
                        }
                    }, goodIdsJoin);
            activeGoodEx.setGoodIds(listReturnData.getData());
            activeGoodEx.setOrderSql(String.format(" FIELD(GOOD_ID, %s) ", String.join(",", goodIdsJoin)));
            activeGoodEx.setActiveType(ActiveGoodsConstants.ActiveType.TUAN);
            activeGoodEx.setBusinessId(null);
            Page<ActiveGood> activeGoodPage = activeGoodService.queryBaseOrder(activeGoodEx);
            if (topGoods != null && !topGoods.isEmpty()) {
                activeGoodPage.getRecords().addAll(0, topGoods);
            }
            activeGoodPage.getRecords().remove(null);
            return initSuccessObjectResult(activeGoodPage);
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "活动商品列表查询-订单")
    @RequestMapping(value = "/queryListOrder", method = RequestMethod.POST)
    public ReturnData<List<ActiveGood>> queryListOrder(@RequestBody ActiveGoodEx activeGoodEx) {
        ActiveGood activeGood = JSON.parseObject(JSON.toJSONString(activeGoodEx), ActiveGood.class);
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>(activeGood);
        entityWrapper.in(activeGoodEx.getGoodIds() != null, "GOOD_ID", activeGoodEx.getGoodIds());
        entityWrapper.in(activeGoodEx.getSaleIds() != null, "SALE_ID", activeGoodEx.getSaleIds());
        return initSuccessObjectResult(activeGoodService.selectList(entityWrapper));
    }

    @ApiOperation(value = "活动商品下单验证")
    @RequestMapping(value = "/orderCheck", method = RequestMethod.POST)
    public ReturnData<ActiveGoodStoreResult> orderCheck(@RequestBody ActiveGoodStore activeGoodStore) {
        logger.info("------------活动商品下单验证:{}_{}_{}", activeGoodStore.getOrderNo(), activeGoodStore.getActiveType(), activeGoodStore.getOrderCheck());
        try {
            Integer activeType = activeGoodStore.getActiveType();
            //限购验证
        /*if (ActiveGoodsConstants.ActiveType.TEN_YUAN_THREE_PIECES == activeType ||
                ActiveGoodsConstants.ActiveType.GROUP_LOTTERY == activeType || ActiveGoodsConstants.ActiveType.CUT == activeType || ActiveGoodsConstants.ActiveType.TUAN == activeType) {
            try {
                limitCheck(activeGoodStore);
            } catch (BusinessException e) {
                return initExcetionObjectResult(e.getMessage());
            }
        }*/

            //秒杀下单验证商品
            Integer inOrOut = 0;
            if (ActiveGoodsConstants.ActiveType.SECKILL == activeType) {
                //库存扣减
                try {
                    if (activeGoodStore.getOrderCheck() != null && !activeGoodStore.getOrderCheck()) {
                        inOrOut = activeGoodService.seckillCheck(activeGoodStore);
                    }
//                    ActiveGoodStoreResult activeGoodStoreResult = new ActiveGoodStoreResult(true);
//                    Integer activeId = JSON.parseObject(activeGoodStore.getPassingData()).getInteger("activeId");
//                    EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
//                    entityWrapper.eq("BUSINESS_ID", activeId);
//                    entityWrapper.eq("GOOD_SKU", activeGoodStore.getGoodSales().get(0).getSku());
//                    entityWrapper.eq("ACTIVE_TYPE", 5);
//                    ActiveGood activeGood = activeGoodService.selectOne(entityWrapper);
//                    if (activeGood != null) {
//                        activeGoodStoreResult.setDiscountAmount(activeGood.getBasePrice() - activeGood.getActivePrice());
//                    }
//                    return initSuccessObjectResult(activeGoodStoreResult);
                } catch (BusinessException e) {
                    return initExcetionObjectResult(e.getMessage());
                }
            } else if (ActiveGoodsConstants.ActiveType.GROUP_JIELIGOU == activeGoodStore.getActiveType()) {
                return initSuccessObjectResult(new ActiveGoodStoreResult(activeGoodService.produceNewComers(activeGoodStore)));
            } else if (ActiveGoodsConstants.ActiveType.CUT == activeGoodStore.getActiveType()) {
                return initSuccessObjectResult(activeGoodService.cutOrderCheck(activeGoodStore));
            }

            //活动下单记录
            if (activeGoodStore.getOrderCheck() != null && !activeGoodStore.getOrderCheck()) {
                if (ActiveGoodsConstants.ActiveType.SECKILL == activeType || ActiveGoodsConstants.ActiveType.TEN_YUAN_THREE_PIECES == activeType ||
                        ActiveGoodsConstants.ActiveType.GROUP_LOTTERY == activeType || ActiveGoodsConstants.ActiveType.CUT == activeType || ActiveGoodsConstants.ActiveType.TUAN == activeType) {
                    //秒杀 十元三件商品 抽奖商品 砍价商品 拼团商品
                    String passingData = activeGoodStore.getPassingData();
                    Integer activeId = JSON.parseObject(passingData).getInteger("activeId");
                    String orderNo = activeGoodStore.getOrderNo();
                    String orderKey = ActiveGoodsConstants.ACTIVE_ORDER + orderNo;
                    String orderSkuKey = ActiveGoodsConstants.ACTIVE_ORDER_SKU;
                    List<ActiveGoodStore.GoodSales> goods = activeGoodStore.getGoodSales();
                    if (goods != null && !goods.isEmpty()) {
                        List<String> listSku = new ArrayList<>();
                        for (ActiveGoodStore.GoodSales good : goods) {
                            listSku.add(good.getSku());
                            Map<String, Object> map = new HashMap<>();
                            map.put("goodSku", good.getSku());
                            map.put("goodNum", good.getGoodNum());
                            map.put("goodId", good.getGoodId());
                            map.put("businessId", activeId);
                            map.put("inOrOut", inOrOut);
                            map.put("userId", activeGoodStore.getUserId());
                            map.put("activeType", activeGoodStore.getActiveType());
                            redisTemplate.opsForHash().putAll(orderSkuKey + good.getSku(), map);
                            redisTemplate.expire(orderSkuKey + good.getSku(), 30, TimeUnit.DAYS);
                            logger.info("--------------seckillCheck秒杀下单记录:" + orderKey);
                        }
                        redisTemplate.opsForSet().add(orderKey, listSku.toArray());
                        redisTemplate.expire(orderKey, 30, TimeUnit.DAYS);
                    }
                }
            }
            return initSuccessObjectResult(new ActiveGoodStoreResult(true));
        } catch (Exception e) {
            logger.error("=> 活动商品下单验证错误", e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation(value = "新增或修改活动商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody List<ActiveGood> activeGoods) {
        if (activeGoods != null && !activeGoods.isEmpty()) {
            boolean b = activeGoodService.insertOrUpdateBatch(activeGoods);
            activeGoodService.cleanGoodCache(activeGoods.get(0).getActiveType());
            if (b) {
                //生成水印
                createMark(activeGoods);
            }
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "删除活动商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ReturnData update(@RequestBody String params) {
        JSONObject o = JSON.parseObject(params);
        Integer activeType = o.getInteger("activeType");
        String arg1 = o.getString("arg1");
        Integer businessId = o.getInteger("businessId");
        JSONArray mapperyIds = o.getJSONArray("goodIds");
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("ACTIVE_TYPE", activeType);
        entityWrapper.eq(arg1 != null, "ARG_1", arg1);
        entityWrapper.eq(businessId != null, "BUSINESS_ID", businessId);
        entityWrapper.in("GOOD_ID", mapperyIds.toJavaList(Integer.class));
        activeGoodService.delete(entityWrapper);
        return initSuccessResult();
    }

    /**
     * activeType：活动类型 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀 6 优惠券 7 砍价 8 主题 9 猜你喜欢 10 免邮热卖 11 分类商品
     * arg1 1：置顶商品 2：例外商品
     *
     * @param activeGood
     * @return
     */
    @ApiOperation(value = "活动商品id查询")
    @RequestMapping(value = "/queryGoodIds", method = RequestMethod.POST)
    public ReturnData<List<Integer>> queryGoodIds(@RequestBody ActiveGood activeGood) {
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>(activeGood);
        List<ActiveGood> activeGoods = activeGoodService.selectList(entityWrapper);
        if (activeGoods != null && !activeGoods.isEmpty()) {
            return initSuccessObjectResult(activeGoods.stream().map(ActiveGood::getGoodId).collect(Collectors.toList()));
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "修改活动商品-商品搜索")
    @RequestMapping(value = "/updateByGoodId", method = RequestMethod.POST)
    public ReturnData updateByGoodId(@RequestBody ActiveGoodEx activeGoodEx) {
        ActiveGood activeGood = JSON.parseObject(JSON.toJSONString(activeGoodEx), ActiveGood.class);
        activeGood.setArg2("1");
        ActiveGood activeGoodOld = activeGoodService.selectById(activeGoodEx.getMapperyId());
        boolean b = activeGoodService.updateById(activeGood);
        if (b) {
            //生成水印
            try {
                createMark(Arrays.asList(activeGood));
            } catch (Exception e) {
                logger.error("updateByGoodId-生成水印:" + e.getMessage(), e);
            }
        }
        //更新缓存
        Integer goodId = activeGoodEx.getGoodId();
        Integer oldGoodId = activeGoodEx.getOldGoodId();
        String oldKey = ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP + oldGoodId;
        String newKey = ActiveGoodsConstants.goodSearch.GOOD_SEARCH_TOP + goodId;
        if (redisTemplate.hasKey(oldKey)) {
            redisTemplate.opsForHash().put(newKey, "goodId", redisTemplate.opsForHash().get(oldKey, "goodId"));
            redisTemplate.opsForHash().put(newKey, "goodOrder", redisTemplate.opsForHash().get(oldKey, "goodOrder"));
            redisTemplate.opsForHash().put(newKey, "arg1", redisTemplate.opsForHash().get(oldKey, "arg1"));
            if (!oldKey.equals(newKey)) {
                redisTemplate.delete(oldKey);
            }
        } else {
            redisTemplate.opsForHash().put(newKey, "goodId", oldGoodId);
            redisTemplate.opsForHash().put(newKey, "goodOrder", activeGoodOld.getGoodOrder());
            redisTemplate.opsForHash().put(newKey, "arg1", activeGoodOld.getArg1());
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "判断商品是否重复")
    @RequestMapping(value = "/isRepeat", method = RequestMethod.POST)
    public ReturnData<List<Map<String, Object>>> isRepeat(@RequestBody List<ActiveGood> activeGoods) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (activeGoods != null && !activeGoods.isEmpty()) {
            for (ActiveGood activeGood : activeGoods) {
                EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>(activeGood);
                entityWrapper.ne("GOOD_STATUS", ActiveGoodsConstants.goodStatus.DELETED);
                List<ActiveGood> activeGoodList = activeGoodService.selectList(entityWrapper);
                if (activeGoodList != null && !activeGoodList.isEmpty()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("goodId", activeGoodList.get(0).getGoodId());
                    map.put("goodName", activeGoodList.get(0).getGoodName());
                    list.add(map);
                }
            }
        }
        return initSuccessObjectResult(list);
    }


    /**
     * 必传字段：
     * ACTIVE_TYPE
     * GOOD_SKU(GOOD_STATUS)
     * BUSINESS_ID(秒杀)
     *
     * @param activeGoodOrder
     * @return
     */
    @ApiOperation(value = "订单查询商品")
    @RequestMapping(value = "/queryOrderGood", method = RequestMethod.POST)
    public ReturnData<List<ActiveGood>> queryOrderGood(@RequestBody ActiveGoodOrder activeGoodOrder) {
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("ACTIVE_TYPE", activeGoodOrder.getActiveType());
        entityWrapper.eq("GOOD_STATUS", 1);
        entityWrapper.in(activeGoodOrder.getGoodSkus() != null, "GOOD_SKU", activeGoodOrder.getGoodSkus());
        entityWrapper.eq(activeGoodOrder.getGoodSku() != null, "GOOD_SKU", activeGoodOrder.getGoodSku());
        if (null != activeGoodOrder.getBusinessId()) {
            entityWrapper.eq("BUSINESS_ID", activeGoodOrder.getBusinessId());
        }
        /*List<ActiveGood> activeGoods = activeGoodService.selectList(entityWrapper);
        if (activeGoods != null && !activeGoods.isEmpty()) {
            for (ActiveGood activeGood : activeGoods) {
                if (activeGood.getCombinaFlag() == 1) {
                    Integer num = sumCombNum(activeGood.getGoodSku());
                    if (num != null && num >= 0) {
                        activeGood.setActiveStore(num);
                    } else {
                        activeGood.setActiveStore(0);
                    }
                }
            }
        }*/
        return initSuccessObjectResult(activeGoodService.selectList(entityWrapper));
    }

    @ApiOperation(value = "查询限购商品-boss")
    @RequestMapping(value = "/queryLimitGood", method = RequestMethod.POST)
    public ReturnData<Page<ActiveGood>> queryLimitGood(@RequestBody ActiveGoodEx activeGoodEx) {
        ActiveGood activeGood = JSON.parseObject(JSON.toJSONString(activeGoodEx), ActiveGood.class);
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>(activeGood);
        if (activeGoodEx.getGoodName() != null && activeGoodEx.getGoodName().length() != 0) {
            entityWrapper.like("GOOD_NAME", activeGoodEx.getGoodName());
            activeGood.setGoodName(null);
        }
        if (activeGoodEx.getActiveTypes() != null && !activeGoodEx.getActiveTypes().isEmpty()) {
            entityWrapper.in("ACTIVE_TYPE", activeGoodEx.getActiveTypes());
        }
        entityWrapper.groupBy("GOOD_ID");
        Page<ActiveGood> page = new Page<>(activeGoodEx.getCurrentPage(), activeGoodEx.getPageSize());
        return initSuccessObjectResult(activeGoodService.selectPage(page, entityWrapper));
    }

    /**
     * 生成水印-抽奖商品
     */
    public void createMark(List<ActiveGood> activeGoods) {
        if (activeGoods != null && !activeGoods.isEmpty()) {
            for (ActiveGood activeGood : activeGoods) {
                if (ActiveGoodsConstants.ActiveType.GROUP_LOTTERY == activeGood.getActiveType()) {
                    try {
                        Integer goodId = activeGood.getGoodId();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("url", activeGood.getGoodImage());//分享图url
                        jsonObject.put("classify", 2);//水印类型：1、商品分享图，2、抽奖分享图
                        jsonObject.put("title", activeGood.getGoodName());
                        jsonObject.put("drawprice", activeGood.getActiveAmount());
                        jsonObject.put("originalprice", activeGood.getBaseAmount());
                        jsonObject.put("memberprice", activeGood.getMemberAmount());
                        String markUrl = watermarkConfigureService.createMark(jsonObject.toJSONString());
                        if (markUrl != null && markUrl.length() != 0) {
                            List<GoodFile> goodFiles = new ArrayList<>();
                            GoodFile goodFile1 = new GoodFile();//小程序
                            goodFile1.setGoodId(goodId);
                            goodFile1.setFileServer("FILE_SERVER");
                            goodFile1.setFileType("WECHAT");
                            goodFile1.setFileUrl(markUrl);
                            goodFile1.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
                            GoodFile goodFile2 = new GoodFile();//H5
                            goodFile2.setGoodId(goodId);
                            goodFile2.setFileServer("FILE_SERVER");
                            goodFile2.setFileType("H5");
                            goodFile2.setFileUrl(markUrl);
                            goodFile2.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
                            goodFiles.add(goodFile1);
                            goodFiles.add(goodFile2);
                            goodFeignClient.saveFile(goodFiles);
                        }
                    } catch (Exception e) {
                        logger.error("生成水印-抽奖商品失败：", new Throwable(e));
                    }

                }
            }
        }
    }

    @ApiOperation(value = "test")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public void test() {
        String GOOD_INFO = "GOOD_INFO";
        String GOOD_INFO_QUERYORDERALL = "SHOWCASEGOOD:";
        List<Object> list = new ArrayList<>();
        Cursor<Map.Entry<Object, Object>> scan = redisTemplate.opsForHash().scan(GOOD_INFO, ScanOptions.scanOptions().match(GOOD_INFO_QUERYORDERALL + "*").build());
        //Cursor<Map.Entry<Object,Object>> cursor = redisTemplate.opsForHash().scan("hashValue",ScanOptions.NONE);
        while (scan.hasNext()) {
            Map.Entry<Object, Object> entry = scan.next();
            list.add(entry.getKey());
            System.out.println("通过scan(H key, ScanOptions options)方法获取匹配键值对:" + entry.getKey() + "---->" + entry.getValue());
        }
        if (list != null && !list.isEmpty()) {
            redisTemplate.opsForHash().delete(GOOD_INFO, list.toArray());
        }
    }

    public Integer sumCombNum(String goodSku) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(ActiveGoodsConstants.SKU_STOCK_COMBINE + goodSku);
        Integer subStock = 0;
        if (entries != null && !entries.isEmpty()) {
            Iterator<Object> iterator = entries.keySet().iterator();
            while (iterator.hasNext()) {
                String subGoodSku = String.valueOf(iterator.next());
                Integer num = (Integer) entries.get(subGoodSku);//包裹数
                if (subGoodSku != null && !"".equals(subGoodSku)) {
                    Object o = redisTemplate.opsForValue().get(ActiveGoodsConstants.SKU_STOCK + subGoodSku);//单品库存
                    Object oU = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku);
                    if (o != null && !"".equals(o)) {
                        Integer sub;
                        if (oU != null && !"".equals(oU)) {
                            sub = (Integer.valueOf(String.valueOf(o)) - Integer.valueOf(String.valueOf(oU))) / num;
                        } else {
                            sub = ((Integer) o) / num;
                        }
                        if (subStock == null || subStock == 0) {
                            subStock = sub;
                        } else if (sub.compareTo(subStock) < 0) {
                            subStock = sub;
                        }
                    }
                }
            }
        }
        return subStock;
    }


    public void limitCheck(ActiveGoodStore activeGoodStore) {
        Integer activeType = activeGoodStore.getActiveType();
        ActiveLimitEx activeLimit = activeLimitService.queryLimit(String.valueOf(activeType), activeGoodStore.getGoodSales().get(0).getGoodId());
        if (activeLimit != null) {
            List<ActiveLimitDetail> activeLimitDetails = activeLimit.getActiveLimitDetails();
            Integer limit1 = null;
            Integer limit2 = null;
            Integer limit3 = null;
            Integer limit = activeLimit.getLimitNum();
            Integer limitGood = activeLimit.getLimitGood();
            Long userId = activeGoodStore.getUserId();
            List<ActiveGoodStore.GoodSales> goodSales = activeGoodStore.getGoodSales();
            Integer goodId = goodSales.get(0).getGoodId();//商品
            int goodNum = goodSales.stream().mapToInt(ActiveGoodStore.GoodSales::getGoodNum).sum();//总数
            logger.info("---------limitCheck:limit-{}_limitGood-{}", limit, limitGood);
            if (activeLimitDetails != null && !activeLimitDetails.isEmpty()) {
                for (ActiveLimitDetail activeLimitDetail : activeLimitDetails) {
                    //每单限购
                    if (activeLimitDetail.getLimitType() == 1) {
                        limit1 = activeLimitDetail.getLimitNum();
                    }
                    //每天限购
                    if (activeLimitDetail.getLimitType() == 2) {
                        limit2 = activeLimitDetail.getLimitNum();
                    }
                    //每人限购
                    if (activeLimitDetail.getLimitType() == 3) {
                        limit3 = activeLimitDetail.getLimitNum();
                    }
                }
                logger.info("---------limitCheck:limit1-{}_limit2-{}_limit3-{}", limit1, limit2, limit3);

                //每单限购
                if (limit1 != null) {
                    if (goodNum > limit1) {
                        throw new BusinessException("该商品每单限购{}件！", limit1);
                    }
                }

                //每人每天限购数量
                if (limit2 != null) {
                    StringBuilder sb = new StringBuilder(ActiveGoodsConstants.LimitType.GOOD_LIMIT_RT);
                    sb.append(activeLimit.getLimitId()).append(DateUtils.SDF10.format(new Date())).append(":").append(userId).append(":").append(goodId);
                    Object o = redisTemplate.opsForValue().get(sb.toString());
                    logger.info("---------limitCheck:limit2Num-{}", o);
                    if (o != null && !"".equals(o)) {
                        Integer num = (Integer) o;//已购数量
                        if (num + goodNum > limit2) {
                            throw new BusinessException("该商品每人每天限购{}件！", limit2);
                        }
                    }
                }

                //每人限购
                if (limit3 != null) {
                    StringBuilder sb1 = new StringBuilder(ActiveGoodsConstants.LimitType.GOOD_LIMIT_R);
                    sb1.append(activeLimit.getLimitId()).append(":").append(userId).append(":").append(goodId);
                    Object o = redisTemplate.opsForValue().get(sb1.toString());
                    logger.info("---------limitCheck:limit3Num-{}", o);
                    if (o != null && !"".equals(o)) {
                        Integer num = (Integer) o;//已购数量
                        if (num + goodNum > limit3) {
                            throw new BusinessException("该商品每人限购{}件！", limit3);
                        }
                    }
                }
            }

            //限购数量 总数量
            if (limit != null) {
                //限购数量
                StringBuilder sb2 = new StringBuilder(ActiveGoodsConstants.LimitType.GOOD_LIMIT_TOTAL);
                sb2.append(activeLimit.getLimitId()).append(":").append(goodId);
                Object o = redisTemplate.opsForValue().get(sb2.toString());
                logger.info("---------limitCheck:limitNum-{}", o);
                if (o != null && !"".equals(o)) {
                    Integer num = (Integer) o;//已购数量
                    if (num + goodNum > limit) {
                        throw new BusinessException("该商品限购{}件！", limit);
                    }
                }
            }

            //款式限定
            if (limitGood != null) {
                StringBuilder sb3 = new StringBuilder(ActiveGoodsConstants.LimitType.GOOD_LIMIT_GOODID);
                sb3.append(activeLimit.getLimitId()).append(":").append(userId);
                Set<Object> members = redisTemplate.opsForSet().members(sb3.toString());
                if (members != null && !members.isEmpty()) {
                    boolean contains = false;
                    Iterator<Object> i = members.iterator();
                    while (i.hasNext()) {
                        Object next = i.next();
                        if (String.valueOf(next).equals(String.valueOf(goodId)) || ((Integer) next).compareTo(goodId) == 0) {
                            contains = true;
                            break;
                        }
                    }
                    int size = members.size();
                    logger.info("---------limitCheck:limitGoodNum-{}", JSON.toJSONString(members));
                    if (!contains && size + 1 >= limitGood) {
                        throw new BusinessException("该款式限购{}件！", limitGood);
                    }
                }
            }

        } else {
            logger.info("活动类型_{}_无限购信息！", activeType);
        }
    }

}
