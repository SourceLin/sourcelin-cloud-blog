package com.sourcelin.gateway.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import com.sourcelin.common.core.utils.ServletUtils;
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

        String msg;
        HttpStatus httpStatus = HttpStatus.OK;
        int code = 500;

        if (ex instanceof NotFoundException)
        {
            msg = "服务未找到";
        }
        else if (ex instanceof NotLoginException
            || ex instanceof SaTokenException
            || ex.getClass().getSimpleName().contains("NotLogin"))
        {
            msg = "请先登录";
            httpStatus = HttpStatus.UNAUTHORIZED;
            code = HttpStatus.UNAUTHORIZED.value();
        }
        else if (ex instanceof ResponseStatusException)
        {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            msg = responseStatusException.getMessage();
            HttpStatus status = responseStatusException.getStatus();
            if (status != null)
            {
                httpStatus = status;
                code = status.value();
            }
        }
        else
        {
            msg = "内部服务器错误";
        }

        log.error("[网关异常处理]请求路径:{},异常信息:{}", exchange.getRequest().getPath(), ex.getMessage());

        return ServletUtils.webFluxResponseWriter(response, httpStatus, msg, code);
    }
}