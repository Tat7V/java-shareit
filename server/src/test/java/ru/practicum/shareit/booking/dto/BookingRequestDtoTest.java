// CHECKSTYLE:OFF
package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingRequestDtoTest {

    @Test
    void testShouldCreateBookingRequestDtoWithAllFields() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);

        assertEquals(1L, bookingRequestDto.getItemId());
        assertEquals(start, bookingRequestDto.getStart());
        assertEquals(end, bookingRequestDto.getEnd());
    }

    @Test
    void testAAC_ShouldCreateBookingRequestDtoWithAllFields() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, start, end);

        assertEquals(1L, bookingRequestDto.getItemId());
        assertEquals(start, bookingRequestDto.getStart());
        assertEquals(end, bookingRequestDto.getEnd());
    }

    @Test
    void testNAC_ShouldCreateEmptyBookingRequestDto() {
        BookingRequestDto emptyBookingRequestDto = new BookingRequestDto();

        assertNull(emptyBookingRequestDto.getItemId());
        assertNull(emptyBookingRequestDto.getStart());
        assertNull(emptyBookingRequestDto.getEnd());
    }

    @Test
    void testShouldReturnTrueForSameBookingRequestDtos() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        
        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, start, end);
        BookingRequestDto bookingRequestDto2 = new BookingRequestDto(1L, start, end);

        assertEquals(bookingRequestDto1, bookingRequestDto2);
        assertEquals(bookingRequestDto1.hashCode(), bookingRequestDto2.hashCode());
    }

    @Test
    void testShouldReturnFalseForDifferentBookingRequestDtos() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        
        BookingRequestDto bookingRequestDto1 = new BookingRequestDto(1L, start, end);
        BookingRequestDto bookingRequestDto2 = new BookingRequestDto(2L, start, end);

        assertNotEquals(bookingRequestDto1, bookingRequestDto2);
    }

    @Test
    void testToStringShouldContainAllFields() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, start, end);
        String toString = bookingRequestDto.toString();

        assertTrue(toString.contains("1"));
    }

    @Test
    void testBookingRequestDtoWithNullFieldsShouldWorkCorrectly() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(null);
        bookingRequestDto.setStart(null);
        bookingRequestDto.setEnd(null);

        assertNull(bookingRequestDto.getItemId());
        assertNull(bookingRequestDto.getStart());
        assertNull(bookingRequestDto.getEnd());
    }
}