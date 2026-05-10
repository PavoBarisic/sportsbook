package com.pavobarisic.sportsbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, Object> odgovor = new HashMap<>();
        odgovor.put("status", 404);
        odgovor.put("poruka", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(odgovor);
    }

    @ExceptionHandler(NedovoljnoSredstavaException.class)
    public ResponseEntity<Map<String, Object>> handleNedovoljnoSredstava(NedovoljnoSredstavaException ex) {
        Map<String, Object> odgovor = new HashMap<>();
        odgovor.put("status", 400);
        odgovor.put("poruka", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(odgovor);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> odgovor = new HashMap<>();
        odgovor.put("status", 400);
        odgovor.put("poruka", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(odgovor);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> odgovor = new HashMap<>();
        odgovor.put("status", 400);
        odgovor.put("poruka", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(odgovor);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> odgovor = new HashMap<>();
        odgovor.put("status", 403);
        odgovor.put("poruka", "Nemate pravo pristupa ovom resursu");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(odgovor);
    }
}