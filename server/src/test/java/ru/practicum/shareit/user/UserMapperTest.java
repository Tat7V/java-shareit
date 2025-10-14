// CHECKSTYLE:OFF
package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class UserMapperTest {

    @Test
    void testToUserDto_ShouldConvertUserToDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Тестовый пользователь");
        user.setEmail("test@example.com");

        UserDto result = UserMapper.toUserDto(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Тестовый пользователь", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testToUserDto_WithNullUser_ShouldReturnNull() {
        UserDto result = UserMapper.toUserDto(null);
        assertNull(result);
    }

    @Test
    void testToUser_ShouldConvertDtoToUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовый пользователь");
        userDto.setEmail("test@example.com");

        User result = UserMapper.toUser(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Тестовый пользователь", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testToUser_WithNullDto_ShouldReturnNull() {
        User result = UserMapper.toUser(null);
        assertNull(result);
    }

    @Test
    void testToUser_WithNullId_ShouldNotSetId() {
        UserDto userDto = new UserDto();
        userDto.setId(null);
        userDto.setName("Тестовый пользователь");
        userDto.setEmail("test@example.com");

        User result = UserMapper.toUser(userDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Тестовый пользователь", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }
}