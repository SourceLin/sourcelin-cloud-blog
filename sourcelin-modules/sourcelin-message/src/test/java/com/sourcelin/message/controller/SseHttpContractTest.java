package com.sourcelin.message.controller;
import com.fasterxml.jackson.databind.*;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.advice.*;
import com.sourcelin.common.core.web.filter.TraceIdFilter;
import com.sourcelin.message.service.SseTicketService;
import com.sourcelin.message.session.SseSessionManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import java.net.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class SseHttpContractTest {
 @Configuration @EnableWebMvc static class HttpConfig {
  @Bean ObjectMapper objectMapper(){return new ObjectMapper().findAndRegisterModules();}
  @Bean ApiResponseBodyAdvice responseAdvice(ObjectMapper mapper){return new ApiResponseBodyAdvice(mapper);}
  @Bean GlobalMvcExceptionHandler exceptionHandler(){return new GlobalMvcExceptionHandler();}
  @Bean SseTicketService tickets(){SseTicketService service=mock(SseTicketService.class);when(service.consume(anyString())).thenThrow(new BusinessException(ResultCode.UNAUTHORIZED));doReturn("blog:1").when(service).consume("valid");return service;}
  @Bean SseSessionManager sessions(){SseSessionManager service=mock(SseSessionManager.class);when(service.addSession(anyString(),any())).thenReturn("connection");return service;}
  @Bean SseConnectController controller(SseTicketService tickets,SseSessionManager sessions){return new SseConnectController(tickets,sessions);}
 }
 @Test void invalidTicketReturnsStandardErrorAndValidTicketStreamsImmediately() throws Exception {
  AnnotationConfigWebApplicationContext context=new AnnotationConfigWebApplicationContext();context.register(HttpConfig.class);
  TomcatServletWebServerFactory factory=new TomcatServletWebServerFactory(0);
  WebServer server=factory.getWebServer(servletContext -> {
   context.setServletContext(servletContext);context.refresh();
   jakarta.servlet.ServletRegistration.Dynamic registration=servletContext.addServlet("dispatcher",new DispatcherServlet(context));registration.setAsyncSupported(true);registration.addMapping("/");
   jakarta.servlet.FilterRegistration.Dynamic filter=servletContext.addFilter("trace",new TraceIdFilter());filter.setAsyncSupported(true);filter.addMappingForUrlPatterns(null,false,"/*");
  });
  server.start();
  try {
   URL base=new URL("http://127.0.0.1:"+server.getPort()+"/stream?ticket=invalid");
   HttpURLConnection invalid=(HttpURLConnection)base.openConnection();invalid.setReadTimeout(3000);
   assertEquals(401,invalid.getResponseCode());
   JsonNode json=new ObjectMapper().readTree(invalid.getErrorStream());assertEquals(40100,json.get("code").asInt());assertTrue(json.has("message"));assertTrue(json.has("data"));assertTrue(json.has("requestId"));assertTrue(json.has("timestamp"));invalid.disconnect();
   HttpURLConnection valid=(HttpURLConnection)new URL("http://127.0.0.1:"+server.getPort()+"/stream?ticket=valid").openConnection();valid.setReadTimeout(3000);
   assertEquals(200,valid.getResponseCode());assertTrue(valid.getContentType().startsWith("text/event-stream"));
   try(BufferedReader reader=new BufferedReader(new InputStreamReader(valid.getInputStream(),java.nio.charset.StandardCharsets.UTF_8))) {assertEquals("event:connect",reader.readLine());assertEquals("data:connected",reader.readLine());}
   valid.disconnect();
  } finally {server.stop();context.close();}
 }
}
