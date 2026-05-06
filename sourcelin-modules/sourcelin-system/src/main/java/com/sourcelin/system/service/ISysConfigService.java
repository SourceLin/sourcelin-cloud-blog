package com.sourcelin.system.service;

import java.util.List;
import com.sourcelin.system.domain.SysConfig;

/**
 * 参数配置 服务层
 * 
 * @author sourcelin
 */
public interface ISysConfigService
{
    /**
     * 查询参数配置信息
     * 
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数键名
     * @return 参数键值
     */
    public String selectConfigByKey(String configKey);

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int insertConfig(SysConfig config);

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int updateConfig(SysConfig config);

    /**
     * 批量删除参数信息
     * 
     * @param configIds 需要删除的参数ID
     */
    public void deleteConfigByIds(Long[] configIds);

    /**
     * 加载参数缓存数据
     */
    public void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    public void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    public void resetConfigCache();

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数信息
     * @return 结果
     */
    public boolean checkConfigKeyUnique(SysConfig config);

    /**
     * 根据键名更新参数值
     * 
     * @param configKey 参数键名
     * @param configValue 参数值
     * @return 结果
     */
    public int updateConfigByKey(String configKey, String configValue);

    /**
     * 是否为保留参数键（仅允许领域专用入口维护）
     *
     * @param configKey 参数键名
     * @return true 保留键；false 非保留键
     */
    public boolean isReservedConfigKey(String configKey);
}
