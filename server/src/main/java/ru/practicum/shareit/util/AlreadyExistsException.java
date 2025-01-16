package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ClientException {
    public AlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
