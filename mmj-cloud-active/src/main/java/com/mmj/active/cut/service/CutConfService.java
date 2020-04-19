package com.mmj.active.cut.service;

import com.mmj.active.cut.model.CutConf;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.cut.model.dto.BossCutSysDto;
import com.mmj.active.cut.model.vo.BossCutSysEditVo;

/**
 * <p>
 * 砍价公共配置表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
public interface CutConfService extends IService<CutConf> {
    /**
     * 获取砍价配置
     *
     * @return
     */
    BossCutSysDto getSys();

    /**
     * 编辑砍价配置
     *
     * @param sysEditVo
     */
    void editSys(BossCutSysEditVo sysEditVo);
}
