package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ItemInMemoryRepository implements ItemRepository {
    private final HashMap<Integer, Item> storage;
    private final HashMap<Integer, List<Item>> itemsByOwner;

    @Override
    public Optional<Item> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Item> findByUserId(Integer userId) {
        return itemsByOwner.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public List<Item> findByText(String text) {
        return storage
                .values()
                .stream()
                .filter(Item::getIsAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    @Override
    public Item save(Item item) {
        storage.put(item.getId(), item);

        addToItemsByOwner(item);

        return item;
    }

    @Override
    public Item update(Item item) {
        return save(item);
    }

    @Override
    public void deleteById(Integer id) {
        findById(id).ifPresent(item -> {
            storage.remove(id);
            removeFromItemsByOwner(item);
        });
    }

    private void addToItemsByOwner(Item item) {
        Integer ownerId = item.getOwner().getId();

        List<Item> items = itemsByOwner.getOrDefault(ownerId, new ArrayList<>());

        items.add(item);

        itemsByOwner.put(ownerId, items);
    }

    private void removeFromItemsByOwner(Item item) {
        Integer ownerId = item.getOwner().getId();

        List<Item> items = itemsByOwner.getOrDefault(ownerId, new ArrayList<>());

        items.remove(item);

        itemsByOwner.put(ownerId, items);
    }
}
