package com.svi.tictactoespringboot.exception;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status; private final String code;

    public ApiException(HttpStatus status, String code, String message) { 
        super(message); 
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() { return status; } public String getCode() { 
        return code; 
    }

    public static ApiException notFound(String resource, Object id) { 
        return new ApiException(HttpStatus.NOT_FOUND, resource.toUpperCase() + "_NOT_FOUND", resource + " not found: " + id); 
    }
    
    public static ApiException conflict(String code, String message) { 
        return new ApiException(HttpStatus.CONFLICT, code, message); 
    }
}
