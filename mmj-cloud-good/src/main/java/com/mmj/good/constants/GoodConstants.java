package com.mmj.good.constants;

public interface GoodConstants {

    //sku 库存
    String SKU_STOCK = "GOOD:SALE:SKU_STOCK:";
    //sku 销量
    String SKU_SALE = "GOOD:SALE:SKU_SALE:";
    //组合商品 sku
    String SKU_STOCK_COMBINE = "GOOD:SALE:SKU_STOCK:COMBINE:";

    /**
     * 商品状态
     * -1：删除 0：暂不发布 1：立即上架 2：自动上架 3：上架失败
     */
    interface InfoStatus {
        String DELETED = "-1";
        String WAIT_ON = "0";
        String PUT_ON = "1";
        String PUT_ON_AUTO = "2";
        String PUT_NO_FAIL = "3";
    }

    /**
     * 商品类型
     * 001 拼团商品
     * 002 抽奖商品
     * 003 店铺商品
     * 004 十元三件商品
     * 005 砍价商品
     * 006 秒杀商品
     */
    interface InfoType {
        String GROUP = "001";
        String LOTTERY = "002";
        String SHOP = "003";
        String TEN_YUAN = "004";
        String BARGAIN = "005";
        String FLASH = "006";
    }

    /**
     * 是否虚拟商品
     */
    interface InfoVirtualFlag {
        Integer YES = 1;
        Integer NO = 0;
    }

    /**
     * 是否会员商品
     */
    interface InfoMemberFlag {
        Integer YES = 1;
        Integer NO = 0;
    }

    /**
     * 是否删除
     */
    interface InfoDelFlag {
        Integer YES = 1;
        Integer NO = 0;
    }

    /**
     * 是否虚拟商品
     */
    interface InfoAutoShow {
        Integer YES = 1;
        Integer NO = 0;
    }

    /**
     * 文件服务商
     * ALIYUN
     * TENGXUN
     */
    interface FileServer {
        String ALIYUN = "ALIYUN";
        String TENGXUN = "TENGXUN";
    }

    /**
     * 附件类型
     * SELLING_POINT：卖点
     * IMAGE：商品图片
     * MAINVIDEO：主视频
     * VIDEOTITLE：视频封面
     * WECHAT：小程序分享
     * H5：H5分享
     * DETAIL：详情
     * DETAILVIDEO 详情视频
     * DETAILTITLE：视频封面
     * SALEMODEL: 规格图片
     * ACTIVE: 活动图片
     */
    interface FileType {
        String SELLING_POINT = "SELLING_POINT";
        String IMAGE = "IMAGE";
        String MAINVIDEO = "MAINVIDEO";
        String VIDEOTITLE = "VIDEOTITLE";
        String WECHAT = "WECHAT";
        String H5 = "H5";
        String DETAIL = "DETAIL";
        String DETAILVIDEO = "DETAILVIDEO";
        String DETAILTITLE = "DETAILTITLE";
        String SALEMODEL = "SALEMODEL";
        String ACTIVE = "ACTIVE";
    }

    /**
     * 是否封面
     */
    interface FileTitleFlag {
        Integer YES = 1;
        Integer NO = 0;
    }

    /**
     * 标签状态
     */
    interface labelStatus {
        Integer OPEN = 1;
        Integer CLOSE = 0;
        Integer DELETE = -1;
    }

    interface ActiveOrderType {
        /**
         * 排序类型
         *  RANDOM 随机
         *  RULE 规则
         *  CUSTOM 自定义
         * 筛选规则
         *  SALE 按销量;
         *  WAREHOUSE 按库存
         *  CREATER 按创建时间
         *  MODIFY 按编辑时间
         *  THIRD 按三级分类
         * 顺序
         *  ASC 升序;
         *  DESC 倒序
         *  按三级分类时的值为规则拼接升降序
         */
        String RANDOM = "RANDOM";
        String RULE = "RULE";
        String CUSTOM = "CUSTOM";

        String SALE = "SALE";
        String WAREHOUSE = "WAREHOUSE";
        String CREATER = "CREATER";
        String MODIFY = "MODIFY";
        String THIRD = "THIRD";
        String ASC = "ASC";
        String DESC = "DESC";

        String SALEDESC = "SALEDESC";
        String WAREHOUSEDESC = "WAREHOUSEDESC";
        String CREATERDESC = "CREATERDESC";
        String MODIFYDESC = "MODIFYDESC";
        String SALEASC = "SALEASC";
        String WAREHOUSEASC = "WAREHOUSEASC";
        String CREATERASC = "CREATERASC";
        String MODIFYASC = "MODIFYASC";
    }

    interface ActiveType {

        int GROUP_LOTTERY = 1;  //普通抽奖

        int GROUP_JIELIGOU = 2;  //接力购

        int GROUP_RELAY_LOTTERY = 3;  //接力购抽奖

        int TEN_YUAN_THREE_PIECES = 4;  //十元三件

        int SECKILL = 5;      //秒杀

        int CUT = 7; //砍价

        int TOPIC = 8; //专题

        int GUESS_LIKE = 9;     //9 猜你喜欢

        int HOT_SALE = 10;      // 10 免邮热卖

        int CLASS_GOOD = 11;    // 11 分类商品

        int TUAN = 12;          //拼团

        int FREE_ORDER = 13;    //免费送

        int PRIZEWHEELS_6 = 14; //转盘6个十元店商品

        int VIRTUAL_GOOD = 15; //虚拟商品

        int SHOP_GOOD = 16; //店铺商品

        int MMJ_GOOD = 17; //买买金兑换商品

        int SEARCH_GOOD = 18; //商品搜索销量前十商品
    }

    //商品比价信息 是否展示(0:否 1:是)
    interface  CompareStatus {
        Integer YES = 1;
        Integer NO = 0;
    }

    //比价设置(1:品牌比价,2:比价活动)
    interface CompareType {
        Integer BRAND = 1;
        Integer ACTIVE = 2;
    }


}
