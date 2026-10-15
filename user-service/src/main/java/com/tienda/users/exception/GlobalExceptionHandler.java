package com.tienda.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex) {
        HttpStatus status;

        if (ex.getMessage().contains("no encontrado")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("Credenciales")) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.CONFLICT;
        }

        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(new ApiError(400, errores, LocalDateTime.now()));
    }
}