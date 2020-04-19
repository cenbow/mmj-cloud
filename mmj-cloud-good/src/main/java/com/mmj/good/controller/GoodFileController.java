package com.mmj.good.controller;


import com.alibaba.fastjson.JSON;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.EnvUtil;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.model.GoodFile;
import com.mmj.good.model.GoodFileEx;
import com.mmj.good.service.GoodFileService;
import com.mmj.good.util.FetchFrameUtil;
import com.mmj.good.util.GoodUtil;
import com.mmj.good.util.RedisCacheUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * 商品附件表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-03
 */
@Slf4j
@RestController
@RequestMapping("/goodFile")
public class GoodFileController extends BaseController {
    Logger logger = LoggerFactory.getLogger(GoodFileController.class);

    @Autowired
    private GoodFileService goodFileService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Value("${good.config.fileServer}")
    private String fileServer;

    /**
     * @return
     */
    @ApiOperation(value = "删除商品文件")
    @RequestMapping(value = "/delByFileIds", method = RequestMethod.POST)
    public ReturnData<String> delByFileIds(@RequestBody List<Integer> fileIds) {
        RedisCacheUtil.clearGoodFileCache(redisTemplate);
        goodFileService.deleteBatchIds(fileIds);
        return initSuccessObjectResult("success");
    }

    @ApiOperation(value = "文件信息保存")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody List<GoodFile> goodFiles){
        RedisCacheUtil.clearGoodFileCache(redisTemplate);
        goodFileService.insertOrUpdateBatch(goodFiles);
        return initSuccessResult();
    }

    @ApiOperation(value = "商品详情资料查询")
    @RequestMapping(value = "/queryInfo/{goodId}", method = RequestMethod.POST)
    public ReturnData<Object> queryInfo(@PathVariable Integer goodId) {
        String key = RedisCacheUtil.GOOD_DETAIL_QUERYINFO + goodId;
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_DETAIL, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseArray(String.valueOf(o)));
        }
        //查询
        List<String> fileTypes = new ArrayList<>();
        fileTypes.add(GoodConstants.FileType.DETAIL);
        fileTypes.add(GoodConstants.FileType.DETAILVIDEO);
        fileTypes.add(GoodConstants.FileType.DETAILTITLE);
        List<GoodFile> goodFiles = goodFileService.queryByGoodId(goodId, GoodConstants.ActiveType.SHOP_GOOD, fileTypes);
        String jsonString = JSON.toJSONString(goodFiles);
        redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_DETAIL, key, jsonString);
        return initSuccessObjectResult(JSON.parseArray(jsonString));
    }

    @ApiOperation(value = "商品文件查询")
    @RequestMapping(value = "/queryByGoodId", method = RequestMethod.POST)
    public ReturnData<Object> queryByGoodId(@RequestBody GoodFileEx goodFileEx) throws Exception {
        String key = RedisCacheUtil.GOOD_DETAIL_QUERYBYGOODID + RedisCacheUtil.getKey(goodFileEx);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_DETAIL, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseArray(String.valueOf(o)));
        }
        List<GoodFile> goodFiles = goodFileService.queryByGoodId(goodFileEx.getGoodId(), goodFileEx.getActiveType(), goodFileEx.getFileTypes());
        String jsonString = JSON.toJSONString(goodFiles);
        redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_DETAIL, key, jsonString);
        return initSuccessObjectResult(JSON.parseArray(jsonString));
    }

    private Random r = new Random();

    @ApiOperation(value = "文件上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ReturnData<List<Map<String, String>>> uploadImg(@RequestParam("file") MultipartFile[] file) throws Exception {
    	log.info("-->文件上传");
        String cloudFolder;
        List<Map<String, String>> list = new ArrayList<>();
        if (EnvUtil.isPro(profile)) {
            cloudFolder = "pre-product";
        } else {
            cloudFolder = "test";
        }
        if (file != null && file.length > 0) {
            Long time = System.currentTimeMillis(); //毫秒
            for (int i = 0; i < file.length; i++) {
                MultipartFile f = file[i];
                Map<String, String> result = new HashMap<>();
                String originalFilename = f.getOriginalFilename();
                String fileName = time + i + r.nextInt(100) + originalFilename.substring(originalFilename.lastIndexOf("."));
                result.put("originalFilename", new String(originalFilename.getBytes(),"UTF-8"));
                result.put("fileServer", fileServer);
                result.put("url", GoodUtil.uploadFile("dxs-mmj-1257049906", "/" + cloudFolder + "/" + fileName, f.getInputStream(), f.getSize()));
                list.add(result);
            }
        }
        return initSuccessObjectResult(list);
    }

    @ApiOperation(value = "推荐视频上传")
    @RequestMapping(value = "/uploadVideo", method = RequestMethod.POST)
    public ReturnData<List<Map<String, String>>> uploadVideo(@RequestParam("file") MultipartFile[] file) throws Exception {
        String cloudFolder;
        List<Map<String, String>> list = new ArrayList<>();
        if (EnvUtil.isPro(profile)) {
            cloudFolder = "pre-product";
        } else {
            cloudFolder = "test";
        }
        if (file != null && file.length > 0) {
            Long time = System.currentTimeMillis(); //毫秒
            for (int i = 0; i < file.length; i++) {
                MultipartFile f = file[i];
                Map<String, String> result = new HashMap<>();
                String originalFilename = f.getOriginalFilename();
                String videoFileName = time + i + r.nextInt(100) + originalFilename.substring(originalFilename.lastIndexOf("."));
                result.put("originalFilename", new String(originalFilename.getBytes(),"UTF-8"));
                result.put("fileServer", fileServer);
                result.put("videoUrl", GoodUtil.uploadFile("dxs-mmj-1257049906", "/" + cloudFolder + "/" + videoFileName, f.getInputStream(), f.getSize()));
                String imgFileName = time + i + r.nextInt(100) +".jpg";
                InputStream imgInputStream = FetchFrameUtil.fetchFrame(f.getInputStream());
                result.put("imgUrl", GoodUtil.uploadFile("dxs-mmj-1257049906", "/" + cloudFolder + "/" + imgFileName, imgInputStream, imgInputStream.available()));
                list.add(result);
            }
        }
        return initSuccessObjectResult(list);
    }


    /*@ApiOperation(value = "图片上传避免跨域设置option" )
    @RequestMapping(value="/upload",method = RequestMethod.OPTIONS)
    public ReturnData uploadImg(HttpServletRequest request, HttpServletResponse response) {
        // 指定允许其他域名访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 响应类型
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        // 响应头设置
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, X-Custom-Header");

        return initSuccessResult();

    }*/

}

