package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message) {
        var res = ErrorResponse.builder()
                .status(status)
                .message(message)
                .build();

        return new ResponseEntity<>(res, status);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<Object> handleClientException(ClientException ex, WebRequest request) {
        log.warn("Client exception occurred: ", ex);

        return buildResponseEntity(ex.getHttpStatus(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Validation errors: {}", errors);

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("validation errors occurred")
                        .payload(errors)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleRuntimeException(Throwable ex, WebRequest request) {
        log.error("Internal server error: ", ex);
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
