package com.mmj.good.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.exception.BaseException;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.feigin.dto.InventoryQuery;
import com.mmj.good.feigin.JunshuitanFeignClient;
import com.mmj.good.model.*;
import com.mmj.good.mapper.GoodInfoMapper;
import com.mmj.good.service.*;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.good.stock.service.GoodStockService;
import com.mmj.good.util.MQProduceGood;
import com.mmj.good.util.RedisCacheUtil;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Service
public class GoodInfoServiceImpl extends ServiceImpl<GoodInfoMapper, GoodInfo> implements GoodInfoService {

    Logger logger = LoggerFactory.getLogger(GoodInfoServiceImpl.class);

    @Autowired
    private GoodFileService goodFileService;

    @Autowired
    private GoodSaleService goodSaleService;

    @Autowired
    private GoodClassService goodClassService;

    @Autowired
    private GoodInfoMapper goodInfoMapper;

    @Autowired
    private GoodCombinationService goodCombinationService;

    @Autowired
    private JunshuitanFeignClient junshuitanFeignClient;

    @Autowired
    private GoodStockService goodStockService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Transactional(rollbackFor = Exception.class)
    public Integer saveInfo(GoodInfoBaseEx entityEx){
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if (null != entityEx.getGoodId()) {
            entityEx.setModifyId(userDetails.getUserId());
            entityEx.setModifyTime(DateUtil.date());
        } else {
            entityEx.setGoodStatus(GoodConstants.InfoStatus.WAIT_ON);
            entityEx.setDelFlag(GoodConstants.InfoDelFlag.NO);
            entityEx.setCreaterId(userDetails.getUserId());
        }
        if (entityEx.getCombinaFlag() == 1) {
            EntityWrapper<GoodCombination> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("GOOD_SPU", entityEx.getGoodSpu());
            List<GoodCombination> goodCombinations = goodCombinationService.selectList(entityWrapper);
            if (goodCombinations == null || goodCombinations.isEmpty()) {
                throw new BaseException("商品组合信息未导入！");
            }
        }
        GoodInfo goodInfo = JSON.parseObject(JSON.toJSONString(entityEx), GoodInfo.class);
        boolean info = insertOrUpdate(goodInfo);
        String image = "";
        if (info) {
            /**
             * 保存卖点图 商品图片
             * 附件类型
             *  SELLING_POINT：卖点
             *  IMAGE：商品图片
             *  MAINVIDEO：主视频
             *  VIDEOTITLE：视频封面
             *  WECHAT：小程序分享
             *  H5：H5分享
             */
            List<GoodFile> goodFiles = entityEx.getGoodFiles();
            if (goodFiles != null && !goodFiles.isEmpty()) {
                for (GoodFile file : goodFiles) {
                    if (file.getFileId() == null) {
                        file.setActiveType(GoodConstants.ActiveType.SHOP_GOOD);
                        file.setGoodId(goodInfo.getGoodId());
                        file.setCreaterId(userDetails.getUserId());
                    }
                    if (file.getTitleFlag() != null && file.getTitleFlag() == 1) {
                        image = file.getFileUrl();
                    }
                }
                boolean file = goodFileService.insertOrUpdateBatch(goodFiles);
                if (!file) {
                    throw new BaseException("图片信息保存失败！");
                }
            }
        } else {
            throw new BaseException("商品信息保存失败！");
        }
        //发送更新消息
        JSONObject object = new JSONObject();
        object.put("image", image);
        String s = JSON.toJSONString(goodInfo);
        object.putAll(JSON.parseObject(s));
        MQProduceGood.goodInfoUpdate(kafkaTemplate, s);
        //发送状态变更模板消息
        JSONObject o = new JSONObject();
        o.put("goodStatus", GoodConstants.InfoStatus.PUT_ON);
        o.put("goodIds", Arrays.asList(goodInfo.getGoodId()));
        MQProduceGood.goodStatusUpdate(kafkaTemplate, o.toJSONString());
        return goodInfo.getGoodId();
    }

