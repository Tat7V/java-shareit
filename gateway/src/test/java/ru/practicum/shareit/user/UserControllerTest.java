package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.ResponseEntity;

@WebMvcTest(UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserClient userClient;

    UserDto userDto;
    UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовый пользователь");
        userDto.setEmail("тест@пример.ру");

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Обновленный пользователь");
        userUpdateDto.setEmail("обновленный@пример.ру");
    }

    @Test
    void testCreateUser_ShouldReturnCreatedUser() throws Exception {
        when(userClient.createUser(any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тестовый пользователь"))
                .andExpect(jsonPath("$.email").value("тест@пример.ру"));
    }

    @Test
    void testCreateUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UserDto invalidUserDto = new UserDto();
        invalidUserDto.setName("");
        invalidUserDto.setEmail("неправильный-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById_ShouldReturnUser() throws Exception {
        when(userClient.getUserById(anyLong())).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тестовый пользователь"))
                .andExpect(jsonPath("$.email").value("тест@пример.ру"));
    }

    @Test
    void testGetAllUsers_ShouldReturnUsersList() throws Exception {
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok(List.of(userDto)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Тестовый пользователь"));
    }

    @Test
    void testUpdateUser_ShouldReturnUpdatedUser() throws Exception {
        when(userClient.updateUser(anyLong(), any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тестовый пользователь"));
    }

    @Test
    void testUpdateUser_WithNonExistentUser_ShouldReturnNotFound() throws Exception {
        when(userClient.updateUser(anyLong(), any(UserDto.class)))
                .thenThrow(new ru.practicum.shareit.exeptions.NotFoundException("Пользователь не найден"));

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_WithNonExistentUser_ShouldReturnNotFound() throws Exception {
        when(userClient.deleteUser(anyLong()))
                .thenThrow(new ru.practicum.shareit.exeptions.NotFoundException("Пользователь не найден"));

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}