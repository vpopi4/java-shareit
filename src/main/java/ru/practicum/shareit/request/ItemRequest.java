package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.sql.Timestamp;

@Data
@Builder
public class ItemRequest {
    @NotNull
    private Integer id;

    private String description;

    @NotNull
    private User requester;

    @NotNull
    private Timestamp createdAt;
}
