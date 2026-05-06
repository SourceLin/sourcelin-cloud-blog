package com.sourcelin.common.security.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaTokenConfigTest
{
    @Test
    void shouldUseAuthorizationBearerHeaderWithoutCookie()
    {
        cn.dev33.satoken.config.SaTokenConfig config = new SaTokenConfig().saTokenConfig();

        assertEquals("Authorization", config.getTokenName());
        assertEquals("Bearer", config.getTokenPrefix());
        assertTrue(config.getIsReadHeader());
        assertFalse(config.getIsReadCookie());
    }
}
