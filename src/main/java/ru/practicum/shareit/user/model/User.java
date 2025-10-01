package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;

    @NotBlank(message = "Имя не может быть пустым")
    String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    String email;
}
