package com.mmj.aftersale.model;

public class Temp {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("CREATE OR REPLACE VIEW `v_after_sales_join` AS ");
        for (int i = 0; i < 10; i++) {
            if (i != 0) {
                sb.append(" UNION ALL ");
            }
            sb.append(" SELECT t").append(i).append(".CREATER_ID AS CREATER_ID,t")
            .append(i).append(".ORDER_NO AS ORDER_NO FROM t_after_sales_").append(i)
            .append(" t").append(i);
            }
        System.out.println(sb);
        }
}
