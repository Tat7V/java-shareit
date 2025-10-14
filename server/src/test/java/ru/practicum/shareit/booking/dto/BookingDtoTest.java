// CHECKSTYLE:OFF
package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingDtoTest {

    UserDto booker;
    UserDto owner;
    ItemDto item;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = new UserDto();
        owner.setId(1L);
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");

        booker = new UserDto();
        booker.setId(2L);
        booker.setName("Бронирующий");
        booker.setEmail("booker@example.com");

        item = new ItemDto();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Электрическая дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(BookingStatus.WAITING);
    }

    @Test
    void testBookingDtoCreation_ShouldCreateBookingDtoWithAllFields() {
        assertEquals(1L, bookingDto.getId());
        assertNotNull(bookingDto.getStart());
        assertNotNull(bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(booker, bookingDto.getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void testBookingDtoAllArgsConstructor_ShouldCreateBookingDtoWithAllFields() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto newBookingDto = new BookingDto(2L, start, end, item, booker, BookingStatus.APPROVED);

        assertEquals(2L, newBookingDto.getId());
        assertEquals(start, newBookingDto.getStart());
        assertEquals(end, newBookingDto.getEnd());
        assertEquals(item, newBookingDto.getItem());
        assertEquals(booker, newBookingDto.getBooker());
        assertEquals(BookingStatus.APPROVED, newBookingDto.getStatus());
    }

    @Test
    void testBookingDtoNoArgsConstructor_ShouldCreateEmptyBookingDto() {
        BookingDto emptyBookingDto = new BookingDto();

        assertNull(emptyBookingDto.getId());
        assertNull(emptyBookingDto.getStart());
        assertNull(emptyBookingDto.getEnd());
        assertNull(emptyBookingDto.getItem());
        assertNull(emptyBookingDto.getBooker());
        assertNull(emptyBookingDto.getStatus());
    }

    @Test
    void testBookingDtoEquals_ShouldReturnTrueForSameBookingDtos() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto1 = new BookingDto(1L, start, end, item, booker, BookingStatus.WAITING);
        BookingDto bookingDto2 = new BookingDto(1L, start, end, item, booker, BookingStatus.WAITING);

        assertEquals(bookingDto1, bookingDto2);
        assertEquals(bookingDto1.hashCode(), bookingDto2.hashCode());
    }

    @Test
    void testBookingDtoEquals_ShouldReturnFalseForDifferentBookingDtos() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto1 = new BookingDto(1L, start, end, item, booker, BookingStatus.WAITING);
        BookingDto bookingDto2 = new BookingDto(2L, start, end, item, booker, BookingStatus.APPROVED);

        assertNotEquals(bookingDto1, bookingDto2);
    }

    @Test
    void testBookingDtoToString_ShouldContainAllFields() {
        String toString = bookingDto.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("WAITING"));
    }

    @Test
    void testBookingDtoStatusValues_ShouldWorkCorrectly() {
        bookingDto.setStatus(BookingStatus.APPROVED);
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());

        bookingDto.setStatus(BookingStatus.REJECTED);
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());

        bookingDto.setStatus(BookingStatus.CANCELED);
        assertEquals(BookingStatus.CANCELED, bookingDto.getStatus());
    }
}