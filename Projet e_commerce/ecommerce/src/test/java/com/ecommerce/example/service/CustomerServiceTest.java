package com.ecommerce.example.service;

import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = Customer.builder()
                .id(1L)
                .firstName("Naziha")
                .lastName("Sassi")
                .email("naziha@example.com")
                .phone("20111111")
                .city("Tunis")
                .country("Tunisia")
                .createdAt(LocalDateTime.now())
                .build();

        customer2 = Customer.builder()
                .id(2L)
                .firstName("Ahmed")
                .lastName("Ben Ali")
                .email("ahmed@example.com")
                .phone("20222222")
                .city("Sfax")
                .country("Tunisia")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Devrait retourner tous les clients sous forme de DTO")
    void shouldFindAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        List<CustomerDTO> result = customerService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Naziha", result.get(0).getFirstName());
        assertEquals("naziha@example.com", result.get(0).getEmail());
        assertEquals("Ahmed", result.get(1).getFirstName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner un client par son ID")
    void shouldFindById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));

        CustomerDTO result = customerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Naziha", result.getFirstName());
        assertEquals("Sassi", result.getLastName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une EntityNotFoundException si le client n'existe pas")
    void shouldFindById_NotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.findById(99L));
        verify(customerRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Devrait créer un nouveau client")
    void shouldCreateCustomer() {
        CustomerDTO inputDto = CustomerDTO.builder()
                .firstName("Sarra")
                .lastName("Trabelsi")
                .email("sarra@example.com")
                .phone("20333333")
                .city("Sousse")
                .country("Tunisia")
                .build();

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(3L);
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });

        CustomerDTO result = customerService.create(inputDto);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Sarra", result.getFirstName());
        assertEquals("sarra@example.com", result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Devrait mettre à jour un client existant")
    void shouldUpdateCustomer_Success() {
        CustomerDTO updateDto = CustomerDTO.builder()
                .firstName("Naziha")
                .lastName("Sassi")
                .email("naziha.new@example.com")
                .phone("20999999")
                .city("Tunis")
                .country("Tunisia")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerDTO result = customerService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("naziha.new@example.com", result.getEmail());
        assertEquals("20999999", result.getPhone());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la mise à jour si le client n'existe pas")
    void shouldUpdateCustomer_NotFound() {
        CustomerDTO updateDto = CustomerDTO.builder()
                .firstName("Unknown")
                .email("unknown@example.com")
                .build();

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.update(99L, updateDto));
        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Devrait supprimer un client existant")
    void shouldDeleteCustomer_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        customerService.delete(1L);

        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la suppression si le client n'existe pas")
    void shouldDeleteCustomer_NotFound() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> customerService.delete(99L));
        verify(customerRepository, times(1)).existsById(99L);
        verify(customerRepository, never()).deleteById(anyLong());
    }
}
