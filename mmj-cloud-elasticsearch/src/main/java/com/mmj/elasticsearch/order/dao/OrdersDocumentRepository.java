package com.mmj.elasticsearch.order.dao;


import com.mmj.elasticsearch.order.domain.OrdersDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @description: 订单
 * @auther: KK
 * @date: 2019/8/8
 */
public interface OrdersDocumentRepository extends ElasticsearchRepository<OrdersDocument, String> {
}
