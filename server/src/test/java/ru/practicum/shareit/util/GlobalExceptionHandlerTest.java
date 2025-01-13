package ru.practicum.shareit.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleClientException_shouldReturnCorrectResponse() {
        // Arrange
        String errorMessage = "Client error occurred";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ClientException exception = new BadRequestException(errorMessage);

        WebRequest webRequest = mock(WebRequest.class);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleClientException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse<Object> errorResponse = (ErrorResponse) response.getBody();
        assertEquals("ru.practicum.shareit.util.BadRequestException", errorResponse.getError());
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void handleValidationExceptions_shouldReturnValidationErrors() {
        // Arrange
        BindException bindException = new BindException(new Object(), "testObject");
        bindException.addError(new FieldError("testObject", "field1", "Field1 is invalid"));
        bindException.addError(new FieldError("testObject", "field2", "Field2 is invalid"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindException);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse<Object> errorResponse = (ErrorResponse) response.getBody();
        assertEquals("ValidationError", errorResponse.getError());
        assertEquals("validation errors occurred", errorResponse.getMessage());

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) errorResponse.getPayload();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Field1 is invalid", errors.get("field1"));
        assertEquals("Field2 is invalid", errors.get("field2"));
    }

    @Test
    void handleRuntimeException_shouldReturnInternalServerError() {
        // Arrange
        String errorMessage = "Unexpected error occurred";
        RuntimeException exception = new RuntimeException(errorMessage);

        WebRequest webRequest = mock(WebRequest.class);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleRuntimeException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("InternalServerError", errorResponse.getError());
        assertEquals(errorMessage, errorResponse.getMessage());
    }
}
