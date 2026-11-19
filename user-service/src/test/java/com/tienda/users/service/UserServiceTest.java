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
    void create_savesUserWithEncodedPassword() {
        when(userRepository.existsByEmail("aaron@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash-bcrypt");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            u.setRol(User.Rol.USER); // en la BD lo rellena @PrePersist, aquí no se ejecuta
            return u;
        });

        UserResponse result = userService.create(request("aaron@test.com"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("hash-bcrypt");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRol()).isEqualTo("USER");
    }

    @Test
    void create_withExistingEmail_throwsConflict() {
        when(userRepository.existsByEmail("aaron@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request("aaron@test.com")))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void findById_whenUserExists_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "aaron@test.com")));

        UserResponse result = userService.findById(1L);

        assertThat(result.getEmail()).isEqualTo("aaron@test.com");
    }

    @Test
    void findById_whenUserDoesNotExist_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_withEmailFromAnotherUser_throwsConflict() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "viejo@test.com")));
        when(userRepository.existsByEmail("ocupado@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, request("ocupado@test.com")))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void update_withSameEmail_skipsDuplicateCheck() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "aaron@test.com")));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequest dto = request("aaron@test.com");
        dto.setNombre("Aaron Marek");
        UserResponse result = userService.update(1L, dto);

        assertThat(result.getNombre()).isEqualTo("Aaron Marek");
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void delete_whenUserDoesNotExist_throwsNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }

    private UserRequest request(String email) {
        UserRequest dto = new UserRequest();
        dto.setNombre("Aaron");
        dto.setEmail(email);
        dto.setPassword("123456");
        return dto;
    }

    private User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setNombre("Aaron");
        user.setEmail(email);
        user.setPassword("hash");
        user.setRol(User.Rol.USER);
        return user;
    }
}