    /**
     * 附件类型
     * DETAIL：详情
     * DETAILVIDEO 详情视频
     * DETAILTITLE：视频封面
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveDetailInfo(GoodInfoBaseEx entityEx) throws Exception {
        List<GoodFile> goodFiles = entityEx.getGoodFiles();
        if (goodFiles != null && !goodFiles.isEmpty()) {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            for (GoodFile file : goodFiles) {
                file.setActiveType(GoodConstants.ActiveType.SHOP_GOOD);
                if (file.getFileId() == null) {
                    file.setCreaterId(userDetails.getUserId());
                }
            }
        }
        boolean file = goodFileService.insertOrUpdateBatch(goodFiles);
        if (!file) {
            throw new Exception("详情保存失败！");
        }
    }

    /**
     * 分页查询商品
     *
     * @param entityEx
     * @return
     */
    @Override
    public Page<GoodInfoBaseQueryEx> queryList(GoodInfoBaseQueryEx entityEx) {
        String goodClass = entityEx.getGoodClass();
        if (goodClass != null && goodClass.length() != 0) {
            entityEx.setGoodClassLike(goodClass);
            entityEx.setGoodClass(null);
        }
        Page<GoodInfoBaseQueryEx> page = new Page<>(entityEx.getCurrentPage(), entityEx.getPageSize());
        List<GoodInfoBaseQueryEx> goodInfoExes = goodInfoMapper.queryList(page, entityEx);
        page.setRecords(goodInfoExes);
        return page;
    }

    /**
     * 分页查询商品 简化
     *
     * @param entityEx
     * @return
     */
    public Page<GoodInfoBaseQueryEx> queryBaseList(GoodInfoBaseQueryEx entityEx) {
        String goodClass = entityEx.getGoodClass();
        if (goodClass != null && goodClass.length() != 0) {
            entityEx.setGoodClassLike(goodClass);
            entityEx.setGoodClass(null);
        }
        Page<GoodInfoBaseQueryEx> page = new Page<>(entityEx.getCurrentPage(), entityEx.getPageSize());
        List<GoodInfoBaseQueryEx> goodInfoExes = goodInfoMapper.queryBaseList(page, entityEx);
        page.setRecords(goodInfoExes);
        return page;
    }

    /**
     * 批量上架
     *
     * @param goodIds
     */
    public void onshelve(List<Integer> goodIds) {
        String listStr = JSON.toJSONString(goodIds);
        if (goodIds != null && !goodIds.isEmpty()) {
            //是否存在虚拟商品
            EntityWrapper<GoodInfo> entityWrapper1 = new EntityWrapper<>();
            entityWrapper1.eq("VIRTUAL_FLAG", 1);
            entityWrapper1.in("GOOD_ID", goodIds);
            List<GoodInfo> goodInfos = selectList(entityWrapper1);
            if (goodInfos != null && !goodInfos.isEmpty()) {
                goodIds.removeAll(goodInfos.stream().map(GoodInfo::getGoodId).collect(Collectors.toList()));
            }
        }

        if (goodIds != null && !goodIds.isEmpty()) {
            //是否存在组合商品
            EntityWrapper<GoodInfo> entityWrapper2 = new EntityWrapper<>();
            entityWrapper2.eq("COMBINA_FLAG", 1);
            entityWrapper2.in("GOOD_ID", goodIds);
            List<GoodInfo> goodInfos1 = selectList(entityWrapper2);
            if (goodInfos1 != null && !goodInfos1.isEmpty()) {
                List<Integer> zhGood = goodInfos1.stream().map(GoodInfo::getGoodId).collect(Collectors.toList());
                goodIds.removeAll(zhGood);
                for (GoodInfo g : goodInfos1) {
                    EntityWrapper<GoodSale> saleWrapper = new EntityWrapper<>();
                    saleWrapper.eq("GOOD_ID", g.getGoodId());
                    List<GoodSale> goodSales = goodSaleService.selectList(saleWrapper);
                    if (goodSales != null && !goodSales.isEmpty()) {
                        boolean flag = false;
                        for (GoodSale gs : goodSales) {
                            Integer num = goodSaleService.queryCombNum(gs.getGoodSku());
                            if (num != null && num > 0) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            throw new BusinessException(g.getGoodName() + "库存不足！");
                        }
                    }

                }
            }
        }

        if (goodIds != null && !goodIds.isEmpty()) {
            //其他商品
            EntityWrapper<GoodSale> saleWrapper = new EntityWrapper<>();
            saleWrapper.in("GOOD_ID", goodIds);
            saleWrapper.groupBy("GOOD_ID");
            saleWrapper.having(" SUM(GOOD_NUM) <= 0");
            List<GoodSale> goodSales = goodSaleService.selectList(saleWrapper);
            if (goodSales != null && !goodSales.isEmpty()) {
                throw new BusinessException(goodSales.get(0).getGoodSku() + "库存不足！");
            }
        }
        GoodInfo goodInfo = new GoodInfo();
        goodInfo.setGoodStatus(GoodConstants.InfoStatus.PUT_ON);
        goodInfo.setUpTime(DateUtil.date());
        EntityWrapper<GoodInfo> wrapper = new EntityWrapper<>();
        wrapper.in("GOOD_ID", JSON.parseArray(listStr, Integer.class));
        goodInfoMapper.update(goodInfo, wrapper);
    }

