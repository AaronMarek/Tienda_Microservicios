package com.tienda.orders.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign busca "user-service" en Eureka automáticamente
@FeignClient(name = "user-service")
public interface UserClient {

    // Solo necesitamos saber si el usuario existe; no importa el body
    @GetMapping("/api/users/{id}")
    Object getUserById(@PathVariable Long id);
}