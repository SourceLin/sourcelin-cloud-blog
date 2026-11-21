package com.sourcelin.ai.controller;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.ProviderTypeEnum;
import com.sourcelin.ai.service.AiAbilityConfigService;
import com.sourcelin.ai.api.domain.*;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.*;
import com.sourcelin.common.security.annotation.InnerAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/ability-configs")
@RequiredArgsConstructor
public class AiAbilityConfigController extends BaseController {
 private final AiAbilityConfigService service;
 @InnerAuth @GetMapping("/provider-types")
 public ListResult<AiProviderOptionVO> getProviderTypes() { return ListResult.of(ProviderTypeEnum.toOptions()); }
 @InnerAuth @GetMapping
 public PageResult<AiAbilityConfig> list(AiAbilityConfig query) { startPage(); return toPageResult(service.list(query)); }
 @InnerAuth @PostMapping
 public Integer create(@RequestBody AiAbilityConfigDTO request) { return service.save(request, false); }
 @InnerAuth @PutMapping
 public Integer update(@RequestBody AiAbilityConfigDTO request) { return service.save(request, true); }
 @InnerAuth @DeleteMapping("/{id}")
 public Integer delete(@PathVariable Long id) { return service.delete(id); }
}
