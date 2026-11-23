package com.sourcelin.blog.controller.internal;
import com.sourcelin.ai.api.domain.AiRecordCreateRequest;
import com.sourcelin.blog.service.BlogAiRecordService;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.security.annotation.InnerAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/inner/blog/ai/records")
@RequiredArgsConstructor
public class RemoteBlogAiController {
 private final BlogAiRecordService service;
 @InnerAuth @PostMapping(consumes = "application/json")
 public R<Long> createRecord(@RequestBody AiRecordCreateRequest request) {
  return R.ok(service.create(request.abilityCode(), request.bizId(), request.aiResult(), request.ownerId()));
 }
 @InnerAuth @PostMapping("/create")
 public R<Long> createRecord(@RequestParam String abilityCode,@RequestParam String bizId,@RequestParam String aiResult,@RequestParam String ownerId) {
  return R.ok(service.create(abilityCode,bizId,aiResult,ownerId));
 }
}
