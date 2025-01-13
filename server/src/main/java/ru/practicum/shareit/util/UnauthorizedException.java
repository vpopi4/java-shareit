package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ClientException {
    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
