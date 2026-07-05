package com.bank.core.api.exception;

import com.bank.core.domain.exception.AccountNotFoundException;
import com.bank.core.domain.exception.InsufficientBalanceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones.
 *
 * @RestControllerAdvice: Captura excepciones de todos los controladores
 * y las convierte en respuestas HTTP consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de validación (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        response.put("message", "Datos de entrada inválidos");
        response.put("details", errors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja excepciones de cuenta no encontrada.
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(
            AccountNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Maneja excepciones de saldo insuficiente.
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalance(
            InsufficientBalanceException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Insufficient Balance");
        response.put("message", ex.getMessage());
        response.put("accountNumber", ex.getAccountNumber());
        response.put("availableBalance", ex.getAvailableBalance());
        response.put("requestedAmount", ex.getRequestedAmount());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja excepciones de argumentos inválidos.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja excepciones generales (catch-all).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "Ha ocurrido un error inesperado");
        response.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}