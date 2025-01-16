package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class ItemPublicDto {
    Integer id;
    String name;
    String description;
    Boolean available;

    Integer requestId;
    List<CommentDto> comments;
    BookingDto lastBooking;
    BookingDto nextBooking;
}
