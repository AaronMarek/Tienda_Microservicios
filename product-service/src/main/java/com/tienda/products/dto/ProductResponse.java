package com.tienda.products.dto;

import com.tienda.products.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String categoria;

    public static ProductResponse from(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.id = product.getId();
        dto.nombre = product.getNombre();
        dto.descripcion = product.getDescripcion();
        dto.precio = product.getPrecio();
        dto.stock = product.getStock();
        dto.categoria = product.getCategoria();
        return dto;
    }
}