package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.DataGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@ExtendWith(SpringExtension.class)
class ItemRequestRepositoryTest {
    private final DataGenerator dataGenerator = new DataGenerator();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void findAllByRequesterId_shouldReturnRequestsOfSpecificUser() {
        // Arrange
        User user = dataGenerator.getUser(null);
        entityManager.persist(user);

        ItemRequest request1 = entityManager.persist(dataGenerator.getItemRequest(null, user));
        ItemRequest request2 = entityManager.persist(dataGenerator.getItemRequest(null, user));

        User anotherUser = dataGenerator.getUser(null);
        entityManager.persist(anotherUser);

        ItemRequest request3 = entityManager.persist(dataGenerator.getItemRequest(null, anotherUser));

        // Act
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(user.getId());

        // Assert
        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertTrue(requests.contains(request1));
        assertTrue(requests.contains(request2));
        assertFalse(requests.contains(request3));
    }

    @Test
    public void findAllOrderByCreatedAtDesc_shouldReturnRequestsSortedByCreatedAtDesc() {
        // Arrange
        User user = dataGenerator.getUser(null);
        entityManager.persist(user);

        ItemRequest request1 = dataGenerator.getItemRequest(null, user);
        request1.setCreatedAt(LocalDateTime.now().minusDays(3));
        request1 = entityManager.persist(request1);

        ItemRequest request2 = dataGenerator.getItemRequest(null, user);
        request2.setCreatedAt(LocalDateTime.now().minusDays(2));
        request2 = entityManager.persist(request2);

        ItemRequest request3 = dataGenerator.getItemRequest(null, user);
        request3.setCreatedAt(LocalDateTime.now().minusDays(1));
        request3 = entityManager.persist(request3);

        // Act
        List<ItemRequest> requests = itemRequestRepository.findAllOrderByCreatedAtDesc();

        // Assert
        assertNotNull(requests);
        assertEquals(3, requests.size());
        assertEquals(request3.getId(), requests.get(0).getId());
        assertEquals(request2.getId(), requests.get(1).getId());
        assertEquals(request1.getId(), requests.get(2).getId());
    }

    @Test
    public void findByIdWithItems_shouldReturnRequestWithItems_whenItemsExist() {
        // Arrange
        User user = dataGenerator.getUser(null);
        entityManager.persist(user);

        ItemRequest itemRequest = entityManager.persist(dataGenerator.getItemRequest(null, user));

        Item item1 = entityManager.persist(dataGenerator.getItem(null, user, itemRequest));
        Item item2 = entityManager.persist(dataGenerator.getItem(null, user, itemRequest));

        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<ItemRequest> result = itemRequestRepository.findByIdWithItems(itemRequest.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(itemRequest.getId(), result.get().getId());
        assertNotNull(result.get().getItems());
        assertEquals(2, result.get().getItems().size());
        assertTrue(result.get().getItems().stream().anyMatch(item -> item.getId().equals(item1.getId())));
        assertTrue(result.get().getItems().stream().anyMatch(item -> item.getId().equals(item2.getId())));
    }
}
