// CHECKSTYLE:OFF
package ru.practicum.shareit.booking.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookingRepository bookingRepository;

    User owner;
    User booker;
    Item testItem;
    Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Петр Иванов");
        owner.setEmail("petr@example.com");
        entityManager.persistAndFlush(owner);

        booker = new User();
        booker.setName("Анна Козлова");
        booker.setEmail("anna@example.com");
        entityManager.persistAndFlush(booker);

        testItem = new Item();
        testItem.setName("Отвертка");
        testItem.setDescription("Набор отверток");
        testItem.setAvailable(true);
        testItem.setOwner(owner);
        entityManager.persistAndFlush(testItem);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(testItem);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void testSaveBooking_ShouldSaveAndReturnBooking() {
        Booking savedBooking = bookingRepository.save(booking);

        assertNotNull(savedBooking.getId());
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        assertEquals(testItem.getId(), savedBooking.getItem().getId());
        assertEquals(booker.getId(), savedBooking.getBooker().getId());
    }

    @Test
    void testFindByBookerIdOrderByStartDesc_ShouldReturnBookingsByBooker() {
        bookingRepository.save(booking);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void testFindByBookerIdOrderByStartDesc_ShouldReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(999L, pageable);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void testFindByItemOwnerIdOrderByStartDesc_ShouldReturnBookingsByOwner() {
        bookingRepository.save(booking);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
        assertEquals(owner.getId(), bookings.get(0).getItem().getOwner().getId());
    }

    @Test
    void testFindByItemOwnerIdOrderByStartDesc_ShouldReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(999L, pageable);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void testFindByItemIdAndBookerIdAndEndBeforeAndStatus_ShouldReturnPastBookings() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatus(
                testItem.getId(), booker.getId(), LocalDateTime.now(), BookingStatus.APPROVED);

        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void testFindByItemIdAndBookerIdAndEndBeforeAndStatus_ShouldReturnEmpty() {
        bookingRepository.save(booking);
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatus(
                testItem.getId(), booker.getId(), LocalDateTime.now(), BookingStatus.WAITING);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void testFindLastBookingForItem_ShouldReturnLastBooking() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        entityManager.flush();

        var lastBooking = bookingRepository.findLastBookingForItem(testItem.getId(), LocalDateTime.now());

        assertTrue(lastBooking.isPresent());
        assertEquals(BookingStatus.APPROVED, lastBooking.get().getStatus());
    }

    @Test
    void testFindNextBookingForItem_ShouldReturnNextBooking() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        entityManager.flush();

        var nextBooking = bookingRepository.findNextBookingForItem(testItem.getId(), LocalDateTime.now());

        assertTrue(nextBooking.isPresent());
        assertEquals(BookingStatus.APPROVED, nextBooking.get().getStatus());
    }

    @Test
    void testFindCurrentBookingForItem_ShouldReturnCurrentBooking() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        entityManager.flush();

        var currentBooking = bookingRepository.findCurrentBookingForItem(testItem.getId(), LocalDateTime.now());

        assertTrue(currentBooking.isPresent());
        assertEquals(BookingStatus.APPROVED, currentBooking.get().getStatus());
    }

    @Test
    void testDeleteById_ShouldDeleteBooking() {
        Booking savedBooking = bookingRepository.save(booking);
        entityManager.flush();

        bookingRepository.deleteById(savedBooking.getId());
        entityManager.flush();

        assertFalse(bookingRepository.findById(savedBooking.getId()).isPresent());
    }
}