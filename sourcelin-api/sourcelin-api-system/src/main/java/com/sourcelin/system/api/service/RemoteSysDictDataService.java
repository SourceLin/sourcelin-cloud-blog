package com.sourcelin.system.api.service;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.system.api.domain.SysDictData;
import com.sourcelin.system.api.factory.RemoteSysDictDataFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 系统字典数据（前台聚合内部调用）
 */
@FeignClient(
    contextId = "remoteSysDictDataService",
    value = ServiceNameConstants.SYSTEM_SERVICE,
    fallbackFactory = RemoteSysDictDataFallbackFactory.class
)
public interface RemoteSysDictDataService
{
    /**
     * 根据字典类型查询字典数据（仅前台可读聚合）
     */
    @GetMapping("/dict/data/type/{dictType}")
    ApiResponse<List<SysDictData>> listByType(
        @PathVariable("dictType") String dictType,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );
}

