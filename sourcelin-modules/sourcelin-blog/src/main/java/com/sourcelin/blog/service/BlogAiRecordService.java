package com.sourcelin.blog.service;
import com.sourcelin.blog.config.BlogAiProperties;
import com.sourcelin.blog.domain.BlogAiRecord;
import com.sourcelin.blog.mapper.BlogAiRecordMapper;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class BlogAiRecordService {
 private final BlogAiRecordMapper mapper;
 private final BlogAiProperties properties;
 public Long create(String abilityCode,String bizId,String result,String ownerId) {
  if(!properties.isEnabled()) throw new BusinessException(ResultCode.CONFLICT,"AI 功能未启用");
  if(abilityCode==null || bizId==null || result==null || ownerId==null || !ownerId.matches("(admin|blog):[0-9]+"))
   throw new BusinessException(ResultCode.VALIDATION_ERROR);
  BlogAiRecord record=new BlogAiRecord();record.setAbilityCode(abilityCode);record.setBizId(bizId);record.setAiResult(result);record.setOwnerId(ownerId);record.setStatus("pending");
  if(mapper.insert(record)<1)throw new BusinessException(ResultCode.BUSINESS_ERROR,"保存 AI 结果失败");
  return record.getId();
 }
}
