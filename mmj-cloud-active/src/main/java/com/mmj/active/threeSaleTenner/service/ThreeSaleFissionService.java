package com.mmj.active.threeSaleTenner.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.threeSaleTenner.model.ThreeSaleFission;
import com.mmj.active.threeSaleTenner.model.ThreeSaleFissionEx;
import com.mmj.common.model.ReturnData;

import java.util.Map;

/**
 * <p>
 * 十元三件红包裂变 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-07-11
 */
public interface ThreeSaleFissionService extends IService<ThreeSaleFission> {

    Object save(ThreeSaleFissionEx threeSaleFissionEx);

    ThreeSaleFissionEx query();

    Object assist(ThreeSaleFissionEx threeSaleFissionEx);

    Map<String,Object> queryList(String type);

    Object updatePay(String orderNo);

    Object updateConfirm(String orderNo,String appId);

    Object cancelled(String orderNo);

    ReturnData<Object> doCash();

    boolean hasRedPackage();

    Object updateInvalid();

    void updateUserId(Long oldUserId, Long newUserId);
}
