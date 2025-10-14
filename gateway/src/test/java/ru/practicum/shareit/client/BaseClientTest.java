package ru.practicum.shareit.client;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BaseClientTest {

    @Mock
    RestTemplate restTemplate;

    BaseClient baseClient;

    @BeforeEach
    void setUp() {
        baseClient = new BaseClient(restTemplate);
    }

    @Test
    void testGet_ShouldCallRestTemplateGet() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.get("/test");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals("test", result.getBody());
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void testPost_ShouldCallRestTemplatePost() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
        Object body = new Object();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.post("/test", body);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals("test", result.getBody());
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void testPatch_ShouldCallRestTemplateExchange() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
        Object body = new Object();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.patch("/test", body);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals("test", result.getBody());
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void testDelete_ShouldCallRestTemplateExchange() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> result = baseClient.delete("/test");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals("test", result.getBody());
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }
}
