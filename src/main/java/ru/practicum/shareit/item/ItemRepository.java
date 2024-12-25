package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.comments " +
            "WHERE i.id = :itemId")
    Optional<Item> findByIdWithComments(@Param("itemId") Integer itemId);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.comments " +
            "WHERE i.owner.id = :ownerId")
    List<Item> findByOwnerId(@Param("ownerId") Integer ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.isAvailable = true " +
            "AND (" +
            "     LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "     OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))" +
            ")")
    List<Item> findByText(@Param("text") String text);
}
