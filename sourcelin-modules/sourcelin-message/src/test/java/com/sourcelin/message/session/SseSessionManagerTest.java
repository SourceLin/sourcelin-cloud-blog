package com.sourcelin.message.session;
import com.sourcelin.common.redis.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
class SseSessionManagerTest {
 @Test void closingOneTabDoesNotRemoveAnotherTab() throws Exception {
  RedisService redis=mock(RedisService.class);RedisTemplate template=mock(RedisTemplate.class,RETURNS_DEEP_STUBS);ReflectionTestUtils.setField(redis,"redisTemplate",template);
  NodeIdentity node=mock(NodeIdentity.class);when(node.getNodeId()).thenReturn("node");SseSessionManager sessions=new SseSessionManager(redis,node);
  SseEmitter first=mock(SseEmitter.class);SseEmitter second=mock(SseEmitter.class);
  String firstId=sessions.addSession("blog:1",first);sessions.addSession("blog:1",second);
  sessions.removeSession("blog:1",firstId);sessions.send("blog:1","payload");
  verify(first,never()).send(any(SseEmitter.SseEventBuilder.class));verify(second).send(any(SseEmitter.SseEventBuilder.class));
 }
 @Test void adminAndBlogUsersWithSameIdRemainIsolated() throws Exception {
  RedisService redis=mock(RedisService.class);ReflectionTestUtils.setField(redis,"redisTemplate",mock(RedisTemplate.class,RETURNS_DEEP_STUBS));NodeIdentity node=mock(NodeIdentity.class);when(node.getNodeId()).thenReturn("node");
  SseSessionManager sessions=new SseSessionManager(redis,node);SseEmitter admin=mock(SseEmitter.class);SseEmitter blog=mock(SseEmitter.class);
  sessions.addSession("admin:1",admin);sessions.addSession("blog:1",blog);sessions.send("blog:1","payload");
  verify(admin,never()).send(any(SseEmitter.SseEventBuilder.class));verify(blog).send(any(SseEmitter.SseEventBuilder.class));
 }
}
