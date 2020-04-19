package com.mmj.aftersale.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.aftersale.model.AfterCustom;
import com.mmj.aftersale.model.vo.ConsumerRemarksVo;

/**
 * <p>
 * 客服沟通记录表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
public interface AfterCustomService extends IService<AfterCustom> {

    /**
     * 用户备注
     * @param consumerRemarksVo
     * @return
     */
    boolean consumerRemarks(ConsumerRemarksVo consumerRemarksVo);
}
