package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    @NotNull
    private Integer id;

    @NotNull
    private String email;

    @NotNull
    private String name;
}
