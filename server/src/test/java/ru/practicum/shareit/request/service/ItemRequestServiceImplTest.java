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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    ItemRequestDto itemRequestDto;
    ItemRequest itemRequest;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Тестовый пользователь");
        user.setEmail("тест@пример.ру");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна дрель");
        itemRequestDto.setCreated(LocalDateTime.now());
    }

    @Test
    void testCreateRequest_ShouldReturnCreatedRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.createRequest(itemRequestDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Нужна дрель", result.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void testCreateRequest_WithNonExistentUser_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(itemRequestDto, 1L));
    }

    @Test
    void testGetUserRequests_ShouldReturnRequestsList() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Нужна дрель", result.get(0).getDescription());
    }

    @Test
    void testGetAllRequests_ShouldReturnRequestsList() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetRequestById_ShouldReturnRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Нужна дрель", result.getDescription());
    }

    @Test
    void testGetRequestById_WithNonExistentRequest_ShouldThrowNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));
    }
}