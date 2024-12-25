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

        return service.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto.Response.PublicInfo updatePartially(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId,
            @Valid @RequestBody ItemDto.Request.UpdatePartially dto
    ) throws ClientException {
        log.info("PATCH /items/{}: editing item: " +
                "X-Sharer-User-Id={}, body={}", itemId, userId, dto);

        return service.updatePartially(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto.Response.PublicInfo getById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId
    ) throws ClientException {
        log.info("GET /items/{}: X-Sharer-User-Id={}", itemId, userId);

        return service.getById(itemId);
    }

    @GetMapping
    public List<ItemDto.Response.PublicInfo> getAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        log.info("GET /items: X-Sharer-User-Id={}", userId);

        return service.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto.Response.PublicInfo> search(@RequestParam("text") String text) {
        log.info("GET /items/search?{}", text);

        return service.search(text);
    }
}
