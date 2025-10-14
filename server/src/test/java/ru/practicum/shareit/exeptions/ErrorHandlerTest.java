package ru.practicum.shareit.exeptions;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ErrorHandlerTest {

    @InjectMocks
    ErrorHandler errorHandler;

    @Test
    void testHandleNotFoundException_ShouldReturnNotFound() {
        NotFoundException exception = new NotFoundException("Тест не найден");
        ErrorHandler.ErrorResponse response = errorHandler.handleNotFoundException(exception);
        assertEquals("Тест не найден", response.getError());
    }

    @Test
    void testHandleValidationException_ShouldReturnBadRequest() {
        ValidationException exception = new ValidationException("Ошибка валидации");
        ErrorHandler.ErrorResponse response = errorHandler.handleBadRequestException(exception);
        assertEquals("Ошибка валидации", response.getError());
    }

    @Test
    void testHandleMissingRequestHeaderException_ShouldReturnBadRequest() {
        RuntimeException exception = new RuntimeException("Missing header");
        ErrorHandler.ErrorResponse response = errorHandler.handleBadRequestException(exception);
        assertNotNull(response.getError());
    }

    @Test
    void testHandleMethodArgumentTypeMismatchException_ShouldReturnBadRequest() {
        RuntimeException exception = new RuntimeException("Type mismatch");
        ErrorHandler.ErrorResponse response = errorHandler.handleBadRequestException(exception);
        assertNotNull(response.getError());
    }

    @Test
    void testHandleMethodArgumentNotValidException_ShouldReturnBadRequest() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getFieldError()).thenReturn(mock(org.springframework.validation.FieldError.class));
        when(exception.getFieldError().getDefaultMessage()).thenReturn("Поле не может быть пустым");
        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);
        assertTrue(response.getError().contains("Поле не может быть пустым"));
    }

    @Test
    void testHandleConstraintViolationException_ShouldReturnBadRequest() {
        ConstraintViolationException exception = new ConstraintViolationException("Ошибка ограничения", null);
        ErrorHandler.ErrorResponse response = errorHandler.handleConstraintViolationException(exception);
        assertTrue(response.getError().contains("Ошибка ограничения"));
    }

    @Test
    void testHandleConflictException_ShouldReturnConflict() {
        ConflictException exception = new ConflictException("Конфликт данных");
        ErrorHandler.ErrorResponse response = errorHandler.handleConflictException(exception);
        assertEquals("Конфликт данных", response.getError());
    }

    @Test
    void testHandleForbiddenException_ShouldReturnForbidden() {
        ForbiddenException exception = new ForbiddenException("Доступ запрещен");
        ErrorHandler.ErrorResponse response = errorHandler.handleForbiddenException(exception);
        assertEquals("Доступ запрещен", response.getError());
    }

    @Test
    void testHandleAllExceptions_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Внутренняя ошибка");
        ErrorHandler.ErrorResponse response = errorHandler.handleAllExceptions(exception);
        assertTrue(response.getError().contains("Внутренняя ошибка"));
    }
}