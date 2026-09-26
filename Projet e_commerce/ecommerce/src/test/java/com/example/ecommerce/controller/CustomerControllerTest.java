package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    @DisplayName("Devrait retourner tous les clients")
    void shouldGetAllCustomers() {
        CustomerDTO dto = CustomerDTO.builder().id(1L).firstName("John").lastName("Doe").build();
        when(customerService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<CustomerDTO>> response = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());
    }

    @Test
    @DisplayName("Devrait créer un nouveau client avec statut 201")
    void shouldCreateCustomer() {
        CustomerDTO input = CustomerDTO.builder().firstName("Jane").build();
        CustomerDTO output = CustomerDTO.builder().id(2L).firstName("Jane").build();
        when(customerService.create(any(CustomerDTO.class))).thenReturn(output);

        ResponseEntity<CustomerDTO> response = customerController.createCustomer(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    @DisplayName("Devrait retourner un client par son id")
    void shouldGetCustomerById() {
        CustomerDTO dto = CustomerDTO.builder().id(1L).firstName("Alice").build();
        when(customerService.findById(1L)).thenReturn(dto);

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice", response.getBody().getFirstName());
    }

    @Test
    @DisplayName("Devrait supprimer un client et retourner 204")
    void shouldDeleteCustomer() {
        doNothing().when(customerService).delete(1L);

        ResponseEntity<Void> response = customerController.deleteCustomer(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService).delete(1L);
    }
}
