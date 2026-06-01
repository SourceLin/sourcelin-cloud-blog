package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.domain.LikeRecord;
import com.sourcelin.blog.mapper.LikeRecordMapper;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.interaction.IInteractionCollectService;
import com.sourcelin.blog.service.interaction.IInteractionLikeService;
import com.sourcelin.blog.vo.interaction.InteractionTargetStatVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.PageUtils;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台统一互动接口（点赞/收藏）。
 */
@RestController
@RequestMapping("/front/interactions")
public class FrontInteractionController
{
    @Autowired
    private IInteractionLikeService interactionLikeService;
    @Autowired
    private IInteractionCollectService interactionCollectService;
    @Autowired
    private ICollectService collectService;
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @PutMapping("/likes/{targetType}/{targetId}")
    public Void like(@PathVariable("targetType") String targetType, @PathVariable("targetId") Long targetId)
    {
        Long userId = requireLogin();
        interactionLikeService.like(userId, targetType, targetId);
        return null;
    }

    @DeleteMapping("/likes/{targetType}/{targetId}")
    public Void unlike(@PathVariable("targetType") String targetType, @PathVariable("targetId") Long targetId)
    {
        Long userId = requireLogin();
        interactionLikeService.unlike(userId, targetType, targetId);
        return null;
    }

    @PutMapping("/collects/{targetType}/{targetId}")
    public Void collect(@PathVariable("targetType") String targetType, @PathVariable("targetId") Long targetId)
    {
        Long userId = requireLogin();
        interactionCollectService.collect(userId, targetType, targetId);
        return null;
    }

    @DeleteMapping("/collects/{targetType}/{targetId}")
    public Void uncollect(@PathVariable("targetType") String targetType, @PathVariable("targetId") Long targetId)
    {
        Long userId = requireLogin();
        interactionCollectService.uncollect(userId, targetType, targetId);
        return null;
    }

