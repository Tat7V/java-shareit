package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestDtoTest {

    @Test
    void testItemRequestDto_ShouldCreateWithAllArgsConstructor() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Нужна дрель", created, null);

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("Нужна дрель", itemRequestDto.getDescription());
        assertEquals(created, itemRequestDto.getCreated());
        assertNull(itemRequestDto.getItems());
    }

    @Test
    void testItemRequestDto_ShouldCreateWithNoArgsConstructor() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        assertNull(itemRequestDto.getId());
        assertNull(itemRequestDto.getDescription());
        assertNull(itemRequestDto.getCreated());
        assertNull(itemRequestDto.getItems());
    }

    @Test
    void testItemRequestDto_ShouldSetAndGetFields() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        LocalDateTime created = LocalDateTime.now();

        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна дрель");
        itemRequestDto.setCreated(created);
        itemRequestDto.setItems(null);

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("Нужна дрель", itemRequestDto.getDescription());
        assertEquals(created, itemRequestDto.getCreated());
        assertNull(itemRequestDto.getItems());
    }

    @Test
    void testItemRequestDto_ShouldHaveCorrectToString() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна дрель");

        String toString = itemRequestDto.toString();

        assertTrue(toString.contains("ItemRequestDto"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("description=Нужна дрель"));
    }

    @Test
    void testItemRequestDto_ShouldHaveCorrectEqualsAndHashCode() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(1L);
        itemRequestDto1.setDescription("Нужна дрель");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(1L);
        itemRequestDto2.setDescription("Нужна дрель");

        ItemRequestDto itemRequestDto3 = new ItemRequestDto();
        itemRequestDto3.setId(2L);
        itemRequestDto3.setDescription("Нужен молоток");

        assertEquals(itemRequestDto1, itemRequestDto2);
        assertNotEquals(itemRequestDto1, itemRequestDto3);
        assertEquals(itemRequestDto1.hashCode(), itemRequestDto2.hashCode());
        assertNotEquals(itemRequestDto1.hashCode(), itemRequestDto3.hashCode());
    }
}