package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemCreatingDto {
    String name;
    String description;
    Boolean available;
    Integer requestId;
}
