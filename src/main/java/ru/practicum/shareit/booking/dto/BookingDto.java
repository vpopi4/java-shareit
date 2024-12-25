package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.response.PublicUserDto;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDto.Response.ShortPublicInfo item;
    PublicUserDto booker;
    BookingStatus status;
    LocalDateTime createdAt;
}
