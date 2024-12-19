package ru.practicum.shareit.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserUpdatingDto {
    @Email
    @Min(3)
    @Max(512)
    String email;

    @Min(3)
    @Max(255)
    String name;
}
