package com.sourcelin.system.api.factory;

import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.api.model.SysLoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.system.api.service.RemoteSysUserService;

/**
 * 用户服务降级处理
 * 
 * @author sourcelin
 */
@Component
public class RemoteSysUserFallbackFactory implements FallbackFactory<RemoteSysUserService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteSysUserFallbackFactory.class);

    @Override
    public RemoteSysUserService create(Throwable throwable)
    {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteSysUserService()
        {
            @Override
            public R<SysLoginUser> getUserInfo(String username, String source)
            {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> registerUserInfo(SysUser sysUser, String source)
            {
                return R.fail("注册用户失败:" + throwable.getMessage());
            }
        };
    }
}
