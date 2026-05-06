package com.sourcelin.system.api.service;

import com.sourcelin.system.api.domain.SysLogininfor;
import com.sourcelin.system.api.domain.SysOperLog;
import com.sourcelin.system.api.factory.RemoteSysLogFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.domain.R;

/**
 * 日志服务
 * 
 * @author sourcelin
 */
@FeignClient(contextId = "remoteSysLogService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSysLogFallbackFactory.class)
public interface RemoteSysLogService
{
    /**
     * 保存系统日志
     *
     * @param sysOperLog 日志实体
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/operlog")
    public R<Boolean> saveLog(@RequestBody SysOperLog sysOperLog, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) throws Exception;

    /**
     * 保存访问记录
     *
     * @param sysLogininfor 访问实体
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/logininfor")
    public R<Boolean> saveLogininfor(@RequestBody SysLogininfor sysLogininfor, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
