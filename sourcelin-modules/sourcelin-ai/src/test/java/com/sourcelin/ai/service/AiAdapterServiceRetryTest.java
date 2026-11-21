package com.sourcelin.ai.service;

import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.AiTask;
import com.sourcelin.ai.domain.AiTaskEvent;
import com.sourcelin.ai.provider.AiProviderClient;
import com.sourcelin.ai.api.domain.AiTaskCreateRequest;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;
import com.sourcelin.common.core.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiAdapterServiceRetryTest {

    @Mock
    private AiAbilityRouter aiAbilityRouter;

    @Mock
    private AiTaskService aiTaskService;

    @Mock
    private AiProviderClient providerClient;

    @InjectMocks
    private AiAdapterService aiAdapterService;

    @Test
    void retryTaskShouldImmediatelyDispatchFailedTaskAgain() throws Exception {
        AiTask task = new AiTask();
        task.setTaskId("task_1");
        task.setRequestId("req_1");
        task.setAppCode("blog");
        task.setDomainCode("article");
        task.setAbilityCode("article_summary");
        task.setBizId("100");
        task.setUserId("user_1");
        task.setTenantId("tenant_1");
        task.setStatus("failed");
        task.setRetryCount(1);
        task.setInputSnapshot("{\"title\":\"A\",\"content\":\"B\"}");

        AiAbilityConfig config = new AiAbilityConfig();
        config.setAbilityCode("article_summary");
        config.setProviderType("direct_llm");

        when(aiTaskService.selectByTaskId("task_1")).thenReturn(task);
        when(aiTaskService.resetFailedTask("task_1", 1)).thenReturn(1);
        when(aiAbilityRouter.getAbilityConfig("article_summary")).thenReturn(config);
        when(aiAbilityRouter.route("direct_llm")).thenReturn(providerClient);
        when(providerClient.invoke(eq(config), anyMap(), eq("user_1"))).thenReturn("retry result");

        config.setEnabled("1");
        org.springframework.test.util.ReflectionTestUtils.setField(aiAdapterService, "taskExecutor", (java.util.concurrent.Executor) Runnable::run);
        String message = aiAdapterService.retryTask("task_1");

        assertTrue(message.contains("task_1"));
        verify(providerClient).invoke(eq(config), any(Map.class), eq("user_1"));

        ArgumentCaptor<AiTaskEvent> eventCaptor = ArgumentCaptor.forClass(AiTaskEvent.class);
        verify(aiTaskService, org.mockito.Mockito.atLeastOnce()).insertEvent(eventCaptor.capture());
        assertTrue(eventCaptor.getAllValues().stream()
                .anyMatch(event -> "RETRY".equals(event.getEventType())));
    }

    @Test
    void retryDispatchesToExecutorBeforeDoingProviderWork() throws Exception {
        AiTask task = new AiTask(); task.setTaskId("queued"); task.setStatus("failed"); task.setAbilityCode("article_summary"); task.setInputSnapshot("{\"content\":\"文章内容\"}");
        when(aiTaskService.selectByTaskId("queued")).thenReturn(task);
        when(aiTaskService.resetFailedTask("queued", 0)).thenReturn(1);
        java.util.concurrent.atomic.AtomicReference<Runnable> queued = new java.util.concurrent.atomic.AtomicReference<>();
        org.springframework.test.util.ReflectionTestUtils.setField(aiAdapterService, "taskExecutor", (java.util.concurrent.Executor) queued::set);
        aiAdapterService.retryTask("queued");
        org.junit.jupiter.api.Assertions.assertNotNull(queued.get());
        org.mockito.Mockito.verifyNoInteractions(providerClient, aiAbilityRouter);
    }

    @Test
    void queueRejectionPersistsFailedTaskForRetry() {
        Executor rejected = work -> { throw new RejectedExecutionException("queue full"); };
        org.springframework.test.util.ReflectionTestUtils.setField(aiAdapterService, "taskExecutor", rejected);
        AiTaskCreateRequest request = new AiTaskCreateRequest();
        request.setAbilityCode("article_cover_generate");

        AiTaskStatusVO result = aiAdapterService.createTask(request);

        assertEquals("failed", result.getStatus());
        assertNotNull(result.getFinishedTime());
        ArgumentCaptor<AiTask> savedTask = ArgumentCaptor.forClass(AiTask.class);
        verify(aiTaskService).updateByTaskId(savedTask.capture());
        assertEquals(result.getTaskId(), savedTask.getValue().getTaskId());
        assertEquals("failed", savedTask.getValue().getStatus());
    }

    @Test
    void staleRetryDoesNotSubmitAnotherProviderCall() {
        AiTask task = new AiTask();
        task.setStatus("failed");
        task.setInputSnapshot("{}");
        when(aiTaskService.selectByTaskId("already-retried")).thenReturn(task);

        assertThrows(BusinessException.class, () -> aiAdapterService.retryTask("already-retried"));
        org.mockito.Mockito.verifyNoInteractions(providerClient, aiAbilityRouter);
        verify(aiTaskService, org.mockito.Mockito.never()).insertEvent(any());
    }
}
