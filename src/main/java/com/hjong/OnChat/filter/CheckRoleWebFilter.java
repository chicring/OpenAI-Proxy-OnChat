package com.hjong.OnChat.filter;

import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.filter.annotation.CheckRole;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.rmi.ServerException;
import java.util.Objects;

import static com.hjong.OnChat.entity.Constants.ADMIN_ROLE;
import static com.hjong.OnChat.entity.Constants.ROLE;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/2
 **/

@Slf4j
@Order(2)
@Component
public class CheckRoleWebFilter implements WebFilter {

    @Resource
    RequestMappingHandlerMapping requestMapping;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return requestMapping.getHandler(exchange).switchIfEmpty(chain.filter(exchange))
                .flatMap(handler -> {

                            if (handler instanceof HandlerMethod methodHandle) {
                                CheckRole permission = methodHandle.getMethodAnnotation(CheckRole.class);

                                if (Objects.isNull(permission)) {

                                    permission = AnnotationUtils.findAnnotation(methodHandle.getBeanType(), CheckRole.class);
                                }

                                if (Objects.nonNull(permission)) {
                                    String role = exchange.getRequest().getHeaders().getFirst(ROLE);

                                    if(role == null || !role.equals(ADMIN_ROLE)) {
                                        log.error("用户权限不足");
                                       return Mono.error(new ServiceException(ServiceExceptionEnum.PERMISSION_DENIED));
                                    }
                                }
                            }
                            return chain.filter(exchange);
                        }
                );
    }
}
