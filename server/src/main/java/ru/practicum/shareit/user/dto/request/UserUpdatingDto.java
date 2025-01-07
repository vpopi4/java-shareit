package ru.practicum.shareit.user.dto.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserUpdatingDto {
    String email;
    String name;
}
