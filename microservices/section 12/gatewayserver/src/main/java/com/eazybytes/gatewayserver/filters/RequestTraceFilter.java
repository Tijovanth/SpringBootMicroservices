package com.eazybytes.gatewayserver.filters;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;

@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        if(isCorrelationIdPresent(httpHeaders)){
            logger.debug("eazyBank-correlation-id found in RequestTraceFilter : {}",
                    filterUtility.getCorrelationId(httpHeaders));
        } else{
            String correlationID = generateCorrelationId();
            filterUtility.setCorrelationId(exchange, correlationID);
            logger.debug("eazyBank-correlation-id generated in RequestTraceFilter : {}", correlationID);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders httpHeaders){
        if(filterUtility.getCorrelationId(httpHeaders) != null){
            return true;
        }
        return false;
    }

    private String generateCorrelationId(){
        return java.util.UUID.randomUUID().toString();
    }
}
