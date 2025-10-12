package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Getting user by id {}", id);
        return userClient.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return userClient.getAllUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                             @RequestBody UserDto userDto) {
        log.info("Updating user {} with data {}", id, userDto);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Deleting user {}", id);
        return userClient.deleteUser(id);
    }
}