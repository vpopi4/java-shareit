package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemCreatingDto;
import ru.practicum.shareit.item.dto.ItemPublicDto;
import ru.practicum.shareit.item.dto.ItemUpdatingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ClientException;
import ru.practicum.shareit.util.DataGenerator;
import ru.practicum.shareit.util.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;
    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;
    private ItemServiceImpl itemService;
    private DataGenerator dataGenerator;

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemMapper = new ItemMapper();
        bookingMapper = new BookingMapper(new UserMapper(), itemMapper);
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository,
                itemMapper,
                bookingMapper
        );
        dataGenerator = new DataGenerator();
    }

    @Test
    void createItem_shouldCreateItem_whenValidDataProvided() throws ClientException {
        // Arrange
        User user = dataGenerator.getUser(dataGenerator.getNextId());
        ItemCreatingDto dto = getItemCreatingDto();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            Item returnedItem = invocationOnMock.getArgument(0);
            returnedItem.setId(dataGenerator.getNextId());
            return returnedItem;
        });

        // Act
        ItemPublicDto createdItem = itemService.createItem(user.getId(), dto);

        // Assert
        assertNotNull(createdItem);
        assertEquals(dto.getName(), createdItem.getName());
        assertEquals(dto.getDescription(), createdItem.getDescription());
        assertEquals(dto.getAvailable(), createdItem.getAvailable());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowNotFoundException_whenUserNotFound() {
        // Arrange
        int userId = 1;
        ItemCreatingDto dto = getItemCreatingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> itemService.createItem(userId, dto));
        verify(itemRepository, never()).save(any(Item.class));
    }


    @Test
    void updatePartially_shouldUpdateItem_whenUserIsOwner() throws ClientException {
        // Arrange
        User user = dataGenerator.getUser(dataGenerator.getNextId());
        Item item = dataGenerator.getItem(dataGenerator.getNextId(), user, null);

        ItemUpdatingDto dto = getItemUpdatingDto();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // Act
        ItemPublicDto updatedItem = itemService.updatePartially(user.getId(), item.getId(), dto);

        // Assert
        assertNotNull(updatedItem);
        assertEquals(dto.getName(), updatedItem.getName());
        assertEquals(dto.getDescription(), updatedItem.getDescription());
        assertEquals(dto.getAvailable(), updatedItem.getAvailable());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updatePartially_shouldThrowForbiddenException_whenUserIsNotOwner() {
        // Arrange
        User owner = dataGenerator.getUser(dataGenerator.getNextId());
        User notAnOwner = dataGenerator.getUser(dataGenerator.getNextId());

        Item item = dataGenerator.getItem(dataGenerator.getNextId(), owner, null);
        ItemUpdatingDto dto = getItemUpdatingDto();

        when(userRepository.findById(notAnOwner.getId())).thenReturn(Optional.of(notAnOwner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> itemService.updatePartially(notAnOwner.getId(), item.getId(), dto)
        );

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getById_shouldReturnItem_whenItemExists() throws ClientException {
        // Arrange
        User user = dataGenerator.getUser(dataGenerator.getNextId());
        Item item = dataGenerator.getItem(dataGenerator.getNextId(), user, null);

        when(itemRepository.findByIdWithComments(item.getId())).thenReturn(Optional.of(item));

        // Act
        ItemPublicDto result = itemService.getById(item.getId());

        // Assert
        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        verify(itemRepository, times(1)).findByIdWithComments(item.getId());
    }

    @Test
    void getById_shouldThrowNotFoundException_whenItemDoesNotExist() {
        // Arrange
        int itemId = 1;
        when(itemRepository.findByIdWithComments(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ClientException.class,
                () -> itemService.getById(itemId)
        );

        verify(itemRepository, times(1)).findByIdWithComments(itemId);
    }

    private ItemCreatingDto getItemCreatingDto() {
        return ItemCreatingDto.builder()
                .name(dataGenerator.getLorem(3))
                .description(dataGenerator.getLorem(12))
                .available(true)
                .requestId(null)
                .build();
    }

    private ItemUpdatingDto getItemUpdatingDto() {
        return ItemUpdatingDto.builder()
                .name(dataGenerator.getLorem(3))
                .description(dataGenerator.getLorem(12))
                .available(false)
                .build();
    }
}
