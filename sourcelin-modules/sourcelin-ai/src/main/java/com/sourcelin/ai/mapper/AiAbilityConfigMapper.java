package com.sourcelin.ai.mapper;

import com.sourcelin.ai.domain.AiAbilityConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiAbilityConfigMapper {
    AiAbilityConfig selectByAbilityCode(@Param("abilityCode") String abilityCode);
    List<AiAbilityConfig> selectList(AiAbilityConfig query);
    int insert(AiAbilityConfig config);
    int update(AiAbilityConfig config);
    int deleteById(@Param("id") Long id);
}
