package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.constant.InteractionTargetTypeEnum;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.domain.Follow;
import com.sourcelin.blog.dto.ArticleInsertDTO;
import com.sourcelin.blog.mapper.LikeRecordMapper;
import com.sourcelin.blog.service.BlogInteractionGuard;
import com.sourcelin.blog.service.IArticleReadAuthService;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.ICommentService;
import com.sourcelin.blog.service.IFollowService;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.blog.vo.FrontArticleDetailVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.PageUtils;
import com.sourcelin.common.core.utils.ServletUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front/articles")
public class FrontArticleController extends BaseController
{
    @Autowired
    private IArticleService articleService;

    @Autowired
    private ICollectService collectService;

    @Autowired
    private IFollowService followService;

    @Autowired
    private IArticleReadAuthService articleReadAuthService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private BlogInteractionGuard blogInteractionGuard;

    @Autowired
    private BlogLoginAccessor blogLoginAccessor;
    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @GetMapping
    public PageResult<ArticleVO> list(Article article,
                                      @RequestParam(value = "orderBy", required = false) String orderBy)
    {
        if (article.getStatus() == null)
        {
            article.setStatus(ArticleConstants.STATUS_PUBLISHED);
        }
        if (orderBy != null && !orderBy.isEmpty())
        {
            String safeOrderBy = null;
            if ("view_count".equalsIgnoreCase(orderBy))
            {
                safeOrderBy = "a.view_count";
            }
            else if ("create_time".equalsIgnoreCase(orderBy))
            {
                safeOrderBy = "a.create_time";
            }
            if (safeOrderBy != null)
            {
                article.getParams().put("orderBy", safeOrderBy);
            }
        }
        startPage();
        List<ArticleVO> list = articleService.selectArticleVoList(article);
        PageInfo<ArticleVO> pageInfo = new PageInfo<ArticleVO>(list);
        Long viewerId = blogLoginAccessor.getCurrentUserId();
        if (list != null)
        {
            for (ArticleVO vo : list)
            {
                articleReadAuthService.applyContentVisibilityVo(vo, viewerId);
            }
        }
        return BlogPageResults.of(list, pageInfo);
    }

    @GetMapping("/{id}")
    public FrontArticleDetailVO detail(@PathVariable("id") Long id)
    {
        Article article = articleService.selectArticleById(id);
        if (article == null || isDeleted(article))
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        Long viewerId = blogLoginAccessor.getCurrentUserId();
        boolean owner = viewerId != null && article.getUserId() != null && viewerId.equals(article.getUserId());
        if (!owner && !isStatus(article.getStatus(), ArticleConstants.STATUS_PUBLISHED))
        {
            throw new BusinessException(ResultCode.FORBIDDEN, "文章当前不可见");
        }

        boolean readFull = articleReadAuthService.canReadFullContent(article, viewerId);
        articleReadAuthService.applyContentVisibility(article, viewerId);
        if (article.getUser() != null && article.getUserId() != null)
        {
            article.getUser().setArticleCount((long) articleService.countArticleByUserId(article.getUserId()));
            article.getUser().setFollowerCount((long) followService.countFansByTargetUserId(article.getUserId()));
        }

        FrontArticleDetailVO detail = new FrontArticleDetailVO();
        BeanUtils.copyProperties(article, detail);
        detail.setCommentCount((long) commentService.countByArticleId(id));
        detail.setReadFull(readFull);
        if (!readFull)
        {
            detail.setNeedLogin(viewerId == null || viewerId <= 0);
            detail.setUnlockHint(articleReadAuthService.buildUnlockHint(article.getReadAuth()));
        }

        Collect collect = viewerId == null
            ? null
            : collectService.selectCollectByUserAndTarget(viewerId, Collect.TARGET_TYPE_ARTICLE, id);
        detail.setIsCollected(collect != null);
        detail.setCollectId(collect == null ? null : collect.getId());
        if (viewerId != null && article.getUserId() != null)
        {
            Follow follow = followService.selectFollowByUserAndTarget(viewerId, article.getUserId());
            detail.setIsFollowed(follow != null);
            detail.setFollowId(follow == null ? null : follow.getId());
        }
        else
        {
            detail.setIsFollowed(false);
            detail.setFollowId(null);
        }
        detail.setIsLike(viewerId != null
            && likeRecordMapper.selectLikeByUserAndTarget(viewerId, InteractionTargetTypeEnum.ARTICLE.getCode(), id) != null);
        return detail;
    }

