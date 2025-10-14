// CHECKSTYLE:OFF
package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    User owner;
    User booker;
    Item testItem;
    Booking booking;
    BookingRequestDto bookingRequest;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Александр Новиков");
        owner.setEmail("alexander@example.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("Елена Соколова");
        booker.setEmail("elena@example.com");

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Газонокосилка");
        testItem.setDescription("Электрическая газонокосилка");
        testItem.setAvailable(true);
        testItem.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(testItem);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        bookingRequest = new BookingRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void testCreateBooking_ShouldReturnCreatedBooking() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(bookingRequest, 2L);

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequest, 999L));
    }

    @Test
    void testCreateBooking_WithNonExistentItem_ShouldThrowNotFoundException() {
        bookingRequest.setItemId(999L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequest, 2L));
    }

    @Test
    void testCreateBooking_WithOwnerAsBooker_ShouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void testCreateBooking_WithUnavailableItem_ShouldThrowValidationException() {
        testItem.setAvailable(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequest, 2L));
    }

    @Test
    void testCreateBooking_WithInvalidDates_ShouldThrowValidationException() {
        bookingRequest.setStart(LocalDateTime.now().plusDays(2));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(1)); // Конец раньше начала

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequest, 2L));
    }

    @Test
    void testApproveBooking_ShouldReturnApprovedBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(1L, true, 1L);

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testApproveBooking_WithNonExistentBooking_ShouldThrowNotFoundException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(999L, true, 1L));
    }

    @Test
    void testApproveBooking_WithNonOwner_ShouldThrowForbiddenException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.approveBooking(1L, true, 2L));
    }

    @Test
    void testApproveBooking_WithAlreadyApprovedBooking_ShouldThrowValidationException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, true, 1L));
    }

    @Test
    void testGetBookingById_ShouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetBookingById_WithNonExistentBooking_ShouldThrowNotFoundException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(999L, 2L));
    }

    @Test
    void testGetBookingById_WithUnauthorizedUser_ShouldThrowNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 999L));
    }

    @Test
    void testGetBookingsByBooker_ShouldReturnBookingsList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByBooker("ALL", 2L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetBookingsByBooker_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByBooker("ALL", 999L, 0, 10));
    }

    @Test
    void testGetBookingsByOwner_ShouldReturnBookingsList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByOwnerIdOrderById(1L)).thenReturn(List.of(testItem));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByOwner("ALL", 1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetBookingsByOwner_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByOwner("ALL", 999L, 0, 10));
    }
}