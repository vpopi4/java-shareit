package ru.practicum.shareit.user.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PublicUserDto {
    Integer id;
    String email;
    String name;
}
