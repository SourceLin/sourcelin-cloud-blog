package com.sourcelin.system.api.service;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.system.api.domain.SysNotice;
import com.sourcelin.system.api.factory.RemoteNoticeFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 系统公告（内部调用）
 */
@FeignClient(contextId = "remoteNoticeService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteNoticeFallbackFactory.class)
public interface RemoteNoticeService
{
    /**
     * 已发布公告列表（status=0）
     */
    @GetMapping("/notice/inner/published")
    R<List<SysNotice>> listPublished(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 已发布公告详情
     */
    @GetMapping("/notice/inner/{noticeId}")
    R<SysNotice> getPublished(@PathVariable("noticeId") Long noticeId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
