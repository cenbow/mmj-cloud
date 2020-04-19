package com.mmj.pay.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.utils.OrderUtils;
import com.mmj.pay.mapper.WxMchInfoMapper;
import com.mmj.pay.model.WxMchInfo;
import com.mmj.pay.service.WxMchInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * <p>
 * 商户号信息 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-09-02
 */
@Service
public class WxMchInfoServiceImpl extends ServiceImpl<WxMchInfoMapper, WxMchInfo> implements WxMchInfoService {

    /**
     * 根据订单号获取商户信息
     *
     * @param orderNo
     * @return
     */
    @Override
    public WxMchInfo getMchInfo(String orderNo) {
        if("all".equals(orderNo)){
            EntityWrapper wxMchInfoEntityWrapper = new EntityWrapper<>();
            wxMchInfoEntityWrapper.eq("ORDER_TYPE", "all");
            return selectOne(wxMchInfoEntityWrapper);
        }
        EntityWrapper<WxMchInfo> wxMchInfoEntityWrapper = new EntityWrapper<>();
        int orderType = OrderUtils.getOrderType(orderNo);
        String sql = "FIND_IN_SET('"+orderType+"',ORDER_TYPE)";
        wxMchInfoEntityWrapper.where(sql);
        List<WxMchInfo> wxMchInfos = selectList(wxMchInfoEntityWrapper);
        if(wxMchInfos.size() == 1){
            return wxMchInfos.get(0);
        }
        int size = wxMchInfos.size();
        return wxMchInfos.get(new Random().nextInt(size));
    }
}
