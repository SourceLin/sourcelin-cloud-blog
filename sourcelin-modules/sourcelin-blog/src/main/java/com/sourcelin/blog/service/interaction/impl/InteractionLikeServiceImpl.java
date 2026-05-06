package com.sourcelin.blog.service.interaction.impl;

import com.sourcelin.blog.constant.InteractionActionTypeEnum;
import com.sourcelin.blog.constant.InteractionTargetTypeEnum;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.domain.LikeRecord;
import com.sourcelin.blog.mapper.LikeRecordMapper;
import com.sourcelin.blog.service.BlogInteractionGuard;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.interaction.IInteractionLikeService;
import com.sourcelin.blog.service.interaction.InteractionTargetSupport;
import com.sourcelin.blog.vo.interaction.InteractionTargetStatVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 点赞互动服务实现。
 */
@Service
public class InteractionLikeServiceImpl implements IInteractionLikeService
{
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    @Autowired
    private ICollectService collectService;
    @Autowired
    private InteractionTargetSupport interactionTargetSupport;
    @Autowired
    private BlogInteractionGuard blogInteractionGuard;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean like(Long userId, String targetType, Long targetId)
    {
        InteractionTargetTypeEnum type = interactionTargetSupport.validateTarget(targetType, targetId);
        if (blogInteractionGuard.tryInteractionAction(userId, InteractionActionTypeEnum.LIKE.getCode())
            == BlogInteractionGuard.Decision.RATE_LIMIT)
        {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "互动过于频繁，请稍后再试");
        }

        LikeRecord active = likeRecordMapper.selectLikeByUserAndTarget(userId, type.getCode(), targetId);
        if (active != null)
        {
            return false;
        }

        LikeRecord history = likeRecordMapper.selectAnyLikeByUserAndTarget(userId, type.getCode(), targetId);
        int affected;
        if (history == null)
        {
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setTargetType(type.getCode());
            record.setTargetId(targetId);
            record.setDeleted(0);
            record.setCreateTime(DateUtils.getNowDate());
            record.setUpdateTime(DateUtils.getNowDate());
            affected = likeRecordMapper.insertLikeRecord(record);
        }
        else
        {
            history.setDeleted(0);
            history.setUpdateTime(DateUtils.getNowDate());
            affected = likeRecordMapper.updateLikeRecord(history);
        }

        // 校验写入/更新影响行数，避免计数漂移
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "点赞记录写入失败，请稍后重试");
        }
        interactionTargetSupport.increaseLikeCount(type, targetId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlike(Long userId, String targetType, Long targetId)
    {
        InteractionTargetTypeEnum type = interactionTargetSupport.validateTarget(targetType, targetId);
        if (blogInteractionGuard.tryInteractionAction(userId, InteractionActionTypeEnum.LIKE.getCode())
            == BlogInteractionGuard.Decision.RATE_LIMIT)
        {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "互动过于频繁，请稍后再试");
        }

        LikeRecord active = likeRecordMapper.selectLikeByUserAndTarget(userId, type.getCode(), targetId);
        if (active == null)
        {
            return false;
        }
        active.setDeleted(1);
        active.setUpdateTime(DateUtils.getNowDate());
        int affected = likeRecordMapper.updateLikeRecord(active);
        // 校验更新影响行数，避免计数漂移
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "取消点赞失败，请稍后重试");
        }
        interactionTargetSupport.decreaseLikeCount(type, targetId);
        return true;
    }

    @Override
    public List<InteractionTargetStatVO> queryStats(Long userId, String targetType, List<Long> targetIds)
    {
        InteractionTargetTypeEnum type = InteractionTargetTypeEnum.fromCode(targetType);
        if (type == null || targetIds == null || targetIds.isEmpty())
        {
            return Collections.emptyList();
        }
        List<InteractionTargetStatVO> result = new ArrayList<InteractionTargetStatVO>();
        for (Long targetId : targetIds)
        {
            if (targetId == null || targetId <= 0)
            {
                continue;
            }
            InteractionTargetStatVO vo = new InteractionTargetStatVO();
            vo.setTargetType(type.getCode());
            vo.setTargetId(targetId);
            vo.setLikeCount((long) likeRecordMapper.countActiveLikeByTarget(type.getCode(), targetId));
            vo.setCollectCount(interactionTargetSupport.readCollectCount(type, targetId));
            if (userId != null && userId > 0)
            {
                vo.setLikedByMe(likeRecordMapper.selectLikeByUserAndTarget(userId, type.getCode(), targetId) != null);
                Collect collect = collectService.selectCollectByUserAndTarget(userId, type.getCode(), targetId);
                vo.setCollectedByMe(collect != null);
            }
            else
            {
                vo.setLikedByMe(false);
                vo.setCollectedByMe(false);
            }
            result.add(vo);
        }
        return result;
    }
}
