package com.sourcelin.ai.service.impl;

import com.sourcelin.ai.domain.AiCallLog;
import com.sourcelin.ai.mapper.AiCallLogMapper;
import com.sourcelin.ai.service.AiCallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AiCallLogServiceImpl implements AiCallLogService {
    @Autowired
    private AiCallLogMapper aiCallLogMapper;

    @Override
    public int insert(AiCallLog log) {
        return aiCallLogMapper.insert(log);
    }

    @Override
    public AiCallLog selectById(Long id) {
        return aiCallLogMapper.selectById(id);
    }

    @Override
    public List<AiCallLog> selectList(AiCallLog query) {
        return aiCallLogMapper.selectList(query);
    }
}
