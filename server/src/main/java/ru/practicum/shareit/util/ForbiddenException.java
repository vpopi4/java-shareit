package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ClientException {
    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
