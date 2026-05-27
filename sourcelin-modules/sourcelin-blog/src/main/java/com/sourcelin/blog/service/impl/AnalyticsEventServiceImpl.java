package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.AnalyticsEvent;
import com.sourcelin.blog.mapper.AnalyticsEventMapper;
import com.sourcelin.blog.service.IAnalyticsEventService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsEventServiceImpl implements IAnalyticsEventService
{
    @Autowired
    private AnalyticsEventMapper analyticsEventMapper;

    @Override
    public int insertAnalyticsEvent(AnalyticsEvent analyticsEvent)
    {
        analyticsEvent.setCreateTime(DateUtils.getNowDate());
        analyticsEvent.setUpdateTime(DateUtils.getNowDate());
        return analyticsEventMapper.insertAnalyticsEvent(analyticsEvent);
    }
}
