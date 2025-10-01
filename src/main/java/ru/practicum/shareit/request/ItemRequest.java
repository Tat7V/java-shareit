package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

//задание для следующего спринта, но нужен метод для маппера
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    Long id;

    @NotBlank(message = "Описание запроса не может быть пустым")
    String description;

    User requestor;
    LocalDateTime created;
}