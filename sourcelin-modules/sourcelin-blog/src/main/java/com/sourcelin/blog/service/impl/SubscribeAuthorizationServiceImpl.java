package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.SubscribeAuthorization;
import com.sourcelin.blog.mapper.SubscribeAuthorizationMapper;
import com.sourcelin.blog.service.ISubscribeAuthorizationService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscribeAuthorizationServiceImpl implements ISubscribeAuthorizationService
{
    @Autowired
    private SubscribeAuthorizationMapper subscribeAuthorizationMapper;

    @Override
    public int insertSubscribeAuthorization(SubscribeAuthorization subscribeAuthorization)
    {
        subscribeAuthorization.setCreateTime(DateUtils.getNowDate());
        subscribeAuthorization.setUpdateTime(DateUtils.getNowDate());
        return subscribeAuthorizationMapper.insertSubscribeAuthorization(subscribeAuthorization);
    }
}
