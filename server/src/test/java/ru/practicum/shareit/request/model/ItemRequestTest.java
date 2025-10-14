// CHECKSTYLE:OFF
package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestTest {

    User requestor;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setId(1L);
        requestor.setName("Запрашивающий");
        requestor.setEmail("requestor@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void testItemRequestCreation_ShouldCreateItemRequestWithAllFields() {
        assertEquals(1L, itemRequest.getId());
        assertEquals("Нужна дрель", itemRequest.getDescription());
        assertEquals(requestor, itemRequest.getRequestor());
        assertNotNull(itemRequest.getCreated());
    }

    @Test
    void testItemRequestAllArgsConstructor_ShouldCreateItemRequestWithAllFields() {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest newRequest = new ItemRequest(2L, "Нужен компрессор", requestor, created);

        assertEquals(2L, newRequest.getId());
        assertEquals("Нужен компрессор", newRequest.getDescription());
        assertEquals(requestor, newRequest.getRequestor());
        assertEquals(created, newRequest.getCreated());
    }

    @Test
    void testItemRequestNoArgsConstructor_ShouldCreateEmptyItemRequest() {
        ItemRequest emptyRequest = new ItemRequest();

        assertNull(emptyRequest.getId());
        assertNull(emptyRequest.getDescription());
        assertNull(emptyRequest.getRequestor());
        assertNull(emptyRequest.getCreated());
    }

    @Test
    void testItemRequestEquals_ShouldReturnTrueForSameRequests() {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "Нужна дрель", requestor, created);
        ItemRequest request2 = new ItemRequest(1L, "Нужна дрель", requestor, created);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testItemRequestEquals_ShouldReturnFalseForDifferentRequests() {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "Нужна дрель", requestor, created);
        ItemRequest request2 = new ItemRequest(2L, "Нужен компрессор", requestor, created);

        assertNotEquals(request1, request2);
    }

    @Test
    void testItemRequestToString_ShouldContainAllFields() {
        String toString = itemRequest.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Нужна дрель"));
    }

    @Test
    void testItemRequestWithNullRequestor_ShouldWorkCorrectly() {
        itemRequest.setRequestor(null);

        assertNull(itemRequest.getRequestor());
        assertEquals("Нужна дрель", itemRequest.getDescription());
        assertNotNull(itemRequest.getCreated());
    }
}