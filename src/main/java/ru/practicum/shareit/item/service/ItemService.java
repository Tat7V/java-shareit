package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemWithBookingsDto getItemById(Long id, Long userId);

    List<ItemWithBookingsDto> getItemsByOwnerId(Long ownerId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId);

    List<ItemDto> searchItems(String text);
}