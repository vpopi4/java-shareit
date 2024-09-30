package ru.practicum.shareit;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
public class ErrorResponse<P> {
    HttpStatus status;
    String message;
    P payload;
}
