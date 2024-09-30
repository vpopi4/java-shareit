package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> findById(Integer id);

    List<Item> findByUserId(Integer userId);

    List<Item> findByText(String text);

    Item save(Item item);

    Item update(Item item);

    void deleteById(Integer id);
}
