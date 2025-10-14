// CHECKSTYLE:OFF
package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    User owner;
    Item testItem;
    ItemDto testItemDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Дмитрий Соколов");
        owner.setEmail("dmitry@example.com");

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Болгарка");
        testItem.setDescription("Углошлифовальная машина");
        testItem.setAvailable(true);
        testItem.setOwner(owner);

        testItemDto = new ItemDto();
        testItemDto.setId(1L);
        testItemDto.setName("Болгарка");
        testItemDto.setDescription("Углошлифовальная машина");
        testItemDto.setAvailable(true);
    }

    @Test
    void testCreateItem_ShouldReturnCreatedItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ItemDto result = itemService.createItem(testItemDto, 1L);

        assertNotNull(result);
        assertEquals("Болгарка", result.getName());
        assertEquals("Углошлифовальная машина", result.getDescription());
        assertTrue(result.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void testCreateItem_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(testItemDto, 999L));
    }

    @Test
    void testUpdateItem_ShouldReturnUpdatedItem() {
        ItemDto updatedDto = new ItemDto();
        updatedDto.setName("Обновленная болгарка");
        updatedDto.setDescription("Новое описание");
        updatedDto.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ItemDto result = itemService.updateItem(1L, updatedDto, 1L);

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void testUpdateItem_WithNonExistentItem_ShouldThrowNotFoundException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(999L, testItemDto, 1L));
    }

    @Test
    void testSearchItems_WithEmptyText_ShouldReturnEmptyList() {
        var result = itemService.searchItems("", 0, 10);

        assertTrue(result.isEmpty());
    }
}