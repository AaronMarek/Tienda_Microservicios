package com.tienda.products.repository;

import com.tienda.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoria(String categoria);

    // Para filtrar productos con stock disponible
    List<Product> findByStockGreaterThan(int minStock);
}
