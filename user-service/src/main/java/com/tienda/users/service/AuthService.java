package com.tienda.users.service;


import com.tienda.users.dto.AuthRequest;
import com.tienda.users.dto.AuthResponse;
import com.tienda.users.dto.UserRequest;
import com.tienda.users.dto.UserResponse;
import com.tienda.users.entity.User;
import com.tienda.users.repository.UserRepository;
import com.tienda.users.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthResponse register(UserRequest dto) {
        // Delegamos la creación al UserService (ya encripta la contraseña)
        UserResponse creado = userService.create(dto);

        String token = jwtUtil.generarToken(creado.getEmail(), creado.getRol());
        return new AuthResponse(token, creado.getEmail(), creado.getRol());
    }

    public AuthResponse login(AuthRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        String token = jwtUtil.generarToken(user.getEmail(), user.getRol().name());
        return new AuthResponse(token, user.getEmail(), user.getRol().name());
    }
}