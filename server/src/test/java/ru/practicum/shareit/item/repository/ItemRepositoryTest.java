// CHECKSTYLE:OFF
package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ItemRepository itemRepository;

    User owner;
    Item testItem;
    Item item2;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Алексей Смирнов");
        owner.setEmail("alexey@example.com");
        entityManager.persistAndFlush(owner);

        testItem = new Item();
        testItem.setName("Дрель");
        testItem.setDescription("Электрическая дрель для домашних работ");
        testItem.setAvailable(true);
        testItem.setOwner(owner);

        item2 = new Item();
        item2.setName("Молоток");
        item2.setDescription("Строительный молоток");
        item2.setAvailable(false);
        item2.setOwner(owner);
    }

    @Test
    void testSaveItem_ShouldSaveAndReturnItem() {
        Item savedItem = itemRepository.save(testItem);

        assertNotNull(savedItem.getId());
        assertEquals("Дрель", savedItem.getName());
        assertEquals("Электрическая дрель для домашних работ", savedItem.getDescription());
        assertTrue(savedItem.getAvailable());
        assertEquals(owner.getId(), savedItem.getOwner().getId());
    }

    @Test
    void testFindByOwnerIdOrderById_ShouldReturnItemsByOwner() {
        itemRepository.save(testItem);
        itemRepository.save(item2);
        entityManager.flush();

        List<Item> items = itemRepository.findByOwnerIdOrderById(owner.getId());

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Дрель")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Молоток")));
    }

    @Test
    void testFindByOwnerIdOrderById_WithNonExistentOwner_ShouldReturnEmpty() {
        List<Item> items = itemRepository.findByOwnerIdOrderById(999L);

        assertTrue(items.isEmpty());
    }

    @Test
    void testSearchAvailableItems_ShouldReturnAvailableItems() {
        itemRepository.save(testItem);
        itemRepository.save(item2);
        entityManager.flush();

        List<Item> items = itemRepository.searchAvailableItems("дрель", null);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.get(0).getName());
        assertTrue(items.get(0).getAvailable());
    }

    @Test
    void testSearchAvailableItems_WithUnavailableItem_ShouldNotReturnIt() {
        itemRepository.save(testItem);
        itemRepository.save(item2);
        entityManager.flush();

        List<Item> items = itemRepository.searchAvailableItems("молоток", null);

        assertTrue(items.isEmpty());
    }

    @Test
    void testSearchAvailableItems_WithEmptySearch_ShouldReturnAllAvailable() {
        itemRepository.save(testItem);
        itemRepository.save(item2);
        entityManager.flush();

        List<Item> items = itemRepository.searchAvailableItems("", null);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.get(0).getName());
    }

    @Test
    void testFindByRequestId_ShouldReturnItemsByRequest() {
        ru.practicum.shareit.request.model.ItemRequest request = new ru.practicum.shareit.request.model.ItemRequest();
        request.setDescription("Тестовый запрос");
        request.setRequestor(owner);
        request.setCreated(java.time.LocalDateTime.now());
        entityManager.persistAndFlush(request);

        testItem.setRequest(request);
        itemRepository.save(testItem);
        entityManager.flush();

        List<Item> items = itemRepository.findByRequestId(request.getId());

        assertEquals(1, items.size());
        assertEquals("Дрель", items.get(0).getName());
        assertEquals(request.getId(), items.get(0).getRequest().getId());
    }

    @Test
    void testFindByRequestId_WithNonExistentRequest_ShouldReturnEmpty() {
        List<Item> items = itemRepository.findByRequestId(999L);

        assertTrue(items.isEmpty());
    }

    @Test
    void testDeleteById_ShouldDeleteItem() {
        Item savedItem = itemRepository.save(testItem);
        entityManager.flush();

        itemRepository.deleteById(savedItem.getId());
        entityManager.flush();

        assertFalse(itemRepository.findById(savedItem.getId()).isPresent());
    }
}