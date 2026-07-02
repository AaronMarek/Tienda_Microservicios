package com.tienda.orders.controller;

import com.tienda.orders.dto.OrderRequest;
import com.tienda.orders.dto.OrderResponse;
import com.tienda.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/usuario/{idUsuario}")
    public List<OrderResponse> getByUsuario(@PathVariable Long idUsuario) {
        return orderService.findByUsuario(idUsuario);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(dto));
    }

    @PatchMapping("/{id}/estado")
    public OrderResponse cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return orderService.cambiarEstado(id, estado);
    }
}