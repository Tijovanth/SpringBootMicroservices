package com.eazybytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class ResponseTraceFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        String correlationId = filterUtility.getCorrelationId(httpHeaders);
        if(exchange.getResponse().getHeaders().containsKey(filterUtility.CORRELATION_ID)){
            logger.debug("Updated the correlation id to the outbound headers: {}", correlationId);
            exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId);
        }
        return chain.filter(exchange);
    }
}
