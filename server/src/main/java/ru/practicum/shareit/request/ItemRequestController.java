package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.util.ClientException;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestShortDto createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody ItemRequestCreationDto dto
    ) throws ClientException {
        log.info("---> Creating item request");

        return service.createItemRequest(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) throws ClientException {
        log.info("---> Getting item requests of user[id={}]", userId);

        return service.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestShortDto> findAll(
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer from
    ) throws ClientException {
        log.info("---> Getting {} item requests from {} index", size, from);

        return service.findAll(from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable("requestId") Integer requestId) throws ClientException {
        log.info("---> Getting item request[id={}]", requestId);

        return service.findById(requestId);
    }
}
