package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.AlreadyExistsException;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping("/{id}")
    public UserDto.Response.PublicInfo getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public UserDto.Response.PublicInfo create(@Valid @RequestBody UserDto.Request.Create dto) throws AlreadyExistsException {
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public UserDto.Response.PublicInfo updatePartially(@PathVariable Integer id,
                                                       @Valid @RequestBody UserDto.Request.UpdatePartially dto) throws AlreadyExistsException {
        return service.updatePartially(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        service.deleteById(id);
    }
}
