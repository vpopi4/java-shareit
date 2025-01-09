package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemCreatingDto {
    @NotBlank String name;
    @NotBlank String description;
    @NotNull Boolean available;
    Integer requestId;
}

