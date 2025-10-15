// CHECKSTYLE:OFF
package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    User testUser;
    User user2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Иван Петров");
        testUser.setEmail("ivan@example.com");

        user2 = new User();
        user2.setName("Мария Сидорова");
        user2.setEmail("maria@example.com");
    }

    @Test
    void testSaveUser_ShouldSaveAndReturnUser() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals("Иван Петров", savedUser.getName());
        assertEquals("ivan@example.com", savedUser.getEmail());
    }

    @Test
    void testFindById_ShouldReturnUser() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("Иван Петров", foundUser.get().getName());
        assertEquals("ivan@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindById_WithNonExistentId_ShouldReturnEmpty() {
        Optional<User> foundUser = userRepository.findById(999L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindAll_ShouldReturnAllUsers() {
        userRepository.save(testUser);
        userRepository.save(user2);
        entityManager.flush();

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Иван Петров")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Мария Сидорова")));
    }

    @Test
    void testDeleteById_ShouldDeleteUser() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        userRepository.deleteById(savedUser.getId());
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testExistsByEmailAndIdNot_ShouldReturnTrue() {
        userRepository.save(testUser);
        entityManager.flush();

        boolean exists = userRepository.existsByEmailAndIdNot("ivan@example.com", 999L);

        assertTrue(exists);
    }

    @Test
    void testExistsByEmailAndIdNot_WithSameId_ShouldReturnFalse() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        boolean exists = userRepository.existsByEmailAndIdNot("ivan@example.com", savedUser.getId());

        assertFalse(exists);
    }

    @Test
    void testFindByEmail_ShouldReturnUser() {
        userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByEmail("ivan@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("Иван Петров", foundUser.get().getName());
    }

    @Test
    void testFindByEmail_WithNonExistentEmail_ShouldReturnEmpty() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }
}