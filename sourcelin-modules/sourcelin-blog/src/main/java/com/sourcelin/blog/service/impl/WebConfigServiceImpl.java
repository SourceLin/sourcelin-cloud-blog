package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.WebConfig;
import com.sourcelin.blog.mapper.WebConfigMapper;
import com.sourcelin.blog.service.IWebConfigService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 网站配置Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-01-04
 */
@Service
public class WebConfigServiceImpl implements IWebConfigService 
{
    @Autowired
    private WebConfigMapper webConfigMapper;

    /**
     * 查询网站配置
     * 
     * @param id 网站配置主键
     * @return 网站配置
     */
    @Override
    public WebConfig selectWebConfigById(Long id)
    {
        return webConfigMapper.selectWebConfigById(id);
    }

    /**
     * 查询网站配置列表
     * 
     * @param webConfig 网站配置
     * @return 网站配置
     */
    @Override
    public List<WebConfig> selectWebConfigList(WebConfig webConfig)
    {
        return webConfigMapper.selectWebConfigList(webConfig);
    }

    /**
     * 新增网站配置
     * 
     * @param webConfig 网站配置
     * @return 结果
     */
    @Override
    public int insertWebConfig(WebConfig webConfig)
    {
        webConfig.setCreateTime(DateUtils.getNowDate());
        return webConfigMapper.insertWebConfig(webConfig);
    }

    /**
     * 修改网站配置
     * 
     * @param webConfig 网站配置
     * @return 结果
     */
    @Override
    public int updateWebConfig(WebConfig webConfig)
    {
        webConfig.setUpdateTime(DateUtils.getNowDate());
        return webConfigMapper.updateWebConfig(webConfig);
    }

    /**
     * 批量删除网站配置
     * 
     * @param ids 需要删除的网站配置主键
     * @return 结果
     */
    @Override
    public int deleteWebConfigByIds(Long[] ids)
    {
        return webConfigMapper.deleteWebConfigByIds(ids);
    }

    /**
     * 删除网站配置信息
     * 
     * @param id 网站配置主键
     * @return 结果
     */
    @Override
    public int deleteWebConfigById(Long id)
    {
        return webConfigMapper.deleteWebConfigById(id);
    }

    @Override
    public WebConfig getWebConfig() {
       List<WebConfig> webConfigs = this.webConfigMapper.selectWebConfigList(new WebConfig());
        return webConfigs.stream().findFirst().orElse(null);
    }
}
