package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithBookingsDtoTest {

    @Autowired
    private JacksonTester<ItemWithBookingsDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeItemWithBookingsDto() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(1L);
        dto.setName("Тестовая вещь");
        dto.setDescription("Описание тестовой вещи");
        dto.setAvailable(true);
        dto.setRequestId(2L);

        ItemWithBookingsDto.BookingInfo lastBooking = new ItemWithBookingsDto.BookingInfo();
        lastBooking.setId(3L);
        lastBooking.setBookerId(4L);
        lastBooking.setStart(LocalDateTime.of(2023, 1, 1, 10, 0));
        lastBooking.setEnd(LocalDateTime.of(2023, 1, 2, 10, 0));
        dto.setLastBooking(lastBooking);

        ItemWithBookingsDto.BookingInfo nextBooking = new ItemWithBookingsDto.BookingInfo();
        nextBooking.setId(5L);
        nextBooking.setBookerId(6L);
        nextBooking.setStart(LocalDateTime.of(2023, 2, 1, 10, 0));
        nextBooking.setEnd(LocalDateTime.of(2023, 2, 2, 10, 0));
        dto.setNextBooking(nextBooking);

        dto.setComments(List.of());

        JsonContent<ItemWithBookingsDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Тестовая вещь");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание тестовой вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2023-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2023-01-02T10:00:00");

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2023-02-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2023-02-02T10:00:00");

        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
    }

    @Test
    void testSerializeWithNullValues() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(1L);
        dto.setName("Тестовая вещь");
        dto.setDescription("Описание тестовой вещи");
        dto.setAvailable(true);
        dto.setRequestId(null);
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(List.of());

        JsonContent<ItemWithBookingsDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Тестовая вещь");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание тестовой вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathValue("$.requestId").isNull();
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
    }
}