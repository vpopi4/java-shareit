package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.BadRequestException;
import ru.practicum.shareit.util.ClientException;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody BookingCreatingDto dto
    ) throws ClientException {
        log.info("--> POST /bookings: userId={}, body={}", userId, dto);

        if (dto.getStart().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new BadRequestException("Incorrect date range");
        }

        if (dto.getEnd().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new BadRequestException("Incorrect date range");
        }

        ResponseEntity<Object> response = bookingClient.createBooking(userId, dto);

        log.info("<--POST /bookings: response={}", response);

        return response;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrRejectBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("bookingId") Integer bookingId,
            @RequestParam("approved") Boolean approved
    ) {
        log.info("--> PATCH /bookings/{}?approved={}: userId={}", bookingId, approved, userId);

        ResponseEntity<Object> response = bookingClient.approveOrRejectBooking(userId, bookingId, approved);

        log.info("<-- PATCH /bookings/{}?approved={}: response={}", bookingId, approved, response);

        return response;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("bookingId") Integer bookingId
    ) {
        log.info("--> GET /bookings/{}: userId={}", bookingId, userId);

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        log.info("<-- GET /bookings/{}: response={}", bookingId, response);

        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) String state
    ) {
        log.info("--> GET /bookings?state={}: userId={}", state, userId);

        ResponseEntity<Object> response = bookingClient.getBookingsByBooker(userId, BookingState.parseString(state));

        log.info("<-- GET /bookings?state{}: response={}", state, response);

        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) String state
    ) {
        log.info("--> GET /bookings/owner?state={}: userId={}", state, userId);

        ResponseEntity<Object> response = bookingClient.getBookingsByOwner(userId, BookingState.parseString(state));

        log.info("GET /bookings/owner?state={}: response={}", state, response);

        return response;
    }
}
