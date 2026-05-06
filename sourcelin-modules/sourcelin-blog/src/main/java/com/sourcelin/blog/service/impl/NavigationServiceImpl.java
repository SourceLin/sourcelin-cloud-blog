package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.Navigation;
import com.sourcelin.blog.mapper.NavigationMapper;
import com.sourcelin.blog.service.INavigationService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NavigationServiceImpl implements INavigationService
{
    @Autowired
    private NavigationMapper navigationMapper;

    @Override
    public Navigation selectNavigationById(Long id)
    {
        return navigationMapper.selectNavigationById(id);
    }

    @Override
    public List<Navigation> selectNavigationList(Navigation navigation)
    {
        return navigationMapper.selectNavigationList(navigation);
    }

    @Override
    public int insertNavigation(Navigation navigation)
    {
        navigation.setCreateTime(DateUtils.getNowDate());
        navigation.setUpdateTime(DateUtils.getNowDate());
        return navigationMapper.insertNavigation(navigation);
    }

    @Override
    public int updateNavigation(Navigation navigation)
    {
        navigation.setUpdateTime(DateUtils.getNowDate());
        return navigationMapper.updateNavigation(navigation);
    }

    @Override
    public int deleteNavigationById(Long id)
    {
        return navigationMapper.deleteNavigationById(id);
    }

    @Override
    public int deleteNavigationByIds(Long[] ids)
    {
        return navigationMapper.deleteNavigationByIds(ids);
    }

    @Override
    public int updateClickCount(Long id)
    {
        return navigationMapper.updateClickCount(id);
    }
}
