package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ItemWithBookingsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    BookingInfo lastBooking;
    BookingInfo nextBooking;
    List<CommentDto> comments = new ArrayList<>();

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
