package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreatingDto;
import ru.practicum.shareit.item.dto.ItemUpdatingDto;
import ru.practicum.shareit.util.ClientException;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody ItemCreatingDto dto
    ) throws ClientException {
        log.info("--> POST /items: userId={}, body={}", userId, dto);

        ResponseEntity<Object> response = client.createItem(userId, dto);

        log.info("<-- POST /items: response={}", response);

        return response;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updatePartially(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId,
            @Valid @RequestBody ItemUpdatingDto dto
    ) throws ClientException {
        log.info("--> PATCH /items/{}: userId={}, body={}", itemId, userId, dto);

        ResponseEntity<Object> response = client.updatePartially(userId, itemId, dto);

        log.info("<-- PATCH /items/{}: response={}", itemId, response);

        return response;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId
    ) throws ClientException {
        log.info("--> GET /items/{}: userId={}", itemId, userId);

        ResponseEntity<Object> response = client.getById(itemId);

        log.info("<-- GET /items/{}: response={}", itemId, response);

        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        log.info("--> GET /items: userId={}", userId);

        ResponseEntity<Object> response = client.getAllByUserId(userId);

        log.info("<-- GET /items: body={}", response);

        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text) {
        log.info("--> GET /items/search?{}", text);

        ResponseEntity<Object> response = client.search(text);

        log.info("<-- GET /items/search?{}: response={}", text, response);

        return response;
    }

    //
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable("itemId") Integer itemId,
            @RequestBody CommentCreationDto dto
    ) throws ClientException {
        log.info("--> POST /items/{}/comment: userId={}, body={}", itemId, userId, dto);

        ResponseEntity<Object> response = client.postComment(userId, itemId, dto);

        log.info("<-- POST /items/{}/comment: response={}", itemId, response);

        return response;
    }
}
