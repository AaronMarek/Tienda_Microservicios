package com.tienda.users.service;

import com.tienda.users.dto.UserRequest;
import com.tienda.users.dto.UserResponse;
import com.tienda.users.entity.User;
import com.tienda.users.exception.ConflictException;
import com.tienda.users.exception.ResourceNotFoundException;
import com.tienda.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    public UserResponse create(UserRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Ya existe un usuario con ese email");
        }

        User user = new User();
        user.setNombre(dto.getNombre());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User saved = userRepository.save(user);
        log.info("Usuario creado: id={}", saved.getId());
        return UserResponse.from(saved);
    }

    public UserResponse update(Long id, UserRequest dto) {
        User user = findOrThrow(id);

        // Solo comprobamos el email si ha cambiado de verdad
        boolean emailChanged = !user.getEmail().equalsIgnoreCase(dto.getEmail());
        if (emailChanged && userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Ya existe un usuario con ese email");
        }

        user.setNombre(dto.getNombre());
        user.setEmail(dto.getEmail());

        return UserResponse.from(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado: " + id);
        }
        userRepository.deleteById(id);
        log.info("Usuario eliminado: id={}", id);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }
}