// CHECKSTYLE:OFF
package ru.practicum.shareit.request.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user;
    ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Сергей Волков");
        user.setEmail("sergey@example.com");
        entityManager.persistAndFlush(user);

        request = new ItemRequest();
        request.setDescription("Нужна лопата для сада");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
    }

    @Test
    void testSaveItemRequest_ShouldSaveAndReturnRequest() {
        ItemRequest savedRequest = itemRequestRepository.save(request);

        assertNotNull(savedRequest.getId());
        assertEquals("Нужна лопата для сада", savedRequest.getDescription());
        assertEquals(user.getId(), savedRequest.getRequestor().getId());
        assertNotNull(savedRequest.getCreated());
    }

    @Test
    void testFindByRequestorIdOrderByCreatedDesc_ShouldReturnRequestsByUser() {
        itemRequestRepository.save(request);
        entityManager.flush();

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId());

        assertEquals(1, requests.size());
        assertEquals("Нужна лопата для сада", requests.get(0).getDescription());
        assertEquals(user.getId(), requests.get(0).getRequestor().getId());
    }

    @Test
    void testFindByRequestorIdOrderByCreatedDesc_WithNonExistentUser_ShouldReturnEmpty() {
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(999L);

        assertTrue(requests.isEmpty());
    }

    @Test
    void testFindByRequestorIdNotOrderByCreatedDesc_ShouldReturnRequestsByOtherUsers() {
        User otherUser = new User();
        otherUser.setName("Елена Морозова");
        otherUser.setEmail("elena@example.com");
        entityManager.persistAndFlush(otherUser);

        ItemRequest otherRequest = new ItemRequest();
        otherRequest.setDescription("Нужен велосипед");
        otherRequest.setRequestor(otherUser);
        otherRequest.setCreated(LocalDateTime.now());

        itemRequestRepository.save(request);
        itemRequestRepository.save(otherRequest);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(user.getId(), pageable);

        assertEquals(1, requests.size());
        assertEquals("Нужен велосипед", requests.get(0).getDescription());
        assertEquals(otherUser.getId(), requests.get(0).getRequestor().getId());
    }

    @Test
    void testFindByRequestorIdNotOrderByCreatedDesc_WithOnlyOwnRequests_ShouldReturnEmpty() {
        itemRequestRepository.save(request);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(user.getId(), pageable);

        assertTrue(requests.isEmpty());
    }

    @Test
    void testFindById_ShouldReturnRequest() {
        ItemRequest savedRequest = itemRequestRepository.save(request);
        entityManager.flush();

        assertTrue(itemRequestRepository.findById(savedRequest.getId()).isPresent());
        ItemRequest foundRequest = itemRequestRepository.findById(savedRequest.getId()).get();
        assertEquals("Нужна лопата для сада", foundRequest.getDescription());
    }

    @Test
    void testFindById_WithNonExistentId_ShouldReturnEmpty() {
        assertFalse(itemRequestRepository.findById(999L).isPresent());
    }

    @Test
    void testDeleteById_ShouldDeleteRequest() {
        ItemRequest savedRequest = itemRequestRepository.save(request);
        entityManager.flush();

        itemRequestRepository.deleteById(savedRequest.getId());
        entityManager.flush();

        assertFalse(itemRequestRepository.findById(savedRequest.getId()).isPresent());
    }

    @Test
    void testFindAll_ShouldReturnAllRequests() {
        User user2 = new User();
        user2.setName("Дмитрий Соколов");
        user2.setEmail("dmitry@example.com");
        entityManager.persistAndFlush(user2);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Нужен компьютер");
        request2.setRequestor(user2);
        request2.setCreated(LocalDateTime.now());

        itemRequestRepository.save(request);
        itemRequestRepository.save(request2);
        entityManager.flush();

        List<ItemRequest> requests = itemRequestRepository.findAll();

        assertEquals(2, requests.size());
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Нужна лопата для сада")));
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Нужен компьютер")));
    }
}
