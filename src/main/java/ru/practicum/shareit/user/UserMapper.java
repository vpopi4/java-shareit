package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto.Response.PublicInfo toDto(User user) {
        return UserDto.Response.PublicInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public User toUser(Integer id, UserDto.Request.Create dto) {
        return User.builder()
                .id(id)
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }
}
