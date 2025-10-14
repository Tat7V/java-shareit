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
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.ResponseEntity;

@WebMvcTest(ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemClient itemClient;

    ItemDto itemDto;
    ItemWithBookingsDto itemWithBookingsDto;
    CommentDto commentDto;

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

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Отличная вещь!");
        commentDto.setAuthorName("Тестовый пользователь");
    }

    @Test
    void testCreateItem_ShouldReturnCreatedItem() throws Exception {
        when(itemClient.createItem(any(ItemDto.class), anyLong())).thenReturn(ResponseEntity.ok(itemDto));

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
        when(itemClient.getItem(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(itemWithBookingsDto));

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
        when(itemClient.getUserItems(anyLong())).thenReturn(ResponseEntity.ok(List.of(itemWithBookingsDto)));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Тестовая вещь"));
    }

    @Test
    void testUpdateItem_ShouldReturnUpdatedItem() throws Exception {
        when(itemClient.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenReturn(ResponseEntity.ok(itemDto));

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

    @Test
    void testSearchItems_ShouldReturnSearchResults() throws Exception {
        when(itemClient.searchItems(anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok(List.of(itemDto)));

        mockMvc.perform(get("/items/search")
                        .param("text", "тест")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Тестовая вещь"));
    }

    @Test
    void testSearchItems_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        when(itemClient.searchItems(anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testAddComment_ShouldReturnCreatedComment() throws Exception {
        CommentDto createCommentDto = new CommentDto();
        createCommentDto.setText("Отличная вещь!");

        when(itemClient.addComment(anyLong(), any(CommentDto.class), anyLong())).thenReturn(ResponseEntity.ok(commentDto));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Отличная вещь!"))
                .andExpect(jsonPath("$.authorName").value("Тестовый пользователь"));
    }

    @Test
    void testAddComment_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        CommentDto createCommentDto = new CommentDto();
        createCommentDto.setText("Отличная вещь!");

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isBadRequest());
    }
}