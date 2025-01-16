package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.util.NotBlankIfNotNull;

@Value
@Builder
public class ItemUpdatingDto {
    @NotBlankIfNotNull
    String name;
    @NotBlankIfNotNull
    String description;
    Boolean available;
}