    /**
     * 批量下架
     *
     * @param goodIds
     */
    public void unshelve(List<Integer> goodIds) {
        EntityWrapper<GoodInfo> wrapper = new EntityWrapper<>();
        wrapper.in("GOOD_ID", goodIds);
        List<GoodInfo> goodInfos = selectList(wrapper);
        if (goodInfos != null) {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            DateTime date = DateUtil.date();
            List<GoodInfo> result = new ArrayList<>();
            for (GoodInfo g : goodInfos) {
                GoodInfo goodInfo = new GoodInfo();
                goodInfo.setGoodId(g.getGoodId());
                if (g.getUpTime() == null) {
                    g.setUpTime(new Date());
                }
                Long time = (date.getTime() - g.getUpTime().getTime()) / 1000 / 60 / 60;
                goodInfo.setSaleDays(time.intValue() + (g.getSaleDays() == null ? 0 : g.getSaleDays()));
                goodInfo.setGoodStatus(GoodConstants.InfoStatus.WAIT_ON);
                goodInfo.setModifyId(userDetails.getUserId());
                goodInfo.setModifyTime(DateUtil.date());
                result.add(goodInfo);
            }
            updateBatchById(result);
        }

    }

    @Override
    public Map<String, Object> batchVerifyGoodSpu(List<String> spuList) {
        GoodInfo goodInfo = new GoodInfo();
        Map<String, Object> map = new HashMap<>();
        spuList.forEach(spu -> {
            goodInfo.setGoodSpu(spu);
            GoodInfo goods = goodInfoMapper.selectOne(goodInfo);
            if (goods == null) { //商品不存在
                map.put(spu, "款式编号" + spu + "不存在!");
            } else {
                if ("-1".equals(goods.getGoodStatus())) { //商品已删除
                    map.put(spu, "款式编号" + spu + "该商品已经被删除！");
                }
                if (!"1".equals(goods.getGoodStatus())) { //商品属于下架的状态
                    map.put(spu, "款式编号" + spu + "为已下架商品，请替换配置以上商品！");
                }
            }
        });
        return map;
    }

    public List<GoodInfo> select(GoodInfo goodInfo) {
        return goodInfoMapper.select(goodInfo);
    }


    private List<String> getGoodClasses(String goodClass) {
        List<String> goodClasses = new ArrayList<>();
        if (goodClass.length() == 8) {
            goodClasses.add(goodClass);
        } else {
            GoodClassEx goodClassEx = new GoodClassEx();
            goodClassEx.setClassCode(goodClass);
            goodClassEx.setCurrentPage(0);
            goodClassEx.setPageSize(Integer.MAX_VALUE);
            Page<GoodClassEx> page = goodClassService.query(goodClassEx);
            if (page != null && page.getRecords() != null && page.getRecords().size() != 0) {
                List<String> codes = new ArrayList<>();
                getGoodClasses(page.getRecords(), codes);
                goodClasses.addAll(codes);
            }
        }
        return goodClasses;
    }

