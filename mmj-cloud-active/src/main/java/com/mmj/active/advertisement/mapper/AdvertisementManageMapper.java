package com.mmj.active.advertisement.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.active.advertisement.model.AdvertisementManage;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-24
 */
public interface AdvertisementManageMapper extends BaseMapper<AdvertisementManage> {

    void deleteAll();

}
