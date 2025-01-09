package ru.practicum.shareit.util;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
public class ErrorResponse<P> {
    HttpStatus status;
    String error;
    String message;
    P payload;
}
