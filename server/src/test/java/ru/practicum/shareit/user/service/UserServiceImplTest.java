package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User testUser;
    UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Анна Кузнецова");
        testUser.setEmail("anna@example.com");

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("Анна Кузнецова");
        testUserDto.setEmail("anna@example.com");
    }

    @Test
    void testCreateUser_ShouldReturnCreatedUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(testUserDto);

        assertNotNull(result);
        assertEquals("Анна Кузнецова", result.getName());
        assertEquals("anna@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_ShouldReturnUpdatedUser() {
        UserDto updatedDto = new UserDto();
        updatedDto.setName("Анна Обновленная");
        updatedDto.setEmail("anna.updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot("anna.updated@example.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUser(1L, updatedDto);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_WithNonExistentId_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(999L, testUserDto));
    }

    @Test
    void testGetUserById_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Анна Кузнецова", result.getName());
        assertEquals("anna@example.com", result.getEmail());
    }

    @Test
    void testGetUserById_WithNonExistentId_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void testGetAllUsers_ShouldReturnAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Петр Иванов");
        user2.setEmail("petr@example.com");

        when(userRepository.findAll()).thenReturn(List.of(testUser, user2));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("Анна Кузнецова")));
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("Петр Иванов")));
    }

    @Test
    void testDeleteUser_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_WithNonExistentId_ShouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
    }
}