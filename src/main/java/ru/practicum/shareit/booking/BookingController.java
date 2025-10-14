package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                    @RequestHeader(USER_ID_HEADER) Long bookerId) {
        return bookingService.createBooking(bookingRequestDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return bookingService.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader(USER_ID_HEADER) Long bookerId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingsByBooker(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(USER_ID_HEADER) Long ownerId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingsByOwner(state, ownerId, from, size);
    }

}
