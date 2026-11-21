package com.sourcelin.message.controller;
import com.sourcelin.message.service.SseTicketService;
import com.sourcelin.message.session.SseSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
@RestController
@RequiredArgsConstructor
public class SseConnectController {
 private final SseTicketService ticketService;
 private final SseSessionManager sessions;
 @Value("${sse.connection-timeout:3600000}") private long timeout;
 @GetMapping(value="/stream",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
 public SseEmitter stream(@RequestParam String ticket) throws IOException {
  String principal=ticketService.consume(ticket);
  SseEmitter emitter=new SseEmitter(timeout);
  String connectionId=sessions.addSession(principal,emitter);
  Runnable close=() -> sessions.removeSession(principal,connectionId);
  emitter.onCompletion(close);emitter.onTimeout(close);emitter.onError(error -> close.run());
  try{emitter.send(SseEmitter.event().name("connect").data("connected"));}
  catch(IOException error){close.run();throw error;}
  return emitter;
 }
}
