package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper map;

    @Override
    public BookingDto createItem(Integer userId,
                                 BookingCreatingDto dto) throws ClientException {
        User user = authorize(userId);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with such id not found"));

        if (item.getOwner().equals(user)) {
            throw new ForbiddenException("You can not booking your item");
        }

        if (!item.getIsAvailable()) {
            throw new ForbiddenException("Booking is not available now");
        }

        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new BadRequestException("Incorrect date range");
        }

        Booking booking = Booking.builder()
                .id(null)
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();

        return saveAndReturnDto(booking);
    }

    @Override
    public BookingDto approveOrRejectBooking(Integer userId,
                                             Integer bookingId,
                                             Boolean isApproved) throws ClientException {
        User user = authorize(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Such booking not found"));
        User owner = booking.getItem().getOwner();

        if (!owner.equals(user)) {
            throw new ForbiddenException("You can not approve or reject this booking");
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return saveAndReturnDto(booking);
    }

    @Override
    public BookingDto getBooking(Integer userId,
                                 Integer bookingId) throws ClientException {
        User user = authorize(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Such booking not found"));
        User owner = booking.getItem().getOwner();
        User booker = booking.getBooker();

        if (!user.equals(owner) && !user.equals(booker)) {
            throw new ForbiddenException("You can not get the booking information");
        }

        return saveAndReturnDto(booking);
    }

    private User authorize(Integer userId) throws UnauthorizedException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User with such id not found"));
    }

    private BookingDto saveAndReturnDto(Booking booking) {
        Booking savedBooking = bookingRepository.save(booking);

        return map.toBookingDto(savedBooking);
    }

    @Override
    public List<BookingDto> getBookingsByBooker(Integer userId, BookingState state) throws ClientException {
        List<Booking> bookings;
        authorize(userId);

        switch (state) {
            case PAST -> bookings = bookingRepository.findPastBookingsByBooker(userId);
            case CURRENT -> bookings = bookingRepository.findCurrentBookingsByBooker(userId);
            case FUTURE -> bookings = bookingRepository.findFutureBookingsByBooker(userId);
            case WAITING -> bookings = bookingRepository.findByStatusAndBooker(BookingStatus.WAITING, userId);
            case REJECTED -> bookings = bookingRepository.findByStatusAndBooker(BookingStatus.REJECTED, userId);
            case ALL -> bookings = bookingRepository.findAllBookingsByBooker(userId);
            case null, default -> throw new BadRequestException("state is not valid");
        }

        return bookings.stream()
                .map(map::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Integer userId, BookingState state) throws ClientException {
        List<Booking> bookings;
        authorize(userId);

        switch (state) {
            case PAST -> bookings = bookingRepository.findPastBookingsByOwner(userId);
            case CURRENT -> bookings = bookingRepository.findCurrentBookingsByOwner(userId);
            case FUTURE -> bookings = bookingRepository.findFutureBookingsByOwner(userId);
            case WAITING -> bookings = bookingRepository.findByStatusAndOwner(BookingStatus.WAITING, userId);
            case REJECTED -> bookings = bookingRepository.findByStatusAndOwner(BookingStatus.REJECTED, userId);
            case ALL -> bookings = bookingRepository.findAllBookingsByOwner(userId);
            case null, default -> throw new BadRequestException("state is not valid");
        }

        return bookings.stream()
                .map(map::toBookingDto)
                .toList();
    }
}
