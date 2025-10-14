// CHECKSTYLE:OFF
package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    ItemDto itemDto;
    ItemWithBookingsDto itemWithBookingsDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовая вещь");
        itemDto.setDescription("Описание тестовой вещи");
        itemDto.setAvailable(true);

        itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(1L);
        itemWithBookingsDto.setName("Тестовая вещь");
        itemWithBookingsDto.setDescription("Описание тестовой вещи");
        itemWithBookingsDto.setAvailable(true);
    }

    @Test
    void testCreateItem_ShouldReturnCreatedItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тестовая вещь"))
                .andExpect(jsonPath("$.description").value("Описание тестовой вещи"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testCreateItem_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetItemById_ShouldReturnItemWithBookings() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemWithBookingsDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тестовая вещь"))
                .andExpect(jsonPath("$.description").value("Описание тестовой вещи"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testGetItemById_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetItemsByOwnerId_ShouldReturnItemsList() throws Exception {
        when(itemService.getItemsByOwnerId(anyLong())).thenReturn(List.of(itemWithBookingsDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Тестовая вещь"));
    }

    @Test
    void testUpdateItem_ShouldReturnUpdatedItem() throws Exception {
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тестовая вещь"));
    }

    @Test
    void testUpdateItem_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }
}