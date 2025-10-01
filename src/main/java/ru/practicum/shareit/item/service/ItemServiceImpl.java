package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Владелец с ID %d не найден.",ownerId)));

        validateItemDto(itemDto);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        item.setAvailable(itemDto.getAvailable());

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemWithBookingsDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с Id %d не найден.",id)));
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> lastBookings = bookingRepository.findLastBookingForItem(id, now);
            if (!lastBookings.isEmpty()) {
                Booking lastBooking = lastBookings.get(0);
                itemWithBookingsDto.setLastBooking(new ItemWithBookingsDto.BookingInfo(
                        lastBooking.getId(),
                        lastBooking.getBooker().getId(),
                        lastBooking.getStart(),
                        lastBooking.getEnd()
                ));
            }

            List<Booking> nextBookings = bookingRepository.findNextBookingForItem(id, now);
            if (!nextBookings.isEmpty()) {
                Booking nextBooking = nextBookings.get(0);
                itemWithBookingsDto.setNextBooking(new ItemWithBookingsDto.BookingInfo(
                        nextBooking.getId(),
                        nextBooking.getBooker().getId(),
                        nextBooking.getStart(),
                        nextBooking.getEnd()
                ));
            }
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
            List<Booking> lastBookings = bookingRepository.findLastBookingForItem(itemId, now);
            if (!lastBookings.isEmpty()) {
                lastBookingsByItem.put(itemId, lastBookings.get(0));
            }

            List<Booking> nextBookings = bookingRepository.findNextBookingForItem(itemId, now);
            if (!nextBookings.isEmpty()) {
                nextBookingsByItem.put(itemId, nextBookings.get(0));
            }
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
            throw new NotFoundException("Редактировать предмет может только её владелец.");
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
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
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
