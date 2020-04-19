package com.mmj.user.common.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @description: 砍价商品信息请求
 * @auther: KK
 * @date: 2019/6/14
 */
@Data
public class CutGoodVo {
    private List<Integer> goodIds; //商品id - 查询条件
}
