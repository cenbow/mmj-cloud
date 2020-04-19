package com.mmj.common.utils;

import com.mmj.common.model.DelayQueueEntity;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时队列
 */
public class CommonDelayQueue implements Delayed,Runnable,Serializable {

    private static final long serialVersionUID = 6989679940386014051L;

    //截止时间
    private Long endTime;

    //参数JSON
    private DelayQueueEntity delayQueueEntity;

    public CommonDelayQueue(TimeUnit timeUnit, Long endTime, DelayQueueEntity delayQueueEntity){
        this.endTime = TimeUnit.MILLISECONDS.convert(endTime, timeUnit) + System.currentTimeMillis();
        this.delayQueueEntity = delayQueueEntity;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        Long l = this.endTime - System.currentTimeMillis();
        if (l > 0) {
            return unit.convert(this.endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        } else {
            return 0L;
        }
    }

    @Override
    public int compareTo(Delayed o) {
        CommonDelayQueue msg = (CommonDelayQueue) o;
        return this.endTime > msg.endTime ? 1 : (this.endTime < msg.endTime ? -1 : 0);
    }

    @Override
    public void run() {
        this.delayQueueEntity.run();
    }
}
