package com.sourcelin.blog.mapper;

import java.util.List;
import com.sourcelin.blog.domain.WebConfig;

/**
 * 网站配置Mapper接口
 * 
 * @author sourcelin
 * @date 2026-01-04
 */
public interface WebConfigMapper 
{
    /**
     * 查询网站配置
     * 
     * @param id 网站配置主键
     * @return 网站配置
     */
    public WebConfig selectWebConfigById(Long id);

    /**
     * 查询网站配置列表
     * 
     * @param webConfig 网站配置
     * @return 网站配置集合
     */
    public List<WebConfig> selectWebConfigList(WebConfig webConfig);

    /**
     * 新增网站配置
     * 
     * @param webConfig 网站配置
     * @return 结果
     */
    public int insertWebConfig(WebConfig webConfig);

    /**
     * 修改网站配置
     * 
     * @param webConfig 网站配置
     * @return 结果
     */
    public int updateWebConfig(WebConfig webConfig);

    /**
     * 删除网站配置
     * 
     * @param id 网站配置主键
     * @return 结果
     */
    public int deleteWebConfigById(Long id);

    /**
     * 批量删除网站配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWebConfigByIds(Long[] ids);
}
