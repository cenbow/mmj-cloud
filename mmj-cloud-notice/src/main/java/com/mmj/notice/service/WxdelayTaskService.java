package com.mmj.notice.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxdelayTask;

/**
 * <p>
 * 延迟任务(一秒钟执行一次) 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-10
 */
public interface WxdelayTaskService extends IService<WxdelayTask> {

    /**
     * 修复延时队列遗漏的数据
     */
    void repair();

    /**
     * 获取间隔最小的时间
     * @return
     */
    Long getLastTime();
}
