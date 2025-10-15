// CHECKSTYLE:OFF
package ru.practicum.shareit.exeptions;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ExceptionsTest {

    @Test
    void testNotFoundException_ShouldCreateWithMessage() {
        String message = "Пользователь не найден";
        NotFoundException exception = new NotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testNotFoundException_WithNullMessage_ShouldHandleGracefully() {
        NotFoundException exception = new NotFoundException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testNotFoundException_WithEmptyMessage_ShouldHandleGracefully() {
        NotFoundException exception = new NotFoundException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    void testValidationException_ShouldCreateWithMessage() {
        String message = "Некорректные данные";
        ValidationException exception = new ValidationException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testValidationException_WithNullMessage_ShouldHandleGracefully() {
        ValidationException exception = new ValidationException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testValidationException_WithEmptyMessage_ShouldHandleGracefully() {
        ValidationException exception = new ValidationException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    void testConflictException_ShouldCreateWithMessage() {
        String message = "Конфликт данных";
        ConflictException exception = new ConflictException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConflictException_WithNullMessage_ShouldHandleGracefully() {
        ConflictException exception = new ConflictException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testConflictException_WithEmptyMessage_ShouldHandleGracefully() {
        ConflictException exception = new ConflictException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    void testForbiddenException_ShouldCreateWithMessage() {
        String message = "Доступ запрещен";
        ForbiddenException exception = new ForbiddenException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testForbiddenException_WithNullMessage_ShouldHandleGracefully() {
        ForbiddenException exception = new ForbiddenException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testForbiddenException_WithEmptyMessage_ShouldHandleGracefully() {
        ForbiddenException exception = new ForbiddenException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    void testNotFoundException_WithLongMessage_ShouldHandleGracefully() {
        String longMessage = "Очень длинное сообщение об ошибке которое может быть очень длинным и содержать много информации о том что именно пошло не так";
        NotFoundException exception = new NotFoundException(longMessage);

        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    void testValidationException_WithSpecialCharacters_ShouldHandleGracefully() {
        String specialMessage = "Сообщение с символами: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        ValidationException exception = new ValidationException(specialMessage);

        assertNotNull(exception);
        assertEquals(specialMessage, exception.getMessage());
    }

    @Test
    void testConflictException_WithRussianMessage_ShouldHandleGracefully() {
        String russianMessage = "Пользователь с таким email уже существует";
        ConflictException exception = new ConflictException(russianMessage);

        assertNotNull(exception);
        assertEquals(russianMessage, exception.getMessage());
    }

    @Test
    void testForbiddenException_WithFormattedMessage_ShouldHandleGracefully() {
        String formattedMessage = String.format("Пользователь с ID %d не может выполнить это действие", 123L);
        ForbiddenException exception = new ForbiddenException(formattedMessage);

        assertNotNull(exception);
        assertEquals(formattedMessage, exception.getMessage());
    }
}