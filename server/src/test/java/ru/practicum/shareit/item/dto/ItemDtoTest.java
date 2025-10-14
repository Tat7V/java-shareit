// CHECKSTYLE:OFF
package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemDtoTest {

    UserDto owner;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = new UserDto();
        owner.setId(1L);
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Электрическая дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        itemDto.setOwner(owner);
    }

    @Test
    void testItemDtoCreation_ShouldCreateItemDtoWithAllFields() {
        assertEquals(1L, itemDto.getId());
        assertEquals("Дрель", itemDto.getName());
        assertEquals("Электрическая дрель", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getRequestId());
        assertEquals(owner, itemDto.getOwner());
    }

    @Test
    void testItemDtoAllArgsConstructor_ShouldCreateItemDtoWithAllFields() {
        ItemDto newItemDto = new ItemDto(2L, "Отвертка", "Набор отверток", true, 2L, owner);

        assertEquals(2L, newItemDto.getId());
        assertEquals("Отвертка", newItemDto.getName());
        assertEquals("Набор отверток", newItemDto.getDescription());
        assertTrue(newItemDto.getAvailable());
        assertEquals(2L, newItemDto.getRequestId());
        assertEquals(owner, newItemDto.getOwner());
    }

    @Test
    void testItemDtoNoArgsConstructor_ShouldCreateEmptyItemDto() {
        ItemDto emptyItemDto = new ItemDto();

        assertNull(emptyItemDto.getId());
        assertNull(emptyItemDto.getName());
        assertNull(emptyItemDto.getDescription());
        assertNull(emptyItemDto.getAvailable());
        assertNull(emptyItemDto.getRequestId());
        assertNull(emptyItemDto.getOwner());
    }

    @Test
    void testItemDtoEquals_ShouldReturnTrueForSameItemDtos() {
        ItemDto itemDto1 = new ItemDto(1L, "Дрель", "Электрическая дрель", true, 1L, owner);
        ItemDto itemDto2 = new ItemDto(1L, "Дрель", "Электрическая дрель", true, 1L, owner);

        assertEquals(itemDto1, itemDto2);
        assertEquals(itemDto1.hashCode(), itemDto2.hashCode());
    }

    @Test
    void testItemDtoEquals_ShouldReturnFalseForDifferentItemDtos() {
        ItemDto itemDto1 = new ItemDto(1L, "Дрель", "Электрическая дрель", true, 1L, owner);
        ItemDto itemDto2 = new ItemDto(2L, "Отвертка", "Набор отверток", true, 2L, owner);

        assertNotEquals(itemDto1, itemDto2);
    }

    @Test
    void testItemDtoToString_ShouldContainAllFields() {
        String toString = itemDto.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Дрель"));
        assertTrue(toString.contains("Электрическая дрель"));
        assertTrue(toString.contains("true"));
    }

    @Test
    void testItemDtoWithNullOwner_ShouldWorkCorrectly() {
        itemDto.setOwner(null);

        assertNull(itemDto.getOwner());
        assertEquals("Дрель", itemDto.getName());
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void testItemDtoWithNullRequestId_ShouldWorkCorrectly() {
        itemDto.setRequestId(null);

        assertNull(itemDto.getRequestId());
        assertEquals("Дрель", itemDto.getName());
        assertTrue(itemDto.getAvailable());
    }
}