package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByBooker(String state, Long bookerId, int from, int size);

    List<BookingDto> getBookingsByOwner(String state, Long ownerId, int from, int size);
}
