package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    @Query("SELECT r " +
            "FROM ItemRequest r " +
            "WHERE r.requester.id = :requesterId " +
            "ORDER BY r.createdAt DESC")
    List<ItemRequest> findAllByRequesterId(@Param("requesterId") Integer requesterId);

    @Query("SELECT r " +
            "FROM ItemRequest r " +
            "ORDER BY r.createdAt DESC " +
            "LIMIT :size OFFSET :from")
    List<ItemRequest> findAllOrderByCreatedAtDesc(@Param("from") Integer from,
                                                  @Param("size") Integer size);

    @Query("SELECT r " +
            "FROM ItemRequest r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.id = :requestId")
    Optional<ItemRequest> findByIdWithItems(@Param("requestId") Integer requestId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.request.id IN :requestIds")
    List<Item> findItemsByRequestIds(@Param("requestIds") List<Integer> requestIds);
}
