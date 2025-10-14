package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingClient bookingClient;

    BookItemRequestDto bookItemRequestDto;

    @BeforeEach
    void setUp() {
        bookItemRequestDto = new BookItemRequestDto();
        bookItemRequestDto.setItemId(1L);
        bookItemRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookItemRequestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void testGetBookings_ShouldReturnBookings() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookings_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookings_WithInvalidState_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "invalid")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookingsByOwner_ShouldReturnBookings() throws Exception {
        when(bookingClient.getBookingsByOwner(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingsByOwner_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBookItem_ShouldReturnCreatedBooking() throws Exception {
        when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(new Object()));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testBookItem_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBooking_ShouldReturnBooking() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(new Object()));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBooking_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testApproveBooking_ShouldReturnUpdatedBooking() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(new Object()));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testApproveBooking_WithoutUserHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }
}
