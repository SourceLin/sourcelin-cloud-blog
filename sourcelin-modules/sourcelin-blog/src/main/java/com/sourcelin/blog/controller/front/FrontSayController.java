package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.constant.InteractionTargetTypeEnum;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.domain.Say;
import com.sourcelin.blog.mapper.LikeRecordMapper;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.ISayService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Front say/瞬间 endpoints.
 */
@RestController
@RequestMapping("/front/says")
public class FrontSayController
{
    @Autowired
    private ISayService sayService;
    @Autowired
    private ICollectService collectService;
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @GetMapping
    public PageResult<Say> list(Say say)
    {
        if (say.getDeleted() == null)
        {
            say.setDeleted(0L);
        }
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<Say> list = sayService.selectSayList(say);
        fillInteractionStatus(list);
        PageInfo<Say> pageInfo = new PageInfo<Say>(list);
        return BlogPageResults.of(list, pageInfo, pageDomain.getPage(), pageDomain.getPageSize());
    }

    @GetMapping("/{id}")
    public Say detail(@PathVariable("id") Long id)
    {
        Say say = sayService.selectSayById(id);
        if (say == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "说说不存在");
        }
        fillInteractionStatus(Collections.singletonList(say));
        return say;
    }

    @PostMapping
    public Long create(@RequestBody Say say)
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        say.setUserId(userId);
        if (say.getLikeCount() == null)
        {
            say.setLikeCount(0L);
        }
        if (say.getCommentCount() == null)
        {
            say.setCommentCount(0L);
        }
        if (say.getStatus() == null)
        {
            say.setStatus(0L);
        }
        if (say.getDeleted() == null)
        {
            say.setDeleted(0L);
        }
        int result = sayService.insertSay(say);
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "发布失败");
        }
        return say.getId();
    }

    @DeleteMapping("/{id}")
    public Void delete(@PathVariable("id") Long id)
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        Say existing = sayService.selectSayById(id);
        if (existing == null || (existing.getDeleted() != null && existing.getDeleted() != 0L))
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "说说不存在");
        }
        if (existing.getUserId() == null || !userId.equals(existing.getUserId()))
        {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除该说说");
        }
        int result = sayService.deleteSayById(id);
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }

    private void fillInteractionStatus(List<Say> says)
    {
        if (says == null || says.isEmpty())
        {
            return;
        }
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            for (Say say : says)
            {
                say.setIsCollected(false);
                say.setLikedByMe(false);
                say.setCollectId(null);
            }
            return;
        }
        List<Long> sayIds = says.stream().map(Say::getId).filter(id -> id != null && id > 0).collect(Collectors.toList());
        if (sayIds.isEmpty())
        {
            return;
        }
        Set<Long> likedTargetIdSet = new HashSet<Long>(likeRecordMapper.selectActiveTargetIdsByUserAndType(
            userId,
            InteractionTargetTypeEnum.SAY.getCode(),
            sayIds
        ));
        Collect filter = new Collect();
        filter.setUserId(userId);
        filter.setDeleted(0);
        filter.setTargetType(Collect.TARGET_TYPE_SAY);
        filter.getParams().put("targetIds", sayIds);
        List<Collect> collects = collectService.selectCollectList(filter);
        Map<Long, Collect> collectMap = new HashMap<Long, Collect>();
        if (collects != null)
        {
            for (Collect collect : collects)
            {
                if (collect.getTargetId() != null)
                {
                    collectMap.put(collect.getTargetId(), collect);
                }
            }
        }
        for (Say say : says)
        {
            Collect collect = collectMap.get(say.getId());
            say.setLikedByMe(likedTargetIdSet.contains(say.getId()));
            say.setIsCollected(collect != null);
            say.setCollectId(collect == null ? null : collect.getId());
        }
    }
}
