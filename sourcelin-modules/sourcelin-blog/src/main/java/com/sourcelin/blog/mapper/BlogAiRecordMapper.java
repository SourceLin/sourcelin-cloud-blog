package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.BlogAiRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BlogAiRecordMapper {
    int insert(BlogAiRecord record);
    int update(BlogAiRecord record);
    BlogAiRecord selectById(@Param("id") Long id);
    List<BlogAiRecord> selectList(BlogAiRecord query);
    List<BlogAiRecord> selectByBizIdAndAbility(@Param("bizId") String bizId, @Param("abilityCode") String abilityCode);
}