    private List<String> getGoodClasses(List<GoodClassEx> list, List<String> goodClasses) {
        if (list != null && !list.isEmpty()) {
            for (GoodClassEx goodClassEx : list) {
                goodClasses.add(goodClassEx.getClassCode());
                List<GoodClassEx> goodClassExes = goodClassEx.getGoodClassExes();
                if (goodClassExes != null && !goodClassExes.isEmpty()) {
                    goodClasses.addAll(getGoodClasses(goodClassExes, goodClasses));
                }
            }
        }
        return goodClasses;
    }

    /**
     * 自定义排序查询 (猜你喜欢，免邮热卖，商品排序)
     *
     * @param entityEx
     * @return
     */
    public Page<GoodInfoEx> queryOrderList(GoodInfoEx entityEx) {
        Page<GoodInfoEx> page = new Page<>(entityEx.getCurrentPage(), entityEx.getPageSize());
        List<GoodInfoEx> goodInfoExes = goodInfoMapper.queryOrderList(page, entityEx);
        page.setRecords(goodInfoExes);
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    public void autoOnshelve() {
        //查询自动上架商品
        EntityWrapper<GoodInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_STATUS", GoodConstants.InfoStatus.PUT_ON_AUTO);
        List<GoodInfo> goodInfos = selectList(entityWrapper);
        if (goodInfos != null && !goodInfos.isEmpty()) {
            for (GoodInfo goodInfo : goodInfos) {
                List<Map<String, String>> skuList = new ArrayList<>();
                try {
                    EntityWrapper<GoodSale> goodSaleEntityWrapper = new EntityWrapper<>();
                    goodSaleEntityWrapper.eq("GOOD_ID", goodInfo.getGoodId());
                    goodSaleEntityWrapper.isNotNull("GOOD_SKU");
                    List<GoodSale> goodSales = goodSaleService.selectList(goodSaleEntityWrapper);
                    boolean syncFlag = true;
                    //1.处理库存
                    if (goodInfo.getCombinaFlag() == 1) {
                        boolean flag = true;
                        for (GoodSale goodSale : goodSales) {
                            //查询组合关系 key:子商品sku     value:数量
                            Map<Object, Object> map = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodSale.getGoodSku());
                            //子商品sku
                            Set<Object> keys = map.keySet();
                            if (keys != null && !keys.isEmpty()) {
                                Integer subStock = 0;
                                List<Object> oList = new ArrayList<>(keys);
                                List<String> skus = new ArrayList<>();
                                for (Object o : oList) {
                                    if (o != null) {
                                        skus.add(String.valueOf(o));
                                    }
                                }
                                InventoryQuery inventoryQuery = new InventoryQuery();
                                inventoryQuery.setSkus(skus);
                                ReturnData<List<InventoryQuery>> listReturnData = junshuitanFeignClient.inventoryQuery(inventoryQuery);
                                if (listReturnData != null && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
                                    if (listReturnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                                        List<InventoryQuery> data = listReturnData.getData();
                                        if (data != null && !data.isEmpty()) {
                                            if (data.size() != skus.size()) {
                                                syncFlag = false;
                                                skus.removeAll(data.stream().map(InventoryQuery::getSku).collect(Collectors.toList()));
                                                logger.info("------------autoOnshelve-zh:{}在聚水潭不存在！", skus);
                                                continue;
                                            }
                                            for (InventoryQuery iq : data) {
                                                Map<String, String> skuMap = new HashMap<>();
                                                String sku = iq.getSku();
                                                Integer stockNum = iq.getStockNum();
                                                Integer num = (Integer) map.get(sku);//包裹数
                                                Integer sub = stockNum / num;
                                                if (subStock == null || subStock == 0) {
                                                    subStock = sub;
                                                } else if (sub.compareTo(subStock) < 0) {
                                                    subStock = sub;
                                                }
                                                skuMap.put("goodSku", sku);
                                                skuMap.put("goodNum", String.valueOf(stockNum));
                                                skuList.add(skuMap);

                                                //设置组合商品库存
                                                EntityWrapper<GoodCombination> combinationEntityWrapper = new EntityWrapper<>();
                                                combinationEntityWrapper.eq("GOOD_SPU", goodInfo.getGoodSpu());
                                                combinationEntityWrapper.eq("GOOD_SKU", goodSale.getGoodSku());
                                                combinationEntityWrapper.eq("SUB_GOOD_SKU", sku);
                                                GoodCombination goodCombination = new GoodCombination();
                                                goodCombination.setSubGoodNum(stockNum);
                                                goodCombinationService.update(goodCombination, combinationEntityWrapper);
                                            }
                                        } else {
                                            syncFlag = false;
                                            logger.info("------------autoOnshelve-zh:SKU在聚水潭不存在！");
                                            continue;
                                        }
                                    } else {
                                        syncFlag = false;
                                        logger.info("------------autoOnshelve-zh:SKU在聚水潭不存在！" + listReturnData.getDesc());
                                        continue;
                                    }
                                } else {
                                    syncFlag = false;
                                    logger.info("------------autoOnshelve-zh:同步聚水潭失败！" + listReturnData.getDesc());
                                    continue;
                                }
                                if (subStock > 0) {
                                    flag = false;
                                }
                            } else {
                                syncFlag = false;
                                logger.info("------------autoOnshelve-zh:组合关系错误！！" );
                                continue;
                            }
                        }
                        if (flag) {
                            syncFlag = false;
                            logger.info("------------autoOnshelve-zh:库存不足上架失败！" );
                        }
                    } else {
                        List<String> goodSkus = goodSales.stream().map(GoodSale::getGoodSku).filter(Objects::nonNull).collect(Collectors.toList());
                        InventoryQuery inventoryQuery = new InventoryQuery();
                        inventoryQuery.setSkus(goodSkus);
                        ReturnData<List<InventoryQuery>> listReturnData = junshuitanFeignClient.inventoryQuery(inventoryQuery);
                        if (listReturnData != null && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
                            if (listReturnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                                List<InventoryQuery> data = listReturnData.getData();
                                if (data != null && !data.isEmpty()) {
                                    boolean flag = true;
                                    if (data.size() != goodSkus.size()) {
                                        List<String> collect = data.stream().map(InventoryQuery::getSku).collect(Collectors.toList());
                                        goodSkus.removeAll(collect);
                                        logger.info("------------autoOnshelve-:{}在聚水潭不存在！", goodSkus);
                                        syncFlag = false;
                                    }else {
                                        for (GoodSale goodSale : goodSales) {
                                            for (InventoryQuery iq : data) {
                                                if (goodSale.getGoodSku().equals(iq.getSku())) {
                                                    goodSale.setGoodNum(iq.getStockNum());
                                                    Map<String, String> skuMap = new HashMap<>();
                                                    skuMap.put("goodSku", goodSale.getGoodSku());
                                                    skuMap.put("goodNum", String.valueOf(iq.getStockNum()));
                                                    skuList.add(skuMap);
                                                }
                                                if (iq.getStockNum() > 0) {
                                                    flag = false;
                                                }
                                            }
                                        }
                                    }
                                    goodSaleService.updateBatchById(goodSales);
                                    if (flag) {
                                        syncFlag = false;
                                        logger.info("------------autoOnshelve:库存不足上架失败！");
                                    }
                                } else {
                                    syncFlag = false;
                                    logger.info("------------autoOnshelve:SKU在聚水潭不存在！");
                                }
                            } else {
                                syncFlag = false;
                                logger.info("------------autoOnshelve:" + listReturnData.getDesc());
                            }
                        } else {
                            syncFlag = false;
                            logger.info("------------autoOnshelve:同步聚水潭失败！");
                        }
                    }
                    //2.处理结果
                    if (syncFlag) {//上架成功
                        goodInfo.setGoodStatus(GoodConstants.InfoStatus.PUT_ON);
                        updateById(goodInfo);
                        //更新库存占用
                        if (goodInfo.getCombinaFlag() == 1) {
                            if (skuList != null && !skuList.isEmpty()) {
                                for (Map<String, String> map : skuList) {
                                    goodStockService.refreshZhOccupy(map.get("goodSku"), Integer.valueOf(map.get("goodNum")));
                                }
                            }
                        } else {
                            if (skuList != null && !skuList.isEmpty()) {
                                for (Map<String, String> map : skuList) {
                                    goodStockService.refreshOccupy(map.get("goodSku"), Integer.valueOf(map.get("goodNum")));
                                }
                            }
                        }
                    } else {//上架失败
                        goodInfo.setGoodStatus(GoodConstants.InfoStatus.PUT_NO_FAIL);
                        updateById(goodInfo);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    logger.error("自动上架处理失败：" + goodInfo.getGoodSpu(), e);
                }
            }
        }
    }

    @Override
    public String queryGoodFile(Integer goodId) {
        return goodInfoMapper.queryGoodFile(goodId);
    }

    /**
     * 根据商品id查询商品分类
     * @param goodIds
     * @return
     */
    public List queryGoodClasses(List<Integer> goodIds) {
        return goodInfoMapper.queryGoodClasses(goodIds);
    }

    /**
     * 查询销量前十商品
     * @return
     */
    public List<Map<String, Object>> queryTopGood() {
        return goodInfoMapper.queryTopGood();
    }

    /**
     * 商品搜索查询
     * @param param
     * @return
     */
    public Page<GoodInfoEx> searchGoods(String param) {
        JSONObject o = JSONObject.parseObject(param);
        String content = o.getString("content");
        Integer currentPage = o.getInteger("currentPage");
        Integer pageSize = o.getInteger("pageSize");
        Page<GoodInfoEx> page = new Page<>(currentPage, pageSize);
        List<GoodInfoEx> goodInfoExes = goodInfoMapper.searchGoods(page, content);
        page.setRecords(goodInfoExes);
        return page;
    }

    public List<GoodNum> queryGoodNumTotal(List<Integer> goodIds) {
        return goodInfoMapper.queryGoodNumTotal(goodIds);
    }

    public GoodInfo getById(Integer id) {
        GoodInfo goodInfo = null;
        String key = RedisCacheUtil.GOOD_INFO_GETBYID + id;
        Object result = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (result != null && !"".equals(result)) {
            goodInfo = JSON.parseObject(String.valueOf(result), GoodInfo.class);
        } else {
            goodInfo = selectById(id);
            if (goodInfo != null) {
                redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, JSON.toJSONString(goodInfo));
            }
        }
        return goodInfo;
    }

