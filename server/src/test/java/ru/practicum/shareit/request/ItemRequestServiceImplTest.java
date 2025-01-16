package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ClientException;
import ru.practicum.shareit.util.DataGenerator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRequestService itemRequestService;
    private DataGenerator dataGenerator;

    @BeforeEach
    void setUp() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository);
        dataGenerator = new DataGenerator();
    }

    @Test
    void createItemRequest_shouldCreateRequestSuccessfully() throws ClientException {
        // Arrange
        int userId = dataGenerator.getNextId();
        User user = dataGenerator.getUser(userId);
        ItemRequestCreationDto dto = new ItemRequestCreationDto(dataGenerator.getItemDescription());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest request = invocation.getArgument(0);
            request.setId(dataGenerator.getNextId());
            return request;
        });

        // Act
        ItemRequestShortDto result = itemRequestService.createItemRequest(userId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }


    @Test
    void createItemRequest_shouldThrowNotFoundException_whenUserNotFound() {
        // Arrange
        int userId = dataGenerator.getNextId();
        ItemRequestCreationDto dto = new ItemRequestCreationDto(dataGenerator.getItemDescription());

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> itemRequestService.createItemRequest(userId, dto)
        );
    }

    @Test
    void findAllByUserId_shouldReturnRequestsSuccessfully() throws ClientException {
        // Arrange
        int userId = dataGenerator.getNextId();
        User user = dataGenerator.getUser(userId);
        ItemRequest request = dataGenerator.getItemRequest(dataGenerator.getNextId(), user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(userId)).thenReturn(List.of(request));

        // Act
        List<ItemRequestDto> result = itemRequestService.findAllByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getDescription(), result.get(0).getDescription());
    }

    @Test
    void findAllByUserId_shouldThrowNotFoundException_whenUserNotFound() {
        // Arrange
        int userId = dataGenerator.getNextId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> itemRequestService.findAllByUserId(userId)
        );
    }

    @Test
    void findAll_shouldReturnAllRequestsSuccessfully() throws ClientException {
        // Arrange
        User user = dataGenerator.getUser(dataGenerator.getNextId());
        ItemRequest request = dataGenerator.getItemRequest(dataGenerator.getNextId(), user);

        when(itemRequestRepository.findAllOrderByCreatedAtDesc(0, 10)).thenReturn(List.of(request));

        // Act
        List<ItemRequestShortDto> result = itemRequestService.findAll(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getDescription(), result.get(0).getDescription());
    }

    @Test
    void findById_shouldMapItemsSuccessfully() throws ClientException {
        // Arrange
        int requestId = dataGenerator.getNextId();
        User requester = dataGenerator.getUser(dataGenerator.getNextId());
        User owner = dataGenerator.getUser(dataGenerator.getNextId());
        ItemRequest request = dataGenerator.getItemRequest(requestId, requester);

        Item item1 = dataGenerator.getItem(dataGenerator.getNextId(), owner, request);
        Item item2 = dataGenerator.getItem(dataGenerator.getNextId(), owner, request);

        request.setItems(List.of(item1, item2)); // Устанавливаем список items

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        // Act
        ItemRequestDto result = itemRequestService.findById(requestId);

        // Assert
        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(2, result.getItems().size());
        assertEquals(item1.getName(), result.getItems().get(0).getName());
        assertEquals(item2.getName(), result.getItems().get(1).getName());
        verify(itemRequestRepository, times(1)).findById(requestId);
    }

    @Test
    void findById_shouldThrowNotFoundException_whenRequestNotFound() {
        // Arrange
        int requestId = dataGenerator.getNextId();

        when(itemRequestRepository.findByIdWithItems(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> itemRequestService.findById(requestId)
        );
    }
}
