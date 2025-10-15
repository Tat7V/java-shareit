// CHECKSTYLE:OFF
package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    UserDto user;
    ItemRequestDto request;

    @BeforeEach
    void setUp() throws Exception {
        user = new UserDto();
        user.setName("Михаил Козлов");
        user.setEmail("mikhail@example.com");

        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        user = objectMapper.readValue(userResponse, UserDto.class);

        request = new ItemRequestDto();
        request.setDescription("Нужен сварочный аппарат для ремонта");
    }

    @Test
    void testCreateRequest_ShouldReturnCreatedRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужен сварочный аппарат для ремонта"))
                .andExpect(jsonPath("$.requestor.id").value(user.getId()))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateRequest_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateRequest_WithEmptyDescription_ShouldReturnBadRequest() throws Exception {
        request.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserRequests_ShouldReturnUserRequests() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужен сварочный аппарат для ремонта"))
                .andExpect(jsonPath("$[0].requestor.id").value(user.getId()));
    }

    @Test
    void testGetAllRequests_ShouldReturnAllRequests() throws Exception {
        UserDto user2 = new UserDto();
        user2.setName("Елена Волкова");
        user2.setEmail("elena@example.com");

        String secondUserResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        user2 = objectMapper.readValue(secondUserResponse, UserDto.class);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setDescription("Нужен генератор");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужен генератор"));
    }

    @Test
    void testGetRequestById_ShouldReturnRequest() throws Exception {
        String response = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var createdRequest = objectMapper.readValue(response, Object.class);
        Long requestId = ((Number) ((java.util.Map<?, ?>) createdRequest).get("id")).longValue();

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужен сварочный аппарат для ремонта"))
                .andExpect(jsonPath("$.requestor.id").value(user.getId()));
    }

    @Test
    void testGetRequestById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/requests/{id}", 999L)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllRequests_WithPagination_ShouldReturnPaginatedRequests() throws Exception {
        UserDto user2 = new UserDto();
        user2.setName("Андрей Соколов");
        user2.setEmail("andrey@example.com");

        String secondUserResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        user2 = objectMapper.readValue(secondUserResponse, UserDto.class);

        for (int i = 0; i < 3; i++) {
            ItemRequestDto newRequest = new ItemRequestDto();
            newRequest.setDescription("request номер " + (i + 1));

            mockMvc.perform(post("/requests")
                            .header("X-Sharer-User-Id", user2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newRequest)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}