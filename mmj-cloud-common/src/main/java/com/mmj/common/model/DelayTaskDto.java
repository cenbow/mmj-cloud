package com.mmj.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 延时任务封装
 * @auther: KK
 * @date: 2019/8/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelayTaskDto {
    private String businessId; //业务唯一ID
    private String businessData; //业务数据
    private String businessType; //业务类型
    private String executeTime; //执行时间
}
