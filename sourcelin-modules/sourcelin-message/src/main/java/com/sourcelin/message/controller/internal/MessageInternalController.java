package com.sourcelin.message.controller.internal;
import com.sourcelin.message.service.MessagePushService;
import com.sourcelin.common.security.annotation.InnerAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
public class MessageInternalController {
 private final MessagePushService service;
 @InnerAuth @PostMapping("/inner/message/push")
 public Void pushMessage(@RequestParam String userId,@RequestParam String content,@RequestParam String metadata) {service.push(userId,content,metadata);return null;}
}
