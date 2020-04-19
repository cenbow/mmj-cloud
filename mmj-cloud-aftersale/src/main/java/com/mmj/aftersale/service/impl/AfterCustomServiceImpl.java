package com.mmj.aftersale.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.aftersale.mapper.AfterCustomMapper;
import com.mmj.aftersale.mapper.AfterSalesMapper;
import com.mmj.aftersale.model.AfterCustom;
import com.mmj.aftersale.model.AfterSales;
import com.mmj.aftersale.model.vo.ConsumerRemarksVo;
import com.mmj.aftersale.service.AfterCustomService;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 客服沟通记录表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Service
public class AfterCustomServiceImpl extends ServiceImpl<AfterCustomMapper, AfterCustom> implements AfterCustomService {

    @Autowired
    private AfterCustomMapper afterCustomMapper;
    @Autowired
    private AfterSalesMapper afterSalesMapper;


    @Override
    public boolean consumerRemarks(ConsumerRemarksVo consumerRemarksVo) {
        EntityWrapper<AfterSales> afterSalesEntityWrapper = new EntityWrapper<>();
        afterSalesEntityWrapper.eq("ORDER_NO",consumerRemarksVo.getOrderNo());
        afterSalesEntityWrapper.ge("DEL_FLAG",1);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,consumerRemarksVo.getCreaterId());
        List<AfterSales> afterSales = afterSalesMapper.selectList(afterSalesEntityWrapper);

        AfterCustom afterCustom = new AfterCustom();
        afterCustom.setOrderNo(consumerRemarksVo.getOrderNo());
        afterCustom.setCustomType(1);//备注类型 0用户备注 1客服备注 2审核备注 3质检备注
        if (afterSales.size() > 0 && afterSales != null) {
            afterCustom.setAfterSaleNo(consumerRemarksVo.getAfterSaleNo());
        } else {
            afterCustom.setAfterSaleNo("");
        }
        if (!StringUtils.isEmpty(consumerRemarksVo.getUserRemark())) {
            afterCustom.setUserRemark(consumerRemarksVo.getUserRemark());
        }
        afterCustom.setCreaterId(Long.valueOf(consumerRemarksVo.getCreaterId()));
        afterCustom.setDelFlag(1);
        afterCustomMapper.insert(afterCustom);
        return true;
    }
}
