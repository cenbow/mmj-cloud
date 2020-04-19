package com.mmj.order.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.order.model.OrderGood;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 订单商品表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-17
 */
@Repository
public interface OrderGoodMapper extends BaseMapper<OrderGood> {

    /**
     * 通过订单
     *
     * @param orderNo
     * @param goodId
     * @param goodSku
     * @return
     */
    OrderGood getOrderGood(HashMap<Object, Object> map);


    /**
     * Boss 后台 通过订单号查询订单商品(通过视图查询)
     *
     * @param orderNo
     * @return
     */
    List<OrderGood> selectByOrderNo(@Param("orderNo") String orderNo);


    /**
     * boss后台通过包裹号获取商品信息
     *
     * @param packageNo
     * @return
     */
    List<OrderGood> selectByPackageNo(@Param("packageNo") String packageNo);

}
