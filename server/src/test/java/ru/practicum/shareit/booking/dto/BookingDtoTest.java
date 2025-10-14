package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingDtoTest {

    @Test
    void testBookingDto_ShouldCreateWithAllArgsConstructor() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        ItemDto item = new ItemDto();
        UserDto booker = new UserDto();

        BookingDto bookingDto = new BookingDto(1L, start, end, item, booker, BookingStatus.WAITING);

        assertEquals(1L, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(booker, bookingDto.getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void testBookingDto_ShouldCreateWithNoArgsConstructor() {
        BookingDto bookingDto = new BookingDto();

        assertNull(bookingDto.getId());
        assertNull(bookingDto.getStart());
        assertNull(bookingDto.getEnd());
        assertNull(bookingDto.getItem());
        assertNull(bookingDto.getBooker());
        assertNull(bookingDto.getStatus());
    }

    @Test
    void testBookingDto_ShouldSetAndGetFields() {
        BookingDto bookingDto = new BookingDto();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        ItemDto item = new ItemDto();
        UserDto booker = new UserDto();

        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(BookingStatus.APPROVED);

        assertEquals(1L, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(booker, bookingDto.getBooker());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void testBookingDto_ShouldHaveCorrectToString() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.WAITING);

        String toString = bookingDto.toString();

        assertTrue(toString.contains("BookingDto"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("status=WAITING"));
    }

    @Test
    void testBookingDto_ShouldHaveCorrectEqualsAndHashCode() {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStatus(BookingStatus.WAITING);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setStatus(BookingStatus.WAITING);

        BookingDto bookingDto3 = new BookingDto();
        bookingDto3.setId(2L);
        bookingDto3.setStatus(BookingStatus.APPROVED);

        assertEquals(bookingDto1, bookingDto2);
        assertNotEquals(bookingDto1, bookingDto3);
        assertEquals(bookingDto1.hashCode(), bookingDto2.hashCode());
        assertNotEquals(bookingDto1.hashCode(), bookingDto3.hashCode());
    }
}
