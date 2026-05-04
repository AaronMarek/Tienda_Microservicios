package com.tienda.orders.dto;


import com.tienda.orders.entity.Order;
import com.tienda.orders.entity.OrderLine;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Long id;
    private Long idUsuario;
    private String estado;
    private LocalDateTime fechaCreacion;
    private List<LineaDto> lineas;
    private BigDecimal total;

    public static OrderResponse from(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.id = order.getId();
        dto.idUsuario = order.getIdUsuario();
        dto.estado = order.getEstado().name();
        dto.fechaCreacion = order.getFechaCreacion();
        dto.lineas = order.getLineas().stream().map(LineaDto::from).toList();
        dto.total = dto.lineas.stream()
                .map(l -> l.precioUnitario.multiply(BigDecimal.valueOf(l.cantidad)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return dto;
    }

    @Getter
    @Setter
    public static class LineaDto {
        private Long idProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;

        public static LineaDto from(OrderLine line) {
            LineaDto dto = new LineaDto();
            dto.idProducto = line.getIdProducto();
            dto.cantidad = line.getCantidad();
            dto.precioUnitario = line.getPrecioUnitario();
            return dto;
        }
    }
}
