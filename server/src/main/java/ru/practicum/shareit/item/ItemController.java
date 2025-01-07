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
        log.info("POST /items: creating item: " +
                "X-Sharer-User-Id={}, body={}", userId, dto);

        ItemPublicDto response = service.createItem(userId, dto);

        log.info("POST /items: response body={}", response);

        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemPublicDto updatePartially(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId,
            @RequestBody ItemCreatingOrUpdatingDto dto
    ) throws ClientException {
        log.info("PATCH /items/{}: editing item: " +
                "X-Sharer-User-Id={}, body={}", itemId, userId, dto);

        ItemPublicDto response = service.updatePartially(userId, itemId, dto);

        log.info("PATCH /items/{}: response body={}", itemId, response);

        return response;
    }

    @GetMapping("/{itemId}")
    public ItemPublicDto getById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId
    ) throws ClientException {
        log.info("GET /items/{}: X-Sharer-User-Id={}", itemId, userId);

        ItemPublicDto response = service.getById(itemId);

        log.info("GET /items/{}: response body={}", itemId, response);

        return response;
    }

    @GetMapping
    public List<ItemPublicDto> getAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        log.info("GET /items: X-Sharer-User-Id={}", userId);

        List<ItemPublicDto> response = service.getAllByUserId(userId);

        log.info("GET /items: body={}", response);

        return response;
    }

    @GetMapping("/search")
    public List<ItemPublicDto> search(@RequestParam("text") String text) {
        log.info("GET /items/search?{}", text);

        List<ItemPublicDto> response = service.search(text);

        log.info("GET /items/search?{}: response body={}", text, response);

        return response;
    }

    //
    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("itemId") Integer itemId,
            @RequestBody CommentCreationDto dto
    ) throws ClientException {
        log.info("--> POST /items/{}/comment: posting a comment: " +
                "X-Sharer-User-Id={}, body={}", itemId, userId, dto);

        CommentDto response = service.postComment(userId, itemId, dto);

        log.info("<-- POST /items/{}/comment: response body={}", itemId, response);

        return response;
    }
}
