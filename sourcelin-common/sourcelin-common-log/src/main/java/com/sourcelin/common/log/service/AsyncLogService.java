package com.sourcelin.common.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.system.api.service.RemoteSysLogService;
import com.sourcelin.system.api.domain.SysOperLog;

/**
 * 异步调用日志服务
 * 
 * @author sourcelin
 */
@Service
public class AsyncLogService
{
    @Autowired
    private RemoteSysLogService remoteSysLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOperLog sysOperLog) throws Exception
    {
        remoteSysLogService.saveLog(sysOperLog, SecurityConstants.INNER);
    }
}
