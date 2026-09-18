package com.svi.tictactoespringboot.exception;

import com.svi.tictactoespringboot.dto.response.ApiErrorResponse;
import com.svi.tictactoespringboot.constants.ResponseMessage;
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

import static com.svi.tictactoespringboot.constants.ResponseMessage.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiErrorResponse> api(ApiException e, HttpServletRequest r) { return response(e.getStatus(), e.getCode(), e.getMessage(), r, List.of()); }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        var fields = e.getBindingResult().getFieldErrors().stream().map(
            f -> new ApiErrorResponse.FieldError(f.getField(), f.getDefaultMessage())).toList();
        return response(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, r, fields);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> malformed(HttpMessageNotReadableException e, HttpServletRequest r) {
        return response(HttpStatus.BAD_REQUEST, MALFORMED_REQUEST, r, List.of());
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiErrorResponse> pathValue(MethodArgumentTypeMismatchException e, HttpServletRequest r) {
        return response(
                HttpStatus.BAD_REQUEST,
                INVALID_PATH_VALUE,
                INVALID_PATH_VALUE.format(e.getName()),
                r,
                List.of());
    }
    
    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ApiErrorResponse> concurrent(OptimisticLockingFailureException e, HttpServletRequest r) {
        return response(HttpStatus.CONFLICT, GAME_STATE_CHANGED, r, List.of());
    }
    
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception e, HttpServletRequest r) { 
        return response(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR, r, List.of());
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, ResponseMessage response, HttpServletRequest request, List<ApiErrorResponse.FieldError> fields) {
        return response(status, response, response.getValue(), request, fields);
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, ResponseMessage response, String message, HttpServletRequest request, List<ApiErrorResponse.FieldError> fields) {
        return response(status, response.name(), message, request, fields);
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
