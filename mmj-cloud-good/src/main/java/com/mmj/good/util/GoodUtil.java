package com.mmj.good.util;

import com.mmj.good.constants.GoodConstants;
import com.mmj.good.feigin.dto.ActiveSort;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;

import java.io.InputStream;

public class GoodUtil {

    // 1 初始化用户身份信息(secretId, secretKey)
    private static COSCredentials cred = new BasicCOSCredentials("AKID2AFmBOYRO9kAO8hI9EDTgtv3458LfLRX", "Pi1M5e1536xeUTRMDA2CD2XvCKISTCUs");
    // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    private static ClientConfig clientConfig = new ClientConfig(new Region("ap-guangzhou"));
    // 3 生成cos客户端
    private static COSClient cosClient = new COSClient(cred, clientConfig);
    //域名
    public static final String domainName = "cdn.polynome.cn";

    /**
     * @Description: 将文件上传到腾讯云（通过流的方式）
     * @param: [bucketName, key, input, length]
     * @return: java.lang.String
     */
    public static String uploadFile(String bucketName, String key, InputStream input, long length) throws Exception{
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        bucketName = bucketName==null || "".equals(bucketName)?"dxs-mmj-1257049906":bucketName;
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(length);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, input , metadata);
            cosClient.putObject(putObjectRequest);
            return domainName + key;
        }catch (Exception e){
            throw new Exception(e);
        }finally {
            cosClient.shutdown();
            if(input != null) {
                input.close();
            }
        }
    }

    /**
     * 删除腾讯云上的文件
     * @param bucketName 存储桶
     * @param key  路径 + 文件名
     * @return 访问路径
     * @throws Exception
     */
    public static String deleteFile(String bucketName,String key) throws Exception{
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        bucketName = bucketName==null || "".equals(bucketName)?"dxs-mmj-1257049906":bucketName;
        try {
            cosClient.deleteObject(bucketName, key);
            return "success";
        }catch (Exception e){
            throw new Exception(e);
        }finally {
            cosClient.shutdown();
        }
    }

    /**
     * 排序类型
     *  RANDOM 随机
     *  RULE 规则
     * 筛选规则
     *  SALE 按销量;   SALE_NUM
     *  WAREHOUSE 按库存   GOOD_NUM
     *  CREATER 按创建时间   CREATER_TIME
     *  MODIFY 按编辑时间    MODIFY_TIME
     *  THIRD 按三级分类 CLASS_ORDER1, CLASS_ORDER2, CLASS_ORDER3
     * 顺序
     *  ASC升序;
     *  DESC 倒序
     *  按三级分类时的值为规则拼接升降序
     *
     *
     */
    public static String getOrderSql(ActiveSort activeSort) {
        String result = " CREATER_TIME desc, GOOD_ID ";
        if (activeSort != null) {
            if (GoodConstants.ActiveOrderType.RULE.equals(activeSort.getOrderType())) {
                if (GoodConstants.ActiveOrderType.THIRD.equals(activeSort.getFilterRule())) {
                    result = " CLASS_ORDER1, CLASS_ORDER2, CLASS_ORDER3";
                    if (GoodConstants.ActiveOrderType.SALEDESC.equals(activeSort.getFilterRule())) {
                        result += ", SALE_NUM desc, GOOD_ID desc ";
                    } else if (GoodConstants.ActiveOrderType.WAREHOUSEDESC.equals(activeSort.getFilterRule())) {
                        result += ", GOOD_NUM desc, GOOD_ID desc ";
                    } else if (GoodConstants.ActiveOrderType.CREATERDESC.equals(activeSort.getFilterRule())) {
                        result += ", CREATER_TIME desc, GOOD_ID desc ";
                    } else if (GoodConstants.ActiveOrderType.MODIFYDESC.equals(activeSort.getFilterRule())) {
                        result += ", MODIFY_TIME desc, GOOD_ID desc ";
                    } else if (GoodConstants.ActiveOrderType.SALEASC.equals(activeSort.getFilterRule())) {
                        result += ", SALE_NUM asc, GOOD_ID asc ";
                    } else if (GoodConstants.ActiveOrderType.WAREHOUSEASC.equals(activeSort.getFilterRule())) {
                        result += ", GOOD_NUM asc, GOOD_ID asc ";
                    } else if (GoodConstants.ActiveOrderType.CREATERASC.equals(activeSort.getFilterRule())) {
                        result += ", CREATER_TIME asc, GOOD_ID asc ";
                    } else if (GoodConstants.ActiveOrderType.MODIFYASC.equals(activeSort.getFilterRule())) {
                        result += ", MODIFY_TIME asc, GOOD_ID asc ";
                    }
                } else {
                    if (GoodConstants.ActiveOrderType.SALE.equals(activeSort.getFilterRule())) {
                        result = " SALE_NUM ";
                    } else if (GoodConstants.ActiveOrderType.WAREHOUSE.equals(activeSort.getFilterRule())) {
                        result = " GOOD_NUM ";
                    } else if (GoodConstants.ActiveOrderType.CREATER.equals(activeSort.getFilterRule())) {
                        result = " CREATER_TIME ";
                    } else if (GoodConstants.ActiveOrderType.MODIFY.equals(activeSort.getFilterRule())) {
                        result = " MODIFY_TIME ";
                    }

                    if (GoodConstants.ActiveOrderType.ASC.equals(activeSort.getOrderBy())) {
                        result += " asc, GOOD_ID asc ";
                    } else if (GoodConstants.ActiveOrderType.DESC.equals(activeSort.getOrderBy())) {
                        result += " desc, GOOD_ID desc  ";
                    }
                }
            } else if (GoodConstants.ActiveOrderType.CUSTOM.equals(activeSort.getOrderType())) {
                result = " FIELD(GOOD_ID,%s) ";
            }
        }
        return result;
    }
}
