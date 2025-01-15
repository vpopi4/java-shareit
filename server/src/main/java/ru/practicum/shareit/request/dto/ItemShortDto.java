package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ItemShortDto {
    Integer id;
    String name;
    Integer ownerId;
    LocalDateTime createdAt;
}
