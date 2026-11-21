package com.sourcelin.ai.service;

import com.sourcelin.ai.domain.AiCallLog;
import java.util.List;

public interface AiCallLogService {
    int insert(AiCallLog log);
    AiCallLog selectById(Long id);
    List<AiCallLog> selectList(AiCallLog query);
}
