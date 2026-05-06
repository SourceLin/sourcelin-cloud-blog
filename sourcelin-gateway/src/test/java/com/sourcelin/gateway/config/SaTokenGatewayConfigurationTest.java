package com.sourcelin.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SaTokenGatewayConfigurationTest {

    @Test
    void shouldDeclareSaReactorFilterBean() {
        Method beanMethod = findMethod("saReactorFilter");

        assertNotNull(beanMethod, "Gateway must declare a SaReactorFilter bean");
        assertTrue(beanMethod.isAnnotationPresent(Bean.class), "SaReactorFilter must be registered as a Spring bean");
    }

    @Test
    void shouldRecognizeBlogAndWhiteRoutes() throws Exception {
        SaTokenGatewayConfiguration configuration = newConfigurationInstance();

        Method isBlogRoute = findMethod("isBlogRoute", String.class);
        Method isAdminRoute = findMethod("isAdminRoute", String.class, String.class);
        Method isWhitePath = findMethod("isWhitePath", String.class);

        assertNotNull(isBlogRoute, "Gateway must distinguish blog-domain routes");
        assertNotNull(isAdminRoute, "Gateway must distinguish admin-domain routes");
        assertNotNull(isWhitePath, "Gateway must evaluate configured whitelist paths");

        isBlogRoute.setAccessible(true);
        isAdminRoute.setAccessible(true);
        isWhitePath.setAccessible(true);

        assertEquals(Boolean.TRUE, isBlogRoute.invoke(configuration, "/blog/front/articles"));
        assertEquals(Boolean.TRUE, isBlogRoute.invoke(configuration, "/blog/front/user/info"));
        assertEquals(Boolean.FALSE, isBlogRoute.invoke(configuration, "/front/articles"));
        assertEquals(Boolean.TRUE, isAdminRoute.invoke(configuration, "/blog/admin/article/list", "GET"));
        assertEquals(Boolean.FALSE, isBlogRoute.invoke(configuration, "/system/user/list"));
        assertEquals(Boolean.TRUE, isWhitePath.invoke(configuration, "/blog/front/articles"));
        assertEquals(Boolean.FALSE, isWhitePath.invoke(configuration, "/system/user/list"));
    }

    private Method findMethod(String name, Class<?>... parameterTypes) {
        try {
            return SaTokenGatewayConfiguration.class.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private SaTokenGatewayConfiguration newConfigurationInstance() throws Exception {
        for (Constructor<?> constructor : SaTokenGatewayConfiguration.class.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            if (constructor.getParameterCount() == 0) {
                return (SaTokenGatewayConfiguration) constructor.newInstance();
            }
            if (constructor.getParameterCount() == 1
                && constructor.getParameterTypes()[0].getName().equals("com.sourcelin.gateway.config.properties.IgnoreWhiteProperties")) {
                Object ignoreWhiteProperties = constructor.getParameterTypes()[0].newInstance();
                Method setWhites = constructor.getParameterTypes()[0].getMethod("setWhites", java.util.List.class);
                setWhites.invoke(ignoreWhiteProperties, Arrays.asList("/auth/login", "/blog/front/articles"));
                return (SaTokenGatewayConfiguration) constructor.newInstance(ignoreWhiteProperties);
            }
        }
        fail("Unable to instantiate SaTokenGatewayConfiguration");
        return null;
    }
}
