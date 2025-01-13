package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ClientException {
    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
