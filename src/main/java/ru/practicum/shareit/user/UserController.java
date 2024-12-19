package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.AlreadyExistsException;
import ru.practicum.shareit.user.dto.request.UserCreatingDto;
import ru.practicum.shareit.user.dto.request.UserUpdatingDto;
import ru.practicum.shareit.user.dto.response.PublicUserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping("/{id}")
    public PublicUserDto getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public PublicUserDto create(@Valid @RequestBody UserCreatingDto dto) throws AlreadyExistsException {
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public PublicUserDto updatePartially(@PathVariable Integer id,
                                         @Valid @RequestBody UserUpdatingDto dto) throws AlreadyExistsException {
        return service.updatePartially(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        service.deleteById(id);
    }
}
