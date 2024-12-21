package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.request.UserCreatingDto;
import ru.practicum.shareit.user.dto.request.UserUpdatingDto;
import ru.practicum.shareit.user.dto.response.PublicUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.AlreadyExistsException;
import ru.practicum.shareit.util.ClientException;
import ru.practicum.shareit.util.NotFoundException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper map;
    private final UserRepository storage;

    public PublicUserDto getById(Integer id) throws ClientException {
        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("user with such id not found"));

        return map.toPublicUserDto(user);
    }

    public PublicUserDto create(UserCreatingDto dto) throws ClientException {
        checkEmailUnique(dto.getEmail());

        User user = User.builder()
                .id(null)
                .email(dto.getEmail())
                .name(dto.getName())
                .createdAt(LocalDateTime.now())
                .build();

        return saveAndReturnDto(user);
    }

    public PublicUserDto updatePartially(Integer id,
                                         UserUpdatingDto dto) throws ClientException {
        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("user with such id not found"));

        String email = dto.getEmail();

        if (email != null) {
            checkEmailUnique(email);
            user.setEmail(email);
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        return saveAndReturnDto(user);
    }

    private PublicUserDto saveAndReturnDto(User user) {
        User savedUser = storage.save(user);

        return map.toPublicUserDto(savedUser);
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
