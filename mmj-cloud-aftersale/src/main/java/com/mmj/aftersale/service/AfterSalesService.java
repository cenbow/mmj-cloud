package com.mmj.aftersale.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.aftersale.model.AfterSales;
import com.mmj.aftersale.model.dto.*;
import com.mmj.aftersale.model.vo.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 售后信息表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
public interface AfterSalesService extends IService<AfterSales> {

    AfterSalesDetailDto getAfterSalesDetail(MoenyVo moenyVo);

    void auditResult(AfterSalesResultVo afterSalesResultVo);

    void afterSalesTest(AfterSalesTestVo afterSalesTestVo);

    MoneyDetailVo getMoneyDetail(MoenyVo moenyVo);

    String getMoneyReturn(MoneyReturnVo moneyReturnVo);

    String orderRefund(String orderNo,String userId,BigDecimal refundAmount,String remarks);

    Page<AfterSalesListDto> getAfterSalesLists(AfterSalesListVo afterSalesListVo);

    MoneyRefundDto queryRefund(String orderNo, Long userid);

    void afterSaleReturn(AfterSaleReturnVo afterSaleReturnVo);

    void afterSaleExpress(AfterSaleExpressVo afterSaleExpressVo);

    List<RemarkDto> orderRemarks(ConsumerRemarksVo consumerRemarksVo);

    boolean refuseGoods(String orderNo, Long userid, String context);

    boolean turnReturn(TrunReturnVo trunReturnVo);

    void updateStatus(String orderNo);

    List<OrderAfterSaleDto> selectByOrderNo(OrderAfterVo orderAfterVo);

    Integer getAfterSaleCount();

    Page<OrderListDto> getUserOrderAfter(OrderListVo orderListVo);

    String addAfterSale(AddAfterSaleVo addAfterSaleVo);

    AfterSaleDto getAfterSaleInfo(AddAfterSaleVo addAfterSaleVo);

    void jstCancelAfterSales(Map<String,String> list);

    void delStatusByAfterSaleNo(String orderNo);
}
