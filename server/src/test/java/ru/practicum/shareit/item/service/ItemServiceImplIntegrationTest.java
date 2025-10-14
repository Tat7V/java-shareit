package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplIntegrationTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingRepository bookingRepository;

    ItemServiceImpl itemService;


    User owner;
    User booker;
    Item item;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                null,
                null
        );

        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("владелец@тест.ру");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("арендатор@тест.ру");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Тестовая вещь");
        item.setDescription("Описание тестовой вещи");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void testGetItemsByOwner_ShouldReturnItemsWithBookings() {
        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        List<ItemWithBookingsDto> result = itemService.getItemsByOwnerId(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        ItemWithBookingsDto itemDto = result.get(0);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertNotNull(itemDto.getLastBooking());
        assertEquals(pastBooking.getId(), itemDto.getLastBooking().getId());
        assertEquals(booker.getId(), itemDto.getLastBooking().getBookerId());
        assertNotNull(itemDto.getNextBooking());
        assertEquals(futureBooking.getId(), itemDto.getNextBooking().getId());
        assertEquals(booker.getId(), itemDto.getNextBooking().getBookerId());
    }

    @Test
    void testGetItemsByOwner_WhenNoBookings_ShouldReturnItemsWithoutBookings() {
        List<ItemWithBookingsDto> result = itemService.getItemsByOwnerId(owner.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        ItemWithBookingsDto itemDto = result.get(0);
        assertEquals(item.getId(), itemDto.getId());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void testGetItemsByOwner_WhenUserNotFound_ShouldThrowException() {
        assertThrows(NotFoundException.class, () ->
            itemService.getItemsByOwnerId(999L)
        );
    }

    @Test
    void testSearchItems_ShouldReturnAvailableItems() {
        ItemDto searchDto = new ItemDto();
        searchDto.setName("Поисковая вещь");
        searchDto.setDescription("Описание поисковой вещи");
        searchDto.setAvailable(true);
        itemService.createItem(searchDto, owner.getId());
        List<ItemDto> result = itemService.searchItems("Поиск", 0, 10);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Поисковая вещь", result.get(0).getName());
    }

    @Test
    void testSearchItems_WithEmptyText_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchItems("", 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateItem_ShouldCreateItemSuccessfully() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Новая вещь");
        itemDto.setDescription("Описание новой вещи");
        itemDto.setAvailable(true);
        ItemDto result = itemService.createItem(itemDto, owner.getId());
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Новая вещь", result.getName());
        assertEquals("Описание новой вещи", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(owner.getId(), itemRepository.findById(result.getId()).get().getOwner().getId());
    }

    @Test
    void testCreateItem_WithInvalidData_ShouldThrowException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);
        assertThrows(ValidationException.class, () ->
            itemService.createItem(itemDto, owner.getId())
        );
    }
}