    @PostMapping
    public Long create(@RequestBody ArticleInsertDTO dto)
    {
        Long userId = currentUserId();
        if (userId == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        dto.setUserId(userId);
        if (dto.getStatus() == null || dto.getStatus() <= 0)
        {
            dto.setStatus(ArticleConstants.STATUS_UNDER_REVIEW);
        }
        if (dto.getReadAuth() == null)
        {
            dto.setReadAuth(ArticleConstants.READ_AUTH_PUBLIC);
        }
        if (dto.getIsComment() == null)
        {
            dto.setIsComment(1);
        }
        if (dto.getIsRecommend() == null)
        {
            dto.setIsRecommend(0);
        }
        if (dto.getIsTop() == null)
        {
            dto.setIsTop(0);
        }
        if (dto.getIsOriginal() == null)
        {
            dto.setIsOriginal(1);
        }
        int result = articleService.insertArticle(dto);
        if (result <= 0 || dto.getId() == null)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文章发布失败");
        }
        return dto.getId();
    }

    @PutMapping("/{id}")
    public Void update(@PathVariable("id") Long id, @RequestBody ArticleInsertDTO dto)
    {
        Long userId = currentUserId();
        if (userId == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        Article existing = articleService.selectArticleById(id);
        if (existing == null || isDeleted(existing))
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        if (!userId.equals(existing.getUserId()))
        {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权编辑该文章");
        }
        dto.setId(id);
        dto.setUserId(userId);
        if (dto.getStatus() == null)
        {
            dto.setStatus(isStatus(existing.getStatus(), ArticleConstants.STATUS_PUBLISHED)
                ? ArticleConstants.STATUS_UNDER_REVIEW
                : existing.getStatus());
        }
        int result = articleService.updateArticle(dto);
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文章更新失败");
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public Void delete(@PathVariable("id") Long id)
    {
        Long userId = currentUserId();
        if (userId == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        Article existing = articleService.selectArticleById(id);
        if (existing == null || isDeleted(existing))
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        if (!userId.equals(existing.getUserId()))
        {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除该文章");
        }
        if (!isStatus(existing.getStatus(), ArticleConstants.STATUS_DRAFT)
            && !isStatus(existing.getStatus(), ArticleConstants.STATUS_UNDER_REVIEW))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "仅草稿或审核中的文章允许删除");
        }
        if (articleService.deleteArticleById(id) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文章删除失败");
        }
        return null;
    }

    @GetMapping("/search")
    public PageResult<ArticleVO> search(@RequestParam("keyword") String keyword,
                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        page = PageUtils.clampPage(page);
        pageSize = PageUtils.clampPageSize(pageSize);
        PageHelper.startPage(page, pageSize);
        Article filter = new Article();
        filter.setStatus(ArticleConstants.STATUS_PUBLISHED);
        filter.setTitle(keyword);
        List<ArticleVO> items = articleService.selectArticleVoList(filter);
        PageInfo<ArticleVO> pageInfo = new PageInfo<ArticleVO>(items);
        return BlogPageResults.of(items, pageInfo, page, pageSize);
    }

    @PostMapping("/view/{id}")
    public Void view(@PathVariable("id") Long id)
    {
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        if (blogInteractionGuard.tryViewReport(ip) == BlogInteractionGuard.Decision.RATE_LIMIT)
        {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "请求过于频繁");
        }
        Article article = articleService.selectArticleById(id);
        if (!isPublicPublishedArticle(article))
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        articleService.updateViewCount(id);
        return null;
    }

    private Long currentUserId()
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        return userId != null && userId > 0 ? userId : null;
    }

    private boolean isDeleted(Article article)
    {
        return article.getDeleted() != null && article.getDeleted() == ArticleConstants.DELETED_YES;
    }

    private boolean isPublicPublishedArticle(Article article)
    {
        return article != null && !isDeleted(article) && isStatus(article.getStatus(), ArticleConstants.STATUS_PUBLISHED);
    }

    private boolean isStatus(Long actual, long expected)
    {
        return actual != null && actual == expected;
    }
}
