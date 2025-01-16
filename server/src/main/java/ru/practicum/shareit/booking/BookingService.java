package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.ClientException;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Integer userId,
                             BookingCreatingDto dto) throws ClientException;

    BookingDto approveOrRejectBooking(Integer userId,
                                      Integer bookingId,
                                      Boolean isApproved) throws ClientException;

    BookingDto getBooking(Integer userId,
                          Integer bookingId) throws ClientException;

    List<BookingDto> getBookingsByBooker(Integer userId,
                                         BookingState state) throws ClientException;

    List<BookingDto> getBookingsByOwner(Integer userId,
                                        BookingState state) throws ClientException;
}
