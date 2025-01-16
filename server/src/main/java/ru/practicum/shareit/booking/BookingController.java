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
        log.info("---> Creating booking: userId={}, body={}", userId, dto);

        return service.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("bookingId") Integer bookingId,
            @RequestParam("approved") Boolean isApproved
    ) throws ClientException {
        log.info("---> {} booking: userId={}, bookingId={}",
                isApproved ? "Approving" : "Rejecting", userId, bookingId);

        return service.approveOrRejectBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("bookingId") Integer bookingId
    ) throws ClientException {
        return service.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) String state
    ) throws ClientException {
        log.info("---> Getting bookings by booker: userId={}, state={}", userId, state);

        return service.getBookingsByBooker(userId, BookingState.parseString(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) String state
    ) throws ClientException {
        log.info("---> Getting bookings by owner: userId={}, state={}", userId, state);

        return service.getBookingsByOwner(userId, BookingState.parseString(state));
    }
}
