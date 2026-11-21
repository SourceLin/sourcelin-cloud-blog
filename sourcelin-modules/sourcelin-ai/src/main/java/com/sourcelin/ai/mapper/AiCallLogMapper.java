package com.sourcelin.ai.mapper;

import com.sourcelin.ai.domain.AiCallLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiCallLogMapper {
    int insert(AiCallLog log);
    AiCallLog selectById(@Param("id") Long id);
    List<AiCallLog> selectList(AiCallLog query);
}
