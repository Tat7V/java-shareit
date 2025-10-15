// CHECKSTYLE:OFF
package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    User user;
    ItemRequest request;
    ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Дмитрий Волков");
        user.setEmail("dmitry@example.com");

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужен сварочный аппарат");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен сварочный аппарат");
    }

    @Test
    void testCreateRequest_ShouldReturnCreatedRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.createRequest(requestDto, 1L);

        assertNotNull(result);
        assertEquals("Нужен сварочный аппарат", result.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void testCreateRequest_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(requestDto, 999L));
    }

    @Test
    void testGetUserRequests_ShouldReturnUserRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Нужен сварочный аппарат", result.get(0).getDescription());
    }

    @Test
    void testGetUserRequests_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(999L));
    }

    @Test
    void testGetAllRequests_ShouldReturnAllRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(), any())).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllRequests_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(999L, 0, 10));
    }

    @Test
    void testGetRequestById_ShouldReturnRequest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals("Нужен сварочный аппарат", result.getDescription());
    }

    @Test
    void testGetRequestById_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 999L));
    }

    @Test
    void testGetRequestById_WithNonExistentRequest_ShouldThrowNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999L, 1L));
    }

    @Test
    void testCreateRequest_WithItems_ShouldReturnRequestWithItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Сварочный аппарат");
        item.setDescription("Полуавтоматический сварочный аппарат");
        item.setAvailable(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.createRequest(requestDto, 1L);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Сварочный аппарат", result.getItems().get(0).getName());
    }

    @Test
    void testGetUserRequests_WithItems_ShouldReturnRequestsWithItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Компрессор");
        item.setDescription("Воздушный компрессор");
        item.setAvailable(true);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getItems());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals("Компрессор", result.get(0).getItems().get(0).getName());
    }
}