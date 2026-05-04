package com.tienda.orders.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderLineRequest {

    @NotNull(message = "El id del producto es obligatorio")
    private Long idProducto;

    @NotNull
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;
}