package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.request.UserCreatingDto;
import ru.practicum.shareit.user.dto.request.UserUpdatingDto;
import ru.practicum.shareit.user.dto.response.PublicUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.DataGenerator;
import ru.practicum.shareit.util.NotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private final UserMapper userMapper = new UserMapper();
    private final DataGenerator dataGenerator = new DataGenerator();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getById_shouldReturnUser() throws Exception {
        PublicUserDto userDto = userMapper.toPublicUserDto(dataGenerator.getUser(1));

        Mockito.when(userService.getById(1)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        User user = dataGenerator.getUser(2);
        PublicUserDto returnedUserDto = userMapper.toPublicUserDto(user);
        UserCreatingDto creatingDto = UserCreatingDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        Mockito.when(userService.create(any(UserCreatingDto.class))).thenReturn(returnedUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creatingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void updatePartially_shouldReturnUpdatedUser() throws Exception {
        User user = dataGenerator.getUser(2);
        UserUpdatingDto updatingDto = UserUpdatingDto.builder()
                .name("Jane Smith")
                .email("jane.doe@example.com")
                .build();

        user.setName(updatingDto.getName());
        user.setEmail(updatingDto.getEmail());

        PublicUserDto userDto = userMapper.toPublicUserDto(user);

        Mockito.when(userService.updatePartially(eq(2), any(UserUpdatingDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }

    @Test
    void deleteById_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteById(1);
    }

    @Test
    void getById_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        Mockito.when(userService.getById(1)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
