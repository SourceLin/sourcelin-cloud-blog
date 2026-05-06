package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Comment;
import com.sourcelin.blog.domain.Say;
import com.sourcelin.blog.domain.Treehole;
import com.sourcelin.blog.dto.CommentDTO;
import com.sourcelin.blog.service.BlogInteractionGuard;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICommentService;
import com.sourcelin.blog.service.ISayService;
import com.sourcelin.blog.service.ITreeholeService;
import com.sourcelin.blog.service.ModerationPipeline;
import com.sourcelin.blog.support.BlogRichTextSanitizer;
import com.sourcelin.blog.vo.CommentVO;
import com.sourcelin.blog.vo.ModerationOutcome;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 前台评论接口。
 */
@RestController
@RequestMapping("/front/comments")
public class FrontCommentController
{
    @Autowired
    private ICommentService commentService;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ISayService sayService;
    @Autowired
    private ITreeholeService treeholeService;
    @Autowired
    private BlogInteractionGuard blogInteractionGuard;
    @Autowired
    private BlogLoginAccessor blogLoginAccessor;
    @Autowired
    private ModerationPipeline moderationPipeline;
    @Autowired
    private BlogRichTextSanitizer blogRichTextSanitizer;

    @GetMapping
    public PageResult<CommentVO> list(CommentDTO dto)
    {
        applyFrontListQueryForViewer(dto, blogLoginAccessor.getCurrentUserId());
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<CommentVO> list = commentService.selectFrontCommentList(dto);
        PageInfo<CommentVO> pageInfo = new PageInfo<CommentVO>(list);
        return BlogPageResults.of(list, pageInfo, pageDomain.getPage(), pageDomain.getPageSize());
    }

    /**
     * 前台列表：与 {@code selectFrontCommentList} 配套。
     */
    void applyFrontListQueryForViewer(CommentDTO dto, Long viewerUserId)
    {
        dto.setFrontMergeOwn(true);
        dto.setViewerUserId(viewerUserId);
        dto.setStatus(null);
    }

