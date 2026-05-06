package com.sourcelin.blog.vo;

import com.sourcelin.blog.constant.ModerationDecisionEnum;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * 机审管道输出
 */
@Value
@Builder
public class ModerationOutcome
{
    ModerationDecisionEnum decision;
    /** b_comment.status：0 待审 1 通过 2 拒绝 */
    int status;
    List<String> hitKeywords;
    /** 为 true 时同事务写入 b_comment_moderation（各来源评论机审统一）。 */
    boolean persistModeration;
}
