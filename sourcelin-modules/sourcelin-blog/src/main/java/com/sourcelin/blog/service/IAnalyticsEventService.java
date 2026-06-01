package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.AnalyticsEvent;

public interface IAnalyticsEventService
{
    int insertAnalyticsEvent(AnalyticsEvent analyticsEvent);
}
