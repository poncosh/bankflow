package com.multimatics.bankflow.gateway;
import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {
    static final String HEADER = "X-Correlation-ID";
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String incoming = exchange.getRequest().getHeaders().getFirst(HEADER);
        String id = valid(incoming) ? incoming : UUID.randomUUID().toString();
        exchange.getResponse().getHeaders().set(HEADER, id);
        ServerWebExchange enriched = exchange.mutate()
                .request(r -> r.headers(h -> h.set(HEADER, id)))
                .build();
        return chain.filter(enriched);
    }
    private boolean valid(String v) {
        return v != null && v.matches("[A-Za-z0-9._-]{8,64}");
    }
    public int getOrder() { return -100; }
}
