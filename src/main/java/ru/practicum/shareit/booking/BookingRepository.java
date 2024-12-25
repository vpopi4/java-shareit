package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllBookingsByBooker(@Param("bookerId") Integer bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.startDate <= CURRENT_TIMESTAMP " +
            "AND b.endDate >= CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.startDate DESC")
    List<Booking> findCurrentBookingsByBooker(@Param("bookerId") Integer bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.endDate < CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findPastBookingsByBooker(@Param("bookerId") Integer bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.startDate > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findFutureBookingsByBooker(@Param("bookerId") Integer bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "ORDER BY b.startDate DESC")
    List<Booking> findByStatusAndBooker(@Param("status") BookingStatus status,
                                        @Param("bookerId") Integer bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :ownerId " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllBookingsByOwner(@Param("ownerId") Integer ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :ownerId " +
            "AND b.startDate <= CURRENT_TIMESTAMP " +
            "AND b.endDate >= CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.startDate DESC")
    List<Booking> findCurrentBookingsByOwner(@Param("ownerId") Integer ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :ownerId " +
            "AND b.endDate < CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findPastBookingsByOwner(@Param("ownerId") Integer ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :ownerId " +
            "AND b.startDate > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findFutureBookingsByOwner(@Param("ownerId") Integer ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.startDate DESC")
    List<Booking> findByStatusAndOwner(@Param("status") BookingStatus status,
                                       @Param("ownerId") Integer ownerId);

}
