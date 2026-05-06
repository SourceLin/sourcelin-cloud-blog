package com.sourcelin.system.api.factory;

import com.sourcelin.common.core.domain.R;
import com.sourcelin.system.api.domain.SysNotice;
import com.sourcelin.system.api.service.RemoteNoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 公告服务降级
 */
@Component
public class RemoteNoticeFallbackFactory implements FallbackFactory<RemoteNoticeService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteNoticeFallbackFactory.class);

    @Override
    public RemoteNoticeService create(Throwable throwable)
    {
        log.error("公告服务调用失败:{}", throwable.getMessage());
        return new RemoteNoticeService()
        {
            @Override
            public R<List<SysNotice>> listPublished(String source)
            {
                return R.fail("获取公告列表失败:" + throwable.getMessage());
            }

            @Override
            public R<SysNotice> getPublished(Long noticeId, String source)
            {
                return R.fail("获取公告详情失败:" + throwable.getMessage());
            }
        };
    }
}
