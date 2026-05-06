package com.sourcelin.system.api.factory;

import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.system.api.domain.SysDictData;
import com.sourcelin.system.api.service.RemoteSysDictDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 字典数据（前台聚合）降级
 */
@Component
public class RemoteSysDictDataFallbackFactory implements FallbackFactory<RemoteSysDictDataService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteSysDictDataFallbackFactory.class);

    @Override
    public RemoteSysDictDataService create(Throwable throwable)
    {
        log.error("system dict data service call failed: {}", throwable.getMessage());
        return new RemoteSysDictDataService()
        {
            @Override
            public ApiResponse<List<SysDictData>> listByType(String dictType, String source)
            {
                return ApiResponse.fail(
                    com.sourcelin.common.core.enums.ResultCode.SYSTEM_ERROR,
                    "system 字典数据获取失败: " + throwable.getMessage(),
                    Collections.emptyList()
                );
            }
        };
    }
}

