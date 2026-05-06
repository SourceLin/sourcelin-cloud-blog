package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.dto.ArticleInsertDTO;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICategoryService;
import com.sourcelin.blog.service.ITagService;
import com.sourcelin.blog.shared.support.BlogPageResults;
import com.sourcelin.blog.vo.AdminArticleDetailVO;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客文章Controller
 * 
 * @author sourcelin
 * @date 2023-11-09
 */
@RestController
@RequestMapping("/admin/article")
public class ArticleController
{
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ITagService tagService;

    /**
     * 查询博客文章列表
     */
    @SaCheckPermission(type = "admin", value = "blog:article:list")
    @GetMapping("/list")
    public PageResult<ArticleVO> list(Article article)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize());
        List<ArticleVO> list = articleService.selectArticleVoList(article);
        PageInfo<ArticleVO> pageInfo = new PageInfo<ArticleVO>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    /**
     * 导出博客文章列表
     */
    @SaCheckPermission(type = "admin", value = "blog:article:export")
    @Log(title = "博客文章", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Article article)
    {
        List<Article> list = articleService.selectArticleList(article);
        ExcelUtil<Article> util = new ExcelUtil<Article>(Article.class);
        util.exportExcel(response, list, "博客文章数据");
    }

    /**
     * 获取博客文章详细信息
     */
    @SaCheckPermission(type = "admin", value = "blog:article:query")
    @GetMapping(value = "/{id}")
    public AdminArticleDetailVO getInfo(@PathVariable("id") Long id)
    {
        Article article = articleService.selectArticleById(id);
        if (article == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        AdminArticleDetailVO detail = new AdminArticleDetailVO();
        BeanUtils.copyProperties(article, detail);
        if (StringUtils.isNotNull(id) && article.getTags() != null)
        {
            detail.setTagIds(article.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
        }
        return detail;
    }

    /**
     * 新增博客文章
     */
    @SaCheckPermission(type = "admin", value = "blog:article:add")
    @Log(title = "博客文章", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody ArticleInsertDTO dto)
    {
        if (articleService.insertArticle(dto) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增文章失败");
        }
        return null;
    }

    /**
     * 修改博客文章。
     * <p>字段 {@code tagIds} 语义见 {@link ArticleInsertDTO#getTagIds()}：null 表示不调整标签，{@code []} 表示清空标签。</p>
     */
    @SaCheckPermission(type = "admin", value = "blog:article:edit")
    @Log(title = "博客文章", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody ArticleInsertDTO articleInsertDTO)
    {
        if (articleService.updateArticle(articleInsertDTO) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改文章失败");
        }
        return null;
    }

    /**
     * 删除博客文章
     */
    @SaCheckPermission(type = "admin", value = "blog:article:remove")
    @Log(title = "博客文章", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (articleService.deleteArticleByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "删除文章失败");
        }
        return null;
    }
}
