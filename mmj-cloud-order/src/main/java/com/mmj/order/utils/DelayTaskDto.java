package com.mmj.order.utils;

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
    private String businessId;
    private String businessData;
    private String businessType;
    private String executeTime;
}
