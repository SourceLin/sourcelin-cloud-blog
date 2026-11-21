package com.sourcelin.message.service;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.redis.service.RedisService;
import com.sourcelin.message.config.RedisMessageConfig;
import com.sourcelin.message.session.NodeIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class MessagePushService {
 private final RedisService redis;
 public void push(String principal,String content,String metadata) {
  if(principal==null || !principal.matches("(blog|admin):([0-9]+|\\*)") || content==null || content.length()>8192)
   throw new BusinessException(ResultCode.VALIDATION_ERROR);
  JSONObject data=new JSONObject();data.put("userId",principal);data.put("content",content);data.put("metadata",metadata);
  String payload=data.toJSONString();
  if(principal.endsWith(":*")){redis.redisTemplate.convertAndSend(RedisMessageConfig.MESSAGE_CHANNEL,payload);return;}
  Map<?,?> routes=redis.redisTemplate.opsForHash().entries(CacheConstants.SSE_USER_NODE_KEY+principal);
  boolean sent=false;
  for(Map.Entry<?,?> route:routes.entrySet()) {
   if(route.getValue() instanceof Number && System.currentTimeMillis()-((Number)route.getValue()).longValue()<90000L) {
    redis.redisTemplate.convertAndSend(NodeIdentity.channelOf(String.valueOf(route.getKey())),payload);sent=true;
   }
  }
  if(!sent)redis.redisTemplate.convertAndSend(RedisMessageConfig.MESSAGE_CHANNEL,payload);
 }
}
