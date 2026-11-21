package com.sourcelin.message.config;

import com.sourcelin.message.redis.RedisMessageSubscriber;
import com.sourcelin.message.session.NodeIdentity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis 消息订阅配置类。
 *
 * <p>每个节点同时订阅两个频道：</p>
 * <ul>
 *     <li>本节点专属频道 {@code message_push_chan:{nodeId}}：承载命中路由后的定向投递（热路径，单实例接收）；</li>
 *     <li>全局广播频道 {@code message_push_chan}：承载路由未命中时的回落广播，保证投递不回归。</li>
 * </ul>
 */
@Configuration
public class RedisMessageConfig {

    public static final String MESSAGE_CHANNEL = "message_push_chan";

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisMessageSubscriber redisMessageSubscriber,
            NodeIdentity nodeIdentity) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 本节点专属频道（定向投递）
        container.addMessageListener(redisMessageSubscriber, new ChannelTopic(nodeIdentity.getNodeChannel()));
        // 全局广播频道（回落兜底）
        container.addMessageListener(redisMessageSubscriber, new ChannelTopic(MESSAGE_CHANNEL));
        return container;
    }
}
