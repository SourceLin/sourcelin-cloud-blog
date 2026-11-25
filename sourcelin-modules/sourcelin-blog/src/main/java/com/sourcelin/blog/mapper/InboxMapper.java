package com.sourcelin.blog.mapper;

import com.sourcelin.blog.vo.InboxMessageVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface InboxMapper {
    List<InboxMessageVO> messages(@Param("user") Long user, @Param("channel") String channel, @Param("id") Long id);
    long unread(@Param("user") Long user);
    int read(@Param("user") Long user, @Param("channel") String channel, @Param("id") Long id);
    int recordComment(@Param("id") Long id);
    Long likeRecipient(@Param("type") String type, @Param("target") Long target);
    int activate(@Param("user") Long user, @Param("actor") Long actor, @Param("channel") String channel,
                 @Param("type") String type, @Param("target") Long target);
    int deactivate(@Param("actor") Long actor, @Param("channel") String channel,
                   @Param("type") String type, @Param("target") Long target);
    List<Long> recipients(@Param("channel") String channel, @Param("type") String type, @Param("source") Long source);
}
