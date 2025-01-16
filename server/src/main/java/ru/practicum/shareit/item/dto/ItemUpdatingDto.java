package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemUpdatingDto {
    String name;
    String description;
    Boolean available;
}
