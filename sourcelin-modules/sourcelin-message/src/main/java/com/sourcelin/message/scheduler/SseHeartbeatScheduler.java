package com.sourcelin.message.scheduler;
import com.sourcelin.message.session.SseSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SseHeartbeatScheduler {
 private final SseSessionManager sessions;
 @Scheduled(fixedDelayString="${sse.heartbeat-interval:20000}")
 public void sendHeartbeat(){sessions.heartbeat();}
}
