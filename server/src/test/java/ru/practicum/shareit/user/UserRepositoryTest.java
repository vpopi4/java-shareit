package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.DataGenerator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(showSql = false)
@ExtendWith(SpringExtension.class)
class UserRepositoryTest {
    private final DataGenerator dataGenerator = new DataGenerator();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByEmail_shouldReturnUser_whenUserWithEmailExist() {
        // Arrange
        User createdUser = dataGenerator.getUser(null);

        entityManager.persistAndFlush(createdUser);

        // Act
        Optional<User> savedUser = userRepository.findByEmail(createdUser.getEmail());

        // Assert
        assertTrue(savedUser.isPresent(), "User should be found");
        assertEquals(createdUser.getId(), savedUser.get().getId(), "ID should match");
        assertEquals(createdUser.getEmail(), savedUser.get().getEmail(), "Email should match");
    }

    @Test
    public void findByEmail_shouldReturnEmptyOptional_whenUserWithEmailDoesNotExist() {
        // Arrange
        String email = dataGenerator.getFaker().internet().safeEmailAddress();

        // Act
        Optional<User> savedUser = userRepository.findByEmail(email);

        // Assert
        assertTrue(savedUser.isEmpty(), "User shouldn't be found");
    }

    @Test
    public void findByEmail_shouldReturnEmptyOptional_whenPassedEmptyString() {
        // Arrange
        String email = "";

        // Act
        Optional<User> savedUser = userRepository.findByEmail(email);

        // Assert
        assertTrue(savedUser.isEmpty(), "User shouldn't be found");
    }

    @Test
    public void findByEmail_shouldReturnEmptyOptional_whenPassedNull() {
        // Arrange
        String email = null;

        // Act
        Optional<User> savedUser = userRepository.findByEmail(email);

        // Assert
        assertTrue(savedUser.isEmpty(), "User shouldn't be found");
    }

    @Test
    public void findByEmail_shouldReturnUser_whenEmailNotValid() {
        // Arrange
        String email = "not_a_email.com";

        User createdUser = dataGenerator.getUser(null);
        createdUser.setEmail(email);

        userRepository.save(createdUser);

        // Act
        Optional<User> savedUser = userRepository.findByEmail(email);

        // Assert
        assertTrue(savedUser.isPresent(), "User should be found");
        assertEquals(createdUser.getId(), savedUser.get().getId(), "ID should match");
        assertEquals(createdUser.getEmail(), savedUser.get().getEmail(), "Email should match");
    }
}
