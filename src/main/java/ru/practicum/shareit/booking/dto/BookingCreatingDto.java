package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingCreatingDto {
    @NotNull
    @Future
    LocalDateTime start;

    @NotNull
    @Future
    LocalDateTime end;

    @NotNull
    Integer itemId;
}
