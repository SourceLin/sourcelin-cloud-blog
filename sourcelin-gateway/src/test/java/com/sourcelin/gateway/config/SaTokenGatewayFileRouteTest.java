package com.sourcelin.gateway.config;

import com.sourcelin.gateway.config.properties.IgnoreWhiteProperties;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaTokenGatewayFileRouteTest
{
    @Test
    void shouldTreatPublicFilePreviewRoutesAsWhiteAndKeepMutatingRoutesProtected() throws Exception
    {
        IgnoreWhiteProperties ignoreWhiteProperties = new IgnoreWhiteProperties();
        ignoreWhiteProperties.setWhites(Arrays.asList("/auth/login", "/blog/front/articles"));

        SaTokenGatewayConfiguration configuration = new SaTokenGatewayConfiguration(ignoreWhiteProperties);

        Method isWhitePath = SaTokenGatewayConfiguration.class.getDeclaredMethod("isWhitePath", String.class);
        Method isAdminRoute = SaTokenGatewayConfiguration.class.getDeclaredMethod("isAdminRoute", String.class, String.class);
        Method isPublicFileRoute = SaTokenGatewayConfiguration.class.getDeclaredMethod("isPublicFileRoute", String.class);
        Method isAdminOrBlogFileRoute = SaTokenGatewayConfiguration.class.getDeclaredMethod("isAdminOrBlogFileRoute", String.class, String.class);
        isWhitePath.setAccessible(true);
        isAdminRoute.setAccessible(true);
        isPublicFileRoute.setAccessible(true);
        isAdminOrBlogFileRoute.setAccessible(true);

        assertEquals(Boolean.FALSE, isWhitePath.invoke(configuration, "/file/statics/2026/04/demo.png"));
        assertEquals(Boolean.FALSE, isWhitePath.invoke(configuration, "/file/download/43"));
        assertEquals(Boolean.TRUE, isPublicFileRoute.invoke(configuration, "/file/statics/2026/04/demo.png"));
        assertEquals(Boolean.TRUE, isPublicFileRoute.invoke(configuration, "/file/download/43"));
        assertEquals(Boolean.FALSE, isWhitePath.invoke(configuration, "/file/upload"));
        assertEquals(Boolean.FALSE, isAdminRoute.invoke(configuration, "/file/download/43", "GET"));
        assertEquals(Boolean.FALSE, isAdminRoute.invoke(configuration, "/file/43", "GET"));
        assertEquals(Boolean.FALSE, isAdminRoute.invoke(configuration, "/file/43", "DELETE"));
        assertEquals(Boolean.TRUE, isAdminRoute.invoke(configuration, "/file/43", "PUT"));
        assertEquals(Boolean.FALSE, isAdminRoute.invoke(configuration, "/file/upload", "POST"));
        assertEquals(Boolean.TRUE, isAdminOrBlogFileRoute.invoke(configuration, "/file/upload", "POST"));
        assertEquals(Boolean.TRUE, isAdminOrBlogFileRoute.invoke(configuration, "/file/confirm/43", "POST"));
        assertEquals(Boolean.TRUE, isAdminOrBlogFileRoute.invoke(configuration, "/file/43", "GET"));
        assertEquals(Boolean.TRUE, isAdminOrBlogFileRoute.invoke(configuration, "/file/43", "DELETE"));
        assertEquals(Boolean.FALSE, isAdminOrBlogFileRoute.invoke(configuration, "/file/43", "PUT"));
    }
}
