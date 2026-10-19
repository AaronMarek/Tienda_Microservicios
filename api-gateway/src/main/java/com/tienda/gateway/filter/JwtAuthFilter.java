package com.tienda.gateway.filter;

import com.tienda.gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Rutas que no necesitan token
    private static final List<String> RUTAS_PUBLICAS = List.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Si es ruta pública, dejamos pasar sin revisar
        if (esRutaPublica(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return rechazar(exchange, "Token no proporcionado");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValido(token)) {
            return rechazar(exchange, "Token inválido o expirado");
        }

        return chain.filter(exchange);
    }

    private boolean esRutaPublica(String path) {
        return RUTAS_PUBLICAS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> rechazar(ServerWebExchange exchange, String motivo) {
        System.out.println("Acceso denegado: " + motivo + " - " + exchange.getRequest().getURI().getPath());
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    // Prioridad alta para que se ejecute antes que otros filtros
    @Override
    public int getOrder() {
        return -1;
    }
}