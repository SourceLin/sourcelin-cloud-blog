package com.sourcelin.auth.service;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.redis.service.RedisService;
import com.sourcelin.common.security.stp.StpBlogUtil;
import com.sourcelin.common.security.stp.StpAdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Service
@RequiredArgsConstructor
public class SseTicketService {
 private final RedisService redis;
 public String issue(String loginType) {
  String principal;
  if("admin".equals(loginType) && StpAdminUtil.stpLogic.isLogin()) principal="admin:"+StpAdminUtil.stpLogic.getLoginIdAsString();
  else if(("blog".equals(loginType)||"mini".equals(loginType)) && StpBlogUtil.stpLogic.isLogin()) principal="blog:"+StpBlogUtil.stpLogic.getLoginIdAsString();
  else throw new BusinessException(ResultCode.UNAUTHORIZED);
  String ticket=UUID.randomUUID().toString().replace("-","");
  redis.setCacheObject(CacheConstants.SSE_TICKET_KEY+ticket,principal,10L,TimeUnit.SECONDS);return ticket;
 }
}
