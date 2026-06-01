package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.AnalyticsEvent;
import com.sourcelin.blog.dto.FrontAnalyticsEventDTO;
import com.sourcelin.blog.service.IAnalyticsEventService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.ServletUtils;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/front/analytics")
public class FrontAnalyticsController extends BaseController
{
    @Autowired
    private IAnalyticsEventService analyticsEventService;

    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @PostMapping("/events")
    public Void createEvent(@Valid @RequestBody FrontAnalyticsEventDTO body)
    {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setUserId(blogLoginAccessor.getCurrentUserId());
        event.setEventType(body.getEventType().trim());
        event.setPagePath(StringUtils.substring(StringUtils.trim(body.getPagePath()), 0, 128));
        event.setTargetType(StringUtils.substring(StringUtils.trim(body.getTargetType()), 0, 32));
        event.setTargetId(body.getTargetId());
        event.setMetadataJson(StringUtils.substring(StringUtils.trim(body.getMetadataJson()), 0, 4000));
        event.setPlatform(StringUtils.substring(StringUtils.trim(body.getPlatform()), 0, 32));
        event.setAppVersion(StringUtils.substring(StringUtils.trim(body.getAppVersion()), 0, 64));
        event.setClientIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        event.setUserAgent(StringUtils.substring(ServletUtils.getRequest().getHeader("User-Agent"), 0, 255));
        if (analyticsEventService.insertAnalyticsEvent(event) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "埋点记录失败");
        }
        return null;
    }
}
