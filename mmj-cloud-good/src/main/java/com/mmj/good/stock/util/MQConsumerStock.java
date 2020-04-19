package com.mmj.good.stock.util;

import com.alibaba.fastjson.JSON;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.good.stock.model.GoodStock;
import com.mmj.good.stock.service.GoodStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MQConsumerStock {

    @Autowired
    private GoodStockService goodStockService;

    @KafkaListener(topics = {MQTopicConstant.HOLD_GOOD_STOCK})
    public void holdGoodStock(List<String> params) {
        if (params != null && !params.isEmpty()) {
            log.info("---------HOLD_GOOD_STOC:" + params);
            for (String param : params) {
                try {
                    List<GoodStock> goodStocks = JSON.parseArray(param, GoodStock.class);
                    Integer status = goodStocks.get(0).getStatus();
                    if (status == CommonConstant.GoodStockStatus.OCCUPY) {
                        //占用库存
                        goodStockService.occupyToCache(goodStocks);
                        goodStockService.insertBatch(goodStocks);
                    } else if (status == CommonConstant.GoodStockStatus.DEDUCT) {
                        //更新数据
                        goodStockService.updateBatch(goodStocks);
                        //扣减库存
                        goodStockService.deductToCache(goodStocks);
                    } else if (status == CommonConstant.GoodStockStatus.RELIEVE) {
                        goodStockService.insertBatch(goodStocks);
                        //释放库存
                        goodStockService.relieveToCache(goodStocks);
                    } else if (status == CommonConstant.GoodStockStatus.ROLLBACK) {
                        goodStockService.insertBatch(goodStocks);
                        //归还库存
                        goodStockService.rollbackToCache(goodStocks);
                    }
                } catch (Exception e) {
                    log.error("---------HOLD_GOOD_STOCK", e);
                }
            }
        }
    }



}
