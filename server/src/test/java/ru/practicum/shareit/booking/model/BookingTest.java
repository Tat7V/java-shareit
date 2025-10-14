// CHECKSTYLE:OFF
package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingTest {

    User booker;
    User owner;
    Item item;
    Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("Бронирующий");
        booker.setEmail("booker@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Электрическая дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void testBookingCreation_ShouldCreateBookingWithAllFields() {
        assertEquals(1L, booking.getId());
        assertNotNull(booking.getStart());
        assertNotNull(booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void testBookingAllArgsConstructor_ShouldCreateBookingWithAllFields() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking newBooking = new Booking(2L, start, end, item, booker, BookingStatus.APPROVED);

        assertEquals(2L, newBooking.getId());
        assertEquals(start, newBooking.getStart());
        assertEquals(end, newBooking.getEnd());
        assertEquals(item, newBooking.getItem());
        assertEquals(booker, newBooking.getBooker());
        assertEquals(BookingStatus.APPROVED, newBooking.getStatus());
    }

    @Test
    void testBookingNoArgsConstructor_ShouldCreateEmptyBooking() {
        Booking emptyBooking = new Booking();

        assertNull(emptyBooking.getId());
        assertNull(emptyBooking.getStart());
        assertNull(emptyBooking.getEnd());
        assertNull(emptyBooking.getItem());
        assertNull(emptyBooking.getBooker());
        assertNull(emptyBooking.getStatus());
    }

    @Test
    void testBookingEquals_ShouldReturnTrueForSameBookings() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        Booking booking2 = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    void testBookingEquals_ShouldReturnFalseForDifferentBookings() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, start, end, item, booker, BookingStatus.APPROVED);

        assertNotEquals(booking1, booking2);
    }

    @Test
    void testBookingToString_ShouldContainAllFields() {
        String toString = booking.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("WAITING"));
    }

    @Test
    void testBookingStatusValues_ShouldWorkCorrectly() {
        booking.setStatus(BookingStatus.APPROVED);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());

        booking.setStatus(BookingStatus.REJECTED);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());

        booking.setStatus(BookingStatus.CANCELED);
        assertEquals(BookingStatus.CANCELED, booking.getStatus());
    }
}