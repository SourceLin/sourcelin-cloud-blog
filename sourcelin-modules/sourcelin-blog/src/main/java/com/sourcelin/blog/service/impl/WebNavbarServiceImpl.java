package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.WebNavbar;
import com.sourcelin.blog.mapper.WebNavbarMapper;
import com.sourcelin.blog.service.IWebNavbarService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门户导航栏Service业务层处理
 * 
 * @author sourcelin
 * @date 2023-11-06
 */
@Service
public class WebNavbarServiceImpl implements IWebNavbarService 
{
    @Autowired
    private WebNavbarMapper webNavbarMapper;

    /**
     * 查询门户导航栏
     * 
     * @param id 门户导航栏主键
     * @return 门户导航栏
     */
    @Override
    public WebNavbar selectWebNavbarById(Long id)
    {
        return webNavbarMapper.selectWebNavbarById(id);
    }

    /**
     * 查询门户导航栏列表
     * 
     * @param webNavbar 门户导航栏
     * @return 门户导航栏
     */
    @Override
    public List<WebNavbar> selectWebNavbarList(WebNavbar webNavbar)
    {
        return webNavbarMapper.selectWebNavbarList(webNavbar);
    }

    /**
     * 新增门户导航栏
     * 
     * @param webNavbar 门户导航栏
     * @return 结果
     */
    @Override
    public int insertWebNavbar(WebNavbar webNavbar)
    {
        webNavbar.setCreateTime(DateUtils.getNowDate());
        return webNavbarMapper.insertWebNavbar(webNavbar);
    }

    /**
     * 修改门户导航栏
     * 
     * @param webNavbar 门户导航栏
     * @return 结果
     */
    @Override
    public int updateWebNavbar(WebNavbar webNavbar)
    {
        webNavbar.setUpdateTime(DateUtils.getNowDate());
        return webNavbarMapper.updateWebNavbar(webNavbar);
    }

    /**
     * 批量删除门户导航栏
     * 
     * @param ids 需要删除的门户导航栏主键
     * @return 结果
     */
    @Override
    public int deleteWebNavbarByIds(Long[] ids)
    {
        return webNavbarMapper.deleteWebNavbarByIds(ids);
    }

    /**
     * 删除门户导航栏信息
     * 
     * @param id 门户导航栏主键
     * @return 结果
     */
    @Override
    public int deleteWebNavbarById(Long id)
    {
        return webNavbarMapper.deleteWebNavbarById(id);
    }
}
