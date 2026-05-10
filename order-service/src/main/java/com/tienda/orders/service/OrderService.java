package com.tienda.orders.service;

import com.tienda.orders.client.ProductClient;
import com.tienda.orders.client.UserClient;
import com.tienda.orders.dto.Product;
import com.tienda.orders.dto.OrderLineRequest;
import com.tienda.orders.dto.OrderRequest;
import com.tienda.orders.dto.OrderResponse;
import com.tienda.orders.entity.Order;
import com.tienda.orders.entity.OrderLine;
import com.tienda.orders.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    public List<OrderResponse> findByUsuario(Long idUsuario) {
        return orderRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));
        return OrderResponse.from(order);
    }

    public OrderResponse create(OrderRequest dto) {
        // Validar que el usuario existe en user-service
        try {
            userClient.getUserById(dto.getIdUsuario());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Usuario no encontrado: " + dto.getIdUsuario());
        }

        // Construir las líneas validando cada producto
        List<OrderLine> lineas = new ArrayList<>();
        for (OrderLineRequest lineaDto : dto.getLineas()) {
            Product producto;
            try {
                producto = productClient.getProductById(lineaDto.getIdProducto());
            } catch (FeignException.NotFound e) {
                throw new RuntimeException("Producto no encontrado: " + lineaDto.getIdProducto());
            }

            if (producto.getStock() < lineaDto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            OrderLine linea = new OrderLine();
            linea.setIdProducto(producto.getId());
            linea.setCantidad(lineaDto.getCantidad());
            // Guardamos el precio actual del producto en el pedido
            linea.setPrecioUnitario(producto.getPrecio());
            lineas.add(linea);
        }

        Order order = new Order();
        order.setIdUsuario(dto.getIdUsuario());
        // Asignamos el pedido a cada línea para que JPA gestione la relación
        lineas.forEach(l -> l.setOrder(order));
        order.setLineas(lineas);

        return OrderResponse.from(orderRepository.save(order));
    }

    public OrderResponse cambiarEstado(Long id, String nuevoEstado) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));

        try {
            order.setEstado(Order.Estado.valueOf(nuevoEstado.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + nuevoEstado);
        }

        return OrderResponse.from(orderRepository.save(order));
    }
}