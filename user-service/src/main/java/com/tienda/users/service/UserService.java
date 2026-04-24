package com.tienda.users.service;

import com.tienda.users.dto.UserRequest;
import com.tienda.users.dto.UserResponse;
import com.tienda.users.entity.User;
import com.tienda.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        return UserResponse.from(user);
    }

    public UserResponse create(UserRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        User user = new User();
        user.setNombre(dto.getNombre());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse update(Long id, UserRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        user.setNombre(dto.getNombre());
        user.setEmail(dto.getEmail());

        return UserResponse.from(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado: " + id);
        }
        userRepository.deleteById(id);
    }
}