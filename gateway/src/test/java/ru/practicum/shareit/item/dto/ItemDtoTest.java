package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Тестовая вещь");
        dto.setDescription("Описание тестовой вещи");
        dto.setAvailable(true);
        dto.setRequestId(2L);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Тестовая вещь");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание тестовой вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }

    @Test
    void testDeserializeItemDto() throws Exception
    {
        String jsonContent = """
                {
                    "id": 1,
                    "name": "Тестовая вещь",
                    "description": "Описание тестовой вещи",
                    "available": true,
                    "requestId": 2
                }
                """;

        ItemDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Тестовая вещь");
        assertThat(result.getDescription()).isEqualTo("Описание тестовой вещи");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(2L);
    }

    @Test
    void testSerializeItemDtoWithNullValues() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Тестовая вещь");
        dto.setDescription("Описание тестовой вещи");
        dto.setAvailable(true);
        dto.setRequestId(null);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Тестовая вещь");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание тестовой вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathValue("$.requestId").isNull();
    }
}