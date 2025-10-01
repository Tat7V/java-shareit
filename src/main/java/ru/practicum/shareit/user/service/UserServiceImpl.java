package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ConflictException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {

        checkEmailUniqueness(userDto.getEmail(), null);

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким ID " + id + " не найден."));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public  UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким ID " + id + " не найден."));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            checkEmailUniqueness(userDto.getEmail(), id);
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        User updatedUser = userRepository.update(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    private void checkEmailUniqueness(String email, Long excludeUserId) {
        if (excludeUserId == null) {
            userRepository.findByEmail(email)
                    .ifPresent(user -> {
                        throw new ConflictException("Email " + email + " уже используется");
                    });
        } else {
            if (userRepository.existsByEmailAndIdNot(email, excludeUserId)) {
                throw new ConflictException("Email " + email + " уже используется другим пользователем");
            }
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
}
