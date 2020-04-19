package com.mmj.good.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.feigin.JunshuitanFeignClient;
import com.mmj.good.feigin.dto.InventoryQuery;
import com.mmj.good.mapper.GoodCombinationMapper;
import com.mmj.good.model.GoodCombination;
import com.mmj.good.model.GoodCombinationExcel;
import com.mmj.good.service.GoodCombinationService;
import com.mmj.good.stock.service.GoodStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 组合商品表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-12
 */
@Service
public class GoodCombinationServiceImpl extends ServiceImpl<GoodCombinationMapper, GoodCombination> implements GoodCombinationService {

    Logger logger = LoggerFactory.getLogger(GoodCombinationServiceImpl.class);

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private JunshuitanFeignClient junshuitanFeignClient;

    @Autowired
    private GoodStockService goodStockService;

    private String GOOD_COMBINATION_SKULIST = "GOOD:COMBINATION:SKULIST";

    @Transactional(rollbackFor = Exception.class)
    public void upload(List<GoodCombinationExcel> goodCombinationExcels) {
        redisTemplate.delete(GOOD_COMBINATION_SKULIST);
        //导入成功对象
        List<GoodCombination> listGc = new ArrayList<>();
        //存在对象
        List<GoodCombination> listEx = new ArrayList<>();
        if (goodCombinationExcels != null && !goodCombinationExcels.isEmpty()) {
            for (GoodCombinationExcel goodCombinationExcel : goodCombinationExcels) {
                EntityWrapper<GoodCombination> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("GOOD_SPU", goodCombinationExcel.getCombinspu());
                entityWrapper.eq("GOOD_SKU", goodCombinationExcel.getCombinsku());
                entityWrapper.eq("SUB_GOOD_SKU", goodCombinationExcel.getSinglesku());
                GoodCombination goodCombinationOld = selectOne(entityWrapper);
                if (goodCombinationOld != null) {
                    goodCombinationOld.setPackageNum(goodCombinationExcel.getPackagenum());
                    listEx.add(goodCombinationOld);
                } else {
                    GoodCombination goodCombination = new GoodCombination();
                    goodCombination.setGoodSpu(goodCombinationExcel.getCombinspu());
                    goodCombination.setGoodSku(goodCombinationExcel.getCombinsku());
                    goodCombination.setSubGoodSku(goodCombinationExcel.getSinglesku());
                    goodCombination.setPackageNum(goodCombinationExcel.getPackagenum());
                    listGc.add(goodCombination);
                }
            }
            //批量插入
            if (listGc != null && !listGc.isEmpty()) {
                insertBatch(listGc);
            }
            //批量修改
            if (listEx != null && !listEx.isEmpty()) {
                updateBatchById(listEx);
            }

            //初始化 组合sku
            initCombination(listGc);

            //更新 组合sku
            initCombination(listEx);
        }
    }

    public void initCombination(List<GoodCombination> goodCombinations) {
        if(goodCombinations != null) {
            for (GoodCombination goodCombination : goodCombinations) {
                String key = GoodConstants.SKU_STOCK_COMBINE + goodCombination.getGoodSku();
                redisTemplate.opsForHash().put(key, goodCombination.getSubGoodSku(), goodCombination.getPackageNum());
            }
        }
    }

    public void initCombinationNum(List<GoodCombination> goodCombinations) {
        if(goodCombinations != null) {
            for (GoodCombination goodCombination : goodCombinations) {
                redisTemplate.opsForValue().set(GoodConstants.SKU_STOCK + goodCombination.getSubGoodSku(), goodCombination.getSubGoodNum());
            }
        }
    }

    public void synGoodsStockZh() {
        EntityWrapper<GoodCombination> entityWrapper = new EntityWrapper<>();
        entityWrapper.isNotNull("SUB_GOOD_SKU");
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
                    Page<GoodCombination> page = new Page<>(i + 1, 20);
                    List<GoodCombination> goodCombinations = selectPage(page, entityWrapper).getRecords();
                    if (goodCombinations != null && !goodCombinations.isEmpty()) {
                        List<String> goodSkus = goodCombinations.stream().map(GoodCombination::getSubGoodSku).collect(Collectors.toList());
                        InventoryQuery inventoryQuery = new InventoryQuery();
                        inventoryQuery.setSkus(goodSkus);
                        ReturnData<List<InventoryQuery>> listReturnData = junshuitanFeignClient.inventoryQuery(inventoryQuery);
                        if (listReturnData != null && listReturnData.getData() != null && !listReturnData.getData().isEmpty()) {
                            if (listReturnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                                List<InventoryQuery> data = listReturnData.getData();
                                if (data != null && !data.isEmpty()) {
                                    List<GoodCombination> updateData = new ArrayList<>();
                                    for (GoodCombination goodCombination : goodCombinations) {
                                        try {
                                            for (InventoryQuery iq : data) {
                                                if (goodCombination.getSubGoodSku().equals(iq.getSku())) {
                                                    goodCombination.setSubGoodNum(iq.getStockNum());
                                                    updateData.add(goodCombination);
                                                    break;
                                                }
                                            }
                                        } catch (Exception e) {
                                            logger.info("------------synGoodsStockZh:SKU同步失败：" + goodCombination.getGoodSku());
                                        }
                                    }
                                    //跟新数据库
                                    updateBatchById(updateData);
                                    //更新库存占用量
                                    goodStockService.refreshZhOccupy(updateData);
                                } else {
                                    logger.info("------------synGoodsStockZh:SKU在聚水潭不存在！");
                                    continue;
                                }
                            } else {
                                logger.info("------------synGoodsStockZh:" + listReturnData.getDesc());
                                continue;
                            }
                        } else {
                            logger.info("------------synGoodsStockZh:同步聚水潭失败！");
                            continue;
                        }

                    }
                } catch (Exception e) {
                    logger.error("------------synGoodsStockZh:处理失败" + e.getMessage(), e);
                }
            }
        }
    }

    public boolean isCombination(String goodSku){
        Object o = redisTemplate.opsForHash().get(GOOD_COMBINATION_SKULIST, goodSku);
        if (o != null && !"".equals(o)) {
            return true;
        }
        EntityWrapper<GoodCombination> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_SKU", goodSku);
        GoodCombination goodCombinations = selectOne(entityWrapper);
        if (goodCombinations != null) {
            redisTemplate.opsForHash().put(GOOD_COMBINATION_SKULIST, goodCombinations.getGoodSku(), 1);
            return true;
        }
        return false;
    }
}
