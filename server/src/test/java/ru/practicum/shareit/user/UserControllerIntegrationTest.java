// CHECKSTYLE:OFF
package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    UserDto testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDto();
        testUser.setName("Александр Новиков");
        testUser.setEmail("alexander@example.com");
    }

    @Test
    void testCreateUser_ShouldReturnCreatedUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Александр Новиков"))
                .andExpect(jsonPath("$.email").value("alexander@example.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        testUser.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUser_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        testUser.setName("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser_ShouldReturnUpdatedUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        UserDto updatedUser = new UserDto();
        updatedUser.setName("Александр Обновленный");
        updatedUser.setEmail("alexander.updated@example.com");

        mockMvc.perform(patch("/users/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Александр Обновленный"))
                .andExpect(jsonPath("$.email").value("alexander.updated@example.com"))
                .andExpect(jsonPath("$.id").value(createdUser.getId()));
    }

    @Test
    void testUpdateUser_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        UserDto updatedUser = new UserDto();
        updatedUser.setName("Несуществующий");
        updatedUser.setEmail("nonexistent@example.com");

        mockMvc.perform(patch("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserById_ShouldReturnUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        mockMvc.perform(get("/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Александр Новиков"))
                .andExpect(jsonPath("$.email").value("alexander@example.com"))
                .andExpect(jsonPath("$.id").value(createdUser.getId()));
    }

    @Test
    void testGetUserById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers_ShouldReturnAllUsers() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());

        UserDto user2 = new UserDto();
        user2.setName("Екатерина Петрова");
        user2.setEmail("ekaterina@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void testDeleteUser_ShouldDeleteUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        mockMvc.perform(delete("/users/{id}", createdUser.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}", createdUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}