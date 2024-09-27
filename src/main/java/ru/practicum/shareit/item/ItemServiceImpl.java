package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper map;
    private int seq = 0;

    @Override
    public ItemDto.Response.PublicInfo createItem(Integer userId, ItemDto.Request.Create dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user not found"));

        Item item = map.toItem(++seq, dto, user);

        repository.save(item);

        return map.toDto(item);
    }

    @Override
    public ItemDto.Response.PublicInfo updatePartially(Integer userId,
                                                       Integer itemId,
                                                       ItemDto.Request.UpdatePartially dto) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user not found"));
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("item not found"));

        if (!Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new AccessDeniedException("editing denied");
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setIsAvailable(dto.getAvailable());
        }

        repository.save(item);

        return map.toDto(item);
    }

    @Override
    public ItemDto.Response.PublicInfo getById(Integer itemId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("item not found"));

        return map.toDto(item);
    }

    @Override
    public List<ItemDto.Response.PublicInfo> getAllByUserId(Integer userId) {
        return repository
                .findByUserId(userId)
                .stream()
                .map(map::toDto)
                .toList();
    }

    @Override
    public List<ItemDto.Response.PublicInfo> search(String text) {
        if (text != null && text.isBlank()) {
            return Collections.emptyList();
        }

        return repository
                .findByText(text)
                .stream()
                .map(map::toDto)
                .toList();
    }
}