    @GetMapping("/collects")
    public PageResult<Collect> collects(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                        @RequestParam(value = "sortBy", required = false) String sortBy,
                                        @RequestParam(value = "sortOrder", required = false) String sortOrder,
                                        @RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "state", required = false) String state,
                                        Long userId)
    {
        if (userId == null || userId <= 0)
        {
            Long currentUserId = blogLoginAccessor.getCurrentUserId();
            if (currentUserId != null && currentUserId > 0)
            {
                userId = currentUserId;
            }
        }
        if (userId == null || userId <= 0)
        {
            return PageResult.empty(page, pageSize);
        }
        String orderBy = buildCollectOrderBy(sortBy, sortOrder);
        page = PageUtils.clampPage(page);
        pageSize = PageUtils.clampPageSize(pageSize);
        if (orderBy == null)
        {
            PageHelper.startPage(page, pageSize);
        }
        else
        {
            PageHelper.startPage(page, pageSize, orderBy);
        }

        Collect filter = new Collect();
        filter.setUserId(userId);
        filter.setDeleted(0);
        String collectType = normalizeCollectType(type);
        if (collectType != null)
        {
            filter.setTargetType(collectType);
        }
        if (state != null && state.length() > 0)
        {
            if ("active".equals(state) || "inactive".equals(state))
            {
                filter.getParams().put("relationState", state);
            }
            else if (!"all".equals(state))
            {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "筛选参数不合法");
            }
        }

        List<Collect> collects = collectService.selectCollectListWithArticle(filter);
        PageInfo<Collect> pageInfo = new PageInfo<Collect>(collects);
        return BlogPageResults.of(collects, pageInfo, page, pageSize);
    }

    @GetMapping("/likes")
    public PageResult<LikeRecord> likes(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                        @RequestParam(value = "sortBy", required = false) String sortBy,
                                        @RequestParam(value = "sortOrder", required = false) String sortOrder,
                                        @RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "state", required = false) String state,
                                        Long userId)
    {
        if (userId == null || userId <= 0)
        {
            Long currentUserId = blogLoginAccessor.getCurrentUserId();
            if (currentUserId != null && currentUserId > 0)
            {
                userId = currentUserId;
            }
        }
        if (userId == null || userId <= 0)
        {
            return PageResult.empty(page, pageSize);
        }
        String orderBy = buildLikeOrderBy(sortBy, sortOrder);
        page = PageUtils.clampPage(page);
        pageSize = PageUtils.clampPageSize(pageSize);
        if (orderBy == null)
        {
            PageHelper.startPage(page, pageSize);
        }
        else
        {
            PageHelper.startPage(page, pageSize, orderBy);
        }

        LikeRecord filter = new LikeRecord();
        filter.setUserId(userId);
        filter.setDeleted(0);
        String likeType = normalizeLikeType(type);
        if (likeType != null)
        {
            filter.setTargetType(likeType);
        }
        if (state != null && state.length() > 0)
        {
            if ("active".equals(state) || "inactive".equals(state))
            {
                filter.getParams().put("relationState", state);
            }
            else if (!"all".equals(state))
            {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "筛选参数不合法");
            }
        }
        else
        {
            filter.getParams().put("relationState", "active");
        }

        List<LikeRecord> likes = likeRecordMapper.selectLikeRecordListWithTarget(filter);
        PageInfo<LikeRecord> pageInfo = new PageInfo<LikeRecord>(likes);
        return BlogPageResults.of(likes, pageInfo, page, pageSize);
    }

    @GetMapping("/targets/stat")
    public List<InteractionTargetStatVO> stats(@RequestParam("targetType") String targetType,
                                               @RequestParam("targetIds") List<Long> targetIds)
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        return interactionLikeService.queryStats(userId, targetType, targetIds);
    }

    private Long requireLogin()
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        return userId;
    }

    private String normalizeLikeType(String type)
    {
        return normalizeCollectType(type);
    }

    private String buildLikeOrderBy(String sortBy, String sortOrder)
    {
        if (sortBy == null || sortBy.length() == 0)
        {
            return null;
        }
        String normalizedOrder = sortOrder == null ? "desc" : sortOrder.toLowerCase();
        if (!"asc".equals(normalizedOrder) && !"desc".equals(normalizedOrder))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "排序参数不合法");
        }
        Map<String, String> sortColumnMap = new HashMap<String, String>();
        sortColumnMap.put("id", "l.id");
        sortColumnMap.put("targetId", "l.target_id");
        sortColumnMap.put("targetType", "l.target_type");
        sortColumnMap.put("createTime", "l.create_time");
        String column = sortColumnMap.get(sortBy);
        if (column == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "排序参数不合法");
        }
        return column + " " + normalizedOrder;
    }

    private String normalizeCollectType(String type)
    {
        if (type == null || type.length() == 0 || "all".equalsIgnoreCase(type))
        {
            return null;
        }
        String normalized = type.toLowerCase();
        if (Collect.TARGET_TYPE_ARTICLE.equals(normalized)
            || Collect.TARGET_TYPE_SAY.equals(normalized)
            || Collect.TARGET_TYPE_TREEHOLE.equals(normalized))
        {
            return normalized;
        }
        throw new BusinessException(ResultCode.VALIDATION_ERROR, "收藏类型不合法");
    }

    private String buildCollectOrderBy(String sortBy, String sortOrder)
    {
        if (sortBy == null || sortBy.length() == 0)
        {
            return null;
        }
        String normalizedOrder = sortOrder == null ? "desc" : sortOrder.toLowerCase();
        if (!"asc".equals(normalizedOrder) && !"desc".equals(normalizedOrder))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "排序参数不合法");
        }
        Map<String, String> sortColumnMap = new HashMap<String, String>();
        sortColumnMap.put("id", "c.id");
        sortColumnMap.put("targetId", "c.target_id");
        sortColumnMap.put("targetType", "c.target_type");
        sortColumnMap.put("createTime", "c.create_time");
        String column = sortColumnMap.get(sortBy);
        if (column == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "排序参数不合法");
        }
        return column + " " + normalizedOrder;
    }
}
