package com.sourcelin.blog.service.interaction.impl;

import com.sourcelin.blog.constant.InteractionActionTypeEnum;
import com.sourcelin.blog.constant.InteractionTargetTypeEnum;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.service.BlogInteractionGuard;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.interaction.IInteractionCollectService;
import com.sourcelin.blog.service.interaction.InteractionTargetSupport;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收藏互动服务实现。
 */
@Service
public class InteractionCollectServiceImpl implements IInteractionCollectService
{
    @Autowired
    private ICollectService collectService;
    @Autowired
    private InteractionTargetSupport interactionTargetSupport;
    @Autowired
    private BlogInteractionGuard blogInteractionGuard;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean collect(Long userId, String targetType, Long targetId)
    {
        InteractionTargetTypeEnum type = interactionTargetSupport.validateTarget(targetType, targetId);
        if (blogInteractionGuard.tryInteractionAction(userId, InteractionActionTypeEnum.COLLECT.getCode())
            == BlogInteractionGuard.Decision.RATE_LIMIT)
        {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "互动过于频繁，请稍后再试");
        }

        Collect active = collectService.selectCollectByUserAndTarget(userId, type.getCode(), targetId);
        if (active != null)
        {
            return false;
        }

        Collect probe = new Collect();
        probe.setUserId(userId);
        probe.setTargetType(type.getCode());
        probe.setTargetId(targetId);
        List<Collect> history = collectService.selectCollectList(probe);

        int affected;
        if (history != null && !history.isEmpty())
        {
            Collect restore = history.get(0);
            restore.setDeleted(0);
            restore.setUpdateTime(DateUtils.getNowDate());
            affected = collectService.updateCollect(restore);
        }
        else
        {
            Collect created = new Collect();
            created.setUserId(userId);
            created.setTargetType(type.getCode());
            created.setTargetId(targetId);
            created.setDeleted(0);
            affected = collectService.insertCollect(created);
        }
        // 校验写入/更新影响行数，避免计数漂移
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "收藏记录写入失败，请稍后重试");
        }
        interactionTargetSupport.increaseCollectCount(type, targetId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uncollect(Long userId, String targetType, Long targetId)
    {
        InteractionTargetTypeEnum type = interactionTargetSupport.validateTarget(targetType, targetId);
        if (blogInteractionGuard.tryInteractionAction(userId, InteractionActionTypeEnum.COLLECT.getCode())
            == BlogInteractionGuard.Decision.RATE_LIMIT)
        {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "互动过于频繁，请稍后再试");
        }

        Collect active = collectService.selectCollectByUserAndTarget(userId, type.getCode(), targetId);
        if (active == null)
        {
            return false;
        }
        active.setDeleted(1);
        active.setUpdateTime(DateUtils.getNowDate());
        int affected = collectService.updateCollect(active);
        // 校验更新影响行数，避免计数漂移
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "取消收藏失败，请稍后重试");
        }
        interactionTargetSupport.decreaseCollectCount(type, targetId);
        return true;
    }
}
