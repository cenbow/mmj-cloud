package com.mmj.good.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.feigin.dto.InventoryQuery;
import com.mmj.good.feigin.JunshuitanFeignClient;
import com.mmj.good.model.*;
import com.mmj.good.mapper.GoodSaleMapper;
import com.mmj.good.service.*;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.good.stock.service.GoodStockService;
import com.mmj.good.util.MQProduceGood;
import com.xiaoleilu.hutool.collection.CollectionUtil;
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
 * 商品销售信息表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Service
public class GoodSaleServiceImpl extends ServiceImpl<GoodSaleMapper, GoodSale> implements GoodSaleService {

    Logger logger = LoggerFactory.getLogger(GoodSaleServiceImpl.class);

    @Autowired
    private GoodModelService goodModelService;

    @Autowired
    private GoodFileService goodFileService;

    @Autowired
    private GoodWarehouseService goodWarehouseService;

    @Autowired
    private GoodInfoService goodInfoService;

    @Autowired
    private JunshuitanFeignClient junshuitanFeignClient;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private GoodSaleMapper goodSaleMapper;

    @Autowired
    private GoodStockService goodStockService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 保存商品销售信息
     * @param goodSaleExes
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(List<GoodSaleEx> goodSaleExes) throws BusinessException, InterruptedException {
        //组合商品不允许上架
        Integer goodId = goodSaleExes.get(0).getGoodId();
        if (goodId == null) {
            throw new BusinessException("商品不存在！");
        }
        GoodInfo goodInfo = goodInfoService.selectById(goodId);
        if (goodInfo == null) {
            throw new BusinessException("商品不存在！");
        }
        String goodStatus = goodSaleExes.get(0).getGoodStatus();
        if (goodInfo.getGoodStatus() != GoodConstants.InfoStatus.PUT_ON && goodStatus == GoodConstants.InfoStatus.PUT_ON) {
            goodInfo.setUpTime(new Date());
            goodInfoService.updateById(goodInfo);
        }
        //保证sku和saleid的一致性
        EntityWrapper<GoodSale> goodSaleEntityWrapper = new EntityWrapper<>();
        goodSaleEntityWrapper.eq("GOOD_ID", goodId);
        List<GoodSale> goodSales = selectList(goodSaleEntityWrapper);
        if (goodSales != null && !goodSales.isEmpty()) {
            for (GoodSale goodSale : goodSales) {
                if (goodSale.getGoodSku() != null) {
                    for (int i = 0; i < goodSaleExes.size(); i++) {
                        if (goodSaleExes.get(i).getGoodSku() != null) {
                            if (goodSale.getGoodSku().compareTo(goodSaleExes.get(i).getGoodSku()) == 0) {
                                goodSaleExes.get(i).setSaleId(goodSale.getSaleId());
                                break;
                            }
                        }
                    }
                }
            }
        }
        //虚拟商品不校验
        if (goodInfo.getVirtualFlag() == 1) {
            for (GoodSaleEx goodSaleEx : goodSaleExes) {
                goodSaleEx.setGoodNum(9999);
                redisTemplate.opsForValue().set(GoodConstants.SKU_STOCK + goodSaleEx.getGoodSku(), 9999);
            }
        }
        List<Map<String, String>> skuList = new ArrayList<>();
        if (goodInfo.getVirtualFlag() == 0 && goodInfo.getGoodStatus() != GoodConstants.InfoStatus.PUT_ON) {
            //组合商品校验子商品库存
            if (goodInfo.getCombinaFlag() == 1) {
                boolean flag = true;
                for (GoodSaleEx goodSaleEx : goodSaleExes) {
                    Map<Object, Object> map = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodSaleEx.getGoodSku());
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
                                        List<String> collect = data.stream().map(InventoryQuery::getSku).collect(Collectors.toList());
                                        skus.removeAll(collect);
                                        throw new BusinessException(String.join(",", skus) + "在聚水潭不存在！");
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
                                    }
                                } else {
                                    throw new BusinessException("SKU在聚水潭不存在");
                                }
                            } else {
                                throw new BusinessException(listReturnData.getDesc());
                            }
                        } else {
                            throw new BusinessException("同步聚水潭失败！");
                        }
                        goodSaleEx.setGoodNum(subStock);//设置组合商品库存
                        if (subStock > 0) {
                            flag = false;
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw new BusinessException("组合关系错误！");
                    }
                }
                if (flag && goodStatus == GoodConstants.InfoStatus.PUT_ON) {
                    throw new BusinessException("库存不足上架失败！");
                }
            } else {
                //非组合商品查询聚水潭
                List<String> goodSkus = goodSaleExes.stream().map(GoodSaleEx::getGoodSku).filter(Objects::nonNull).collect(Collectors.toList());
                InventoryQuery inventoryQuery = new InventoryQuery();
                inventoryQuery.setSkus(goodSkus);
                ReturnData<List<InventoryQuery>> listReturnData = junshuitanFeignClient.inventoryQuery(inventoryQuery);
                if (listReturnData != null && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
                    if (listReturnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                        List<InventoryQuery> data = listReturnData.getData();
                        if (data != null && !data.isEmpty()) {
                            if (data.size() != goodSkus.size()) {
                                List<String> collect = data.stream().map(InventoryQuery::getSku).collect(Collectors.toList());
                                goodSkus.removeAll(collect);
                                throw new BusinessException(String.join(",", goodSkus) + "在聚水潭不存在！");
                            }
                            boolean flag = true;
                            for (GoodSaleEx goodSaleEx : goodSaleExes) {
                                for (InventoryQuery iq : data) {
                                    if (goodSaleEx.getGoodSku().equals(iq.getSku())) {
                                        Map<String, String> skuMap = new HashMap<>();
                                        goodSaleEx.setGoodNum(iq.getStockNum());
                                        skuMap.put("goodSku", goodSaleEx.getGoodSku());
                                        skuMap.put("goodNum", String.valueOf(iq.getStockNum()));
                                        skuList.add(skuMap);
                                    }
                                    if (iq.getStockNum() > 0) {
                                        flag = false;
                                    }
                                }
                            }
                            if (flag && goodStatus == GoodConstants.InfoStatus.PUT_ON) {
                                throw new BusinessException("库存不足上架失败！");
                            }
                        } else {
                            throw new BusinessException("SKU在聚水潭不存在");
                        }
                    } else {
                        throw new BusinessException(listReturnData.getDesc());
                    }
                } else {
                    throw new BusinessException("同步聚水潭失败！");
                }
            }
        }

        if (goodId != null) {//清除数据
            cleanForUpdate(goodId, goodSaleExes.stream().map(GoodSaleEx::getSaleId).collect(Collectors.toList()));
        }
        //更新数据
        if (CollectionUtil.isNotEmpty(goodSaleExes)) {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            DateTime date = DateUtil.date();
            for (GoodSaleEx n : goodSaleExes) {
                GoodSale goodSale = JSON.parseObject(JSON.toJSONString(n), GoodSale.class);
                if (null == goodSale.getSaleId()) {
                    goodSale.setCreaterId(userDetails.getUserId());
                    goodSale.setCreaterTime(date);
                } else {
                    goodSale.setModifyId(userDetails.getUserId());
                    goodSale.setModifyTime(date);
                }
                insertOrUpdate(goodSale);
                List<GoodModelEx> goodModelExes = n.getGoodModelExes();
                if (CollectionUtil.isNotEmpty(goodModelExes)) {
                    for (GoodModelEx m : goodModelExes) {//保存商品规格
                        GoodModel goodModel = JSON.parseObject(JSON.toJSONString(m), GoodModel.class);
                        if (null == goodModel.getModelId()) {
                            goodModel.setCreaterId(userDetails.getUserId());
                            goodModel.setCreaterTime(date);
                        }
                        goodModel.setSaleId(goodSale.getSaleId());
                        goodModelService.insert(goodModel); //保存商品规格父节点

                        GoodFile goodFile = m.getGoodFile();
                        //规格值对应的图片 SALEMODEL
                        if (null != goodFile) {
                            if (null == goodFile.getFileId()) {
                                goodFile.setCreaterId(userDetails.getUserId());
                                goodFile.setCreaterTime(date);
                            }
                            goodFile.setFileType(GoodConstants.FileType.SALEMODEL);
                            goodFile.setActiveType(GoodConstants.ActiveType.SHOP_GOOD);
                            goodFile.setSaleId(goodSale.getSaleId());
                            goodFile.setModelId(goodModel.getModelId());
                            goodFileService.insert(goodFile);
                        }
                    }
                }
                List<GoodWarehouse> goodWarehouses = n.getGoodWarehouses();
                if (goodWarehouses != null && !goodWarehouses.isEmpty()) {
                    for (GoodWarehouse goodWarehouse : goodWarehouses) {
                        if (goodWarehouse.getWarehouseId() == null) {
                            goodWarehouse.setCreaterId(userDetails.getUserId());
                        }
                        goodWarehouse.setSaleId(goodSale.getSaleId());
                        goodWarehouseService.insert(goodWarehouse);
                    }
                }
            }
            //发送销售信息更新消息
            MQProduceGood.goodSaleUpdate(kafkaTemplate, JSON.toJSONString(goodSaleExes));

            //虚拟商品不校验
            if (goodInfo.getVirtualFlag() == 0 && goodInfo.getGoodStatus() != GoodConstants.InfoStatus.PUT_ON) {
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
            }
        }
    }

    public void cleanForUpdate(Integer goodId, List<Integer> saleIds) {
        //删除sale
        EntityWrapper<GoodSale> goodSaleEntityWrapper = new EntityWrapper<>();
        goodSaleEntityWrapper.eq("GOOD_ID", goodId);
        if (saleIds != null && !saleIds.isEmpty()) {
            saleIds.remove(null);
        }
        goodSaleEntityWrapper.notIn(saleIds != null && !saleIds.isEmpty(), "SALE_ID", saleIds);
        delete(goodSaleEntityWrapper);
        //删除model
        EntityWrapper<GoodModel> goodModelEntityWrapper = new EntityWrapper<>();
        goodModelEntityWrapper.eq("GOOD_ID", goodId);
        goodModelService.delete(goodModelEntityWrapper);
        //删除file
        EntityWrapper<GoodFile> goodFileEntityWrapper = new EntityWrapper<>();
        goodFileEntityWrapper.eq("FILE_TYPE", GoodConstants.FileType.SALEMODEL);
        goodFileEntityWrapper.eq("ACTIVE_TYPE", GoodConstants.ActiveType.SHOP_GOOD);
        goodFileEntityWrapper.eq("GOOD_ID", goodId);
        goodFileService.delete(goodFileEntityWrapper);
        //删除Warehouse
        EntityWrapper<GoodWarehouse> goodWarehouseEntityWrapper = new EntityWrapper<>();
        goodWarehouseEntityWrapper.eq("GOOD_ID", goodId);
        goodWarehouseService.delete(goodWarehouseEntityWrapper);
    }

    public List<GoodSale> select(GoodSale goodSale) {
        return goodSaleMapper.select(goodSale);
    }

    /**
     * 根据商品id分组查询金额信息
     * @param goodSaleEx
     * @return
     */
    public List<GoodSaleEx> queryGroupByInfo(GoodSaleEx goodSaleEx) {
        return goodSaleMapper.queryGroupByInfo(goodSaleEx);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateGoodNum(List<GoodSale> goodSales) {
        for (GoodSale goodSale : goodSales) {
            EntityWrapper<GoodSale> goodSaleEntityWrapper = new EntityWrapper<>();
            goodSaleEntityWrapper.eq("GOOD_SKU", goodSale.getGoodSku());
            goodSaleMapper.update(goodSale, goodSaleEntityWrapper);
        }
    }

    public Integer updateNum(Integer num,  String goodSku) {
        return goodSaleMapper.updateNum(num, goodSku);
    }

    public List<GoodOrder> queryOrderGood(List<String> goodSku){
        return goodSaleMapper.queryOrderGood(goodSku);
    }

    public Integer sumCombNum(Integer goodId) {
        EntityWrapper<GoodSale> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_ID", goodId);
        List<GoodSale> goodSales = selectList(entityWrapper);
        Integer sum = 0;
        for (GoodSale goodSale : goodSales) {
            sum += queryCombNum(goodSale.getGoodSku());
        }
        return sum;
    }

    public Integer sumNum(Integer goodId) {
        EntityWrapper<GoodSale> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_ID", goodId);
        List<GoodSale> goodSales = selectList(entityWrapper);
        Integer sum = 0;
        for (GoodSale goodSale : goodSales) {
            sum += queryNum(goodSale.getGoodSku());
        }
        return sum;
    }

    /**

     String subGoodSku = String.valueOf(iterator.next());//子sku
     Integer num = (Integer) map.get(subGoodSku);//包裹数
     if (subGoodSku != null && !"".equals(subGoodSku)) {
     Object stock = redisTemplate.opsForValue().get(GoodConstants.SKU_STOCK + String.valueOf(subGoodSku));//单品库存
     if (stock != null && !"".equals(stock)) {
     Integer sub = ((Integer) stock)/num;
     if (subStock == null) {
     subStock = sub;
     } else if (sub.compareTo(subStock) < 0) {
     subStock = sub;
     }
     }
     }

     */

    public Integer queryCombNum(String goodSku){
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodSku);
        Integer subStock = 0;
        if (entries != null && !entries.isEmpty()) {
            Iterator<Object> iterator = entries.keySet().iterator();
            while (iterator.hasNext()) {
                String subGoodSku = String.valueOf(iterator.next());
                Integer num = (Integer) entries.get(subGoodSku);//包裹数
                if (subGoodSku != null && !"".equals(subGoodSku)) {
                    Object o = redisTemplate.opsForValue().get(GoodConstants.SKU_STOCK + subGoodSku);//单品库存
                    Object oU = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku);
                    if (o != null && !"".equals(o)) {
                        Integer sub;
                        if (oU != null && !"".equals(oU)) {
                            sub = (Integer.valueOf(String.valueOf(o)) - Integer.valueOf(String.valueOf(oU))) / num;
                        } else {
                            sub = ((Integer) o)/num;
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

    public Integer queryNum(String goodSku) {
        Object o = redisTemplate.opsForValue().get(GoodConstants.SKU_STOCK + goodSku);//库存
        Object oU = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY + goodSku);
        Integer stock = 0;
        if (o != null && !"".equals(o)) {
            if (oU != null && !"".equals(oU)) {
                stock = Integer.valueOf(String.valueOf(o)) - Integer.valueOf(String.valueOf(oU));
            } else {
                stock = ((Integer) o);
            }
        }
        return stock;
    }

    public void synGoodsStock() {
        EntityWrapper<GoodSale> entityWrapper = new EntityWrapper<>();
        entityWrapper.isNotNull("GOOD_SKU");
        int count = selectCount(entityWrapper);
        if (count > 0) {
            int m = 1;
            if (count > 20) {
                m = count / 20;
                if (m * 20 < count) {
                    m++;
                }
            }
            for (int i = 0; i < m; i++) {
                try {
                    Page<GoodSale> page = new Page<>(i + 1, 20);
                    List<GoodSale> goodSales = selectPage(page, entityWrapper).getRecords();
                    if (goodSales != null && !goodSales.isEmpty()) {
                        List<String> goodSkus = goodSales.stream().map(GoodSale::getGoodSku).collect(Collectors.toList());
                        InventoryQuery inventoryQuery = new InventoryQuery();
                        inventoryQuery.setSkus(goodSkus);
                        ReturnData<List<InventoryQuery>> listReturnData = junshuitanFeignClient.inventoryQuery(inventoryQuery);
                        if (listReturnData != null && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
                            if (listReturnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                                List<InventoryQuery> data = listReturnData.getData();
                                if (data != null && !data.isEmpty()) {
                                    List<GoodSale> updateData = new ArrayList<>();
                                    for (GoodSale goodSale : goodSales) {
                                        try {
                                            for (InventoryQuery iq : data) {
                                                if (goodSale.getGoodSku().equals(iq.getSku())) {
                                                    goodSale.setGoodNum(iq.getStockNum());
                                                    updateData.add(goodSale);
                                                    break;
                                                }
                                            }
                                        } catch (Exception e) {
                                            logger.info("------------synGoodsStock:SKU同步失败：" + goodSale.getGoodSku());
                                        }
                                    }
                                    //修改数据库
                                    updateBatchById(updateData);
                                    //更新库存占用量
                                    goodStockService.refreshOccupy(updateData);
                                } else {
                                    logger.info("------------synGoodsStock:SKU在聚水潭不存在！");
                                    continue;
                                }
                            } else {
                                logger.info("------------synGoodsStock:" + listReturnData.getDesc());
                                continue;
                            }
                        } else {
                            logger.info("------------synGoodsStock:同步聚水潭失败！");
                            continue;
                        }
                    }
                } catch (Exception e) {
                    logger.error("------------synGoodsStock:处理失败" + e.getMessage(), e);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
