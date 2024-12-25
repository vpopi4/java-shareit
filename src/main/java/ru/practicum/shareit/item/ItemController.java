package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.ClientException;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto.Response.PublicInfo createItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody ItemDto.Request.Create dto
    ) throws ClientException {
        log.info("POST /items: creating item: " +
                "X-Sharer-User-Id={}, body={}", userId, dto);

        ItemDto.Response.PublicInfo response = service.createItem(userId, dto);

        log.info("POST /items: response body={}", response);

        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemDto.Response.PublicInfo updatePartially(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId,
            @Valid @RequestBody ItemDto.Request.UpdatePartially dto
    ) throws ClientException {
        log.info("PATCH /items/{}: editing item: " +
                "X-Sharer-User-Id={}, body={}", itemId, userId, dto);

        ItemDto.Response.PublicInfo response = service.updatePartially(userId, itemId, dto);

        log.info("PATCH /items/{}: response body={}", itemId, response);

        return response;
    }

    @GetMapping("/{itemId}")
    public ItemDto.Response.PublicInfo getById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId
    ) throws ClientException {
        log.info("GET /items/{}: X-Sharer-User-Id={}", itemId, userId);

        ItemDto.Response.PublicInfo response = service.getById(itemId);

        log.info("GET /items/{}: response body={}", itemId, response);

        return response;
    }

    @GetMapping
    public List<ItemDto.Response.PublicInfo> getAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        log.info("GET /items: X-Sharer-User-Id={}", userId);

        List<ItemDto.Response.PublicInfo> response = service.getAllByUserId(userId);

        log.info("GET /items: body={}", response);

        return response;
    }

    @GetMapping("/search")
    public List<ItemDto.Response.PublicInfo> search(@RequestParam("text") String text) {
        log.info("GET /items/search?{}", text);

        List<ItemDto.Response.PublicInfo> response = service.search(text);

        log.info("GET /items/search?{}: response body={}", text, response);

        return response;
    }
}
