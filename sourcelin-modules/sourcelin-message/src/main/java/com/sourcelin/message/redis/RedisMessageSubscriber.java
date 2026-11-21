package com.sourcelin.message.redis;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.message.session.SseSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.*;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
 private final SseSessionManager sessions;
 @Override public void onMessage(Message message,byte[] pattern) {
  try {
   Object decoded=JSON.parse(new String(message.getBody(),StandardCharsets.UTF_8));
   JSONObject json=decoded instanceof String ? JSON.parseObject((String)decoded) : (JSONObject)decoded;
   String principal=json.getString("userId");if(principal==null)return;
   Map<String,String> payload=new HashMap<>();payload.put("content",json.getString("content"));payload.put("metadata",json.getString("metadata"));
   if(principal.endsWith(":*"))sessions.broadcast(principal.substring(0,principal.length()-1),payload);else sessions.send(principal,payload);
  } catch(Exception ignored) { /* 无效事件不影响其余连接。 */ }
 }
}
