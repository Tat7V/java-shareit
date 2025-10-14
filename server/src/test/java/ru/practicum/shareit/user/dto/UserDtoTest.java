// CHECKSTYLE:OFF
package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class UserDtoTest {

    @Test
    void testUserDtoCreation_ShouldCreateUserDtoWithAllFields() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовый пользователь");
        userDto.setEmail("test@example.com");

        assertEquals(1L, userDto.getId());
        assertEquals("Тестовый пользователь", userDto.getName());
        assertEquals("test@example.com", userDto.getEmail());
    }

    @Test
    void testUserDtoAllArgsConstructor_ShouldCreateUserDtoWithAllFields() {
        UserDto userDto = new UserDto(1L, "Тестовый пользователь", "test@example.com");

        assertEquals(1L, userDto.getId());
        assertEquals("Тестовый пользователь", userDto.getName());
        assertEquals("test@example.com", userDto.getEmail());
    }

    @Test
    void testUserDtoNoArgsConstructor_ShouldCreateEmptyUserDto() {
        UserDto userDto = new UserDto();

        assertNull(userDto.getId());
        assertNull(userDto.getName());
        assertNull(userDto.getEmail());
    }

    @Test
    void testUserDtoEquals_ShouldReturnTrueForSameUserDtos() {
        UserDto userDto1 = new UserDto(1L, "Тестовый пользователь", "test@example.com");
        UserDto userDto2 = new UserDto(1L, "Тестовый пользователь", "test@example.com");

        assertEquals(userDto1, userDto2);
        assertEquals(userDto1.hashCode(), userDto2.hashCode());
    }

    @Test
    void testUserDtoEquals_ShouldReturnFalseForDifferentUserDtos() {
        UserDto userDto1 = new UserDto(1L, "Тестовый пользователь", "test@example.com");
        UserDto userDto2 = new UserDto(2L, "Другой пользователь", "other@example.com");

        assertNotEquals(userDto1, userDto2);
    }

    @Test
    void testUserDtoToString_ShouldContainAllFields() {
        UserDto userDto = new UserDto(1L, "Тестовый пользователь", "test@example.com");
        String toString = userDto.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Тестовый пользователь"));
        assertTrue(toString.contains("test@example.com"));
    }
}