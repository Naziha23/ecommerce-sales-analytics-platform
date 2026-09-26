package com.example.ecommerce.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Devrait gérer EntityNotFoundException et retourner 404")
    public void testHandleNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity non trouvée");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Entity non trouvée", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Devrait gérer BusinessException et retourner 409")
    public void testHandleBusiness() {
        BusinessException ex = new BusinessException("Conflit métier");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBusiness(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Conflit métier", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Devrait gérer Exception générique et retourner 500")
    public void testHandleGeneric() {
        Exception ex = new Exception("Erreur imprévue");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Une erreur interne s'est produite.", response.getBody().get("message"));
    }
}
