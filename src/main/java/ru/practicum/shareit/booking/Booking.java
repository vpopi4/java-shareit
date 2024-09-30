package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.sql.Timestamp;

@Data
@Builder
public class Booking {
    @NotNull
    private Integer id; // pk

    @NotNull
    private Timestamp start;

    @NotNull
    private Timestamp end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    @NotNull
    private BookingStatus status;
}
