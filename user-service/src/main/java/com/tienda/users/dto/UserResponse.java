package com.tienda.users.dto;

import com.tienda.users.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String nombre;
    private String email;
    private LocalDateTime fechaAlta;
    private String rol;

    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();
        dto.id = user.getId();
        dto.nombre = user.getNombre();
        dto.email = user.getEmail();
        dto.fechaAlta = user.getFechaAlta();
        dto.rol = user.getRol().name();
        return dto;
    }
}
