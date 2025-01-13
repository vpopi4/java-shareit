package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ClientException {
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
