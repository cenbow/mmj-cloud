package com.mmj.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

@Component
public class HttpTools {

    private static Logger log = Logger.getLogger(HttpTools.class);

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 30000;

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        requestConfig = configBuilder.build();
    }


    public String getString(String path, String param) {
        try {
            StringBuilder sb = new StringBuilder(path);
            if(!StringUtils.isEmpty(param)){
                sb.append("?");
                sb.append(param);
            }
            URL url = new URL(sb.toString());
            log.info("请求地址:"+path+"==========入参:"+param);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            String result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            log.info("请求地址:"+path+"==========出参:"+ result);
            in.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 发送get请求返回json
     * @param path
     * @param map
     * @return
     */
    public JSONObject doGet(String path, Map<String, String> map) {
        JSONObject result = JSONObject.parseObject(getString(path, mapToStr(map)));
        return result;
    }

    /**
     * 发送get请求返回json
     * @param path
     * @param map
     * @return
     */
    public String doGetString(String path, Map<String, String> map) {
        return getString(path, mapToStr(map));
    }

    /**
     * map转化成string
     * @param map
     * @return
     */
    private String mapToStr(Map<String, String> map){
        StringBuilder params = new StringBuilder();
        Set<String> set = map.keySet();
        for (String key: set){
            params.append(key + "=" + map.get(key) + "&");
        }
        return params.toString();
    }

    public JSONObject doPost(String url, Map jsonParam) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost httpPost = new HttpPost(url);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000).setConnectTimeout(10000).build();
        httpPost.setConfig(requestConfig);
        try {
            if (null != jsonParam) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(JSONObject.toJSONString(jsonParam),
                        "utf-8");
                entity.setContentEncoding("utf-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            log.info("请求地址:"+url+"==========入参:"+ JSONObject.toJSONString(jsonParam));
            CloseableHttpResponse result = httpClient.execute(httpPost);
            //请求发送成功，并得到响应
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String str = "";
                try {
                    //读取服务器返回过来的json字符串数据
                    str = EntityUtils.toString(result.getEntity(), "utf-8");
                    //把json字符串转换成json对象
                    jsonResult = JSONObject.parseObject(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpPost.releaseConnection();
        }
        log.info("请求地址:"+url+"==========出参:"+jsonResult.toJSONString());
        return jsonResult;
    }

    /**
     * 发送post请求 返回流
     * @param path
     * @param jsonObject
     * @return
     */
    public InputStream doPostInputStream(String path, JSONObject jsonObject){
        try {
            String encoding = "UTF-8";
            String params = jsonObject.toJSONString();
            byte[] data = params.getBytes(encoding);
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            log.info("请求地址:"+path+"==========入参:"+params);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=" + encoding);
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            conn.setConnectTimeout(5 * 1000);
            OutputStream outStream = conn.getOutputStream();
            outStream.write(data);
            outStream.flush();
            outStream.close();
            if (conn.getResponseCode() == 200) {
                InputStream inStream = conn.getInputStream();
                return inStream;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public JSONObject postFile(String url, String filePath, String type) {
        log.info(url+"入参:========:filePath"+ filePath + "====type"+type);
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslContext;
        String result = null;
        try {
            sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url1 = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) url1.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            // 设置请求头信息
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            // 设置边界
            String boundary = "-----------------------------" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            StringBuffer sbuff = new StringBuffer();
            sbuff.append("--").append(boundary).append("\r\n");
            sbuff.append("Content-Disposition: form-data;name=\"type\" \r\n\r\n");
            sbuff.append(type);
            sbuff.append("\r\n--").append(boundary).append("\r\n");
            //在上传视频素材时需要POST另一个表单，id为description
            if ("video/mp4".equals(type)) {
                sbuff.append("Content-Disposition: form-data;name=\"description\"\r\n\r\n");
                sbuff.append("{\"title\":\"hello title\", \"introduction\":\"hello introduction\"}");
                sbuff.append("\r\n--").append(boundary).append("\r\n");
            }
            sbuff.append("Content-Disposition: form-data;name=\"media\";filename=\"" + StringUtils.getUUid()+".jpg" + "\" \r\n");
            sbuff.append("Content-Type:application/octet-stream\r\n\r\n");

            System.out.println(sbuff.toString());

            byte[] head = sbuff.toString().getBytes("utf-8");
            // 获得输出流
            OutputStream output = new DataOutputStream(conn.getOutputStream());
// 输出表头
            output.write(head);

// 文件正文部分
            // 把文件已流文件的方式 推入到url中
            byte[] data = new byte[1024];
            int len = 0;

            DataInputStream input = new DataInputStream(getImageStream(filePath));
            while ((len = input.read(data)) > -1) {
                output.write(data, 0, len);
            }

            input.close();
// 结尾部分
            byte[] foot = ("\r\n--" + boundary + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
            output.write(foot);
            output.flush();
            output.close();

            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();
            log.info(url+"出参:========"+result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("postFile数据传输失败" +url+"入参:========:filePath"+ filePath + "====type"+type);
        }

        return JSONObject.parseObject(result);
    }


    public  InputStream getImageStream(String url) {
        try {
            if(!url.startsWith("http")){
                url = "https://" + url;
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                return inputStream;
            }
        } catch (IOException e) {
            System.out.println("获取网络图片出现异常，图片路径为：" + url);
            e.printStackTrace();
        }
        return null;
    }
}
