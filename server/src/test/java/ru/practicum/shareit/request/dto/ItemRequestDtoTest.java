package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestDtoTest {

    @Test
    void testItemRequestDto_ShouldCreateWithAllFields() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Нужен сварочный аппарат");
        dto.setCreated(LocalDateTime.now());

        assertNotNull(dto);
        assertEquals("Нужен сварочный аппарат", dto.getDescription());
        assertNotNull(dto.getCreated());
    }

    @Test
    void testItemRequestDto_WithNullValues_ShouldHandleGracefully() {
        ItemRequestDto dto = new ItemRequestDto();

        assertNull(dto.getDescription());
        assertNull(dto.getCreated());
    }

    @Test
    void testItemRequestDto_AllArgsConstructor_ShouldCreateWithAllFields() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequestDto dto = new ItemRequestDto(1L, "Нужен компрессор", created, null);

        assertEquals(1L, dto.getId());
        assertEquals("Нужен компрессор", dto.getDescription());
        assertEquals(created, dto.getCreated());
    }

    @Test
    void testItemRequestDto_NoArgsConstructor_ShouldCreateEmptyObject() {
        ItemRequestDto dto = new ItemRequestDto();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getDescription());
        assertNull(dto.getCreated());
    }

    @Test
    void testItemRequestDto_EqualsAndHashCode_ShouldWorkCorrectly() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequestDto dto1 = new ItemRequestDto(1L, "Нужен генератор", created, null);
        ItemRequestDto dto2 = new ItemRequestDto(1L, "Нужен генератор", created, null);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testItemRequestDto_ToString_ShouldContainAllFields() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequestDto dto = new ItemRequestDto(1L, "Нужен лобзик", created, null);
        String toString = dto.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("description=Нужен лобзик"));
        assertTrue(toString.contains("created="));
    }

    @Test
    void testItemRequestDto_SetId_ShouldSetIdCorrectly() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(5L);

        assertEquals(5L, dto.getId());
    }

    @Test
    void testItemRequestDto_SetDescription_ShouldSetDescriptionCorrectly() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Новое описание");

        assertEquals("Новое описание", dto.getDescription());
    }

    @Test
    void testItemRequestDto_SetCreated_ShouldSetCreatedCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto dto = new ItemRequestDto();
        dto.setCreated(now);

        assertEquals(now, dto.getCreated());
    }
}