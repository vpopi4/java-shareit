package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.ClientException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody BookingCreatingDto dto
    ) throws ClientException {
        log.info("POST /bookings: creating booking: " +
                "X-Sharer-User-Id={}, body={}", userId, dto);

        BookingDto response = service.createBooking(userId, dto);

        log.info("POST /bookings: response body={}", response);

        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("bookingId") Integer bookingId,
            @RequestParam("approved") Boolean isApproved
    ) throws ClientException {
        log.info("PATCH /bookings/{}: approving or rejecting booking: " +
                "X-Sharer-User-Id={}, isApproved={}", bookingId, userId, isApproved);

        BookingDto response = service.approveOrRejectBooking(userId, bookingId, isApproved);

        log.info("PATCH /bookings/{}: response body={}", bookingId, response);

        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("bookingId") Integer bookingId
    ) throws ClientException {
        log.info("GET /bookings/{}: X-Sharer-User-Id={}", bookingId, userId);

        BookingDto response = service.getBooking(userId, bookingId);

        log.info("GET /bookings/{}: response body={}", bookingId, response);

        return response;
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) String state
    ) throws ClientException {
        log.info("GET /bookings: getting bookings by booker and state: " +
                "X-Sharer-User-Id={}, state={}", userId, state);

        List<BookingDto> response = service.getBookingsByBooker(userId, BookingState.parseString(state));

        log.info("GET /bookings: response body={}", response);

        return response;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) String state
    ) throws ClientException {
        log.info("GET /bookings/owner: getting bookings by owner and state: " +
                "X-Sharer-User-Id={}, state={}", userId, state);

        List<BookingDto> response = service.getBookingsByOwner(userId, BookingState.parseString(state));

        log.info("GET /bookings/owner: response body={}", response);

        return response;
    }
}
