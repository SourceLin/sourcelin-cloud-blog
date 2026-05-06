package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.constant.InteractionActionTypeEnum;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.domain.LikeRecord;
import com.sourcelin.blog.dto.interaction.InteractionActionDeleteCommand;
import com.sourcelin.blog.mapper.LikeRecordMapper;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.vo.interaction.InteractionActionRecordVO;
import com.sourcelin.blog.vo.interaction.InteractionOverviewVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 互动治理后台接口。
 */
@RestController
@RequestMapping("/admin/interactions")
public class AdminInteractionController
{
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    @Autowired
    private ICollectService collectService;

    @SaCheckPermission(type = "admin", value = "blog:interaction:view")
    @GetMapping("/dashboard/overview")
    public InteractionOverviewVO overview()
    {
        long likeTotal = likeRecordMapper.countActiveLikeRecords();
        long collectTotal = collectService.countActiveCollectRecords();
        long todayLikeUsers = likeRecordMapper.countTodayActiveLikeUsers();
        long todayCollectUsers = collectService.countTodayActiveCollectUsers();

        InteractionOverviewVO vo = new InteractionOverviewVO();
        vo.setLikeTotal(likeTotal);
        vo.setCollectTotal(collectTotal);
        vo.setTodayLikeUsers(todayLikeUsers);
        vo.setTodayCollectUsers(todayCollectUsers);
        vo.setTodayActionUsers(todayLikeUsers + todayCollectUsers);
        return vo;
    }

    @SaCheckPermission(type = "admin", value = "blog:interaction:list")
    @GetMapping("/actions")
    public PageResult<InteractionActionRecordVO> actions(@RequestParam(value = "actionType", defaultValue = "LIKE") String actionType,
                                                         @RequestParam(value = "userId", required = false) Long userId,
                                                         @RequestParam(value = "targetType", required = false) String targetType,
                                                         @RequestParam(value = "targetId", required = false) Long targetId,
                                                         @RequestParam(value = "state", required = false) String state)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());

        String normalizedAction = normalizeActionType(actionType);
        if (InteractionActionTypeEnum.COLLECT.name().equals(normalizedAction))
        {
            Collect collect = new Collect();
            collect.setUserId(userId);
            collect.setTargetType(targetType);
            collect.setTargetId(targetId);
            if ("active".equalsIgnoreCase(state))
            {
                collect.setDeleted(0);
            }
            else if ("inactive".equalsIgnoreCase(state))
            {
                collect.setDeleted(1);
            }
            else if (state != null && state.length() > 0 && !"all".equalsIgnoreCase(state))
            {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "状态参数不合法");
            }
            List<Collect> collects = collectService.selectCollectList(collect);
            List<InteractionActionRecordVO> records = new ArrayList<InteractionActionRecordVO>(collects.size());
            for (Collect item : collects)
            {
                records.add(toCollectRecord(item));
            }
            PageInfo<Collect> pageInfo = new PageInfo<Collect>(collects);
            return BlogPageResults.of(records, pageInfo);
        }

        LikeRecord query = new LikeRecord();
        query.setUserId(userId);
        query.setTargetType(targetType);
        query.setTargetId(targetId);
        if ("active".equalsIgnoreCase(state))
        {
            query.setDeleted(0);
        }
        else if ("inactive".equalsIgnoreCase(state))
        {
            query.setDeleted(1);
        }
        else if (state != null && state.length() > 0 && !"all".equalsIgnoreCase(state))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "状态参数不合法");
        }
        List<LikeRecord> likes = likeRecordMapper.selectLikeRecordList(query);
        List<InteractionActionRecordVO> records = new ArrayList<InteractionActionRecordVO>(likes.size());
        for (LikeRecord item : likes)
        {
            records.add(toLikeRecord(item));
        }
        PageInfo<LikeRecord> pageInfo = new PageInfo<LikeRecord>(likes);
        return BlogPageResults.of(records, pageInfo);
    }

    @SaCheckPermission(type = "admin", value = "blog:interaction:query")
    @GetMapping("/actions/detail")
    public InteractionActionRecordVO actionDetail(@RequestParam("actionType") String actionType,
                                                  @RequestParam("id") Long id)
    {
        String normalizedAction = normalizeActionType(actionType);
        if (InteractionActionTypeEnum.COLLECT.name().equals(normalizedAction))
        {
            Collect collect = collectService.selectCollectById(id);
            if (collect == null)
            {
                throw new BusinessException(ResultCode.NOT_FOUND, "互动记录不存在");
            }
            return toCollectRecord(collect);
        }
        LikeRecord likeRecord = likeRecordMapper.selectLikeRecordById(id);
        if (likeRecord == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "互动记录不存在");
        }
        return toLikeRecord(likeRecord);
    }

    @SaCheckPermission(type = "admin", value = "blog:interaction:remove")
    @Log(title = "互动行为记录", businessType = BusinessType.DELETE)
    @PostMapping("/actions/delete")
    public Void remove(@RequestBody List<InteractionActionDeleteCommand> commands)
    {
        if (CollectionUtils.isEmpty(commands))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "删除参数不能为空");
        }
        int affected = 0;
        for (InteractionActionDeleteCommand command : commands)
        {
            if (command == null || command.getIds() == null || command.getIds().length == 0)
            {
                continue;
            }
            String actionType = normalizeActionType(command.getActionType());
            if (InteractionActionTypeEnum.COLLECT.name().equals(actionType))
            {
                affected += collectService.deleteCollectByIds(command.getIds());
            }
            else
            {
                affected += likeRecordMapper.deleteLikeRecordByIds(command.getIds());
            }
        }
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }

    private String normalizeActionType(String actionType)
    {
        String normalizedAction = actionType == null
                ? InteractionActionTypeEnum.LIKE.name()
                : actionType.trim().toUpperCase(Locale.ROOT);
        if (!InteractionActionTypeEnum.LIKE.name().equals(normalizedAction)
                && !InteractionActionTypeEnum.COLLECT.name().equals(normalizedAction))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "行为类型不合法");
        }
        return normalizedAction;
    }

    private InteractionActionRecordVO toCollectRecord(Collect item)
    {
        InteractionActionRecordVO vo = new InteractionActionRecordVO();
        vo.setId(item.getId());
        vo.setActionType(InteractionActionTypeEnum.COLLECT.name());
        vo.setUserId(item.getUserId());
        vo.setTargetType(item.getTargetType());
        vo.setTargetId(item.getTargetId());
        vo.setActive(item.getDeleted() == null || item.getDeleted() == 0);
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        return vo;
    }

    private InteractionActionRecordVO toLikeRecord(LikeRecord item)
    {
        InteractionActionRecordVO vo = new InteractionActionRecordVO();
        vo.setId(item.getId());
        vo.setActionType(InteractionActionTypeEnum.LIKE.name());
        vo.setUserId(item.getUserId());
        vo.setTargetType(item.getTargetType());
        vo.setTargetId(item.getTargetId());
        vo.setActive(item.getDeleted() == null || item.getDeleted() == 0);
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        return vo;
    }
}
