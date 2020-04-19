package com.mmj.notice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.notice.mapper.WxdelayTaskMapper;
import com.mmj.notice.model.WxdelayTask;
import com.mmj.notice.service.WxdelayTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 延迟任务(一秒钟执行一次) 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-10
 */
@Service
public class WxdelayTaskServiceImpl extends ServiceImpl<WxdelayTaskMapper, WxdelayTask> implements WxdelayTaskService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 修复延时队列遗漏的数据
     */
    @Override
    public void repair() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,-30);
        EntityWrapper<WxdelayTask> wxdelayTaskEntityWrapper = new EntityWrapper<>();
        wxdelayTaskEntityWrapper.lt("EXECUTE_TIME", calendar.getTime()).orderBy("EXECUTE_TIME");
        List<WxdelayTask> wxdelayTasks = selectList(wxdelayTaskEntityWrapper);
        if(!wxdelayTasks.isEmpty()){
            wxdelayTasks.parallelStream().forEach( wxdelayTask -> {
                Calendar calendarTemp = Calendar.getInstance();
                calendarTemp.add(Calendar.MINUTE,1);
                wxdelayTask.setExecuteTime(calendarTemp.getTime());
            });
            updateBatchById(wxdelayTasks);
            redisTemplate.opsForValue().set("WX_DELAY_TASK_SEND_MIN", getLastTime()+"");
        }
    }

    /**
     * 获取间隔最小的时间
     * @return
     */
    @Override
    public Long getLastTime(){
        Page<WxdelayTask> page = new Page<>();
        page.setCurrent(1);
        page.setSize(1);
        EntityWrapper<WxdelayTask> wxdelayTaskEntityWrapper1 = new EntityWrapper<>();
        wxdelayTaskEntityWrapper1.gt("EXECUTE_TIME", new Date()).orderBy("EXECUTE_TIME");
        Page<WxdelayTask> page1 = selectPage(page, wxdelayTaskEntityWrapper1);
        List<WxdelayTask> records = page1.getRecords();
        if(null != records && records.size() > 0){
            Date executeTime = records.get(0).getExecuteTime();
            return  executeTime.getTime();
        }
        return -1L;
    }
}
