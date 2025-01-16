package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ItemRequestDto {
    Integer id;
    String description;
    LocalDateTime created;
    List<ItemShortDto> items;
}
