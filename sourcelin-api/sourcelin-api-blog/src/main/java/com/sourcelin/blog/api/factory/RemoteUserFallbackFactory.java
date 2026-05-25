package com.sourcelin.blog.api.factory;


import com.sourcelin.blog.api.service.RemoteUserService;
import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * 用户服务降级处理
 *
 * @author sourcelin
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService() {
            @Override
            public R<LoginUser> getUserInfo(String param, String source) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Long> registerUserInfo(User user, String source) {
                return R.fail("注册用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Integer> updateUserInfo(User user, String source) {
                return R.fail("更新用户失败:" + throwable.getMessage());
            }

            @Override
            public R<LoginUser> getUserInfoByEmail(String email, String source) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<LoginUser> getUserInfoByOpenId(String openId, String source) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

        };
    }
}
