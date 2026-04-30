package ro.unibuc.authx.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if("User is not authenticated".equals(ex.getMessage()))
            status = HttpStatus.UNAUTHORIZED;

        return ResponseEntity
                .status(status)
                .body(Map.of("error", ex.getMessage()));
    }
}
