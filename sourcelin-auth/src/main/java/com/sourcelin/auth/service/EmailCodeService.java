package com.sourcelin.auth.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.sourcelin.auth.config.properties.EmailProperties;
import com.sourcelin.common.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EmailCodeService {

    private static final Logger logger = LoggerFactory.getLogger(EmailCodeService.class);

    private static final String EMAIL_CODE_PREFIX = "email:code:";
    private static final String EMAIL_CODE_INTERVAL_PREFIX = "email:code:interval:";
    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRE_TIME = 5;
    private static final long INTERVAL_TIME = 60;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmailProperties emailProperties;

    private MailAccount getMailAccount() {
        MailAccount account = new MailAccount();
        account.setHost(emailProperties.getHost());
        account.setPort(emailProperties.getPort());
        account.setUser(emailProperties.getUsername());
        account.setPass(emailProperties.getPassword());
        account.setFrom(emailProperties.getFrom());
        // 邮箱必须开启认证
        account.setAuth(true);
        // SSL 开关
        if (emailProperties.getUseSsl() != null && emailProperties.getUseSsl()) {
            account.setSslEnable(true);
        }

        return account;
    }

    public void sendEmailCode(String email) {
        if (email == null || email.isEmpty()) {
            throw new ServiceException("邮箱地址不能为空");
        }

        if (!ReUtil.isMatch(EMAIL_REGEX, email)) {
            throw new ServiceException("邮箱格式不正确");
        }

        // 发送频率限制
        String intervalKey = EMAIL_CODE_INTERVAL_PREFIX + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(intervalKey))) {
            throw new ServiceException("发送频繁，请" + INTERVAL_TIME + "秒后重试");
        }

        // 生成验证码
        String code = RandomUtil.randomNumbers(CODE_LENGTH);

        try {
            MailUtil.send(
                    getMailAccount(),
                    email,
                    "圆圈博客验证码",
                    "您的验证码是：" + code + "，5分钟内有效，请勿泄露给他人。",
                    false
            );
        } catch (Exception e) {
            logger.error("发送邮件失败: {}", e.getMessage(), e);
            throw new ServiceException("邮件发送失败，请检查邮箱配置");
        }

        // 保存验证码 + 记录发送间隔
        String codeKey = EMAIL_CODE_PREFIX + email;
        stringRedisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(intervalKey, "1", INTERVAL_TIME, TimeUnit.SECONDS);

        logger.info("邮箱验证码发送成功：{}", email);
    }

    public boolean verifyCode(String email, String code) {
        if (email == null || code == null) {
            return false;
        }

        String codeKey = EMAIL_CODE_PREFIX + email;
        String cachedCode = stringRedisTemplate.opsForValue().get(codeKey);

        if (cachedCode == null) {
            throw new ServiceException("验证码已过期，请重新获取");
        }

        boolean success = cachedCode.equals(code);
        if (!success) {
            return false;
        }

        // 验证成功后删除
        stringRedisTemplate.delete(codeKey);
        return true;
    }
}
