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
    void testToUser_ShouldConvertUserDtoToUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовый пользователь");
        userDto.setEmail("тест@пример.ру");

        User user = UserMapper.toUser(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void testToUser_WithNullUserDto_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> UserMapper.toUser(null));
    }

    @Test
    void testToUserDto_ShouldConvertUserToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Тестовый пользователь");
        user.setEmail("тест@пример.ру");

        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void testToUserDto_WithNullUser_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> UserMapper.toUserDto(null));
    }

    @Test
    void testToUser_WithEmptyFields_ShouldCreateUserWithNullFields() {
        UserDto userDto = new UserDto();

        User user = UserMapper.toUser(userDto);

        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    void testToUserDto_WithEmptyFields_ShouldCreateUserDtoWithNullFields() {
        User user = new User();

        UserDto userDto = UserMapper.toUserDto(user);

        assertNull(userDto.getId());
        assertNull(userDto.getName());
        assertNull(userDto.getEmail());
    }
}