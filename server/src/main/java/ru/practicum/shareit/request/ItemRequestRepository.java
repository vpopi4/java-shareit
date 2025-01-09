package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    @Query("SELECT r " +
            "FROM ItemRequest r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.requester.id = :requesterId")
    List<ItemRequest> findAllByRequesterId(@Param("requesterId") Integer requesterId);

    @Query("SELECT r " +
            "FROM ItemRequest r " +
            "ORDER BY r.createdAt DESC")
    List<ItemRequest> findAllOrderByCreatedAtDesc();

    @Query("SELECT r " +
            "FROM ItemRequest r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.id = :requestId")
    Optional<ItemRequest> findByIdWithItems(@Param("requestId") Integer requestId);
}
