package com.mmj.notice.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-23
 */
public interface WxFileService extends IService<WxFile> {

    WxFile queryByBusinessId(String businessId);

    void create(WxFile wxFile);

}
