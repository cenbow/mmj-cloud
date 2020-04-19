package com.mmj.job.handler;

import org.springframework.stereotype.Component;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;

@JobHandler(value="sendMsgJob")
@Component
@Slf4j
public class SendMsgExecuteHandler extends IJobHandler{
    
    
    @Override
    public ReturnT<String> execute(String str) throws Exception {
        log.info("SendMsgExecuteHandler {}", str);
        
        
        return SUCCESS;
    }

}
