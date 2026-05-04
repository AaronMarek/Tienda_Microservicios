package com.tienda.orders.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotEmpty(message = "El pedido debe tener al menos una línea")
    private List<OrderLineRequest> lineas;
}