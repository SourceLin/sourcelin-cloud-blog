package com.sourcelin.ai.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimitConfigTest {

    @Test
    void shouldApplyRateLimitToActualAiServicePathsAfterGatewayStripPrefix() {
        RateLimitConfig config = new RateLimitConfig();
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class, RETURNS_SELF);

        when(registry.addInterceptor(any(RateLimitInterceptor.class))).thenReturn(registration);

        config.addInterceptors(registry);

        verify(registration).addPathPatterns(
                "/abilities/**",
                "/tasks/**",
                "/ability-configs/**",
                "/call-logs/**",
                "/admin/tasks/**");
        verify(registration).excludePathPatterns("/actuator/**");
    }
}
