package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerializeUserDto() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Тестовый пользователь");
        dto.setEmail("тест@пример.ру");

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Тестовый пользователь");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("тест@пример.ру");
    }

    @Test
    void testSerializeUserDtoWithNullValues() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName(null);
        dto.setEmail(null);

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }
}