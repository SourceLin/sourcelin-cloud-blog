package com.sourcelin.blog.service;
import com.sourcelin.blog.service.impl.BlogAiServiceImpl;
import com.sourcelin.blog.config.BlogAiProperties;
import com.sourcelin.blog.domain.*;
import com.sourcelin.blog.mapper.*;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class BlogAiSecurityTest {
 private BlogAiServiceImpl service() {
  BlogAiServiceImpl service=new BlogAiServiceImpl();BlogAiProperties properties=new BlogAiProperties();properties.setEnabled(true);
  ReflectionTestUtils.setField(service,"blogAiProperties",properties);return service;
 }
 @Test void cannotAdoptAnotherUsersRecord() {
  BlogAiServiceImpl service=service();BlogAiRecordMapper records=mock(BlogAiRecordMapper.class);ArticleMapper articles=mock(ArticleMapper.class);
  BlogAiRecord record=new BlogAiRecord();record.setOwnerId("blog:2");record.setStatus("pending");when(records.selectById(1L)).thenReturn(record);
  ReflectionTestUtils.setField(service,"blogAiRecordMapper",records);ReflectionTestUtils.setField(service,"articleMapper",articles);
  assertThrows(BusinessException.class,() -> service.adoptResult(1L,"blog:1"));verifyNoInteractions(articles);verify(records,never()).update(any());
 }
 @Test void cannotAdoptOwnedRecordIntoAnotherUsersArticle() {
  BlogAiServiceImpl service=service();BlogAiRecordMapper records=mock(BlogAiRecordMapper.class);ArticleMapper articles=mock(ArticleMapper.class);
  BlogAiRecord record=new BlogAiRecord();record.setOwnerId("blog:1");record.setStatus("pending");record.setBizId("9");record.setAiResult("title");record.setAbilityCode("article_title_generate");
  when(records.selectById(1L)).thenReturn(record);Article article=new Article();article.setUserId(2L);when(articles.selectArticleById(9L)).thenReturn(article);
  ReflectionTestUtils.setField(service,"blogAiRecordMapper",records);ReflectionTestUtils.setField(service,"articleMapper",articles);
  assertThrows(BusinessException.class,() -> service.adoptResult(1L,"blog:1"));verify(articles,never()).updateArticle(any());
 }
 @Test void cannotReadAnotherUsersTask() {
  BlogAiServiceImpl service=service();RemoteAiService remote=mock(RemoteAiService.class);AiTaskStatusVO task=new AiTaskStatusVO();task.setUserId("blog:2");
  when(remote.getTaskStatus(anyString(),anyString())).thenReturn(ApiResponse.success(task));ReflectionTestUtils.setField(service,"remoteAiService",remote);
  assertThrows(BusinessException.class,() -> service.getAsyncTaskStatus("task","blog:1"));
 }
 @Test void streamIsQueuedBeforeCallingUpstream() {
  BlogAiServiceImpl service=service();AtomicReference<Runnable> queued=new AtomicReference<>();
  ReflectionTestUtils.setField(service,"streamExecutor",(java.util.concurrent.Executor)queued::set);
  assertNotNull(service.invokeStream("ai_agent_chat",null,java.util.Collections.emptyMap(),"anonymous"));assertNotNull(queued.get());
 }
}
