package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ClientExceptionTest {
    @Test
    void testUnauthorizedException() {
        UnauthorizedException exception = new UnauthorizedException("Unauthorized access");

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
        assertEquals("Unauthorized access", exception.getMessage());
    }

    @Test
    void testForbiddenException() {
        ForbiddenException exception = new ForbiddenException("Forbidden access");

        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        assertEquals("Forbidden access", exception.getMessage());
    }

    @Test
    void testNotFoundException() {
        NotFoundException exception = new NotFoundException("Resource not found");

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("Resource not found", exception.getMessage());
    }

    @Test
    void testAlreadyExistsException() {
        AlreadyExistsException exception = new AlreadyExistsException("Already exists");

        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        assertEquals("Already exists", exception.getMessage());
    }

    @Test
    void testBadRequestException() {
        BadRequestException exception = new BadRequestException("Bad request");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Bad request", exception.getMessage());
    }
}
