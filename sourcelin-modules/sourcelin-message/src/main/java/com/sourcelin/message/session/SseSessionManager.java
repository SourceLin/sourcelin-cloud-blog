package com.sourcelin.message.session;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class SseSessionManager {
 private static final Logger log=LoggerFactory.getLogger(SseSessionManager.class);
 private final Map<String,Map<String,SseEmitter>> sessions=new ConcurrentHashMap<>();
 private final RedisService redisService;
 private final NodeIdentity nodeIdentity;
 public synchronized String addSession(String principal,SseEmitter emitter) {
  String connectionId=UUID.randomUUID().toString();
  sessions.computeIfAbsent(principal,k -> new ConcurrentHashMap<>()).put(connectionId,emitter);
  writeRoute(principal);return connectionId;
 }
 public synchronized void removeSession(String principal,String connectionId) {
  Map<String,SseEmitter> active=sessions.get(principal);if(active==null)return;
  active.remove(connectionId);
  if(active.isEmpty()) { sessions.remove(principal); try {redisService.redisTemplate.opsForHash().delete(CacheConstants.SSE_USER_NODE_KEY+principal,nodeIdentity.getNodeId());} catch(Exception e){log.warn("SSE 节点路由清理失败");} }
 }
 public void send(String principal,Object payload) { dispatch(principal,"message",payload); }
 public void broadcast(String prefix,Object payload) { sessions.keySet().stream().filter(key -> key.startsWith(prefix)).forEach(key -> send(key,payload)); }
 private void dispatch(String principal,String event,Object payload) {
  Map<String,SseEmitter> active=sessions.get(principal);if(active==null)return;
  active.forEach((id,emitter) -> {try{emitter.send(SseEmitter.event().name(event).data(payload));}catch(Exception e){removeSession(principal,id);}});
 }
 public synchronized void heartbeat() {
  sessions.keySet().forEach(principal -> {
   dispatch(principal, "heartbeat", Collections.singletonMap("timestamp", System.currentTimeMillis()));
   if (sessions.containsKey(principal)) writeRoute(principal);
  });
 }
 private void writeRoute(String principal) {
  try {
   String key=CacheConstants.SSE_USER_NODE_KEY+principal;
   redisService.redisTemplate.opsForHash().put(key,nodeIdentity.getNodeId(),System.currentTimeMillis());
   redisService.expire(key,90L,TimeUnit.SECONDS);
  } catch(Exception e) {log.warn("SSE 节点路由刷新失败");}
 }
}
