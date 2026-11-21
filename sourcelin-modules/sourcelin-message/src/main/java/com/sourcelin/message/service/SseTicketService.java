package com.sourcelin.message.service;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.Collections;
@Service
@RequiredArgsConstructor
public class SseTicketService {
 private final RedisService redis;
 private static final DefaultRedisScript<String> CONSUME=new DefaultRedisScript<>("local v=redis.call('GET',KEYS[1]); if v then redis.call('DEL',KEYS[1]); end; return v",String.class);
 public String consume(String ticket) {
  if(ticket==null || !ticket.matches("[a-f0-9]{32}")) throw new BusinessException(ResultCode.UNAUTHORIZED);
  Object result=redis.redisTemplate.execute(CONSUME,Collections.singletonList(CacheConstants.SSE_TICKET_KEY+ticket));
  String principal=result==null?null:String.valueOf(result);
  if(principal==null || !principal.matches("(admin|blog):[0-9]+")) throw new BusinessException(ResultCode.UNAUTHORIZED);
  return principal;
 }
}
