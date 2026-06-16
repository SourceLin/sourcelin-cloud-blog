package com.sourcelin.blog.controller.front;

import com.alibaba.fastjson2.JSON;
import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.constant.InteractionTargetTypeEnum;
import com.sourcelin.blog.constant.ModerationDecisionEnum;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.domain.Treehole;
import com.sourcelin.blog.mapper.LikeRecordMapper;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.ITreeholeService;
import com.sourcelin.blog.service.ModerationPipeline;
import com.sourcelin.blog.vo.ModerationOutcome;
import com.sourcelin.blog.vo.TreeholeBarrageVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.redis.service.RedisService;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/front/treeholes")
public class FrontTreeholeController
{
    @Autowired
    private ITreeholeService treeholeService;
    @Autowired
    private ICollectService collectService;
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    @Autowired
    private BlogLoginAccessor blogLoginAccessor;
    @Autowired
    private ModerationPipeline moderationPipeline;
    @Autowired
    private RedisService redisService;

    private static final String BARRAGE_REDIS_KEY = "blog:treehole:barrage:list";

    @GetMapping
    public PageResult<Treehole> list(Treehole treehole)
    {
        if (treehole.getDeleted() == null)
        {
            treehole.setDeleted(0L);
        }
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<Treehole> list = treeholeService.selectTreeholeList(treehole);
        fillInteractionStatus(list);
        PageInfo<Treehole> pageInfo = new PageInfo<Treehole>(list);
        return BlogPageResults.of(list, pageInfo, pageDomain.getPage(), pageDomain.getPageSize());
    }

    @GetMapping("/barrage")
    public List<TreeholeBarrageVO> getBarrageList()
    {
        List<String> list = redisService.getCacheList(BARRAGE_REDIS_KEY);
        if (list == null || list.isEmpty())
        {
            // 缓存不存在，触发懒加载并预热
            Treehole filter = new Treehole();
            filter.setDeleted(0L);
            PageHelper.startPage(1, 100, "create_time desc");
            List<Treehole> latest = treeholeService.selectTreeholeList(filter);
            PageHelper.clearPage();

            if (latest != null && !latest.isEmpty())
            {
                List<String> jsonList = latest.stream().map(t -> {
                    TreeholeBarrageVO barrageVO = new TreeholeBarrageVO();
                    barrageVO.setId(t.getId());
                    barrageVO.setMsg(t.getContent());
                    if (t.getUser() != null)
                    {
                        barrageVO.setAvatar(t.getUser().getAvatar());
                        barrageVO.setNickname(t.getUser().getNickname());
                    }
                    else
                    {
                        barrageVO.setNickname(t.getUserNickname());
                    }
                    return JSON.toJSONString(barrageVO);
                }).collect(Collectors.toList());

                // 批量刷入 Redis 缓存并设置 24 小时过期
                redisService.setCacheList(BARRAGE_REDIS_KEY, jsonList);
                redisService.expire(BARRAGE_REDIS_KEY, 24 * 3600);

                return jsonList.stream().map(json -> JSON.parseObject(json, TreeholeBarrageVO.class)).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        // 缓存命中，直接反序列化返回
        return list.stream()
                .map(json -> JSON.parseObject(json, TreeholeBarrageVO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Treehole detail(@PathVariable("id") Long id)
    {
        Treehole treehole = treeholeService.selectTreeholeById(id);
        if (treehole == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "树洞不存在");
        }
        fillInteractionStatus(Collections.singletonList(treehole));
        return treehole;
    }

    @PostMapping
    public Long create(@RequestBody Treehole treehole)
    {
        ModerationOutcome moderationOutcome = moderationPipeline.moderate(treehole.getContent());
        if (moderationOutcome.getDecision() == ModerationDecisionEnum.REJECT)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "内容包含敏感词，请修改后重试");
        }
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId != null && userId > 0)
        {
            treehole.setUserId(userId);
        }
        if (treehole.getLikeCount() == null)
        {
            treehole.setLikeCount(0L);
        }
        if (treehole.getCommentCount() == null)
        {
            treehole.setCommentCount(0L);
        }
        if (treehole.getStatus() == null)
        {
            treehole.setStatus(0L);
        }
        if (treehole.getDeleted() == null)
        {
            treehole.setDeleted(0L);
        }
        int result = treeholeService.insertTreehole(treehole);
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "发布失败");
        }

        try
        {
            // 通过主键获取完整的树洞信息（包含联表用户昵称、头像）
            Treehole fullTreehole = treeholeService.selectTreeholeById(treehole.getId());
            if (fullTreehole != null)
            {
                TreeholeBarrageVO barrageVO = new TreeholeBarrageVO();
                barrageVO.setId(fullTreehole.getId());
                barrageVO.setMsg(fullTreehole.getContent());
                if (fullTreehole.getUser() != null)
                {
                    barrageVO.setAvatar(fullTreehole.getUser().getAvatar());
                    barrageVO.setNickname(fullTreehole.getUser().getNickname());
                }
                else
                {
                    barrageVO.setNickname(fullTreehole.getUserNickname());
                }

                String jsonStr = JSON.toJSONString(barrageVO);
                redisService.redisTemplate.opsForList().leftPush(BARRAGE_REDIS_KEY, jsonStr);
                redisService.redisTemplate.opsForList().trim(BARRAGE_REDIS_KEY, 0, 99);
            }
        }
        catch (Exception e)
        {
            // 缓存写入静默失败，不阻断发布主流程
        }

        return treehole.getId();
    }

    @DeleteMapping("/{id}")
    public Void delete(@PathVariable("id") Long id)
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        Treehole existing = treeholeService.selectTreeholeById(id);
        if (existing == null || (existing.getDeleted() != null && existing.getDeleted() != 0L))
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "树洞不存在");
        }
        if (existing.getUserId() == null || !userId.equals(existing.getUserId()))
        {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除该树洞");
        }
        int result = treeholeService.deleteTreeholeById(id);
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }

    private void fillInteractionStatus(List<Treehole> treeholes)
    {
        if (treeholes == null || treeholes.isEmpty())
        {
            return;
        }
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            for (Treehole treehole : treeholes)
            {
                treehole.setLikedByMe(false);
                treehole.setCollectedByMe(false);
            }
            return;
        }
        List<Long> treeholeIds = treeholes.stream()
            .map(Treehole::getId)
            .filter(id -> id != null && id > 0)
            .collect(Collectors.toList());
        if (treeholeIds.isEmpty())
        {
            return;
        }
        Set<Long> likedTargetIdSet = new HashSet<Long>(likeRecordMapper.selectActiveTargetIdsByUserAndType(
            userId,
            InteractionTargetTypeEnum.TREEHOLE.getCode(),
            treeholeIds
        ));
        Collect collectFilter = new Collect();
        collectFilter.setUserId(userId);
        collectFilter.setDeleted(0);
        collectFilter.setTargetType(Collect.TARGET_TYPE_TREEHOLE);
        collectFilter.getParams().put("targetIds", treeholeIds);
        List<Collect> collects = collectService.selectCollectList(collectFilter);
        Set<Long> collectTargetIdSet = new HashSet<Long>();
        if (collects != null)
        {
            for (Collect collect : collects)
            {
                if (collect.getTargetId() != null)
                {
                    collectTargetIdSet.add(collect.getTargetId());
                }
            }
        }
        for (Treehole treehole : treeholes)
        {
            treehole.setLikedByMe(likedTargetIdSet.contains(treehole.getId()));
            treehole.setCollectedByMe(collectTargetIdSet.contains(treehole.getId()));
        }
    }
}
