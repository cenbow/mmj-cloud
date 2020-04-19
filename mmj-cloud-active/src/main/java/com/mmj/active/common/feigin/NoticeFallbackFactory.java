package com.mmj.active.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.constants.WxMedia;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class NoticeFallbackFactory implements FallbackFactory<NoticeFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(NoticeFallbackFactory.class);

    @Override
    public NoticeFeignClient create(Throwable cause) {
        logger.info("NoticeFallbackFactory error message is {}", cause.getMessage());
        return new NoticeFeignClient() {
            public ReturnData<String> createImage(@RequestBody String params) {
                throw new BusinessException("调用绘图接口报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<BaseDict> queryGlobalConfigByDictCode(
                    String dictCode) {
                throw new BusinessException("调用获取数据字典接口queryGlobalConfigByDictCode报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<WxMedia> wxMediaUpload(WxMedia wxMedia) {
                throw new BusinessException("上传微信素材发生错误" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<String> freeGoodsCompose(JSONObject params) {
                throw new BusinessException("合成免费送图片失败" + cause.getMessage(), 500);
            }
        };
    }


}
