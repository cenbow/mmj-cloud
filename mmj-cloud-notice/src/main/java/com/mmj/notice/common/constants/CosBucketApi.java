package com.mmj.notice.common.constants;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;

import java.io.File;
import java.io.InputStream;

public class CosBucketApi {
    // 1 初始化用户身份信息(secretId, secretKey)
    static COSCredentials cred = new BasicCOSCredentials("AKID2AFmBOYRO9kAO8hI9EDTgtv3458LfLRX", "Pi1M5e1536xeUTRMDA2CD2XvCKISTCUs");
    // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    static ClientConfig clientConfig = new ClientConfig(new Region("ap-guangzhou"));
    // 3 生成cos客户端
    static COSClient cosClient = new COSClient(cred, clientConfig);
    //域名
    public static String domainName = "cdn.polynome.cn";

    /**
     * 将文件上传到腾讯云指定目录
     * @param filePath 文件本地路径
     * @param bucketName 存储桶
     * @param key  路径 + 文件名
     * @return 访问路径
     * @throws Exception
     */
    public static String uploadFile(String filePath, String bucketName,String key) throws Exception{
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        bucketName = bucketName==null || "".equals(bucketName)?"dxs-mmj-1257049906":bucketName;
        try {
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            // 大文件上传请参照 API 文档高级 API 上传
            File localFile = new File(filePath);
            // 指定要上传到 COS 上对象键
            // 对象键（Key）是对象在存储桶中的唯一标识
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            //Date expiration = new Date(new Date().getTime() + 5 * 60 * 10000);
            //URL url = cosClient.generatePresignedUrl(bucketName, key, expiration);
            return domainName + key;
        }catch (Exception e){
            throw new Exception(e);
        }finally {
            cosClient.shutdown();
        }
    }

    /**
     * @Description: 将文件上传到腾讯云（通过流的方式）
     * @author: KK
     * @date: 2018/9/18
     * @param: [bucketName, key, input, length]
     * @return: java.lang.String
     */
    public static String uploadFile(String bucketName, String key, InputStream input,long length) throws Exception{
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        bucketName = bucketName==null || "".equals(bucketName)?"dxs-mmj-1257049906":bucketName;
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            if(0 !=length){
                metadata.setContentLength(length);
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, input , metadata);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            return domainName + key;
        }catch (Exception e){
            throw new Exception(e);
        }finally {
            cosClient.shutdown();
            input.close();
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
}
