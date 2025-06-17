package com.example.baitapt1.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResponseStatusException ex, HttpServletRequest request) {
        Locale locale = request.getLocale();
        String messageKey = ex.getReason();

        String message = messageSource.getMessage(messageKey, null, messageKey, locale);
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getStatusCode().toString());
        body.put("message", message);
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, ex.getStatusCode());
    }
}

