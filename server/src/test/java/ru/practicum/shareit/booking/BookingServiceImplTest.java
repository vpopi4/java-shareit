package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
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
import java.util.List;
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

    private static BookingCreatingDto getBookingCreatingDto(Integer itemId) {
        return BookingCreatingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

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
    void createBooking_shouldThrowBadRequestException_whenEndDateNotAfterStartDate() {
        // Arrange
        int userId = 1;
        BookingCreatingDto dto = BookingCreatingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> bookingService.createBooking(userId, dto)
        );
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenItemIsUnavailable() {
        // Arrange
        int userId = 1;
        User user = dataGenerator.getUser(userId);
        Item item = dataGenerator.getItem(1, dataGenerator.getUser(2), null);
        item.setIsAvailable(false);
        BookingCreatingDto dto = getBookingCreatingDto(item.getId());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.of(item));

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> bookingService.createBooking(userId, dto)
        );
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
    void approveOrRejectBooking_shouldSetStatusRejected_whenBookingIsRejected() throws ClientException {
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
        BookingDto result = bookingService.approveOrRejectBooking(userId, booking.getId(), false);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepository, times(1)).save(booking);
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

    @Test
    public void getBookingsByBooker_shouldReturnAllBookings_whenStateIsAll() throws ClientException {
        // Arrange
        int userId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(2), null), dataGenerator.getUser(userId)),
                dataGenerator.getBooking(2, dataGenerator.getItem(2, dataGenerator.getUser(3), null), dataGenerator.getUser(userId))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findAllBookingsByBooker(userId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByBooker(userId, BookingState.ALL);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findAllBookingsByBooker(userId);
    }

    @Test
    public void getBookingsByBooker_shouldThrowBadRequestException_whenStateIsNull() {
        // Arrange
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> bookingService.getBookingsByBooker(userId, null)
        );
    }

    @Test
    public void getBookingsByBooker_shouldReturnPastBookings_whenStateIsPast() throws ClientException {
        // Arrange
        int userId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(2), null), dataGenerator.getUser(userId))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findPastBookingsByBooker(userId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByBooker(userId, BookingState.PAST);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findPastBookingsByBooker(userId);
    }


    @Test
    public void getBookingsByBooker_shouldReturnEmptyList_whenNoBookingsFound() throws ClientException {
        // Arrange
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findPastBookingsByBooker(userId)).thenReturn(List.of());

        // Act
        List<BookingDto> result = bookingService.getBookingsByBooker(userId, BookingState.PAST);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).findPastBookingsByBooker(userId);
    }

    @Test
    public void getBookingsByBooker_shouldThrowForbiddenException_whenUserIsUnauthorized() {
        // Arrange
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> bookingService.getBookingsByBooker(userId, BookingState.ALL)
        );
    }

    @Test
    public void getBookingsByBooker_shouldReturnCurrentBookings_whenStateIsCurrent() throws ClientException {
        // Arrange
        int userId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(2), null), dataGenerator.getUser(userId))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findCurrentBookingsByBooker(userId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByBooker(userId, BookingState.CURRENT);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findCurrentBookingsByBooker(userId);
    }

    @Test
    public void getBookingsByBooker_shouldReturnFutureBookings_whenStateIsFuture() throws ClientException {
        // Arrange
        int userId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(2), null), dataGenerator.getUser(userId))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findFutureBookingsByBooker(userId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByBooker(userId, BookingState.FUTURE);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findFutureBookingsByBooker(userId);
    }

    @Test
    public void getBookingsByBooker_shouldReturnWaitingBookings_whenStateIsWaiting() throws ClientException {
        // Arrange
        int userId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(2), null), dataGenerator.getUser(userId))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findByStatusAndBooker(BookingStatus.WAITING, userId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByBooker(userId, BookingState.WAITING);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findByStatusAndBooker(BookingStatus.WAITING, userId);
    }

    @Test
    public void getBookingsByBooker_shouldReturnRejectedBookings_whenStateIsRejected() throws ClientException {
        // Arrange
        int userId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(2), null), dataGenerator.getUser(userId))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findByStatusAndBooker(BookingStatus.REJECTED, userId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByBooker(userId, BookingState.REJECTED);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findByStatusAndBooker(BookingStatus.REJECTED, userId);
    }

    @Test
    public void getBookingsByOwner_shouldReturnAllBookings_whenStateIsAll() throws ClientException {
        // Arrange
        int ownerId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(ownerId), null), dataGenerator.getUser(2)),
                dataGenerator.getBooking(2, dataGenerator.getItem(2, dataGenerator.getUser(ownerId), null), dataGenerator.getUser(3))
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(dataGenerator.getUser(ownerId)));
        when(bookingRepository.findAllBookingsByOwner(ownerId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByOwner(ownerId, BookingState.ALL);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findAllBookingsByOwner(ownerId);
    }

    @Test
    public void getBookingsByOwner_shouldReturnPastBookings_whenStateIsPast() throws ClientException {
        // Arrange
        int ownerId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(ownerId), null), dataGenerator.getUser(2))
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(dataGenerator.getUser(ownerId)));
        when(bookingRepository.findPastBookingsByOwner(ownerId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByOwner(ownerId, BookingState.PAST);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findPastBookingsByOwner(ownerId);
    }

    @Test
    public void getBookingsByOwner_shouldReturnCurrentBookings_whenStateIsCurrent() throws ClientException {
        // Arrange
        int ownerId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(ownerId), null), dataGenerator.getUser(2))
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(dataGenerator.getUser(ownerId)));
        when(bookingRepository.findCurrentBookingsByOwner(ownerId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByOwner(ownerId, BookingState.CURRENT);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findCurrentBookingsByOwner(ownerId);
    }

    @Test
    public void getBookingsByOwner_shouldReturnFutureBookings_whenStateIsFuture() throws ClientException {
        // Arrange
        int ownerId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(ownerId), null), dataGenerator.getUser(2))
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(dataGenerator.getUser(ownerId)));
        when(bookingRepository.findFutureBookingsByOwner(ownerId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByOwner(ownerId, BookingState.FUTURE);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findFutureBookingsByOwner(ownerId);
    }

    @Test
    public void getBookingsByOwner_shouldReturnWaitingBookings_whenStateIsWaiting() throws ClientException {
        // Arrange
        int ownerId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(ownerId), null), dataGenerator.getUser(2))
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(dataGenerator.getUser(ownerId)));
        when(bookingRepository.findByStatusAndOwner(BookingStatus.WAITING, ownerId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByOwner(ownerId, BookingState.WAITING);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findByStatusAndOwner(BookingStatus.WAITING, ownerId);
    }

    @Test
    public void getBookingsByOwner_shouldThrowForbiddenException_whenUserIsUnauthorized() {
        // Arrange
        int ownerId = 1;
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> bookingService.getBookingsByOwner(ownerId, BookingState.ALL)
        );
    }

    @Test
    public void getBookingsByOwner_shouldReturnEmptyList_whenNoBookingsFound() throws ClientException {
        // Arrange
        int ownerId = 1;

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(dataGenerator.getUser(ownerId)));
        when(bookingRepository.findAllBookingsByOwner(ownerId)).thenReturn(List.of());

        // Act
        List<BookingDto> result = bookingService.getBookingsByOwner(ownerId, BookingState.ALL);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).findAllBookingsByOwner(ownerId);
    }

    @Test
    void getBookingsByOwner_shouldReturnRejectedBookings_whenStateIsRejected() throws ClientException {
        // Arrange
        int userId = 1;
        List<Booking> bookings = List.of(
                dataGenerator.getBooking(1, dataGenerator.getItem(1, dataGenerator.getUser(userId), null), dataGenerator.getUser(2))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));
        when(bookingRepository.findByStatusAndOwner(BookingStatus.REJECTED, userId)).thenReturn(bookings);

        // Act
        List<BookingDto> result = bookingService.getBookingsByOwner(userId, BookingState.REJECTED);

        // Assert
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
        verify(bookingRepository, times(1)).findByStatusAndOwner(BookingStatus.REJECTED, userId);
    }

    @Test
    void getBookingsByOwner_shouldThrowBadRequestException_whenStateIsInvalid() {
        // Arrange
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(dataGenerator.getUser(userId)));

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> bookingService.getBookingsByOwner(userId, null)
        );
    }
}
