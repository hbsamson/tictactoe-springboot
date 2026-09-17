package com.svi.tictactoespringboot.exception;

import com.svi.tictactoespringboot.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiErrorResponse> api(ApiException e, HttpServletRequest r) { return response(e.getStatus(), e.getCode(), e.getMessage(), r, List.of()); }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        var fields = e.getBindingResult().getFieldErrors().stream().map(
            f -> new ApiErrorResponse.FieldError(f.getField(), f.getDefaultMessage())).toList();
        return response(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","Request validation failed", r ,fields);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> malformed(HttpMessageNotReadableException e, HttpServletRequest r) {
        return response(HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST",
                "Request body is malformed",
                r,
                List.of());
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiErrorResponse> pathValue(MethodArgumentTypeMismatchException e, HttpServletRequest r) {
        return response(HttpStatus.BAD_REQUEST,
                "INVALID_PATH_VALUE",
                "Invalid value for " + e.getName(),
                r,
                List.of());
    }
    
    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ApiErrorResponse> concurrent(OptimisticLockingFailureException e, HttpServletRequest r) {
        return response(HttpStatus.CONFLICT,
                "CONCURRENT_MOVE",
                "Game changed while the move was being processed",
                r,
                List.of());
    }
    
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception e, HttpServletRequest r) { 
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                r,
                List.of()); 
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus s, String c, String m, HttpServletRequest r, List<ApiErrorResponse.FieldError> f) { 
        return ResponseEntity.status(s).body(
            new ApiErrorResponse(Instant.now(),
            s.value(),
            c,
            m,
            r.getRequestURI(),
            f)
        ); 
    }
}
