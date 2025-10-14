package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    UserDto owner;
    ItemDto testItem;

    @BeforeEach
    void setUp() throws Exception {
        owner = new UserDto();
        owner.setName("Владимир Соколов");
        owner.setEmail("vladimir@example.com");

        String ownerResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        owner = objectMapper.readValue(ownerResponse, UserDto.class);

        testItem = new ItemDto();
        testItem.setName("Перфоратор");
        testItem.setDescription("Мощный перфоратор для строительных работ");
        testItem.setAvailable(true);
    }

    @Test
    void testCreateItem_ShouldReturnCreatedItem() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Перфоратор"))
                .andExpect(jsonPath("$.description").value("Мощный перфоратор для строительных работ"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateItem_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateItem_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        testItem.setName("");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateItem_ShouldReturnUpdatedItem() throws Exception {
        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto createdItem = objectMapper.readValue(response, ItemDto.class);

        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("Обновленный перфоратор");
        updatedItem.setDescription("Новое описание");
        updatedItem.setAvailable(false);

        mockMvc.perform(patch("/items/{id}", createdItem.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновленный перфоратор"))
                .andExpect(jsonPath("$.description").value("Новое описание"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.id").value(createdItem.getId()));
    }

    @Test
    void testUpdateItem_WithNonOwner_ShouldReturnNotFound() throws Exception {
        UserDto user2 = new UserDto();
        user2.setName("Дмитрий Козлов");
        user2.setEmail("dmitry@example.com");

        String otherUserResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto otherUser = objectMapper.readValue(otherUserResponse, UserDto.class);

        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto createdItem = objectMapper.readValue(response, ItemDto.class);

        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("Другое название");

        mockMvc.perform(patch("/items/{id}", createdItem.getId())
                        .header("X-Sharer-User-Id", otherUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetItemById_ShouldReturnItem() throws Exception {
        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto createdItem = objectMapper.readValue(response, ItemDto.class);

        mockMvc.perform(get("/items/{id}", createdItem.getId())
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Перфоратор"))
                .andExpect(jsonPath("$.description").value("Мощный перфоратор для строительных работ"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.id").value(createdItem.getId()));
    }

    @Test
    void testGetItemById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/items/{id}", 999L)
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllItemsByOwner_ShouldReturnOwnerItems() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk());

        ItemDto item2 = new ItemDto();
        item2.setName("Дрель");
        item2.setDescription("Электрическая дрель");
        item2.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void testSearchItems_ShouldReturnAvailableItems() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/search")
                        .param("text", "перфоратор"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Перфоратор"));
    }

    @Test
    void testSearchItems_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}