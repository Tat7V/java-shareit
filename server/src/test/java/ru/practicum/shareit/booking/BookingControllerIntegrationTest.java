// CHECKSTYLE:OFF
package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    UserDto owner;
    UserDto user;
    ItemDto testItem;
    BookingRequestDto booking;

    @BeforeEach
    void setUp() throws Exception {
        owner = new UserDto();
        owner.setName("Николай Петров");
        owner.setEmail("nikolay@example.com");

        String ownerResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        owner = objectMapper.readValue(ownerResponse, UserDto.class);

        user = new UserDto();
        user.setName("Татьяна Смирнова");
        user.setEmail("tatyana@example.com");

        String bookerResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        user = objectMapper.readValue(bookerResponse, UserDto.class);

        testItem = new ItemDto();
        testItem.setName("Лобзик");
        testItem.setDescription("Электрический лобзик для резки дерева");
        testItem.setAvailable(true);

        String itemResponse = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        testItem = objectMapper.readValue(itemResponse, ItemDto.class);

        booking = new BookingRequestDto();
        booking.setItemId(testItem.getId());
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void testCreateBooking_ShouldReturnCreatedBooking() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(testItem.getId()))
                .andExpect(jsonPath("$.booker.id").value(user.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateBooking_WithOwnerAsBooker_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBooking_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testApproveBooking_ShouldReturnApprovedBooking() throws Exception {
        String response = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var createdBooking = objectMapper.readValue(response, Object.class);
        Long bookingId = ((Number) ((java.util.Map<?, ?>) createdBooking).get("id")).longValue();

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void testRejectBooking_ShouldReturnRejectedBooking() throws Exception {
        String response = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var createdBooking = objectMapper.readValue(response, Object.class);
        Long bookingId = ((Number) ((java.util.Map<?, ?>) createdBooking).get("id")).longValue();

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void testGetBookingById_ShouldReturnBooking() throws Exception {
        String response = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var createdBooking = objectMapper.readValue(response, Object.class);
        Long bookingId = ((Number) ((java.util.Map<?, ?>) createdBooking).get("id")).longValue();

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(testItem.getId()))
                .andExpect(jsonPath("$.booker.id").value(user.getId()));
    }

    @Test
    void testGetBookingById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/bookings/{id}", 999L)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBookingsByBooker_ShouldReturnBookingsList() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].booker.id").value(user.getId()));
    }

    @Test
    void testGetBookingsByOwner_ShouldReturnBookingsList() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].item.owner.id").value(owner.getId()));
    }

    @Test
    void testGetBookingsByBooker_WithState_ShouldReturnFilteredBookings() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetBookingsByOwner_WithState_ShouldReturnFilteredBookings() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}