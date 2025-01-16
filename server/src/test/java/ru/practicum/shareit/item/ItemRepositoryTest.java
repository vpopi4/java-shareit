package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.DataGenerator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@ExtendWith(SpringExtension.class)
class ItemRepositoryTest {
    private final DataGenerator dataGenerator = new DataGenerator();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findByIdWithComments_shouldReturnItemWithComment_whenCommentExist() {
        // Arrange
        User user1 = entityManager.persist(dataGenerator.getUser(null));
        User user2 = entityManager.persist(dataGenerator.getUser(null));
        Item item = entityManager.persist(dataGenerator.getItem(null, user1, null));
        Comment comment = entityManager.persist(dataGenerator.getComment(null, item, user2));

        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Item> result = itemRepository.findByIdWithComments(item.getId());

        // Arrange
        assertTrue(result.isPresent());
        assertEquals(item.getId(), result.get().getId());
        assertNotNull(result.get().getComments());
        assertEquals(1, result.get().getComments().size());
        assertEquals(comment.getText(), result.get().getComments().get(0).getText());
    }

    @Test
    public void findByIdWithComments_shouldReturnItemWithoutComment_whenCommentDoesNotExist() {
        // Arrange
        User user = entityManager.persistAndFlush(dataGenerator.getUser(null));
        Item item = entityManager.persistAndFlush(dataGenerator.getItem(null, user, null));

        // Act
        Optional<Item> result = itemRepository.findByIdWithComments(item.getId());

        // Arrange
        assertTrue(result.isPresent());
        assertEquals(item.getId(), result.get().getId());
        assertTrue(result.get().getComments().isEmpty());
    }

    @Test
    public void findByOwnerId_shouldReturnOnlyOwnedItems_whenUserHaveItems() {
        // Arrange
        User user1 = entityManager.persistAndFlush(dataGenerator.getUser(null));
        User user2 = entityManager.persistAndFlush(dataGenerator.getUser(null));
        User user3 = entityManager.persistAndFlush(dataGenerator.getUser(null));

        Item item1 = entityManager.persistAndFlush(dataGenerator.getItem(null, user1, null));
        Item item2 = entityManager.persistAndFlush(dataGenerator.getItem(null, user1, null));
        Item item3 = entityManager.persistAndFlush(dataGenerator.getItem(null, user1, null));

        entityManager.persistAndFlush(dataGenerator.getItem(null, user2, null));
        entityManager.persistAndFlush(dataGenerator.getItem(null, user3, null));

        // Act
        List<Item> items = itemRepository.findByOwnerId(user1.getId());

        // Assert
        assertEquals(3, items.size());

        assertTrue(items.stream().anyMatch(item -> item.getId().equals(item1.getId())));
        assertTrue(items.stream().anyMatch(item -> item.getId().equals(item2.getId())));
        assertTrue(items.stream().anyMatch(item -> item.getId().equals(item3.getId())));

        for (Item item : items) {
            assertEquals(user1.getId(), item.getOwner().getId());
        }
    }

    @Test
    void findByText_shouldReturn2Items_when2ItemsHaveSearchedSubstring() {
        // Arrange
        User owner = dataGenerator.getUser(null);
        entityManager.persist(owner);

        Item item1 = dataGenerator.getItem(null, owner, null);
        item1.setName("Lorem ipsum dollar");
        entityManager.persist(item1);

        Item item2 = dataGenerator.getItem(null, owner, null);
        entityManager.persist(item2);

        Item item3 = dataGenerator.getItem(null, owner, null);
        item3.setDescription("Fake 100 US Dollar");
        entityManager.persist(item3);

        // Act
        List<Item> result = itemRepository.findByText("dollar");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getId().equals(item1.getId())));
        assertTrue(result.stream().anyMatch(item -> item.getId().equals(item3.getId())));
    }

    @Test
    void findByText_shouldReturnOnly1Item_whenOnlyOneItemHasSearchedSubstring() {
        // Arrange
        User owner = dataGenerator.getUser(null);
        entityManager.persist(owner);

        Item item1 = dataGenerator.getItem(null, owner, null);
        item1.setName("Lorem ipsum dollar");
        entityManager.persist(item1);

        Item item2 = dataGenerator.getItem(null, owner, null);
        item2.setName("Lorem ipsum");
        entityManager.persist(item2);

        // Act
        List<Item> result = itemRepository.findByText("dollar");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getId().equals(item1.getId())));
        assertFalse(result.stream().anyMatch(item -> item.getId().equals(item2.getId())));
    }

    @Test
    void findByText_shouldReturnEmptyList_whenItemHasSearchedSubstringButNotAvailable() {
        // Arrange
        User owner = dataGenerator.getUser(null);
        entityManager.persist(owner);

        Item item1 = dataGenerator.getItem(null, owner, null);
        item1.setName("Lorem ipsum dollar");
        item1.setIsAvailable(false);
        entityManager.persist(item1);

        Item item2 = dataGenerator.getItem(null, owner, null);
        item2.setName("Lorem ipsum");
        entityManager.persist(item2);

        // Act
        List<Item> result = itemRepository.findByText("dollar");

        // Assert
        assertEquals(0, result.size());
        assertFalse(result.stream().anyMatch(item -> item.getId().equals(item1.getId())));
        assertFalse(result.stream().anyMatch(item -> item.getId().equals(item2.getId())));
    }
}
