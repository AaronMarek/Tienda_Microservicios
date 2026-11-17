package com.tienda.users.service;

import com.tienda.users.dto.AuthRequest;
import com.tienda.users.dto.AuthResponse;
import com.tienda.users.dto.UserRequest;
import com.tienda.users.dto.UserResponse;
import com.tienda.users.entity.User;
import com.tienda.users.exception.InvalidCredentialsException;
import com.tienda.users.repository.UserRepository;
import com.tienda.users.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthResponse register(UserRequest dto) {
        // UserService ya comprueba el email y encripta la contraseña
        UserResponse created = userService.create(dto);

        String token = jwtUtil.generarToken(created.getEmail(), created.getRol());
        return new AuthResponse(token, created.getEmail(), created.getRol());
    }

    public AuthResponse login(AuthRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> invalidCredentials(dto.getEmail()));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw invalidCredentials(dto.getEmail());
        }

        log.info("Login correcto: {}", user.getEmail());
        String token = jwtUtil.generarToken(user.getEmail(), user.getRol().name());
        return new AuthResponse(token, user.getEmail(), user.getRol().name());
    }

    // Mismo mensaje en los dos casos para no revelar si el email existe
    private InvalidCredentialsException invalidCredentials(String email) {
        log.warn("Login fallido para {}", email);
        return new InvalidCredentialsException("Credenciales incorrectas");
    }
}