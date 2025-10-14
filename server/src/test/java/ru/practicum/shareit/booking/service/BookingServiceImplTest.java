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
import static org.mockito.ArgumentMatchers.*;
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

    BookingDto bookingDto;
    BookingRequestDto bookingRequestDto;
    Booking booking;
    Item item;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Тестовый пользователь");
        user.setEmail("тест@пример.ру");

        item = new Item();
        item.setId(1L);
        item.setName("Тестовая вещь");
        item.setDescription("Описание тестовой вещи");
        item.setAvailable(true);
        item.setOwner(user);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(BookingStatus.WAITING);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void testCreateBooking_ShouldReturnCreatedBooking() {
        User booker = new User();
        booker.setId(2L);
        booker.setName("Бронирующий пользователь");
        booker.setEmail("booker@пример.ру");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(bookingRequestDto, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequestDto, 1L));
    }

    @Test
    void testCreateBooking_WithNonExistentItem_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequestDto, 1L));
    }

    @Test
    void testCreateBooking_WithUnavailableItem_ShouldThrowValidationException() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequestDto, 1L));
    }

    @Test
    void testCreateBooking_WithOwnerBookingOwnItem_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequestDto, 1L));
    }

    @Test
    void testApproveBooking_ShouldReturnUpdatedBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(1L, true, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testApproveBooking_WithNonExistentBooking_ShouldThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, true, 1L));
    }

    @Test
    void testGetBookingById_ShouldReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetBookingById_WithNonExistentBooking_ShouldThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 1L));
    }

    @Test
    void testGetBookingsByBooker_ShouldReturnBookingsList() {
        User booker = new User();
        booker.setId(2L);
        booker.setName("Бронирующий пользователь");
        booker.setEmail("booker@пример.ру");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByBooker("ALL", 2L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetBookingsByOwner_ShouldReturnBookingsList() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerIdOrderById(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByOwner("ALL", 1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
