package com.tienda.orders.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// DTO mínimo para leer la respuesta del product-service
@Getter
@Setter
public class Product {

    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
}