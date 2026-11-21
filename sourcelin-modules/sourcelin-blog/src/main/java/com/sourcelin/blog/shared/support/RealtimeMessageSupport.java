package com.sourcelin.blog.shared.support;
import com.sourcelin.message.api.service.RemoteMessageService;
import com.sourcelin.common.core.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
@RequiredArgsConstructor
public class RealtimeMessageSupport {
 private static final Logger log=LoggerFactory.getLogger(RealtimeMessageSupport.class);
 private final RemoteMessageService remote;
 @Value("${blog.message.enabled:false}") private boolean enabled;
 public void invalidate(Long userId) {
  if(!enabled)return;
  Runnable publish=() -> {try {remote.pushMessage(userId==null?"blog:*":"blog:"+userId,"{\"type\":\"unread_changed\"}","system",SecurityConstants.INNER);}catch(Exception e){log.warn("实时消息发送失败，客户端可通过查询恢复");}};
  if(org.springframework.transaction.support.TransactionSynchronizationManager.isSynchronizationActive())
   org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization(){public void afterCommit(){publish.run();}});
  else publish.run();
 }
}
