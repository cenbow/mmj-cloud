package com.mmj.active.cut.service;

import com.mmj.active.cut.model.CutTask;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.cut.model.dto.CutUserTaskDto;
import com.mmj.common.model.BaseUser;

/**
 * <p>
 * 砍价任务 服务类
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
public interface CutTaskService extends IService<CutTask> {
    /**
     * 获取用户任务状态
     *
     * @return
     */
    CutUserTaskDto getCutUserTask();

    /**
     * 添加发起砍价数
     */
    void addSponsorNumber();

    /**
     * 添加帮砍数
     *
     * @param baseUser      用户基本信息
     * @param sponsorUserId 发起人
     * @param assistUserId  帮砍人
     * @param cutNo         砍价编码
     * @param cutId         砍价ID
     */
    void addAssistNumber(BaseUser baseUser, Long sponsorUserId, Long assistUserId, String cutNo, Integer cutId);

}
