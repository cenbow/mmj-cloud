package com.mmj.active.cut.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 我的砍价信息
 * @auther: KK
 * @date: 2019/6/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyCutCache {
    private int cutStage = 1;
    /**
     * 待砍价金额(第一阶段)
     */
    private int firstWaitAmount;
    /**
     * 待砍的数量(第一阶段)
     */
    private int firstWaitNum;

    /**
     * 待砍价金额(第二阶段)
     */
    private int secondWaitAmount;
    /**
     * 待砍的数量(第二阶段)
     */
    private int secondWaitNum;

    /**
     * 待砍价金额(第三阶段)
     */
    private int thirdWaitAmount;
    /**
     * 待砍的数量(第三阶段)
     */
    private int thirdWaitNum;
}
