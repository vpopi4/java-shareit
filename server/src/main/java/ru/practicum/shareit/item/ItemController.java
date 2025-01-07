package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreatingOrUpdatingDto;
import ru.practicum.shareit.item.dto.ItemPublicDto;
import ru.practicum.shareit.util.ClientException;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemPublicDto createItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody ItemCreatingOrUpdatingDto dto
    ) throws ClientException {
        log.info("---> Creating item: userId={}, body={}", userId, dto);

        return service.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemPublicDto updatePartially(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId,
            @RequestBody ItemCreatingOrUpdatingDto dto
    ) throws ClientException {
        log.info("---> Editing item[id={}]: userId={}, body={}", itemId, userId, dto);

        return service.updatePartially(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemPublicDto getById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId
    ) throws ClientException {
        return service.getById(itemId);
    }

    @GetMapping
    public List<ItemPublicDto> getAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        return service.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemPublicDto> search(@RequestParam("text") String text) {
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("itemId") Integer itemId,
            @RequestBody CommentCreationDto dto
    ) throws ClientException {
        log.info("---> Posting a comment to item[id={}]: userId={}, body={}", itemId, userId, dto);

        return service.postComment(userId, itemId, dto);
    }
}
