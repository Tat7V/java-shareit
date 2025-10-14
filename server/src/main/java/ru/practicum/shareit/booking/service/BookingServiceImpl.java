package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с Id %d не найден", bookerId)));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с Id %d не найдена", bookingRequestDto.getItemId())));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (bookingRequestDto.getStart() == null || bookingRequestDto.getEnd() == null) {
            throw new ValidationException("Дата начала и окончания бронирования должны быть указаны");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) ||
                bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }

        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с Id %d не найдено", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Только владелец вещи может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с Id %d не найдено", bookingId)));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Просмотр бронирования доступен только автору или владельцу вещи");
        }

        return convertToDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBooker(String state, Long bookerId, int from, int size) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с Id %d не найден", bookerId)));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                bookerId, now, now, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                                bookerId, BookingStatus.WAITING, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                                bookerId, BookingStatus.REJECTED, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            default:
                throw new ValidationException(String.format("Unknown state %s", state));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsByOwner(String state, Long ownerId, int from, int size) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("Пользователь с Id %d не найден", ownerId));
        }

        List<Item> userItems = itemRepository.findByOwnerIdOrderById(ownerId);
        if (userItems.isEmpty()) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                ownerId, now, now, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                                ownerId, BookingStatus.WAITING, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                                ownerId, BookingStatus.REJECTED, pageable)
                        .stream().map(this::convertToDto).collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    private BookingDto convertToDto(Booking booking) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        itemDto.setDescription(booking.getItem().getDescription());
        itemDto.setAvailable(booking.getItem().getAvailable());
        itemDto.setRequestId(booking.getItem().getRequest() != null ? booking.getItem().getRequest().getId() : null);
        UserDto ownerDto = new UserDto();
        ownerDto.setId(booking.getItem().getOwner().getId());
        ownerDto.setName(booking.getItem().getOwner().getName());
        ownerDto.setEmail(booking.getItem().getOwner().getEmail());
        itemDto.setOwner(ownerDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setId(booking.getBooker().getId());
        bookerDto.setName(booking.getBooker().getName());
        bookerDto.setEmail(booking.getBooker().getEmail());

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                bookerDto,
                booking.getStatus()
        );
    }
}