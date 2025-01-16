package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreatingDto;
import ru.practicum.shareit.user.dto.UserUpdatingDto;
import ru.practicum.shareit.util.ClientException;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient client;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) throws ClientException {
        log.info("--> GET /users/{}", id);

        ResponseEntity<Object> response = client.getById(id);

        log.info("<-- GET /users/{}: response={}", id, response);

        return response;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreatingDto dto) throws ClientException {
        log.info("--> POST /users: body={}", dto);

        ResponseEntity<Object> response = client.create(dto);

        log.info("<-- POST /users: response={}", response);

        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updatePartially(
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdatingDto dto
    ) throws ClientException {
        log.info("--> PATCH /users/{}: body={}", id, dto);

        ResponseEntity<Object> response = client.updatePartially(id, dto);

        log.info("<-- PATCH /users/{}: response={}", id, response);

        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        log.info("--> DELETE /users/{}", id);

        client.deleteById(id);

        log.info("<-- DELETE /users/{}: no content", id);

        return ResponseEntity.noContent().build();
    }
}
