package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserCreatingDto {
    @NotNull
    @Email
    @Size(min = 3, max = 512)
    String email;

    @NotNull
    @Size(min = 3, max = 255)
    String name;
}
