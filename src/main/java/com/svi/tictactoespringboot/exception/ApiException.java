package com.svi.tictactoespringboot.exception;
import com.svi.tictactoespringboot.constants.ResponseMessage;
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

    public static ApiException notFound(ResponseMessage response, Object id) {
        return new ApiException(HttpStatus.NOT_FOUND, response.name(), response.format(id));
    }
    
    public static ApiException conflict(ResponseMessage response) {
        return new ApiException(HttpStatus.CONFLICT, response.name(), response.getValue());
    }
}
