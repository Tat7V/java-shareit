// CHECKSTYLE:OFF
package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemMapperTest {

    User owner;
    Item testItem;
    ItemDto testItemDto;
    ItemRequest request;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Алексей Соколов");
        owner.setEmail("alexey@example.com");

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужен компрессор");

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Компрессор");
        testItem.setDescription("Воздушный компрессор");
        testItem.setAvailable(true);
        testItem.setOwner(owner);
        testItem.setRequest(request);

        testItemDto = new ItemDto();
        testItemDto.setId(1L);
        testItemDto.setName("Компрессор");
        testItemDto.setDescription("Воздушный компрессор");
        testItemDto.setAvailable(true);
        testItemDto.setRequestId(1L);
    }

    @Test
    void testToItemDto_ShouldConvertItemToDto() {
        ItemDto result = ItemMapper.toItemDto(testItem);

        assertNotNull(result);
        assertEquals(testItem.getId(), result.getId());
        assertEquals(testItem.getName(), result.getName());
        assertEquals(testItem.getDescription(), result.getDescription());
        assertEquals(testItem.getAvailable(), result.getAvailable());
        assertEquals(testItem.getRequest().getId(), result.getRequestId());
    }

    @Test
    void testToItemDto_WithNullItem_ShouldReturnNull() {
        ItemDto result = ItemMapper.toItemDto(null);

        assertNull(result);
    }

    @Test
    void testToItem_ShouldConvertDtoToItem() {
        Item result = ItemMapper.toItem(testItemDto);

        assertNotNull(result);
        assertEquals(testItemDto.getId(), result.getId());
        assertEquals(testItemDto.getName(), result.getName());
        assertEquals(testItemDto.getDescription(), result.getDescription());
        assertEquals(testItemDto.getAvailable(), result.getAvailable());
    }

    @Test
    void testToItem_WithNullDto_ShouldReturnNull() {
        Item result = ItemMapper.toItem(null);

        assertNull(result);
    }

    @Test
    void testToItemDto_WithAllFields_ShouldMapCorrectly() {
        testItem.setId(5L);
        testItem.setName("Новое название");
        testItem.setDescription("Новое описание");
        testItem.setAvailable(false);

        ItemDto result = ItemMapper.toItemDto(testItem);

        assertEquals(5L, result.getId());
        assertEquals("Новое название", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void testToItem_WithAllFields_ShouldMapCorrectly() {
        testItemDto.setId(10L);
        testItemDto.setName("Другое название");
        testItemDto.setDescription("Другое описание");
        testItemDto.setAvailable(false);

        Item result = ItemMapper.toItem(testItemDto);

        assertEquals(10L, result.getId());
        assertEquals("Другое название", result.getName());
        assertEquals("Другое описание", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void testToItemDto_WithEmptyName_ShouldMapCorrectly() {
        testItem.setName("");
        ItemDto result = ItemMapper.toItemDto(testItem);
        assertEquals("", result.getName());
    }

    @Test
    void testToItem_WithEmptyName_ShouldMapCorrectly() {
        testItemDto.setName("");
        Item result = ItemMapper.toItem(testItemDto);
        assertEquals("", result.getName());
    }

    @Test
    void testToItemDto_WithEmptyDescription_ShouldMapCorrectly() {
        testItem.setDescription("");
        ItemDto result = ItemMapper.toItemDto(testItem);
        assertEquals("", result.getDescription());
    }

    @Test
    void testToItem_WithEmptyDescription_ShouldMapCorrectly() {
        testItemDto.setDescription("");
        Item result = ItemMapper.toItem(testItemDto);
        assertEquals("", result.getDescription());
    }

    @Test
    void testToItemDto_WithLongName_ShouldMapCorrectly() {
        String longName = "Очень длинное название вещи которое может быть очень длинным";
        testItem.setName(longName);
        ItemDto result = ItemMapper.toItemDto(testItem);
        assertEquals(longName, result.getName());
    }

    @Test
    void testToItem_WithLongName_ShouldMapCorrectly() {
        String longName = "Очень длинное название вещи которое может быть очень длинным";
        testItemDto.setName(longName);
        Item result = ItemMapper.toItem(testItemDto);
        assertEquals(longName, result.getName());
    }

    @Test
    void testToItemDto_WithSpecialCharacters_ShouldMapCorrectly() {
        String specialName = "Название с символами: !@#$%^&*()";
        testItem.setName(specialName);
        ItemDto result = ItemMapper.toItemDto(testItem);
        assertEquals(specialName, result.getName());
    }

    @Test
    void testToItem_WithSpecialCharacters_ShouldMapCorrectly() {
        String specialName = "Название с символами: !@#$%^&*()";
        testItemDto.setName(specialName);
        Item result = ItemMapper.toItem(testItemDto);
        assertEquals(specialName, result.getName());
    }
}