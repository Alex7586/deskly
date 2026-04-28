package ro.unibuc.deskly.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = ex.getMessage();

        if("Invalid credentials".equals(message) || "User is not authenticated".equals(message))
            status = HttpStatus.UNAUTHORIZED;
        else if("Account locked temporarily. Try again later!".equals(message))
            status = HttpStatus.TOO_MANY_REQUESTS;
        else if("Ticket not found".equals(message) || "Authenticated user not found".equals(message))
            status = HttpStatus.NOT_FOUND;

        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(RuntimeException ex){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "error", "INTERNAL SERVER ERROR"));
    }
}