    @PostMapping
    public Void add(@RequestBody Comment comment, HttpServletRequest request)
    {
        if (comment.getArticleId() == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "评论目标不能为空");
        }
        // BUG-002: 空内容校验
        if (comment.getContent() == null || comment.getContent().trim().isEmpty())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "评论内容不能为空");
        }
        // BUG-003: XSS 防护——对评论内容执行纯文本清洗，移除所有 HTML 标签
        comment.setContent(blogRichTextSanitizer.sanitizePlainText(comment.getContent()));
        if (comment.getContent() == null || comment.getContent().trim().isEmpty())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "评论内容不能为空");
        }
        String source = normalizeSource(comment.getSource());
        Long userId = blogLoginAccessor.getCurrentUserId();
        boolean anonymousTreeholeComment = isAnonymousTreeholeComment(source, userId);
        if (!anonymousTreeholeComment && (userId == null || userId <= 0))
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        BlogInteractionGuard.Decision rate = anonymousTreeholeComment
            ? blogInteractionGuard.tryAnonymousCommentAdd(IpUtils.getIpAddr())
            : blogInteractionGuard.tryCommentAdd(userId);
        if (rate == BlogInteractionGuard.Decision.RATE_LIMIT)
        {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "评论过于频繁，请稍后再试");
        }

        validateTarget(comment.getArticleId(), source);

        comment.setUserId(anonymousTreeholeComment ? null : userId);
        if (comment.getParentCommentId() != null && comment.getParentUserId() == null)
        {
            CommentVO parent = commentService.selectCommentById(comment.getParentCommentId());
            if (parent != null)
            {
                comment.setParentUserId(parent.getUserId());
            }
        }
        if (comment.getDeleted() == null)
        {
            comment.setDeleted(0);
        }
        if (comment.getLikeCount() == null)
        {
            comment.setLikeCount(0L);
        }
        comment.setSource(source);
        // 填充IP地址与来源信息
        String ipAddress = IpUtils.getIpAddr(request);
        comment.setIpAddress(ipAddress);
        comment.setIpSource(resolveIpLocation(ipAddress));
        // 填充浏览器与系统信息
        String ua = request.getHeader("User-Agent");
        comment.setBrowser(resolveBrowser(ua));
        comment.setBrowserVersion("");
        comment.setSystem(resolveOs(ua));
        comment.setSystemVersion("");

        int result;
        if (needsContentModeration(source))
        {
            comment.setStatus(null);
            ModerationOutcome outcome = moderationPipeline.moderate(comment.getContent());
            result = commentService.insertCommentWithModeration(comment, outcome);
            if (result > 0 && outcome.getStatus() == 1)
            {
                syncTargetCommentCount(comment.getArticleId(), source);
            }
        }
        else
        {
            if (comment.getStatus() == null)
            {
                comment.setStatus(1);
            }
            result = commentService.insertComment(comment);
            if (result > 0 && Integer.valueOf(1).equals(comment.getStatus()))
            {
                syncTargetCommentCount(comment.getArticleId(), source);
            }
        }
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "评论失败");
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public Void delete(@PathVariable("id") Long id)
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        CommentVO comment = commentService.selectCommentById(id);
        if (comment == null || (comment.getDeleted() != null && comment.getDeleted() != 0))
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "评论不存在");
        }
        if (comment.getUserId() == null || !userId.equals(comment.getUserId()))
        {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除该评论");
        }
        int result = commentService.deleteCommentById(id);
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除评论失败");
        }
        if (comment.getStatus() != null && comment.getStatus() == 1)
        {
            syncTargetCommentCountAfterDelete(comment.getArticleId(), normalizeSource(comment.getSource()));
        }
        return null;
    }

    @PostMapping("/like/{id}")
    public Void like(@PathVariable("id") Long id)
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        boolean loggedIn = userId != null && userId > 0;

        CommentVO comment = commentService.selectCommentById(id);
        if (comment == null || comment.getDeleted() != null && comment.getDeleted() != 0)
        {
            throw new BusinessException(loggedIn ? ResultCode.NOT_FOUND : ResultCode.UNAUTHORIZED, loggedIn ? "评论不存在" : "请先登录");
        }

        String source = normalizeSource(comment.getSource());
        boolean anonymousTreeholeComment = isTreeholeSource(source) && !loggedIn;
        if (!anonymousTreeholeComment && !loggedIn)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        if (comment.getStatus() == null || comment.getStatus() != 1)
        {
            if (!loggedIn)
            {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
            }
            boolean viewerIsAuthor = userId != null && userId > 0
                && comment.getUserId() != null && userId.equals(comment.getUserId());
            if (viewerIsAuthor)
            {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "评论未通过审核，暂不支持互动");
            }
            throw new BusinessException(ResultCode.NOT_FOUND, "评论不存在");
        }
        BlogInteractionGuard.Decision decision = anonymousTreeholeComment
            ? blogInteractionGuard.tryAnonymousCommentLike(IpUtils.getIpAddr(), id)
            : blogInteractionGuard.tryCommentLike(userId, id);
        if (decision == BlogInteractionGuard.Decision.RATE_LIMIT)
        {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "操作过于频繁，请稍后再试");
        }
        if (decision == BlogInteractionGuard.Decision.DUPLICATE)
        {
            throw new BusinessException(ResultCode.CONFLICT, "您已经点过赞了");
        }
        int result = commentService.updateLikeCount(id);
        if (result <= 0)
        {
            if (anonymousTreeholeComment)
            {
                blogInteractionGuard.rollbackAnonymousCommentLike(IpUtils.getIpAddr(), id);
            }
            else
            {
                blogInteractionGuard.rollbackCommentLike(userId, id);
            }
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "点赞失败");
        }
        return null;
    }

    private boolean isAnonymousTreeholeComment(String source, Long userId)
    {
        return isTreeholeSource(source) && (userId == null || userId <= 0);
    }

    private boolean isTreeholeSource(String source)
    {
        return "treehole".equals(source);
    }

    /** article / say / treehole 评论均走机审。 */
    private boolean needsContentModeration(String source)
    {
        return "article".equals(source) || "say".equals(source) || "treehole".equals(source);
    }

    private String normalizeSource(String source)
    {
        if (source == null || source.trim().isEmpty())
        {
            return "article";
        }
        return source.trim().toLowerCase();
    }

    private void validateTarget(Long targetId, String source)
    {
        if ("say".equals(source))
        {
            Say say = sayService.selectSayById(targetId);
            if (say == null || (say.getDeleted() != null && say.getDeleted() != 0L))
            {
                throw new BusinessException(ResultCode.NOT_FOUND, "说说不存在");
            }
            return;
        }
        if ("treehole".equals(source))
        {
            Treehole treehole = treeholeService.selectTreeholeById(targetId);
            if (treehole == null || (treehole.getDeleted() != null && treehole.getDeleted() != 0L))
            {
                throw new BusinessException(ResultCode.NOT_FOUND, "树洞不存在");
            }
            return;
        }

        Article article = articleService.selectArticleById(targetId);
        if (article == null || (article.getDeleted() != null && article.getDeleted() == ArticleConstants.DELETED_YES))
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        if (article.getStatus() == null || article.getStatus() != ArticleConstants.STATUS_PUBLISHED)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "文章未发布，无法评论");
        }
        if (article.getIsComment() != null && article.getIsComment() == 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "该文章已关闭评论");
        }
    }

    private void syncTargetCommentCount(Long targetId, String source)
    {
        if ("say".equals(source))
        {
            sayService.updateCommentCount(targetId);
            return;
        }
        if ("treehole".equals(source))
        {
            treeholeService.updateCommentCount(targetId);
        }
    }

    private void syncTargetCommentCountAfterDelete(Long targetId, String source)
    {
        if (targetId == null)
        {
            return;
        }
        if ("say".equals(source))
        {
            sayService.updateCommentCountDecrease(targetId);
            return;
        }
        if ("treehole".equals(source))
        {
            treeholeService.updateCommentCountDecrease(targetId);
        }
    }

    /** 解析浏览器名称 */
    private String resolveBrowser(String userAgent)
    {
        if (com.sourcelin.common.core.utils.StringUtils.isEmpty(userAgent))
        {
            return "未知";
        }
        if (userAgent.contains("Edg/")) { return "Edge"; }
        if (userAgent.contains("OPR/") || userAgent.contains("Opera")) { return "Opera"; }
        if (userAgent.contains("Chrome/")) { return "Chrome"; }
        if (userAgent.contains("Firefox/")) { return "Firefox"; }
        if (userAgent.contains("Safari/")) { return "Safari"; }
        if (userAgent.contains("MSIE") || userAgent.contains("Trident/")) { return "IE"; }
        return "其他";
    }

    /** 解析操作系统 */
    private String resolveOs(String userAgent)
    {
        if (com.sourcelin.common.core.utils.StringUtils.isEmpty(userAgent))
        {
            return "未知";
        }
        if (userAgent.contains("Windows")) { return "Windows"; }
        if (userAgent.contains("Android")) { return "Android"; }
        if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) { return "iOS"; }
        if (userAgent.contains("Mac OS") || userAgent.contains("Macintosh")) { return "Mac OS"; }
        if (userAgent.contains("Linux")) { return "Linux"; }
        return "其他";
    }

    /** 解析IP归属地 */
    private String resolveIpLocation(String ipaddr)
    {
        if (com.sourcelin.common.core.utils.StringUtils.isEmpty(ipaddr)
            || "unknown".equalsIgnoreCase(ipaddr) || "null".equalsIgnoreCase(ipaddr))
        {
            return "未知";
        }
        if (IpUtils.internalIp(ipaddr))
        {
            return "内网IP";
        }
        return "外网IP";
    }
}
