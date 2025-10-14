package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exeptions.ConflictException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceImplIntegrationTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testCreateUser_ShouldCreateUserSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setName("Тестовый пользователь");
        userDto.setEmail("тест@пример.ру");

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Тестовый пользователь", result.getName());
        assertEquals("тест@пример.ру", result.getEmail());

        User savedUser = userRepository.findById(result.getId()).orElse(null);
        assertNotNull(savedUser);
        assertEquals("Тестовый пользователь", savedUser.getName());
        assertEquals("тест@пример.ру", savedUser.getEmail());
    }

    @Test
    void testCreateUser_WithDuplicateEmail_ShouldThrowException() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("Пользователь 1");
        userDto1.setEmail("дубликат@пример.ру");
        userService.createUser(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("Пользователь 2");
        userDto2.setEmail("дубликат@пример.ру");

        assertThrows(ConflictException.class, () -> 
            userService.createUser(userDto2)
        );
    }

    @Test
    void testUpdateUser_ShouldUpdateUserSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setName("Имя");
        userDto.setEmail("имя@пример.ру");
        UserDto createdUser = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Другое имя");
        updateDto.setEmail("другое@пример.ру");

        UserDto result = userService.updateUser(createdUser.getId(), updateDto);

        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals("Другое имя", result.getName());
        assertEquals("другое@пример.ру", result.getEmail());
    }

    @Test
    void testUpdateUser_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        UserDto userDto = new UserDto();
        userDto.setName("Имя");
        userDto.setEmail("имя@пример.ру");
        UserDto createdUser = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Другое имя");

        UserDto result = userService.updateUser(createdUser.getId(), updateDto);

        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals("Другое имя", result.getName());
        assertEquals("имя@пример.ру", result.getEmail());
    }

    @Test
    void testUpdateUser_WithNonExistentUser_ShouldThrowException() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Другое имя");

        assertThrows(NotFoundException.class, () -> 
            userService.updateUser(999L, updateDto)
        );
    }

    @Test
    void testGetUserById_ShouldReturnUserSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setName("Тестовый пользователь");
        userDto.setEmail("тест@пример.ру");
        UserDto createdUser = userService.createUser(userDto);

        UserDto result = userService.getUserById(createdUser.getId());

        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals("Тестовый пользователь", result.getName());
        assertEquals("тест@пример.ру", result.getEmail());
    }

    @Test
    void testGetUserById_WithNonExistentUser_ShouldThrowException() {
        assertThrows(NotFoundException.class, () -> 
            userService.getUserById(999L)
        );
    }

    @Test
    void testGetAllUsers_ShouldReturnAllUsers() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("Пользователь 1");
        userDto1.setEmail("пользователь1@пример.ру");
        userService.createUser(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("Пользователь 2");
        userDto2.setEmail("пользователь2@пример.ру");
        userService.createUser(userDto2);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteUser_ShouldDeleteUserSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setName("Тестовый пользователь");
        userDto.setEmail("тест@пример.ру");
        UserDto createdUser = userService.createUser(userDto);

        userService.deleteUser(createdUser.getId());

        assertThrows(NotFoundException.class, () -> 
            userService.getUserById(createdUser.getId())
        );
    }

    @Test
    void testDeleteUser_WithNonExistentUser_ShouldThrowException() {
        assertThrows(NotFoundException.class, () -> 
            userService.deleteUser(999L)
        );
    }
}