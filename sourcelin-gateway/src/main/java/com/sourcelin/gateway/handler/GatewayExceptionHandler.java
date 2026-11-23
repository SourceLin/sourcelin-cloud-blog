package com.sourcelin.gateway.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关统一异常处理
 *
 * @author sourcelin
 */
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex)
    {
        ServerHttpResponse response = exchange.getResponse();

        if (exchange.getResponse().isCommitted())
        {
            return Mono.error(ex);
        }

        HttpStatusCode httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ResultCode code = ResultCode.SYSTEM_ERROR;

        if (ex instanceof NotFoundException)
        {
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            code = ResultCode.REMOTE_SERVICE_ERROR;
        }
        else if (ex instanceof NotLoginException
            || ex instanceof SaTokenException
            || ex.getClass().getSimpleName().contains("NotLogin"))
        {
            httpStatus = HttpStatus.UNAUTHORIZED;
            code = ResultCode.UNAUTHORIZED;
        }
        else if (ex instanceof ResponseStatusException)
        {
            httpStatus = ((ResponseStatusException) ex).getStatusCode();
            switch (httpStatus.value())
            {
                case 404: code = ResultCode.NOT_FOUND; break;
                case 401: code = ResultCode.UNAUTHORIZED; break;
                case 403: code = ResultCode.FORBIDDEN; break;
                case 429: code = ResultCode.TOO_MANY_REQUESTS; break;
                default: if (httpStatus.is4xxClientError()) code = ResultCode.BAD_REQUEST;
            }
        }

        if (httpStatus.is5xxServerError())
        {
            log.warn("网关转发失败，路径={}，异常类型={}", exchange.getRequest().getPath(), ex.getClass().getSimpleName());
        }

        ApiResponse<Object> body = ApiResponse.fail(code);
        String header = "X-Request-Id";
        String requestId = response.getHeaders().getFirst(header);
        if (requestId == null)
        {
            requestId = exchange.getRequest().getHeaders().getFirst(header);
        }
        if (requestId != null && requestId.matches("[A-Za-z0-9._-]{1,64}"))
        {
            body.setRequestId(requestId);
        }
        response.setStatusCode(httpStatus);
        response.getHeaders().set(header, body.getRequestId());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(
            JSON.toJSONBytes(body, JSONWriter.Feature.WriteNulls))));
    }
}
