package com.sourcelin.ai.provider.common;

import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.AiCallLog;
import com.sourcelin.ai.service.AiCallLogService;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DirectLlmSseEventHandlerTest {
    private final AiCallLogService logs = mock(AiCallLogService.class);
    private final AiStreamResultCallback archive = mock(AiStreamResultCallback.class);
    private final EventSource source = mock(EventSource.class);
    private final DirectLlmSseEventHandler handler = new DirectLlmSseEventHandler(
            new SseEmitter(), new AiAbilityConfig(), Map.of(), "article", "request", logs, archive);

    @Test
    void finishReasonThenDoneAndCancelProduceOneSuccess() {
        handler.onEvent(source, null, null, """
                {"choices":[{"delta":{"content":"9"},"finish_reason":"stop"}]}
                """);
        handler.onEvent(source, null, null, "[DONE]");
        handler.onFailure(source, new IOException("CANCEL"), null);

        verify(archive).onComplete("9");
        verify(source).cancel();
        var entry = ArgumentCaptor.forClass(AiCallLog.class);
        verify(logs).insert(entry.capture());
        assertEquals("success", entry.getValue().getStatus());
        assertEquals("9", entry.getValue().getResponseSummary());
        verifyNoMoreInteractions(logs, archive);
    }

    @Test
    void upstreamFailureBeforeFinishRecordsOneError() {
        handler.onFailure(source, new IOException("connection lost"), null);
        handler.onEvent(source, null, null, "[DONE]");

        var entry = ArgumentCaptor.forClass(AiCallLog.class);
        verify(logs).insert(entry.capture());
        assertEquals("failed", entry.getValue().getStatus());
        assertEquals("STREAM_ERROR", entry.getValue().getErrorCode());
        verifyNoInteractions(archive);
    }
}
