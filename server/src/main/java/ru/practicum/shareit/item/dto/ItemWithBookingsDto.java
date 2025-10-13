package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithBookingsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    BookingInfo lastBooking = null;
    BookingInfo nextBooking = null;
    List<CommentDto> comments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BookingInfo {
        Long id;
        Long bookerId;
        LocalDateTime start;
        LocalDateTime end;
    }
}
