package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Владелец с ID %d не найден.",ownerId)));

        validateItemDto(itemDto);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        item.setAvailable(itemDto.getAvailable());

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item.setRequest(request);
        }

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemWithBookingsDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с Id %d не найден.", id)));

        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
        LocalDateTime now = LocalDateTime.now();

        if (item.getOwner().getId().equals(userId)) {
            Optional<Booking> lastBookingOpt = bookingRepository.findLastBookingForItem(id, now);
            if (lastBookingOpt.isPresent()) {
                Booking lastBooking = lastBookingOpt.get();
                itemWithBookingsDto.setLastBooking(new ItemWithBookingsDto.BookingInfo(
                        lastBooking.getId(),
                        lastBooking.getBooker().getId(),
                        lastBooking.getStart(),
                        lastBooking.getEnd()
                ));
            } else {
                Optional<Booking> currentBookingOpt = bookingRepository.findCurrentBookingForItem(id, now);
                if (currentBookingOpt.isPresent()) {
                    Booking currentBooking = currentBookingOpt.get();
                    itemWithBookingsDto.setLastBooking(new ItemWithBookingsDto.BookingInfo(
                            currentBooking.getId(),
                            currentBooking.getBooker().getId(),
                            currentBooking.getStart(),
                            currentBooking.getEnd()
                    ));
                }
            }

            Optional<Booking> nextBookingOpt = bookingRepository.findNextBookingForItem(id, now);
            if (nextBookingOpt.isPresent()) {
                Booking nextBooking = nextBookingOpt.get();
                itemWithBookingsDto.setNextBooking(new ItemWithBookingsDto.BookingInfo(
                        nextBooking.getId(),
                        nextBooking.getBooker().getId(),
                        nextBooking.getStart(),
                        nextBooking.getEnd()
                ));
            }
        } else {
            itemWithBookingsDto.setLastBooking(null);
            itemWithBookingsDto.setNextBooking(null);
        }

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(id);
        List<CommentDto> commentDtos = comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
        itemWithBookingsDto.setComments(commentDtos);

        return itemWithBookingsDto;
    }

    @Override
    public List<ItemWithBookingsDto> getItemsByOwnerId(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("Пользователь с Id %d не найден", ownerId));
        }

        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<CommentDto>> commentsByItem = commentRepository.findByItemIdInOrderByCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(this::convertToCommentDto, Collectors.toList())
                ));

        LocalDateTime now = LocalDateTime.now();
        Map<Long, Booking> lastBookingsByItem = new HashMap<>();
        Map<Long, Booking> nextBookingsByItem = new HashMap<>();

        for (Long itemId : itemIds) {
            bookingRepository.findLastBookingForItem(itemId, now)
                    .ifPresent(booking -> lastBookingsByItem.put(itemId, booking));

            bookingRepository.findNextBookingForItem(itemId, now)
                    .ifPresent(booking -> nextBookingsByItem.put(itemId, booking));
        }

        return items.stream().map(item -> {
            ItemWithBookingsDto dto = ItemMapper.toItemWithBookingsDto(item);

            Booking lastBooking = lastBookingsByItem.get(item.getId());
            if (lastBooking != null) {
                dto.setLastBooking(new ItemWithBookingsDto.BookingInfo(
                        lastBooking.getId(),
                        lastBooking.getBooker().getId(),
                        lastBooking.getStart(),
                        lastBooking.getEnd()
                ));
            }

            Booking nextBooking = nextBookingsByItem.get(item.getId());
            if (nextBooking != null) {
                dto.setNextBooking(new ItemWithBookingsDto.BookingInfo(
                        nextBooking.getId(),
                        nextBooking.getBooker().getId(),
                        nextBooking.getStart(),
                        nextBooking.getEnd()
                ));
            }

            List<CommentDto> itemComments = commentsByItem.getOrDefault(item.getId(), Collections.emptyList());
            dto.setComments(itemComments);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с Id %d не найден.", itemId)));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Редактировать предмет может только её владелец");
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым");
            }
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Описание не может быть пустым");
            }
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.searchAvailableItems(text, pageable);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с Id %d не найден", authorId)));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с Id %d не найдена",itemId)));

        List<Booking> userBookings = bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatus(
                itemId, authorId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (userBookings.isEmpty()) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду или аренда еще не завершена");
        }

        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        commentRepository.flush();
        return convertToCommentDto(savedComment);
    }

    private CommentDto convertToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан");
        }
    }
}