package com.sourcelin.auth.service;

import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.sourcelin.auth.config.properties.CaptchaProperties;
import com.sourcelin.auth.vo.CaptchaVo;
import com.sourcelin.common.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

@Service
public class ShearCaptchaService {

    private static final String LOGIN_TYPE_BLOG = "blog";
    private static final String LOGIN_TYPE_ADMIN = "admin";

    private static final String CAPTCHA_PREFIX = "captcha:shear:";
    private static final int WIDTH = 150;
    private static final int HEIGHT = 40;
    private static final int CODE_COUNT = 4;
    private static final int THICKNESS = 2;
    private static final long EXPIRE_MINUTES = 5;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CaptchaProperties captchaProperties;

    public CaptchaVo createCaptcha() {
        return createCaptcha(LOGIN_TYPE_BLOG);
    }

    public CaptchaVo createCaptcha(String loginType) {
        if (!isCaptchaRequired(loginType)) {
            return CaptchaVo.builder()
                .captchaEnabled(false)
                .build();
        }

        ShearCaptcha captcha = new ShearCaptcha(WIDTH, HEIGHT, CODE_COUNT, THICKNESS);
        String code = captcha.getCode();
        String uuid = IdUtil.simpleUUID();

        String redisKey = CAPTCHA_PREFIX + uuid;
        stringRedisTemplate.opsForValue().set(redisKey, code, EXPIRE_MINUTES, TimeUnit.MINUTES);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        captcha.write(os);
        String imgBase64 = Base64.encode(os.toByteArray());

        return CaptchaVo.builder()
            .captchaEnabled(true)
            .uuid(uuid)
            .img(imgBase64)
            .build();
    }

    /**
     * 校验并消费验证码（删除 Redis key），用于登录/注册等终态操作
     */
    public void verifyCaptcha(String captchaCode, String captchaUuid) {
        verifyCaptcha(captchaCode, captchaUuid, LOGIN_TYPE_BLOG);
    }

    public void verifyCaptcha(String captchaCode, String captchaUuid, String loginType) {
        if (!isCaptchaRequired(loginType)) {
            return;
        }
        verifyCaptchaInternal(captchaCode, captchaUuid, true);
    }

    /**
     * 只校验不消费验证码（保留 Redis key），用于发送邮箱验证码等中间操作
     */
    public void verifyCaptchaWithoutConsume(String captchaCode, String captchaUuid) {
        verifyCaptchaWithoutConsume(captchaCode, captchaUuid, LOGIN_TYPE_BLOG);
    }

    public void verifyCaptchaWithoutConsume(String captchaCode, String captchaUuid, String loginType) {
        if (!isCaptchaRequired(loginType)) {
            return;
        }
        verifyCaptchaInternal(captchaCode, captchaUuid, false);
    }

    private void verifyCaptchaInternal(String captchaCode, String captchaUuid, boolean consumeAfterVerify) {
        if (captchaCode != null) {
            captchaCode = captchaCode.trim();
        }
        if (captchaUuid != null) {
            captchaUuid = captchaUuid.trim();
        }
        validateParams(captchaCode, captchaUuid);

        String redisKey = CAPTCHA_PREFIX + captchaUuid;
        String cachedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (consumeAfterVerify) {
            stringRedisTemplate.delete(redisKey);
        }

        if (cachedCode == null) {
            throw new ServiceException("验证码已失效");
        }
        if (!cachedCode.equalsIgnoreCase(captchaCode)) {
            throw new ServiceException("验证码错误");
        }
    }

    private boolean isCaptchaRequired(String loginType) {
        if (LOGIN_TYPE_ADMIN.equals(loginType)) {
            return Boolean.TRUE.equals(captchaProperties.getAdminEnabled());
        }
        return true;
    }

    private void validateParams(String captchaCode, String captchaUuid) {
        if (captchaCode == null || captchaCode.isEmpty()) {
            throw new ServiceException("图形验证码不能为空");
        }
        if (captchaUuid == null || captchaUuid.isEmpty()) {
            throw new ServiceException("验证码已失效");
        }
    }
}
