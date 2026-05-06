package com.sourcelin.system.api.factory;

import com.sourcelin.system.api.service.RemoteSysLogService;
import com.sourcelin.system.api.domain.SysLogininfor;
import com.sourcelin.system.api.domain.SysOperLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.sourcelin.common.core.domain.R;

/**
 * 日志服务降级处理
 * 
 * @author sourcelin
 */
@Component
public class RemoteSysLogFallbackFactory implements FallbackFactory<RemoteSysLogService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteSysLogFallbackFactory.class);

    @Override
    public RemoteSysLogService create(Throwable throwable)
    {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteSysLogService()
        {
            @Override
            public R<Boolean> saveLog(SysOperLog sysOperLog, String source)
            {
                return R.fail("保存操作日志失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> saveLogininfor(SysLogininfor sysLogininfor, String source)
            {
                return R.fail("保存登录日志失败:" + throwable.getMessage());
            }
        };

    }
}
