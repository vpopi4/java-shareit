package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ClientException;
import ru.practicum.shareit.util.DataGenerator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingMapper bookingMapper;
    private BookingService bookingService;
    private DataGenerator dataGenerator;

    @BeforeEach
    void setUp() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingMapper = new BookingMapper(new UserMapper(), new ItemMapper());

        bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository,
                bookingMapper
        );

        dataGenerator = new DataGenerator();
    }

    @Test
    void createBooking_shouldCreateBooking_whenValidRequest() throws ClientException {
        // Arrange
        int userId = 1;
        User user = dataGenerator.getUser(userId);
        Item item = dataGenerator.getItem(1, dataGenerator.getUser(2), null);
        BookingCreatingDto dto = getBookingCreatingDto(item.getId());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(dataGenerator.getNextId());
            return booking;
        });

        // Act
        BookingDto result = bookingService.createBooking(userId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getStart(), result.getStart());
        assertEquals(dto.getEnd(), result.getEnd());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenEndDateIsBeforeStartDate() {
        // Arrange
        int userId = 1;
        BookingCreatingDto dto = getBookingCreatingDto(null);

        // Act & Assert
        assertThrows(ClientException.class, () -> bookingService.createBooking(userId, dto));
    }

    @Test
    void createBooking_shouldThrowForbiddenException_whenUserIsOwner() {
        // Arrange
        int userId = 1;
        User user = dataGenerator.getUser(userId);
        Item item = dataGenerator.getItem(1, user, null);
        BookingCreatingDto dto = getBookingCreatingDto(item.getId());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.of(item));

        // Act & Assert
        assertThrows(ClientException.class, () -> bookingService.createBooking(userId, dto));
    }

    @Test
    void approveOrRejectBooking_shouldApproveBooking_whenUserIsOwner() throws ClientException {
        // Arrange
        int userId = 1;
        User owner = dataGenerator.getUser(userId);
        User booker = dataGenerator.getUser(2);
        Item item = dataGenerator.getItem(1, owner, null);
        Booking booking = dataGenerator.getBooking(1, item, booker);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Act
        BookingDto result = bookingService.approveOrRejectBooking(userId, booking.getId(), true);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveOrRejectBooking_shouldThrowForbiddenException_whenUserIsNotOwner() {
        // Arrange
        int userId = 1;
        User owner = dataGenerator.getUser(2);
        User booker = dataGenerator.getUser(3);
        Item item = dataGenerator.getItem(1, owner, null);
        Booking booking = dataGenerator.getBooking(1, item, booker);

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> bookingService.approveOrRejectBooking(userId, booking.getId(), true)
        );
    }

    @Test
    void getBooking_shouldReturnBooking_whenUserIsOwnerOrBooker() throws ClientException {
        // Arrange
        int userId = 1;
        User owner = dataGenerator.getUser(userId);
        User booker = dataGenerator.getUser(2);
        Item item = dataGenerator.getItem(1, owner, null);
        Booking booking = dataGenerator.getBooking(1, item, booker);
        booking.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Act
        BookingDto result = bookingService.getBooking(userId, booking.getId());

        // Assert
        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        verify(bookingRepository, times(1)).findById(booking.getId());
    }


    @Test
    void getBooking_shouldThrowForbiddenException_whenUserIsNotOwnerOrBooker() {
        // Arrange
        int userId = 1;
        User owner = dataGenerator.getUser(2);
        User booker = dataGenerator.getUser(3);
        Item item = dataGenerator.getItem(1, owner, null);
        Booking booking = dataGenerator.getBooking(1, item, booker);
        booking.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> bookingService.getBooking(userId, booking.getId())
        );
    }

    private static BookingCreatingDto getBookingCreatingDto(Integer itemId) {
        return BookingCreatingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }
}