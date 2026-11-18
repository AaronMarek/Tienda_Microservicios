package com.tienda.users.service;

import com.tienda.users.dto.UserRequest;
import com.tienda.users.dto.UserResponse;
import com.tienda.users.entity.User;
import com.tienda.users.exception.ConflictException;
import com.tienda.users.exception.ResourceNotFoundException;
import com.tienda.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void crear_guardaElUsuarioConLaPasswordEncriptada() {
        when(userRepository.existsByEmail("aaron@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash-bcrypt");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            u.setRol(User.Rol.USER); // en la BD lo rellena @PrePersist, aquí no se ejecuta
            return u;
        });

        UserResponse resultado = userService.create(peticion("aaron@test.com"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("hash-bcrypt");
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getRol()).isEqualTo("USER");
    }

    @Test
    void crear_conEmailRepetido_lanzaConflicto() {
        when(userRepository.existsByEmail("aaron@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(peticion("aaron@test.com")))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void buscarPorId_siExiste_devuelveElUsuario() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "aaron@test.com")));

        UserResponse resultado = userService.findById(1L);

        assertThat(resultado.getEmail()).isEqualTo("aaron@test.com");
    }

    @Test
    void buscarPorId_siNoExiste_lanzaNoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void actualizar_conElEmailDeOtroUsuario_lanzaConflicto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "viejo@test.com")));
        when(userRepository.existsByEmail("ocupado@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, peticion("ocupado@test.com")))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void actualizar_conElMismoEmail_noComprueba_duplicados() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "aaron@test.com")));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequest dto = peticion("aaron@test.com");
        dto.setNombre("Aaron Marek");
        UserResponse resultado = userService.update(1L, dto);

        assertThat(resultado.getNombre()).isEqualTo("Aaron Marek");
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void eliminar_siNoExiste_lanzaNoEncontrado() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }

    private UserRequest peticion(String email) {
        UserRequest dto = new UserRequest();
        dto.setNombre("Aaron");
        dto.setEmail(email);
        dto.setPassword("123456");
        return dto;
    }

    private User usuario(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setNombre("Aaron");
        user.setEmail(email);
        user.setPassword("hash");
        user.setRol(User.Rol.USER);
        return user;
    }
}