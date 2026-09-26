package com.ecommerce.example.service;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private com.example.ecommerce.repository.OrderItemRepository orderItemRepository;

    @Mock
    private com.example.ecommerce.repository.PaymentRepository paymentRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("Naziha")
                .lastName("Sassi")
                .email("naziha@example.com")
                .build();

        order1 = Order.builder()
                .id(1L)
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("250.00"))
                .orderItems(new ArrayList<>())
                .build();

        order2 = Order.builder()
                .id(2L)
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CONFIRMED)
                .totalAmount(new BigDecimal("1200.00"))
                .orderItems(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Devrait retourner toutes les commandes sous forme de DTO")
    void shouldFindAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<OrderDTO> result = orderService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Naziha Sassi", result.get(0).getCustomerName());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner une commande par son ID")
    void shouldFindById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        OrderDTO result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCustomerId());
        assertEquals(new BigDecimal("250.00"), result.getTotalAmount());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une EntityNotFoundException si la commande n'existe pas")
    void shouldFindById_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.findById(99L));
        verify(orderRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Devrait retourner les commandes par ID client")
    void shouldFindByCustomer() {
        when(orderRepository.findByCustomerId(1L)).thenReturn(List.of(order1, order2));

        List<OrderDTO> result = orderService.findByCustomer(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Devrait retourner les commandes par statut")
    void shouldFindByStatus() {
        when(orderRepository.findByStatus(OrderStatus.CONFIRMED)).thenReturn(List.of(order2));

        List<OrderDTO> result = orderService.findByStatus(OrderStatus.CONFIRMED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.CONFIRMED, result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Devrait créer une nouvelle commande")
    void shouldCreateOrder_Success() {
        OrderDTO inputDto = OrderDTO.builder()
                .customerId(1L)
                .status(OrderStatus.PENDING)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(10L);
            saved.setOrderDate(LocalDateTime.now());
            saved.setTotalAmount(BigDecimal.ZERO);
            if (saved.getOrderItems() == null) {
                saved.setOrderItems(new ArrayList<>());
            }
            return saved;
        });

        OrderDTO result = orderService.create(inputDto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getCustomerId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(customerRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la création si le client n'existe pas")
    void shouldCreateOrder_CustomerNotFound() {
        OrderDTO inputDto = OrderDTO.builder()
                .customerId(99L)
                .build();

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.create(inputDto));
        verify(customerRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Devrait mettre à jour le statut d'une commande")
    void shouldUpdateStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDTO result = orderService.updateStatus(1L, OrderStatus.DELIVERED);

        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la mise à jour du statut si la commande n'existe pas")
    void shouldUpdateStatus_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.updateStatus(99L, OrderStatus.DELIVERED));
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Devrait supprimer une commande existante")
    void shouldDeleteOrder_Success() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        orderService.delete(1L);

        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la suppression si la commande n'existe pas")
    void shouldDeleteOrder_NotFound() {
        when(orderRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.delete(99L));
        verify(orderRepository, times(1)).existsById(99L);
        verify(orderRepository, never()).deleteById(anyLong());
    }
}
