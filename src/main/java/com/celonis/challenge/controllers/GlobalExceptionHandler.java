package com.celonis.challenge.controllers;

import com.celonis.challenge.api.model.ApiError; // generado por OpenAPI
import com.celonis.challenge.exceptions.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req, "NOT_FOUND", null);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ApiError> handleNotAuth(NotAuthorizedException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), req, "UNAUTHORIZED", null);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ApiError> handleInternal(InternalException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), req, "INTERNAL_ERROR", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req, "BAD_REQUEST", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), req, "INTERNAL_ERROR", null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String error, String message,
                                           HttpServletRequest req, String code, List<String> details) {
        ApiError body = new ApiError()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(error)
                .message(message != null ? message : error)
                .path(req.getRequestURI())
                .code(code)
                .details(details);
        return ResponseEntity.status(status).body(body);
    }
}

