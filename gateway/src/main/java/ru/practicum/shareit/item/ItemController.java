package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Creating item {} by user {}", itemDto, ownerId);
        return itemClient.createItem(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Getting item {} by user {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Getting items for owner {}", ownerId);
        return itemClient.getUserItems(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Updating item {} with data {} by user {}", itemId, itemDto, ownerId);
        return itemClient.updateItem(itemId, itemDto, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Searching items with text: {}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @RequestHeader(USER_ID_HEADER) Long authorId) {
        log.info("Adding comment {} to item {} by user {}", commentDto, itemId, authorId);
        return itemClient.addComment(itemId, commentDto, authorId);
    }
}