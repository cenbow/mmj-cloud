package com.mmj.active.common.constants;

public interface OrderTypeConstants {

    public interface OrderType{
        public final static String CUSTOM = "CUSTOM";   //自定义排序

        public final static String RULE = "RULE";   //规则排序
    }

    public interface filterRule{
        public final static String SALE_ASC = "SALE_ASC";   //按销量升序

        public final static String SALE_DESC = "SALE_DESC";  //按销量倒序

        public final static String WAREHOUSE_ASC = "WAREHOUSE_ASC"; //按库存升序

        public final static String WAREHOUSE_DESC = "WAREHOUSE_DESC"; //按库存倒序

        public final static String CREATER_ASC = "CREATER_ASC"; //按创建时间升序

        public final static String CREATER_DESC = "CREATER_DESC"; //按创建时间倒序

        public final static String MODIFY_ASC = "MODIFY_ASC";   //按编辑时间升序

        public final static String MODIFY_DESC = "MODIFY_DESC"; //按编辑时间倒序
    }
}
