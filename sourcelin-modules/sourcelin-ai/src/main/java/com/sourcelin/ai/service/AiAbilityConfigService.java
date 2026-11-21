package com.sourcelin.ai.service;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.ProviderTypeEnum;
import com.sourcelin.ai.mapper.AiAbilityConfigMapper;
import com.sourcelin.ai.api.domain.AiAbilityConfigDTO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AiAbilityConfigService {
 private final AiAbilityConfigMapper mapper;
 private final AiAbilityRouter router;
 public List<AiAbilityConfig> list(AiAbilityConfig query) {
  List<AiAbilityConfig> items = mapper.selectList(query);
  // 列表不得返回服务商密钥；编辑时留空表示保持原密钥。
  items.forEach(item -> item.setApiKey(null));
  return items;
 }
 public int save(AiAbilityConfigDTO request, boolean update) {
  if (request == null || (update && request.getId() == null) || (!update && (request.getAbilityCode() == null || request.getAbilityCode().trim().isEmpty())))
   throw new BusinessException(ResultCode.VALIDATION_ERROR, "能力编码或配置编号不能为空");
  if (request.getProviderType() != null) ProviderTypeEnum.fromCode(request.getProviderType());
  AiAbilityConfig config = new AiAbilityConfig(); BeanUtils.copyProperties(request, config);
  config.setUpdateTime(DateUtils.getNowDate());
  if (!update) { config.setCreateTime(DateUtils.getNowDate()); if (config.getVersion() == null) config.setVersion(1); if(config.getEnabled() == null) config.setEnabled("0"); }
  int rows = update ? mapper.update(config) : mapper.insert(config);
  if (rows > 0) router.evictAllAbilityConfigCache();
  return rows;
 }
 public int delete(Long id) { int rows = mapper.deleteById(id); if(rows > 0) router.evictAllAbilityConfigCache(); return rows; }
}
