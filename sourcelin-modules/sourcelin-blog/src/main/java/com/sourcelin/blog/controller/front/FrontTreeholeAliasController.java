package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Treehole;
import com.sourcelin.blog.service.ITreeholeService;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front/treehole")
public class FrontTreeholeAliasController
{
    @Autowired
    private ITreeholeService treeholeService;

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
        PageInfo<Treehole> pageInfo = new PageInfo<Treehole>(list);
        return BlogPageResults.of(list, pageInfo, pageDomain.getPage(), pageDomain.getPageSize());
    }
}
