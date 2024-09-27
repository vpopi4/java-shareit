package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.AlreadyExistsException;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper map;
    private final UserRepository storage;
    private int seq = 0;

    public UserDto.Response.PublicInfo getById(Integer id) throws NoSuchElementException {
        User user = storage.findById(id)
                .orElseThrow(() -> new NoSuchElementException("user with such id not found"));

        return map.toDto(user);
    }

    public UserDto.Response.PublicInfo create(UserDto.Request.Create dto) throws AlreadyExistsException {
        checkEmailUnique(dto.getEmail());

        int id = ++seq;

        User user = map.toUser(id, dto);

        storage.save(user);

        return map.toDto(user);
    }

    public UserDto.Response.PublicInfo updatePartially(Integer id,
                                                       UserDto.Request.UpdatePartially dto) throws AlreadyExistsException {
        User user = storage.findById(id)
                .orElseThrow(() -> new NoSuchElementException("user with such id not found"));

        String email = dto.getEmail();

        if (email != null) {
            checkEmailUnique(email);
            user.setEmail(email);
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        storage.update(user);

        return map.toDto(user);
    }

    public void deleteById(Integer id) {
        storage.deleteById(id);
    }

    private void checkEmailUnique(String email) throws AlreadyExistsException {
        if (storage.findByEmail(email).isPresent()) {
            throw new AlreadyExistsException("such email already exist");
        }
    }
}
