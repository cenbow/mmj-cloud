package com.mmj.user.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.member.model.KingRepalce;

import java.util.List;

/**
 * <p>
 * 买买金兑换表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
public interface KingRepalceService extends IService<KingRepalce> {

    void batchSaveOrUpdate(List<KingRepalce> list);

    void getCoupon(Integer templateId);
}
