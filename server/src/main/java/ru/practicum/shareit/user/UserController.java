package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.request.UserCreatingDto;
import ru.practicum.shareit.user.dto.request.UserUpdatingDto;
import ru.practicum.shareit.user.dto.response.PublicUserDto;
import ru.practicum.shareit.util.ClientException;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping("/{id}")
    public PublicUserDto getById(@PathVariable Integer id) throws ClientException {
        log.info("GET /users/{}: getting by user id", id);

        PublicUserDto response = service.getById(id);

        log.info("GET /users/{}: response body={}", id, response);

        return response;
    }

    @PostMapping
    public PublicUserDto create(@Valid @RequestBody UserCreatingDto dto) throws ClientException {
        log.info("POST /users: creating user: body={}", dto);

        PublicUserDto response = service.create(dto);

        log.info("POST /users: response body={}", response);

        return response;
    }

    @PatchMapping("/{id}")
    public PublicUserDto updatePartially(
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdatingDto dto
    ) throws ClientException {
        log.info("PATCH /users/{}: editing user: body={}", id, dto);

        PublicUserDto response = service.updatePartially(id, dto);

        log.info("PATCH /users/{}: response body={}", id, response);

        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        log.info("DELETE /users/{}: deleting user", id);

        service.deleteById(id);
    }
}
