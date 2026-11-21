package com.sourcelin.message.session;

import com.sourcelin.message.config.RedisMessageConfig;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 消息服务实例标识。
 *
 * <p>每个进程启动时生成一个稳定的 {@code nodeId}，用于：</p>
 * <ul>
 *     <li>在 Redis 中登记"用户→所在节点"路由（{@code sse:user:node:{userId} = nodeId}）；</li>
 *     <li>订阅本节点专属的 Redis 频道 {@code message_push_chan:{nodeId}}，实现定向投递。</li>
 * </ul>
 */
@Component
public class NodeIdentity
{
    private final String nodeId = UUID.randomUUID().toString().replace("-", "");

    public String getNodeId()
    {
        return nodeId;
    }

    /**
     * 本节点专属推送频道。
     */
    public String getNodeChannel()
    {
        return RedisMessageConfig.MESSAGE_CHANNEL + ":" + nodeId;
    }

    /**
     * 指定节点的专属推送频道。
     */
    public static String channelOf(String nodeId)
    {
        return RedisMessageConfig.MESSAGE_CHANNEL + ":" + nodeId;
    }
}