    public GoodInfo getBySku(String goodSku) {
        GoodInfo goodInfo = null;
        String key = RedisCacheUtil.GOOD_INFO_GETBYSKU + goodSku;
        Object result = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_INFO, key);
        if (result != null && !"".equals(result)) {
            goodInfo = JSON.parseObject(String.valueOf(result), GoodInfo.class);
        } else {
            EntityWrapper<GoodSale> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("GOOD_SKU" , goodSku);
            List<GoodSale> goodSales = goodSaleService.selectList(entityWrapper);
            if (goodSales != null && !goodSales.isEmpty()) {
                goodInfo = selectById(goodSales.get(0).getGoodId());
                if (goodInfo != null) {
                    redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_INFO, key, JSON.toJSONString(goodInfo));
                }
            }
        }
        return goodInfo;
    }

    public List<GoodInfoProperty> loadGoodInfo(GoodInfoBaseQueryEx goodInfoBaseQueryEx) {
        return goodInfoMapper.loadGoodInfo(goodInfoBaseQueryEx);
    }

    public List<GoodSaleProperty> loadGoodSale(List<Integer> goodIds) {
        return goodInfoMapper.loadGoodSale(goodIds);
    }

    public List<GoodImageProperty> loadGoodImage(List<Integer> goodIds) {
        return goodInfoMapper.loadGoodImage(goodIds);
    }
}
