package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.request.UserCreatingDto;
import ru.practicum.shareit.user.dto.request.UserUpdatingDto;
import ru.practicum.shareit.user.dto.response.PublicUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ClientException;
import ru.practicum.shareit.util.DataGenerator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserService(new UserMapper(), userRepository);
    private final DataGenerator dataGenerator = new DataGenerator();

    @Test
    void getById_shouldReturnPublicUserDto_whenUserExists() throws ClientException {
        // Arrange
        Integer userId = dataGenerator.getNextId();
        User user = dataGenerator.getUser(userId);
        PublicUserDto expectedDto = PublicUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        PublicUserDto result = userService.getById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getById_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ClientException exception = assertThrows(
                ClientException.class,
                () -> userService.getById(userId)
        );

        assertEquals("user with such id not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void create_shouldCreateUser_whenValidDataProvided() throws ClientException {
        // Arrange
        User user = dataGenerator.getUser(null);
        user.setCreatedAt(null);

        UserCreatingDto dto = UserCreatingDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user1 = invocation.getArgument(0);
            user1.setId(dataGenerator.getNextId());
            return user1;
        });

        // Act
        PublicUserDto createdUser = userService.create(dto);

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getName(), createdUser.getName());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());

        User capturedUser = captor.getValue();
        assertEquals(user.getEmail(), capturedUser.getEmail());
        assertEquals(user.getName(), capturedUser.getName());
        assertNotNull(capturedUser.getCreatedAt());
    }

    @Test
    void create_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        User user = dataGenerator.getUser(dataGenerator.getNextId());

        UserCreatingDto dto = UserCreatingDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        ClientException exception = assertThrows(
                ClientException.class,
                () -> userService.create(dto)
        );

        assertEquals("such email already exist", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updatePartially_shouldUpdateUser_whenValidDataProvided() throws ClientException {
        // Arrange
        User existingUser = dataGenerator.getUser(dataGenerator.getNextId());
        User updatedData = dataGenerator.getUser(existingUser.getId());
        updatedData.setCreatedAt(existingUser.getCreatedAt());

        UserUpdatingDto dto = UserUpdatingDto.builder()
                .name(updatedData.getName())
                .email(updatedData.getEmail())
                .build();

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedData);

        // Act
        PublicUserDto updatedUser = userService.updatePartially(existingUser.getId(), dto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedData.getName(), updatedUser.getName());
        assertEquals(updatedData.getEmail(), updatedUser.getEmail());
        verify(userRepository, times(1)).save(updatedData);
    }

    @Test
    void deleteById_shouldDeleteUser_whenUserExists() {
        // Arrange
        User user = dataGenerator.getUser(dataGenerator.getNextId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        userService.deleteById(user.getId());

        // Assert
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}
