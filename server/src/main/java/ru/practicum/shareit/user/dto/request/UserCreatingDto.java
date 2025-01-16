package ru.practicum.shareit.user.dto.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserCreatingDto {
    String email;
    String name;
}
