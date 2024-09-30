package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class Item {
    @NotNull
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Boolean isAvailable;

    @NotNull
    private User owner;

    private ItemRequest request;
}
