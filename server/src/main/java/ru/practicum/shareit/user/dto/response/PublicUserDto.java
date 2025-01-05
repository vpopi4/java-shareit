package ru.practicum.shareit.user.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PublicUserDto {
    Integer id;

    @Email
    @Size(min = 3, max = 512)
    String email;


    @Size(min = 3, max = 255)
    String name;
}
