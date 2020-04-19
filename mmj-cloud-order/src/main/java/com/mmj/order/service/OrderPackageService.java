package com.mmj.order.service;

import com.mmj.order.common.model.vo.CartOrderGoodsDetails;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderPackage;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.order.model.vo.OrderSavePackageVo;
import com.mmj.order.tools.Depot;

import java.util.List;

/**
 * <p>
 * 订单包裹信息 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-20
 */
public interface OrderPackageService extends IService<OrderPackage> {

    /**
     * 根据主订单号批量修改包裹状态
     *
     * @param orderNos
     * @param orderStatus
     * @return
     */
    boolean batchUpdateOrderPackageStatusByOrderNo(List<String> orderNos, Integer orderStatus);

    /**
     * 批量修改包裹状态
     *
     * @param packageNos
     * @param orderStatus
     * @return
     */
    boolean batchUpdateOrderPackageStatusByPackageNo(List<String> packageNos, Integer orderStatus);

    /**
     * 更新包裹上传聚水潭状态
     *
     * @param packageNos
     */
    void updateUploadErpStatus(List<String> packageNos);

    /**
     * 下单时保存子订单信息
     *
     * @param orderInfo
     * @param packageNo   自定义包裹号
     * @param virtualGood 是否虚拟商品 0否 1是
     * @param packageDesc 包裹备注
     */
    OrderPackage saveOrderPackage(OrderInfo orderInfo, String packageNo, Integer virtualGood, String packageDesc);

    /**
     * 通过订单号查询子订单列表
     *
     * @param orderNo
     * @return
     */
    List<OrderPackage> selectByOrderNo(String orderNo);

    /**
     * 通过包裹号查询
     *
     * @param packageNo
     * @return
     */
    OrderPackage selectByPackageNo(String packageNo);

    /**
     * 支付成功后拆单
     *
     * @param orderInfo
     */
    void unpick(OrderInfo orderInfo);

    /**
     * 通过包裹号发货
     *
     * @param packageNos
     */
    void packageToBeDelivered(List<String> packageNos);

    /**
     * 通过主订单号发货
     *
     * @param orderNos
     */
    void orderToBeDelivered(List<String> orderNos);

    /**
     * 包裹发货
     *
     * @param orderPackageList
     */
    void toBeDelivered(List<OrderPackage> orderPackageList);

    /**
     * 虚拟商品发货
     *
     * @param orderPackage
     * @param orderGoods
     * @param auto         是否发货
     */
    void virtualGoodToBeDelivered(OrderPackage orderPackage, List<OrderGood> orderGoods, boolean auto);
}
