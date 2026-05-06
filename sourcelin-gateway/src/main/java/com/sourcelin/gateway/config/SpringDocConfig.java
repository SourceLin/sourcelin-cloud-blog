package com.sourcelin.gateway.config;

import java.util.Set;
import java.util.stream.Collectors;
import org.springdoc.core.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.sourcelin.common.core.utils.StringUtils;

/**
 * 网关 SpringDoc 聚合配置
 *
 * @author sourcelin
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "springdoc.api-docs.enabled", matchIfMissing = true)
public class SpringDocConfig implements InitializingBean
{
    @Autowired
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public void afterPropertiesSet()
    {
        NotifyCenter.registerSubscriber(new SwaggerDocRegister(swaggerUiConfigProperties, discoveryClient));
    }
}

class SwaggerDocRegister extends Subscriber<InstancesChangeEvent>
{
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    private DiscoveryClient discoveryClient;

    private static final String[] EXCLUDE_ROUTES = new String[] {
        "sourcelin-gateway", "sourcelin-auth", "sourcelin-file", "sourcelin-monitor"
    };

    SwaggerDocRegister(SwaggerUiConfigProperties swaggerUiConfigProperties, DiscoveryClient discoveryClient)
    {
        this.swaggerUiConfigProperties = swaggerUiConfigProperties;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void onEvent(InstancesChangeEvent event)
    {
        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrlSet = discoveryClient.getServices()
            .stream()
            .flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
            .filter(instance -> !StringUtils.equalsAnyIgnoreCase(instance.getServiceId(), EXCLUDE_ROUTES))
            .map(instance -> {
                AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
                swaggerUrl.setName(instance.getServiceId());
                swaggerUrl.setUrl(String.format("/%s/v3/api-docs", instance.getServiceId()));
                return swaggerUrl;
            })
            .collect(Collectors.toSet());

        swaggerUiConfigProperties.setUrls(swaggerUrlSet);
    }

    @Override
    public Class<? extends Event> subscribeType()
    {
        return InstancesChangeEvent.class;
    }
}
