package com.mmj.active.advertisement.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.advertisement.model.AdvertisementManage;
import com.mmj.active.advertisement.model.AdvertisementManageVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-24
 */
public interface AdvertisementManageService extends IService<AdvertisementManage> {

    List<AdvertisementManage> queryList();

    AdvertisementManage queryAdvertisement(String pageType);

    String save(AdvertisementManageVo manageVo);

}
