package com.example.social_media.controllers;

import com.example.social_media.exceptions.BadRequestException;
import com.example.social_media.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
        var errors = new ArrayList<Map<String, String>>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            Map<String, String> errorMap = Map.of(
                    "field", error.getField(),
                    "message", error.getDefaultMessage()
            );
            errors.add(errorMap);
        });

        var response = buildErrorResponse("Validation failed", HttpStatus.BAD_REQUEST);
        response.getBody().put("errors", errors);
        return ResponseEntity.badRequest().body(response.getBody());
    }

    // Handle bad credentials (auth error)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex){
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // Handle resource-not-found errors (controller)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex){
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Handle bad request errors (controller)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex){
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

//    //Generic error handler
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleException(Exception ex){
//        return buildErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    // Build error response containing basic information along with HTTP status
    public ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status){
        Map<String, Object> errors = Map.of(
                "timestamp", LocalDateTime.now(),
                "statusCode", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
                );
        return ResponseEntity.status(status).body(errors);

    }
}
