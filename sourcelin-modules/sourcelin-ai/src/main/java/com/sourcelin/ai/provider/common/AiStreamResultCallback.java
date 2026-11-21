package com.sourcelin.ai.provider.common;

/**
 * AI 流式调用完成后的回调接口，用于解耦对外部服务（如 RemoteBlogAiService）的直接依赖。
 */
@FunctionalInterface
public interface AiStreamResultCallback {
    /**
     * 当流式调用完成时触发
     *
     * @param fullText 完整的 AI 响应文本
     * @return 归档后的记录 ID，若无归档或归档失败则返回 null
     */
    Long onComplete(String fullText);
}
