// CHECKSTYLE:OFF
package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class UserTest {

    @Test
    void testUserCreation_ShouldCreateUserWithAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Тестовый пользователь");
        user.setEmail("test@example.com");

        assertEquals(1L, user.getId());
        assertEquals("Тестовый пользователь", user.getName());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testUserAllArgsConstructor_ShouldCreateUserWithAllFields() {
        User user = new User(1L, "Тестовый пользователь", "test@example.com");

        assertEquals(1L, user.getId());
        assertEquals("Тестовый пользователь", user.getName());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testUserNoArgsConstructor_ShouldCreateEmptyUser() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    void testUserEquals_ShouldReturnTrueForSameUsers() {
        User user1 = new User(1L, "Тестовый пользователь", "test@example.com");
        User user2 = new User(1L, "Тестовый пользователь", "test@example.com");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testUserEquals_ShouldReturnFalseForDifferentUsers() {
        User user1 = new User(1L, "Тестовый пользователь", "test@example.com");
        User user2 = new User(2L, "Другой пользователь", "other@example.com");

        assertNotEquals(user1, user2);
    }

    @Test
    void testUserToString_ShouldContainAllFields() {
        User user = new User(1L, "Тестовый пользователь", "test@example.com");
        String toString = user.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Тестовый пользователь"));
        assertTrue(toString.contains("test@example.com"));
    }
}