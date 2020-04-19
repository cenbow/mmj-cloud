package com.mmj.notice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.notice.mapper.WxFormMapper;
import com.mmj.notice.model.WxForm;
import com.mmj.notice.service.WxFormService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 微信模板消息 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-19
 */
@Service
public class WxFormServiceImpl extends ServiceImpl<WxFormMapper, WxForm> implements WxFormService {

    /**
     * 保存模板消息id
     *
     * @param wxForm
     * @return
     */
    @Override
    public WxForm save(WxForm wxForm) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        wxForm.setAppid(userDetails.getAppId());
        wxForm.setOpenid(userDetails.getOpenId());
        insert(wxForm);
        return wxForm;
    }

    /**
     * 删除七天以前的formid
     */
    @Override
    public void del() {
        EntityWrapper<WxForm> entityWrapper = new EntityWrapper<>();
        Date beforeByMinute = DateUtils.getBeforeByMinute(8640);//只要大于6天就删除
        entityWrapper.gt("CREATE_TIME",beforeByMinute);
        delete(entityWrapper);
    }
}
