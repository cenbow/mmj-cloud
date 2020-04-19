package com.mmj.active.homeManagement.constant;

public interface WebAlertConstant {
    /**
     * 跳转类型：优惠券
     */
    String JUMP_TYPE_COUPON = "COUPON";

    /**
     * 跳转类型：指定链接
     */
    String JUMP_TYPE_LINK = "HRAF";


    public interface webShow{
        //是否显示分类
        Integer NO_SHOW_FLAG = 0;
        Integer SHOW_FLAG = 1;

        //顶部大图是否显示
        Integer NO_TOP_SHOW = 0;
        Integer TOP_SHOW = 1;

        //营销是否显示
        Integer NO_MAKETING_SHOW = 0;
        Integer MAKETING_SHOW = 1;

        //橱窗是否显示
        Integer NO_SHOWCASE_SHOW = 0;
        Integer SHOWCASE_SHOW = 1;

        //小程序分享图是否展示
        Integer NO_WXSHARD_SHOW = 0;
        Integer WXSHARD_SHOW = 1;

        //是否显示商品排序
        Integer NO_GODD_ORDER = 0;
        Integer GODD_ORDER = 1;
    }
}
