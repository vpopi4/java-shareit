package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.util.ClientException;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody ItemRequestCreationDto dto
    ) throws ClientException {
        log.info("--> POST /requests: userId={}, body={}", userId, dto);

        ResponseEntity<Object> response = itemRequestClient.createItemRequest(userId, dto);

        log.info("<-- POST /requests: response={}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) throws ClientException {
        log.info("--> GET /requests: userId={}", userId);

        ResponseEntity<Object> response = itemRequestClient.findAllByUserId(userId);

        log.info("<-- GET /requests: response={}", response);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() throws ClientException {
        log.info("--> GET /requests/all");

        ResponseEntity<Object> response = itemRequestClient.findAll();

        log.info("<-- GET /requests/all: response={}", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @PathVariable("requestId") Integer requestId
    ) throws ClientException {
        log.info("--> GET /requests/{}", requestId);

        ResponseEntity<Object> response = itemRequestClient.findById(requestId);

        log.info("<-- GET /requests/{}: response={}", requestId, response);
        return response;
    }
}
