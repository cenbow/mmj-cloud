package com.mmj.elasticsearch.order.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.mmj.common.model.order.OrderPayDto;
import com.mmj.common.model.order.OrderSearchConditionDto;
import com.mmj.common.model.order.OrderSearchResultDto;
import com.mmj.common.utils.DateUtils;
import com.mmj.elasticsearch.ElasticsearchApplication;
import com.mmj.elasticsearch.order.domain.OrdersDocument;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description: 订单单元测试
 * @auther: KK
 * @date: 2019/8/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ElasticsearchApplication.class})
public class OrdersDocumentServiceTest {
    @Autowired
    private OrdersDocumentService ordersDocumentService;

    @Test
    public void create() {
        long s = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            OrdersDocument ordersDocument = new OrdersDocument();
            ordersDocument.setOrderNo(RandomStringUtils.randomNumeric(18));
            ordersDocument.setOrderAmount(122);
            ordersDocument.setOrderStatus(2);
            ordersDocument.setOrderTime(System.currentTimeMillis());
//            ordersDocument.setPayAmount(10);
//            ordersDocument.setPayTime(System.currentTimeMillis());
            ordersDocument.setOrderType(4);
            ordersDocument.setUserId(456456465161161L);
            List<OrdersDocument.Goods> goodsList = Lists.newArrayList();
            OrdersDocument.Goods goods1 = new OrdersDocument.Goods("A类棉婴童纯棉隔尿垫子", "https://cdn.polynome.cn/test/1565091690338.jpg", "");
            OrdersDocument.Goods goods2 = new OrdersDocument.Goods("B类棉婴童纯棉隔尿垫子", "https://cdn.polynome.cn/test/1565091690338.jpg", "");
            goodsList.add(goods1);
            goodsList.add(goods2);
            ordersDocument.setGoods(goodsList);
            ordersDocument.setConsigneeName("张三");
            ordersDocument.setConsigneeTelNumber("13117175254");
            ordersDocumentService.create(ordersDocument);
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时：" + (e - s) / 1000);
    }

    @Test
    public void updateOrderStatusByOrderNo() {
        ordersDocumentService.updateOrderStatusByOrderNo("1234567890", 8);
    }

    @Test
    public void updateOrderPayByOrderNo() {
        OrderPayDto orderPayDto = new OrderPayDto();
        orderPayDto.setOrderNo("750150542963635801");
        orderPayDto.setPayAmount(1220);
        orderPayDto.setPayTime(new Date());
        ordersDocumentService.updateOrderPayByOrderNo(orderPayDto);
    }

    @Test
    public void search() {
        Date startTime = DateUtils.parse("2019-07-31 14:15:44");
        Date endTime = DateUtils.parse("2019-08-31 14:15:44");
//        Calendar calendar = Calendar.getInstance();
//        Date endTime = calendar.getTime();
//        calendar.add(Calendar.DATE, -1);
//        Date startTime = calendar.getTime();
        OrderSearchConditionDto dto = new OrderSearchConditionDto();
        dto.setCurrentPage(1);
        dto.setPageSize(10);
        dto.setOrderType(4);
//        dto.setOrderNo("112781148302240789");
        dto.setCreateTimeStart(startTime.getTime());
        dto.setCreateTimeEnd(endTime.getTime());
        Page<OrderSearchResultDto> orderSearchResultDtoPage = ordersDocumentService.search(dto);
        System.out.println(orderSearchResultDtoPage);
    }

}
