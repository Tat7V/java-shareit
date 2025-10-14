package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemMapperTest {

    @Test
    void testToItem_ShouldConvertItemDtoToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовая вещь");
        itemDto.setDescription("Описание тестовой вещи");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);

        Item item = ItemMapper.toItem(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequest());
    }

    @Test
    void testToItem_WithNullItemDto_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> ItemMapper.toItem(null));
    }

    @Test
    void testToItemDto_ShouldConvertItemToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Тестовая вещь");
        item.setDescription("Описание тестовой вещи");
        item.setAvailable(true);
        item.setRequest(null);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest(), itemDto.getRequestId());
    }

    @Test
    void testToItemDto_WithNullItem_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> ItemMapper.toItemDto(null));
    }

    @Test
    void testToItemWithBookingsDto_ShouldConvertItemToItemWithBookingsDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Тестовая вещь");
        item.setDescription("Описание тестовой вещи");
        item.setAvailable(true);
        item.setRequest(null);

        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);

        assertEquals(item.getId(), itemWithBookingsDto.getId());
        assertEquals(item.getName(), itemWithBookingsDto.getName());
        assertEquals(item.getDescription(), itemWithBookingsDto.getDescription());
        assertEquals(item.getAvailable(), itemWithBookingsDto.getAvailable());
        assertEquals(item.getRequest(), itemWithBookingsDto.getRequestId());
    }

    @Test
    void testToItemWithBookingsDto_WithNullItem_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> ItemMapper.toItemWithBookingsDto(null));
    }

    @Test
    void testToItem_WithEmptyFields_ShouldCreateItemWithNullFields() {
        ItemDto itemDto = new ItemDto();

        Item item = ItemMapper.toItem(itemDto);

        assertNull(item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getAvailable());
        assertNull(item.getRequest());
    }

    @Test
    void testToItemDto_WithEmptyFields_ShouldCreateItemDtoWithNullFields() {
        Item item = new Item();

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertNull(itemDto.getId());
        assertNull(itemDto.getName());
        assertNull(itemDto.getDescription());
        assertNull(itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }
}