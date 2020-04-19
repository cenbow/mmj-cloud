package com.mmj.common.model.third.recharge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description: 号码归属地信息
 * @auther: KK
 * @date: 2019/8/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NumberInfoDto {
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 所在省名称
     */
    private String province;
    /**
     * 所在市名称
     */
    private String city;
    /**
     * 运营商名称
     */
    private String operator;
}
