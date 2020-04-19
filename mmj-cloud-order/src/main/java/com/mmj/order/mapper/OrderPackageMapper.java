package com.mmj.order.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.order.model.OrderPackage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 订单包裹信息 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-13
 */
@Repository
public interface OrderPackageMapper extends BaseMapper<OrderPackage> {

    /**
     * 通过订单号获取所有包裹编号
     *
     * @param orderNo
     * @return
     */
    List<String> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 通过包裹编号查询包裹商品信息
     *
     * @param packageNo
     * @return
     */
    List<OrderPackage> selectByPackageNo(@Param("packageNo") String packageNo);


    /**
     * boss 后台通过订单号获取包裹信息
     *
     * @param orderNo
     * @return
     */
    List<OrderPackage> getPackage(@Param("orderNo") String orderNo);


}
