package ru.practicum.shareit.user.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PublicUserDto {
    Integer id;

    @Email
    @Min(3)
    @Max(512)
    String email;

    @Min(3)
    @Max(255)
    String name;
}
