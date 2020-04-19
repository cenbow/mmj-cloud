package com.mmj.aftersale.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.aftersale.model.AfterSales;
import com.mmj.aftersale.model.dto.AfterSalesListDto;
import com.mmj.aftersale.model.vo.AfterSalesListVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 售后信息表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Repository
public interface AfterSalesMapper extends BaseMapper<AfterSales> {

    List<AfterSales> queryAfterSalesList(Page<AfterSalesListDto> page, AfterSalesListVo afterSalesListVo);
}
