package com.mmj.user.address.service;

import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserMerge;
import com.mmj.user.address.model.BaseUserAddr;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 用户收货地址 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-07-01
 */
public interface BaseUserAddrService extends IService<BaseUserAddr> {

    ReturnData<Object> save(BaseUserAddr baseUserAddr);

    Object deleteByAddrId(Integer addrId, Long userid);

    List<BaseUserAddr> selectAddressList(Long userid);

    BaseUserAddr selectByAddrId(Long userid, Integer addrId);

    Object updateDefaultAddress(Long userid, Integer addrId);

    void updateUserId(UserMerge userMerge);
}
