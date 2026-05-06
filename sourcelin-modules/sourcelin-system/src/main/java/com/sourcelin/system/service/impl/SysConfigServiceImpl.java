package com.sourcelin.system.service.impl;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;

import com.sourcelin.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.constant.UserConstants;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.text.Convert;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.redis.service.RedisService;
import com.sourcelin.system.domain.SysConfig;
import com.sourcelin.system.mapper.SysConfigMapper;

/**
 * 参数配置 服务层实现
 * 
 * @author sourcelin
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService
{
    private static final String RESERVED_ADMIN_UI_PREFIX = "sys.admin.ui.";

    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private RedisService redisService;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {
        loadingConfigCache();
    }

    /**
     * 查询参数配置信息
     * 
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId)
    {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        String configValue = Convert.toStr(redisService.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig))
        {
            redisService.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        return configMapper.selectConfigList(config);
    }

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        if (isReservedConfigKey(config.getConfigKey()))
        {
            throw new ServiceException("保留参数键不允许通过参数中心写入: " + config.getConfigKey());
        }
        int row = configMapper.insertConfig(config);
        if (row > 0)
        {
            redisService.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        SysConfig temp = configMapper.selectConfigById(config.getConfigId());
        if (StringUtils.isNull(temp))
        {
            throw new ServiceException("参数不存在: " + config.getConfigId());
        }
        if (isReservedConfigKey(temp.getConfigKey()) || isReservedConfigKey(config.getConfigKey()))
        {
            throw new ServiceException("保留参数键不允许通过参数中心修改: " + config.getConfigKey());
        }
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey()))
        {
            redisService.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        int row = configMapper.updateConfig(config);
        if (row > 0)
        {
            redisService.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     * 
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.isNull(config))
            {
                throw new ServiceException("参数不存在: " + configId);
            }
            if (isReservedConfigKey(config.getConfigKey()))
            {
                throw new ServiceException(String.format("保留参数【%1$s】不能删除", config.getConfigKey()));
            }
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteConfigById(configId);
            redisService.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache()
    {
        List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configsList)
        {
            redisService.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache()
    {
        Collection<String> keys = redisService.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisService.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache()
    {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }

    /**
     * 根据键名更新参数值
     * 
     * @param configKey 参数键名
     * @param configValue 参数值
     * @return 结果
     */
    @Override
    public int updateConfigByKey(String configKey, String configValue)
    {
        if (isReservedConfigKey(configKey))
        {
            throw new ServiceException("保留参数键不允许通过参数中心更新: " + configKey);
        }
        int row = configMapper.updateConfigByKey(configKey, configValue);
        if (row > 0)
        {
            redisService.setCacheObject(getCacheKey(configKey), configValue);
        }
        return row;
    }

    @Override
    public boolean isReservedConfigKey(String configKey)
    {
        return StringUtils.isNotEmpty(configKey) && configKey.startsWith(RESERVED_ADMIN_UI_PREFIX);
    }
}
