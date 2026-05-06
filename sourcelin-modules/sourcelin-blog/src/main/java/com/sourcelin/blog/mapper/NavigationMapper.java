package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Navigation;

import java.util.List;

public interface NavigationMapper
{
    Navigation selectNavigationById(Long id);

    List<Navigation> selectNavigationList(Navigation navigation);

    int insertNavigation(Navigation navigation);

    int updateNavigation(Navigation navigation);

    int deleteNavigationById(Long id);

    int deleteNavigationByIds(Long[] ids);

    int updateClickCount(Long id);
}
