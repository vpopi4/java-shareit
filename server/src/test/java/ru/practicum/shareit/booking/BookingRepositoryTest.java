package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.DataGenerator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@ExtendWith(SpringExtension.class)
class BookingRepositoryTest {
    private final DataGenerator dataGenerator = new DataGenerator();

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void findAllBookingsByBooker_shouldReturnBookingsOfSpecificUser() {
        // Arrange
        User booker = entityManager.persist(dataGenerator.getUser(null));
        Item item = entityManager.persist(dataGenerator.getItem(null, booker, null));
        Booking booking1 = entityManager.persist(dataGenerator.getBooking(null, item, booker));
        Booking booking2 = entityManager.persist(dataGenerator.getBooking(null, item, booker));
        entityManager.flush();

        // Act
        List<Booking> bookings = bookingRepository.findAllBookingsByBooker(booker.getId());

        // Assert
        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
        assertTrue(bookings.get(0).getStart().isAfter(bookings.get(1).getStart()));
    }

    @Test
    public void findCurrentBookingsByBooker_shouldReturnApprovedBookingsOnly() {
        // Arrange
        User booker = entityManager.persist(dataGenerator.getUser(null));
        Item item = entityManager.persist(dataGenerator.getItem(null, booker, null));

        Booking approvedBooking = dataGenerator.getBooking(null, item, booker);
        approvedBooking.setStatus(BookingStatus.APPROVED);
        approvedBooking.setStart(LocalDateTime.now().minusDays(3));
        approvedBooking.setEnd(LocalDateTime.now().plusDays(2));

        Booking rejectedBooking = dataGenerator.getBooking(null, item, booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        entityManager.persistAndFlush(approvedBooking);
        entityManager.persistAndFlush(rejectedBooking);

        // Act
        List<Booking> bookings = bookingRepository.findCurrentBookingsByBooker(booker.getId());

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(approvedBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findPastBookingsByBooker_shouldReturnCompletedBookings() {
        // Arrange
        User booker = entityManager.persist(dataGenerator.getUser(null));
        Item item = entityManager.persist(dataGenerator.getItem(null, booker, null));

        Booking pastBooking = dataGenerator.getBooking(null, item, booker);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));

        entityManager.persistAndFlush(pastBooking);

        // Act
        List<Booking> bookings = bookingRepository.findPastBookingsByBooker(booker.getId());

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findByStatusAndBooker_shouldReturnBookingsWithStatusAndUser() {
        // Arrange
        User booker = entityManager.persist(dataGenerator.getUser(null));
        Item item = entityManager.persist(dataGenerator.getItem(null, booker, null));

        Booking approvedBooking = dataGenerator.getBooking(null, item, booker);
        approvedBooking.setStatus(BookingStatus.APPROVED);

        Booking waitingBooking = dataGenerator.getBooking(null, item, booker);
        waitingBooking.setStatus(BookingStatus.WAITING);

        entityManager.persistAndFlush(approvedBooking);
        entityManager.persistAndFlush(waitingBooking);

        // Act
        List<Booking> bookings = bookingRepository.findByStatusAndBooker(BookingStatus.APPROVED, booker.getId());

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(approvedBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findAllBookingsByOwner_shouldReturnBookingsForOwner() {
        // Arrange
        User owner = entityManager.persist(dataGenerator.getUser(null));
        User booker = entityManager.persist(dataGenerator.getUser(null));
        Item item = entityManager.persist(dataGenerator.getItem(null, owner, null));

        Booking booking1 = dataGenerator.getBooking(null, item, booker);
        Booking booking2 = dataGenerator.getBooking(null, item, booker);

        booking1 = entityManager.persistAndFlush(booking1);
        booking2 = entityManager.persistAndFlush(booking2);

        // Act
        List<Booking> bookings = bookingRepository.findAllBookingsByOwner(owner.getId());

        // Assert
        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    public void findLatestBooking_shouldReturnLatestCompletedBooking() {
        // Arrange
        User owner = entityManager.persist(dataGenerator.getUser(null));
        User booker = entityManager.persist(dataGenerator.getUser(null));

        Item item = entityManager.persist(dataGenerator.getItem(null, owner, null));

        Booking pastBooking1 = dataGenerator.getBooking(null, item, booker);
        pastBooking1.setStatus(BookingStatus.APPROVED);
        pastBooking1.setStart(LocalDateTime.now().minusDays(3));
        pastBooking1.setEnd(LocalDateTime.now().minusDays(2));

        Booking pastBooking2 = dataGenerator.getBooking(null, item, booker);
        pastBooking2.setStatus(BookingStatus.APPROVED);
        pastBooking2.setStart(LocalDateTime.now().minusDays(5));
        pastBooking2.setEnd(LocalDateTime.now().minusDays(4));

        pastBooking1 = entityManager.persist(pastBooking1);
        pastBooking2 = entityManager.persist(pastBooking2);

        entityManager.flush();
        entityManager.clear();

        // Act
        Booking latestBooking = bookingRepository.findLatestBooking(item.getId());

        // Assert
        assertNotNull(latestBooking);
        assertEquals(pastBooking1.getId(), latestBooking.getId());
    }


    @Test
    public void findNextBooking_shouldReturnNextExpectedBooking() {
        // Arrange
        User owner = entityManager.persist(dataGenerator.getUser(null));
        User booker = entityManager.persist(dataGenerator.getUser(null));

        Item item = entityManager.persist(dataGenerator.getItem(null, owner, null));

        Booking futureBooking1 = dataGenerator.getBooking(null, item, booker);
        futureBooking1.setStatus(BookingStatus.APPROVED);
        futureBooking1.setStart(LocalDateTime.now().plusDays(2));
        futureBooking1.setEnd(LocalDateTime.now().plusDays(3));

        Booking futureBooking2 = dataGenerator.getBooking(null, item, booker);
        futureBooking2.setStatus(BookingStatus.APPROVED);
        futureBooking2.setStart(LocalDateTime.now().plusDays(4));
        futureBooking2.setEnd(LocalDateTime.now().plusDays(5));

        futureBooking1 = entityManager.persist(futureBooking1);
        futureBooking2 = entityManager.persist(futureBooking2);

        entityManager.flush();
        entityManager.clear();

        // Act
        Booking latestBooking = bookingRepository.findNextBooking(item.getId());

        // Assert
        assertNotNull(latestBooking);
        assertEquals(futureBooking1.getId(), latestBooking.getId());
    }
}
