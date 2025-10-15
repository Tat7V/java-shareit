// CHECKSTYLE:OFF
package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemTest {

    User owner;
    ItemRequest request;
    Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");

        item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Электрическая дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
    }

    @Test
    void testItemCreation_ShouldCreateItemWithAllFields() {
        assertEquals(1L, item.getId());
        assertEquals("Дрель", item.getName());
        assertEquals("Электрическая дрель", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testItemAllArgsConstructor_ShouldCreateItemWithAllFields() {
        Item newItem = new Item(2L, "Отвертка", "Набор отверток", true, owner, request);

        assertEquals(2L, newItem.getId());
        assertEquals("Отвертка", newItem.getName());
        assertEquals("Набор отверток", newItem.getDescription());
        assertTrue(newItem.getAvailable());
        assertEquals(owner, newItem.getOwner());
        assertEquals(request, newItem.getRequest());
    }

    @Test
    void testItemNoArgsConstructor_ShouldCreateEmptyItem() {
        Item emptyItem = new Item();

        assertNull(emptyItem.getId());
        assertNull(emptyItem.getName());
        assertNull(emptyItem.getDescription());
        assertNull(emptyItem.getAvailable());
        assertNull(emptyItem.getOwner());
        assertNull(emptyItem.getRequest());
    }

    @Test
    void testItemEquals_ShouldReturnTrueForSameItems() {
        Item item1 = new Item(1L, "Дрель", "Электрическая дрель", true, owner, request);
        Item item2 = new Item(1L, "Дрель", "Электрическая дрель", true, owner, request);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testItemEquals_ShouldReturnFalseForDifferentItems() {
        Item item1 = new Item(1L, "Дрель", "Электрическая дрель", true, owner, request);
        Item item2 = new Item(2L, "Отвертка", "Набор отверток", true, owner, request);

        assertNotEquals(item1, item2);
    }

    @Test
    void testItemToString_ShouldContainAllFields() {
        String toString = item.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Дрель"));
        assertTrue(toString.contains("Электрическая дрель"));
        assertTrue(toString.contains("true"));
    }

    @Test
    void testItemWithNullRequest_ShouldWorkCorrectly() {
        item.setRequest(null);

        assertNull(item.getRequest());
        assertNotNull(item.getOwner());
        assertEquals("Дрель", item.getName());
    }
}