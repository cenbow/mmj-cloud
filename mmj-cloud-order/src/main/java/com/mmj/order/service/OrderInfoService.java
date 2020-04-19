package com.mmj.order.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserOrderStatistics;
import com.mmj.common.model.UserOrderStatisticsParam;
import com.mmj.common.model.order.OrderProduceDto;
import com.mmj.common.model.order.OrderSearchResultDto;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderPayment;
import com.mmj.order.model.dto.*;
import com.mmj.order.model.vo.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单信息表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
public interface OrderInfoService extends IService<OrderInfo> {

    OrderInfo getByOrderNo(String orderNo);

    /**
     * 下单
     *
     * @param orderSaveVo
     * @return
     */
    SaveOrderDto produce(OrderSaveVo orderSaveVo);

    /**
     * z  支付回调
     *
     * @param orderNo
     * @param payAmount
     * @param serialNum
     * @param data
     * @return
     */
    void addOrderPayInfo(String orderNo, Integer payAmount, String serialNum, Date data, Long userId, String appId, String openId);

    /**
     * 待发货
     */
    void toBeDelivered(String... orderNos);

    /**
     * 待开奖
     *
     * @param orderNos
     */
    void toBeAwarded(String... orderNos);

    /**
     * 待分享
     *
     * @param orderNos
     */
    void pendingSharing(String... orderNos);

    /**
     * 关闭订单-将订单状态更改为关闭
     *
     * @param orderNos
     */
    void closeOrder(String... orderNos);

    /**
     * 获取订单列表(小程序端)
     *
     * @param orderListVo
     */
    Page<OrderListDto> getOrderList(OrderListVo orderListVo);

    /**
     * 小程序详情
     *
     * @param orderDetailVo
     * @return
     */
    OrderDetaislDto getDetails(OrderDetailVo orderDetailVo);

    /**
     * boss后台查询订单列表
     *
     * @param bossListVo
     * @return
     */
    ReturnData<Page<OrderSearchResultDto>> getOrderListFromES(BossListVo bossListVo);


    /**
     * 获取Boss订单详情
     *
     * @param bossDetailVo
     * @return
     */
    BossDetailDto getBossDetail(BossDetailVo bossDetailVo) throws Exception;


    OrderInfo selectByOrderNo(String orderNo, Long userId);


    /**
     * 取消订单
     *
     * @param cancelVo
     * @return
     */
    String cancel(CancelVo cancelVo) throws Exception;

    /**
     * 删除订单
     *
     * @param removeOrderVo
     * @return
     */
    String removeOrder(RemoveOrderVo removeOrderVo) throws Exception;

    /**
     * 确认收货
     *
     * @param receiveOrderVo
     * @return
     * @throws Exception
     */
    String receiveOrder(ReceiveOrderVo receiveOrderVo) throws Exception;

    /**
     * 自动确认收货
     *
     * @param orderNo
     * @return
     */
    boolean autoReceipt(String orderNo);


    /**
     * 通过订单号获取获取支付信息
     *
     * @param
     * @param userId
     */
    OrderPayment getOrderPayment(String orderNo, Long userId);

    /**
     * 获取用户订单统计数量
     *
     * @param userId
     * @return
     */
    OrderStatsDto getUserOrderStats(Long userId);

    /**
     * 虚拟商品发货
     *
     * @param
     * @return
     */
    String sendVirtualGood(OrderGoodVo orderGoodVo);

    /**
     * 重新上传ERP
     *
     * @param uploadErpVo
     */
    void uploadErp(UploadErpVo uploadErpVo);

    /**
     * 扣减库存
     *
     * @param decrGoodNum
     * @return
     */
    Boolean decrGood(DecrGoodNum decrGoodNum);


    /**
     * 通过订单号获取订单商品信息
     *
     * @param orderGoodVo
     * @return
     */
    List<OrderGoodsDto> getOrderGoodList(OrderGoodVo orderGoodVo);

