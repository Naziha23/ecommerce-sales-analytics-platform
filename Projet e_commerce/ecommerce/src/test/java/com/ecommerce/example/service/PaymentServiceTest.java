package com.ecommerce.example.service;

import com.example.ecommerce.dto.PaymentDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.PaymentMethod;
import com.example.ecommerce.entity.PaymentStatus;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.service.PaymentService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;
    private Payment payment1;
    private Payment payment2;

    @BeforeEach
    void setUp() {
        order = Order.builder().id(10L).build();

        payment1 = Payment.builder()
                .id(1L)
                .order(order)
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.PAID)
                .amount(new BigDecimal("250.00"))
                .paymentDate(LocalDateTime.now())
                .build();

        payment2 = Payment.builder()
                .id(2L)
                .order(order)
                .method(PaymentMethod.PAYPAL)
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("80.00"))
                .paymentDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Devrait retourner tous les paiements sous forme de DTO")
    void shouldFindAllPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));

        List<PaymentDTO> result = paymentService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(PaymentMethod.CARD, result.get(0).getMethod());
        assertEquals(PaymentStatus.PAID, result.get(0).getStatus());
        assertEquals(10L, result.get(0).getOrderId());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner un paiement par son ID")
    void shouldFindById_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment1));

        PaymentDTO result = paymentService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("250.00"), result.getAmount());
        assertEquals(PaymentMethod.CARD, result.getMethod());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une EntityNotFoundException si le paiement n'existe pas")
    void shouldFindById_NotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.findById(99L));
        verify(paymentRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Devrait retourner un paiement par son ID de commande")
    void shouldFindByOrder_Success() {
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(payment1));

        PaymentDTO result = paymentService.findByOrder(10L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10L, result.getOrderId());
        verify(paymentRepository, times(1)).findByOrderId(10L);
    }

    @Test
    @DisplayName("Devrait lancer une exception si aucun paiement n'est trouvé pour la commande")
    void shouldFindByOrder_NotFound() {
        when(paymentRepository.findByOrderId(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.findByOrder(99L));
        verify(paymentRepository, times(1)).findByOrderId(99L);
    }

    @Test
    @DisplayName("Devrait créer un nouveau paiement avec succès")
    void shouldCreatePayment_Success() {
        PaymentDTO inputDto = PaymentDTO.builder()
                .orderId(10L)
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.PAID)
                .amount(new BigDecimal("250.00"))
                .build();

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment saved = invocation.getArgument(0);
            saved.setId(5L);
            saved.setPaymentDate(LocalDateTime.now());
            return saved;
        });

        PaymentDTO result = paymentService.create(inputDto);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals(10L, result.getOrderId());
        assertEquals(PaymentMethod.CARD, result.getMethod());
        assertEquals(new BigDecimal("250.00"), result.getAmount());
        verify(orderRepository, times(1)).findById(10L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la création si la commande n'existe pas")
    void shouldCreatePayment_OrderNotFound() {
        PaymentDTO inputDto = PaymentDTO.builder()
                .orderId(99L)
                .build();

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.create(inputDto));
        verify(orderRepository, times(1)).findById(99L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Devrait supprimer un paiement existant")
    void shouldDeletePayment_Success() {
        when(paymentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(paymentRepository).deleteById(1L);

        paymentService.delete(1L);

        verify(paymentRepository, times(1)).existsById(1L);
        verify(paymentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la suppression si le paiement n'existe pas")
    void shouldDeletePayment_NotFound() {
        when(paymentRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> paymentService.delete(99L));
        verify(paymentRepository, times(1)).existsById(99L);
        verify(paymentRepository, never()).deleteById(anyLong());
    }
}
