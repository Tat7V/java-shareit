package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.ResponseEntity;

@WebMvcTest(ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestClient itemRequestClient;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна дрель для ремонта");
        itemRequestDto.setCreated(LocalDateTime.of(2023, 1, 1, 10, 0));
        itemRequestDto.setItems(List.of());
    }

    @Test
    void testCreateItemRequest_ShouldReturnCreatedRequest() throws Exception {
        ItemRequestDto createRequestDto = new ItemRequestDto();
        createRequestDto.setDescription("Нужна дрель для ремонта");

        when(itemRequestClient.createRequest(any(ItemRequestDto.class), anyLong())).thenReturn(ResponseEntity.ok(itemRequestDto));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель для ремонта"))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void testCreateItemRequest_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        ItemRequestDto createRequestDto = new ItemRequestDto();
        createRequestDto.setDescription("Нужна дрель для ремонта");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserRequests_ShouldReturnRequestsList() throws Exception {
        when(itemRequestClient.getUserRequests(anyLong())).thenReturn(ResponseEntity.ok(List.of(itemRequestDto)));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель для ремонта"));
    }

    @Test
    void testGetUserRequests_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllRequests_ShouldReturnRequestsList() throws Exception {
        when(itemRequestClient.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok(List.of(itemRequestDto)));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель для ремонта"));
    }

    @Test
    void testGetAllRequests_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRequestById_ShouldReturnRequest() throws Exception {
        when(itemRequestClient.getRequestById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(itemRequestDto));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель для ремонта"));
    }

    @Test
    void testGetRequestById_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRequestById_WithNonExistentRequest_ShouldReturnNotFound() throws Exception {
        when(itemRequestClient.getRequestById(anyLong(), anyLong()))
                .thenThrow(new ru.practicum.shareit.exeptions.NotFoundException("Запрос не найден"));

        mockMvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}