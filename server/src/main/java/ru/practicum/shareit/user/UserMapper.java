package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.response.PublicUserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public PublicUserDto toPublicUserDto(User user) {
        return PublicUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
