package com.mmj.pay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.utils.HttpTools;
import com.mmj.common.utils.MD5Util;
import com.mmj.common.utils.StringUtils;
import com.mmj.pay.sdk.weixin.MmjPayWxPayConfig;
import com.mmj.pay.sdk.weixin.WXPay;
import com.mmj.pay.sdk.weixin.WXPayConstants;
import com.mmj.pay.sdk.weixin.WXPayUtil;
import com.mysql.jdbc.JDBC4PreparedStatement;
import org.apache.commons.lang.StringEscapeUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {

    public static void main(String[] args) throws Exception{
        doConcurrentPost();
    }

    /**
     * 并发请求
     */
    public static void doConcurrentPost(){
        int size = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpTools httpTools = new HttpTools();
                    Map<String, String> map = new HashMap<>();
                    map.put("txtUserName", StringUtils.getUUid());
                    map.put("password", StringUtils.getUUid());
                    map.put("txtSecretCode", StringUtils.getUUid());
                    while (true){
                        try {
                            httpTools.doPost("https://music.163.com/", map);
                        } catch (Exception e) {

                        }
                    }
                }
            };
            countDownLatch.countDown();
            thread.start();
        }
    }

    /**
     * 解密退款结果通知
     */
    public static void decodeRefund() throws Exception{
        String xml = "<xml><return_code>SUCCESS</return_code><appid><![CDATA[wxca855317c075f407]]></appid><mch_id><![CDATA[1517122521]]></mch_id><nonce_str><![CDATA[91a9bd41be273298d965d33a2039735c]]></nonce_str><req_info><![CDATA[U2VctlOGcb5EzusPErnqrhYSQSOfRaPhQqamlBskzDFtrvSsQ++sQYLey9cmP24R8z3K9nc8SACib12v81uzRAfsXUevBjfqqNCIE2nWCMYqsRGS+q43YITha7NuPwX2Kdl51jRPdREBE6q+NroGwU8301oeAxZqQtt3A9Vwb3n9I0yGjs2dj7llP5F0rODQXvHG+/WYHw+u5/vtSbPDCq3hZDogmxCDZPBCqU9ONfCwxCREwNGUlbmOiIV2Vzah45yoTCuCXHNM5sVJGXqiKwU6/sCHsnOoC1fmkVlyGRhK6PhgvBFJMe3QhydwqYuZASTx2+1YhwwEt0aQl+uZHQtwMXsTu+wXarfrFsekdiNypYIjGCjuEMoRRll2/tgdHonms5/RdUrPWYaibFKzz6l/lRX/PKg4TThxCD3ebK6om1TS03sA61igETe050+LdOgssI+Vb0LIzXv/EIfZDe3bHKH9PatTQRAZqNn3DpDAXRExcUN0Y5H341y/X1XAMtxnro7lzPBo1xYL6F5s3atxvoqSKhs+1/+OjFQ/hdXklsFx3QV1SuXdxmbxJldnBDFzJ78C6dTDyTVROsasK33EJWFa98JL9srdkbCtGeFvzlx7rO21mMs3/7Rj4HZn1+yRVcHw/B+RlN1neIf4OcLkvQ1tPSVDn2RloEWdbmOkdPYKEtIu3pdLBSKrt9/gD7BXCMCKHC2NbSE6l1s4FIZzcc9uDeS1AK/CB3/yFX3PeCRKxa8Wb2hQtKayVcMctPP9Jg/KVYMJWhahYOP/R+2hr0M3nPDb4e6DG+d0EnuHR6VSpEADDvw6EIJ1ZZ+mNNNppxBpOpSF+8yhmdcLzrh/snfzv4QaPnL+6Bhp8N12B6TS72I78X+maHavzX7SSAaUSsPtbr1giIJCg6Hwj6SA+z46djA6iq8id6v0PoaHzhyEh4Fwba/a/cMg04xwL3n+xjsuFxUebKuLllbUB40yM8ABig1FpaJLZx5pS/l2cvkt5aWmb8kukDKGiIu81WIG021O9FHHlT+aTCEP56CGp++V5QXMx/FhpRPHhVyGpYavDraWeqMXqA3rLjO3gOVvM7yOcRZ/iXqTd0q0hQ==]]></req_info></xml>";
        Map<String, String> xmlToMap = WXPayUtil.xmlToMap(xml);
        byte[] b = org.bouncycastle.util.encoders.Base64.decode(xmlToMap.get("req_info"));
        String key =  MD5Util.MD5Encode("F822F2746F7393E76144D06000AF2993", "utf-8").toLowerCase();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        System.out.println(new String(cipher.doFinal(b)));
    }


    /**
     * 生成推广红包码
     */
    public static void generateRedCode(){
        Set<String> set = new HashSet<>();
        while (set.size() != 60){
            set.add("MMJRED-"+ StringUtils.getUUid(8));
        }
        for (String item: set){
            String str = "INSERT INTO `t_wx_red_activity` (`id`, `red_code`, `red_min_money`, `red_max_money`, " +
                    "`limit_times`, `create_time`) VALUES ('MMJRED-rRd44tKU', 'MMJRED-rRd44tKU', '1010', '1010', '1', '2019-09-23 09:55:42');";
            str = str.replaceAll("MMJRED-rRd44tKU", item).replaceAll("1010","5480");
            System.out.println(str);
        }
    }

    /**
     * 销毁推广红包码
     */
    public static void destroyRedCode() throws Exception{
        String sql = "select * from t_wx_red_activity where red_code in (";
        List<String> lines = Files.lines(Paths.get("C:\\Users\\84797\\Desktop\\destroy_redcode.txt"), StandardCharsets.UTF_8).collect(Collectors.toList());
        for (String item: lines){
            sql += "\"" + item.trim() + "\",";
        }
        sql = sql.substring(0, sql.length()-1) + ")";
        System.out.println(sql);
    }

     /**
     * 公众号二维码管理数据迁移
     */
    public static void transferWxQrcodeManager(){
        try {
            Connection proOld = getProOld();
            Statement statementOld = proOld.createStatement();
            ResultSet resultSetOld = statementOld.executeQuery("select * from wx_mp_manager");
            Connection proNew = getProNew();
            PreparedStatement statementNew = proNew.prepareStatement(
                    "INSERT INTO `mmj`.`t_wx_qrcode_manager` ( `QRCODE_NAME`, `APP_NAME`, `APP_ID`, `USER_TAG_NAMES`, `USER_TAG_IDS`, `CHANNEL_ID`, `CHANNEL_NAME`, `PERSON_COUNT`," +
                            " `PATH`, `REPLY_ONE_TYPE`, `REPLY_ONE_CONTENT`, `REPLY_ONE_DATA`, `REPLY_ONE_IMG`, `REPLY_TWO_TYPE`, `REPLY_TWO_CONTENT`, `REPLY_TWO_DATA`," +
                            " `REPLY_TWO_IMG`, `REPLY_THRID_TYPE`, `REPLY_THRID_CONTENT`, `REPLY_THRID_DATA`, `REPLY_THRID_IMG`, `CREATE_TIME`, `CREATE_ID`, " +
                            "`CREATE_NAME`, `UPDATE_ID`, `UPDATE_TIME`, `UPDATE_NAME`) VALUES " +
                            "(?, ?, ?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "NULL, ?, ?, ?, " +
                            "NULL, ?, ?, ?, NULL,?," +
                            " ?, null, ?, null, ?, ?);\n");
            while (resultSetOld.next()){
                String qrcodeName = resultSetOld.getString("qrcode_name"); //公众号二维码名称
                statementNew.setString(1, qrcodeName);
                String mpName = resultSetOld.getString("mp_name"); //公众号名称
                statementNew.setString(2, mpName);
                String appid = resultSetOld.getString("mp_id"); //公众号appid
                statementNew.setString(3, appid);
                String userTagNames = resultSetOld.getString("user_tag_names");// 公众号标签
                statementNew.setString(4, userTagNames);
                String userTagIds = resultSetOld.getString("user_tag_ids");// 公众号标签id
                statementNew.setString(5, userTagIds);
                String channelId = resultSetOld.getString("channel_id"); //渠道id
                statementNew.setString(6, channelId);
                String channelName = resultSetOld.getString("channel_name"); //渠道名称
                statementNew.setString(7, channelName);
                int personCount = resultSetOld.getInt("person_count");  //扫码人数
                statementNew.setInt(8, personCount);

                String path = resultSetOld.getString("path").replaceAll("https://", ""); // 二维码图片地址
                statementNew.setString(9, path);
                String replyOneType = resultSetOld.getString("reply_one_type"); //回复1的类型
                String replyOneContent = resultSetOld.getString("reply_one_content"); //回复1的内容
                Map<String, String> map = wxQrcodeContentTransfer(replyOneContent, replyOneType, resultSetOld.getString("reply_one_media_id"));
                statementNew.setString(10, map.get("type"));
                statementNew.setString(11, map.get("content"));
                statementNew.setString(12, map.get("img"));

                String replyTwoType = resultSetOld.getString("reply_two_type"); //回复2的类型
                String replyTwoContent = resultSetOld.getString("reply_two_content"); //回复2的内容
                map = wxQrcodeContentTransfer(replyTwoContent, replyTwoType, resultSetOld.getString("reply_two_media_id"));
                statementNew.setString(13, map.get("type"));
                statementNew.setString(14, map.get("content"));
                statementNew.setString(15, map.get("img"));

                String replyThridType = resultSetOld.getString("reply_thrid_type"); //回复3的类型
                String replyThridContent = resultSetOld.getString("reply_thrid_content"); //回复3的内容
                map = wxQrcodeContentTransfer(replyThridContent, replyThridType, resultSetOld.getString("reply_thrid_media_id"));
                statementNew.setString(16, map.get("type"));
                statementNew.setString(17, map.get("content"));
                statementNew.setString(18, map.get("img"));

                statementNew.setString(19, resultSetOld.getString("create_time"));
                statementNew.setString(20, resultSetOld.getString("create_name"));
                statementNew.setString(21, resultSetOld.getString("update_time"));
                statementNew.setString(22, resultSetOld.getString("update_name"));

                String rsq = ((JDBC4PreparedStatement)statementNew).asSql();
                System.out.print(rsq);
                statementNew.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 新老库公众号码内容转换
     * @param content
     * @param type
     * @return
     */
    private static Map<String, String> wxQrcodeContentTransfer(String content, String type,String mediaId){
        Map<String, String> map = new HashMap<>();
        if("text".equals(type)){ //文本内容转换
            //System.out.println("文本消息转换入参:\n" + content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            String tempMsg = "{\"msgtype\":\"text\",\"text\":{\"content\":\"000000\"}}";
            JSONObject msgJson = JSON.parseObject(tempMsg);
            msgJson.getJSONObject("text").put("content", jsonObject.getString("contentText"));
            //content = tempMsg.replaceAll("000000", unEscapeString(jsonObject.getString("contentText")));
            //System.out.println("文本消息转换出:\n" + content);
            map.put("content", msgJson.toJSONString());
            map.put("img", null);
            map.put("type", "text");
        }else if("pic".equals(type)){ //图文消息转换
            String tempMsg = "{\"image\":{\"media_id\":\"000000\"},\"msgtype\":\"image\"}";
            JSONObject jsonObject = JSONObject.parseObject(content);
            content = tempMsg.replaceAll("000000", mediaId);
            map.put("content", content);
            map.put("img", jsonObject.getString("contentImage"));
            map.put("type", "image");
        }else if("mpPage".equals(type)){ //小程序卡片消息转换
            String tempMsg = "{\"miniprogrampage\":{\"pagepath\":\"000000\",\"thumb_media_id\":\"111111\"," +
                    "\"appid\":\"222222\",\"title\":\"333333\"},\"msgtype\":\"miniprogrampage\"}";
            JSONObject jsonObject = JSONObject.parseObject(content);
            content = tempMsg.replaceAll("000000", jsonObject.getString("pagepath")).replaceAll("111111", mediaId).replaceAll("222222", "wx7a01aef90c714fe2")
            .replaceAll("333333", jsonObject.getString("mpTitleValue"));
            map.put("content", content);
            map.put("img", jsonObject.getString("mpImageValue"));
            map.put("type", "miniprogrampage");
        }
        return map;
    }

    /**
     * 获取新环境的数据库链接
     * @return
     */
    public static Connection getProNew(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://118.31.15.139/mmj?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true", "root", "mX66TGIwntkFHJzC");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 获取旧环境的数据库链接
     * @return
     */
    public static Connection getProOld(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://134.175.203.88:3306/mmjdatabase?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true", "mmjdbuser", "M*.*m^.^-j-$!@#1821");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
