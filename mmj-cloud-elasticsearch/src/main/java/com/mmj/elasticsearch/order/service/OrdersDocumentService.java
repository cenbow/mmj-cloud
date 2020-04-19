package com.mmj.elasticsearch.order.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.mmj.common.model.order.*;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.StringUtils;
import com.mmj.elasticsearch.order.dao.OrdersDocumentRepository;
import com.mmj.elasticsearch.order.domain.OrdersDocument;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description: 订单服务
 * @auther: KK
 * @date: 2019/8/8
 */
@Slf4j
@Service
public class OrdersDocumentService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private OrdersDocumentRepository ordersDocumentRepository;

    public Page<OrderSearchResultDto> search(OrderSearchConditionDto dto) {
        log.info("=> 订单检索条件：{}", dto);
        int currentPage = null == dto.getCurrentPage() ? 0 : (dto.getCurrentPage() - 1 < 0 ? 0 : dto.getCurrentPage() - 1);
        int pageSize = null == dto.getPageSize() ? 10 : dto.getPageSize();

        NativeSearchQueryBuilder build = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(dto.getOrderNo())) {
//            boolQueryBuilder.must(QueryBuilders.termQuery("orderNo", dto.getOrderNo()));
            boolQueryBuilder.must(
                    QueryBuilders.boolQuery().should(QueryBuilders.termQuery("orderNo", dto.getOrderNo()))
                            .should(QueryBuilders.termQuery("parentNo", dto.getOrderNo())));
        }
        if (Objects.nonNull(dto.getUserId())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("userId", dto.getUserId()));
        }
        if (Objects.nonNull(dto.getOrderStatus())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("orderStatus", dto.getOrderStatus()));
        }
        if (Objects.nonNull(dto.getVirtualGood())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("virtualGood", dto.getVirtualGood()));
        }
        if (Objects.nonNull(dto.getUploadErp())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("uploadErp", dto.getUploadErp()));
        }
        if (Objects.nonNull(dto.getOrderType())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("orderType", dto.getOrderType()));
        }
        if (Objects.nonNull(dto.getChannel())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("channel", dto.getChannel()));
        }
        if (Objects.nonNull(dto.getSource())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("source", dto.getSource()));
        }
        if (StringUtils.isNotEmpty(dto.getName())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("consigneeName", dto.getName()));
        }
        if (StringUtils.isNotEmpty(dto.getTelNumber())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("consigneeTelNumber", dto.getTelNumber()));
        }
        if (Objects.nonNull(dto.getCreateTimeStart()) && Objects.nonNull(dto.getCreateTimeEnd())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("orderTime").gte(dto.getCreateTimeStart()).lte(dto.getCreateTimeEnd()));
        }
        build.withQuery(boolQueryBuilder);
        build.withPageable(new PageRequest(currentPage, pageSize, new Sort(Sort.Direction.DESC, "orderTime")));
        SearchQuery searchQuery = build.build();
        log.info("=> 订单检索条件：{}", JSON.parseObject(searchQuery.getQuery().toString()).toJSONString());
        org.springframework.data.domain.Page<OrdersDocument> ordersDocumentPage = ordersDocumentRepository.search(searchQuery);
        List<OrdersDocument> ordersDocuments = ordersDocumentPage.getContent();
        List<OrderSearchResultDto> orderSearchResultDtos = Lists.newArrayListWithCapacity(ordersDocuments.size());
        ordersDocuments.forEach(ordersDocument -> {
            OrderSearchResultDto orderSearchResultDto = new OrderSearchResultDto();
            BeanUtils.copyProperties(ordersDocument, orderSearchResultDto);
            if (StringUtils.isNotEmpty(ordersDocument.getParentNo())) {
                orderSearchResultDto.setPackageNo(ordersDocument.getOrderNo());
                orderSearchResultDto.setOrderNo(ordersDocument.getParentNo());
            }
            orderSearchResultDto.setOrderAmount(PriceConversion.intToString(ordersDocument.getOrderAmount()));
            orderSearchResultDto.setOrderDate(new Date(ordersDocument.getOrderTime()));
            orderSearchResultDto.setUserId(String.valueOf(ordersDocument.getUserId()));
            if (Objects.nonNull(ordersDocument.getPayTime())) {
                orderSearchResultDto.setPayDate(new Date(ordersDocument.getPayTime()));
            }
            List<OrdersDocument.Goods> ordersDocumentGoods = ordersDocument.getGoods();
            List<OrdersMQDto.Goods> resultGoods = Lists.newArrayListWithCapacity(ordersDocumentGoods.size());
            ordersDocumentGoods.forEach(goods -> {
                OrdersMQDto.Goods goodsDto = new OrdersMQDto.Goods();
                BeanUtils.copyProperties(goods, goodsDto);
                resultGoods.add(goodsDto);
            });
            orderSearchResultDto.setGoods(resultGoods);
            orderSearchResultDtos.add(orderSearchResultDto);
        });
        Page page = new Page();
        page.setCurrent(dto.getCurrentPage());
        page.setSize(dto.getPageSize());
        page.setRecords(orderSearchResultDtos);
        page.setTotal((int) ordersDocumentPage.getTotalElements());
        return page;
    }

    /**
     * 创建订单
     *
     * @param ordersDocument
     */
    public void create(OrdersDocument ordersDocument) {
        OrdersDocument newOrdersDocument = ordersDocumentRepository.save(ordersDocument);
        log.info("ES新增结果:{}", newOrdersDocument);
    }

    /**
     * 批量新增包裹
     *
     * @param ordersPackageMQDtoList
     */
    public void batchCreatePackage(List<OrdersPackageMQDto> ordersPackageMQDtoList) {
        final OrdersDocument ordersDocument = ordersDocumentRepository.findOne(ordersPackageMQDtoList.get(0).getOrderNo());
        List<OrdersDocument> ordersDocumentList = Lists.newArrayListWithCapacity(ordersPackageMQDtoList.size());
        ordersPackageMQDtoList.forEach(ordersPackageMQDto -> {
            OrdersDocument packageDocument = new OrdersDocument();
            BeanUtils.copyProperties(ordersDocument, packageDocument);
            packageDocument.setOrderNo(ordersPackageMQDto.getPackageNo());
            packageDocument.setParentNo(ordersPackageMQDto.getOrderNo());
            packageDocument.setOrderTime(ordersPackageMQDto.getOrderDate().getTime());
            packageDocument.setOrderAmount(ordersPackageMQDto.getOrderAmount());
            packageDocument.setUploadErp(false);
            packageDocument.setVirtualGood(ordersPackageMQDto.getVirtualGood());
            packageDocument.setConsigneeName(ordersDocument.getConsigneeName());
            packageDocument.setConsigneeTelNumber(ordersDocument.getConsigneeTelNumber());
            List<OrdersDocument.Goods> goodsList = Lists.newArrayListWithCapacity(ordersPackageMQDto.getGoods().size());
            ordersPackageMQDto.getGoods().forEach(goods -> {
                OrdersDocument.Goods g = new OrdersDocument.Goods();
                g.setGoodName(goods.getGoodName());
                g.setGoodImage(goods.getGoodImage());
                g.setGoodSku(goods.getGoodSku());
                goodsList.add(g);
            });
            packageDocument.setGoods(goodsList);
            ordersDocumentList.add(packageDocument);
        });
        Iterable<OrdersDocument> resultDocument = ordersDocumentRepository.save(ordersDocumentList);
        log.info("ES新增包裹结果:{}", resultDocument);
        ordersDocumentRepository.delete(ordersDocument.getOrderNo());
    }

    /**
     * 根据订单号修改订单状态
     *
     * @param orderNo
     * @param orderStatus
     */
    public void updateOrderStatusByOrderNo(String orderNo, Integer orderStatus) {
        UpdateQuery updateQuery = new UpdateQuery();
        updateQuery.setId(orderNo);
        updateQuery.setClazz(OrdersDocument.class);
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.doc("orderStatus", orderStatus);
        updateRequest.fields("", "");
        updateQuery.setUpdateRequest(updateRequest);
        UpdateResponse updateResponse = elasticsearchTemplate.update(updateQuery);
        System.out.println(updateResponse);
    }

    /**
     * 批量更新订单状态
     *
     * @param orderStatusMQDtoList
     */
    public void updateOrderStatusByPackageNo(List<OrderStatusMQDto> orderStatusMQDtoList) {
        List<UpdateQuery> updateRequestList = Lists.newArrayListWithCapacity(orderStatusMQDtoList.size());
        orderStatusMQDtoList.forEach(orderStatusMQDto -> {
            UpdateQuery updateQuery = new UpdateQuery();
            updateQuery.setId(orderStatusMQDto.getOrderNo());
            updateQuery.setClazz(OrdersDocument.class);
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.doc("orderStatus", orderStatusMQDto.getOrderStatus());
            updateQuery.setUpdateRequest(updateRequest);
            updateRequestList.add(updateQuery);
        });
        elasticsearchTemplate.bulkUpdate(updateRequestList);
    }

    /**
     * 更改订单上传ERP状态
     *
     * @param uploadPackageNoList
     */
    public void updateUploadErpStatusByPackageNo(List<String> uploadPackageNoList) {
        List<UpdateQuery> updateRequestList = Lists.newArrayListWithCapacity(uploadPackageNoList.size());
        uploadPackageNoList.forEach(packageNo -> {
            UpdateQuery updateQuery = new UpdateQuery();
            updateQuery.setId(packageNo);
            updateQuery.setClazz(OrdersDocument.class);
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.doc("uploadErp", true);
            updateQuery.setUpdateRequest(updateRequest);
            updateRequestList.add(updateQuery);
        });
        elasticsearchTemplate.bulkUpdate(updateRequestList);
    }

    /**
     * 根据订单号更新支付信息
     *
     * @param orderPayDto
     * @param retry       重试次数
     */
    public void updateOrderPayByOrderNo(OrderPayDto orderPayDto, int retry) {
        while (retry < 3) {
            try {
                UpdateResponse updateResponse = updateOrderPayByOrderNo(orderPayDto);
                log.info("新增订单支付信息结果:{}", updateResponse);
                break;
            } catch (VersionConflictEngineException e) {
                log.error("=> 新增订单支付信息 message:{}", JSON.toJSONString(orderPayDto));
            }
            retry++;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 根据订单号更新支付信息
     *
     * @param orderPayDto
     */
    public UpdateResponse updateOrderPayByOrderNo(OrderPayDto orderPayDto) {
        UpdateQuery updateQuery = new UpdateQuery();
        updateQuery.setId(orderPayDto.getOrderNo());
        updateQuery.setClazz(OrdersDocument.class);
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest
                .doc("payTime", orderPayDto.getPayTime().getTime(), "payAmount", orderPayDto.getPayAmount());
        updateQuery.setUpdateRequest(updateRequest);
        return elasticsearchTemplate.update(updateQuery);
    }
}
