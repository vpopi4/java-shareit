package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState parseString(String string) {
        try {
            return BookingState.valueOf(string);
        } catch (IllegalArgumentException e) {
            return BookingState.ALL;
        }
    }
}