    /**
     * 通过订单号获取包裹信息
     *
     * @param orderNo
     * @param userId
     * @return
     */
    List<OrderPackageDto> getOrderPackages(String orderNo, String userId);


    /**
     * 获取用户所有的订单号
     * （抽奖订单与接力购抽奖排除）
     *
     * @param useId
     * @return
     */
    List<OrderInfo> getUserAllOrderNos(String useId);


    /**
     * 获取订单信息
     *
     * @param orderInfoGoodVo
     * @return
     */
    List<OrderGood> getOrderInfoByGood(OrderInfoGoodVo orderInfoGoodVo);

    /***
     *  0元支付等 修改订单并上传聚水潭
     * @param updateStatusVo
     * @return
     */
    String updateOrderInfo(UpdateStatusVo updateStatusVo);


    /**
     * 获取订单信息（供前端用）
     *
     * @param appId
     * @param openId
     * @param orderNo
     * @return
     */
    PayInfoDto getOrderPayInfo(String appId, String openId, String orderNo, Long userId) throws Exception;

    /**
     * 查询订单列表
     *
     * @param userId
     * @return
     */
    double getConsumeMoney(Long userId);


    /**
     * 小程序订单详情之会员，返现，商品推荐等
     *
     * @param orderDetailVo
     * @return
     */
    OrderDetaisMemberDto getMemberDetails(OrderDetailVo orderDetailVo);


    /**
     * 小程序获取拼团接口
     *
     * @param orderDetailVo
     * @return
     */
    OrderDetailGroupDto getGroupDetails(OrderDetailVo orderDetailVo) throws InterruptedException;

    Map<String, Object> lotteryDetail(String orderNo);

    /**
     * 查询是否会员首单
     *
     * @param memberOrderVo
     * @return
     */
    List<OrderInfo> getOrderList(MemberOrderVo memberOrderVo);

    /**
     * 查询历史消费金额Two
     *
     * @param orderDetailVo
     * @return
     */
    double getConsumeMoneyTwo(OrderDetailVo orderDetailVo);

    /**
     * 查询快递信息
     *
     * @paramOrd
     */
    PollQueryResponse queryLogistics(LogisticsQueryVo logisticsQueryVo);

    Integer getLotteryId(String groupNo);


    /**
     * 新老用户判断
     *
     * @param userId
     * @return
     */
    boolean checkOldUser(Long userId);

    /**
     * 聚水潭取消订单
     *
     * @param
     */
    void jstCancelOrder(Map<String, String> map) throws Exception;

    boolean batchUpdateStatus(String orderNo, Integer status);

    /**
     * 过期订单
     *
     * @return
     */
    boolean timeoutCancel(String orderNo);

    /**
     * 订单复购
     *
     * @param userId
     * @param orderNo
     */
    int buyAgain(Long userId, String orderNo);


    String updateAfterSaleFlag(UserOrderVo userOrderVo);

    /**
     * 更新收货地址
     *
     * @param orderAddressVo
     * @return
     */
    boolean updateOrderAddress(OrderAddressVo orderAddressVo);


    /**
     * 查询已完成的订单
     *
     * @param orderFinishGoodVo
     * @return
     */
    List<OrderGoodsDto> getOrderFinishList(OrderFinishGoodVo orderFinishGoodVo);

    /**
     * 批量修改主订单号状态
     *
     * @param orderNos
     * @param orderStatus
     * @return
     */
    boolean batchUpdateOrderStatus(List<String> orderNos, Integer orderStatus);

    /**
     * 修改订单号
     *
     * @param orderStatus
     * @param orderNos
     * @return
     */
    boolean updateOrderStatus(Integer orderStatus, String... orderNos);

    List<OrderInfo> getLotteryWaitPay(Integer busId);

    List<UserOrderStatistics> getUsersOrdersDataForChannel(UserOrderStatisticsParam param);

    /**
     * 生成活动订单
     *
     * @param orderProduceDto
     */
    void produceActiveOrder(OrderProduceDto orderProduceDto);
}
