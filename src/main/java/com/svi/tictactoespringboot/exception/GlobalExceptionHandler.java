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
    ResponseEntity<ApiErrorResponse> api(ApiException exception, HttpServletRequest request) {
        return response(exception.getStatus(), exception.getCode(), exception.getMessage(), request, List.of());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        var fields = exception.getBindingResult().getFieldErrors().stream().map(
            fieldError -> new ApiErrorResponse.FieldError(fieldError.getField(), fieldError.getDefaultMessage())).toList();
        return response(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, request, fields);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> malformed(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, MALFORMED_REQUEST, request, List.of());
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiErrorResponse> pathValue(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        return response(
                HttpStatus.BAD_REQUEST,
                INVALID_PATH_VALUE,
                INVALID_PATH_VALUE.format(exception.getName()),
                request,
                List.of());
    }
    
    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ApiErrorResponse> concurrent(OptimisticLockingFailureException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, GAME_STATE_CHANGED, request, List.of());
    }
    
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR, request, List.of());
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, ResponseMessage response, HttpServletRequest request, List<ApiErrorResponse.FieldError> fields) {
        return response(status, response, response.getValue(), request, fields);
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, ResponseMessage response, String message, HttpServletRequest request, List<ApiErrorResponse.FieldError> fields) {
        return response(status, response.name(), message, request, fields);
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String code, String message, HttpServletRequest request, List<ApiErrorResponse.FieldError> fields) {
        return ResponseEntity.status(status).body(
            new ApiErrorResponse(Instant.now(),
            status.value(),
            code,
            message,
            request.getRequestURI(),
            fields)
        ); 
    }
}
