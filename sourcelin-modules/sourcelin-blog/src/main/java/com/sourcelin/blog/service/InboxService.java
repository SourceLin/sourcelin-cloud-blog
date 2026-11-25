package com.sourcelin.blog.service;

import com.sourcelin.blog.mapper.InboxMapper;
import com.sourcelin.blog.shared.support.RealtimeMessageSupport;
import com.sourcelin.blog.vo.InboxMessageVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InboxService {
    private final InboxMapper mapper;
    private final RealtimeMessageSupport realtime;
    private static final Set<String> CHANNELS = Set.of("interaction", "star", "follow");

    public List<InboxMessageVO> messages(Long user, String channel, Long id) {
        validateChannel(channel);
        return mapper.messages(user, channel, id);
    }

    public InboxMessageVO detail(Long user, String channel, Long id) {
        return messages(user, channel, id).stream().findFirst().orElseThrow(
            () -> new BusinessException(ResultCode.NOT_FOUND, "消息不存在"));
    }

    public long unread(Long user) {
        return mapper.unread(user);
    }

    @Transactional
    public void markRead(Long user, String channel, Long id) {
        validateChannel(channel);
        if (id != null) detail(user, channel, id);
        if (mapper.read(user, channel, id) > 0) realtime.invalidate(user);
    }

    public void commentChanged(Long id) {
        mapper.recordComment(id);
        mapper.recipients("interaction", "article", id).forEach(realtime::invalidate);
    }

    public void likeChanged(Long actor, String type, Long target, boolean active) {
        Long owner = mapper.likeRecipient(type, target);
        if (active) activate(owner, actor, "star", type, target);
        else if (mapper.deactivate(actor, "star", type, target) > 0) {
            if (owner != null) realtime.invalidate(owner);
        }
    }

    public void followChanged(Long actor, Long target, boolean active) {
        if (active) activate(target, actor, "follow", "user", target);
        else if (mapper.deactivate(actor, "follow", "user", target) > 0) realtime.invalidate(target);
    }

    private void activate(Long owner, Long actor, String channel, String type, Long target) {
        if (owner != null && !owner.equals(actor)) {
            mapper.activate(owner, actor, channel, type, target);
            realtime.invalidate(owner);
        }
    }

    private void validateChannel(String channel) {
        if (!CHANNELS.contains(channel)) throw new BusinessException(ResultCode.BAD_REQUEST, "无效消息频道");
    }
}
