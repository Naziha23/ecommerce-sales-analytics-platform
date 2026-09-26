package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PaymentDTO;
import com.example.ecommerce.entity.PaymentMethod;
import com.example.ecommerce.service.PaymentService;
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
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    @DisplayName("Devrait retourner tous les paiements")
    void shouldGetAllPayments() {
        PaymentDTO dto = PaymentDTO.builder().id(1L).orderId(1L).method(PaymentMethod.CARD).build();
        when(paymentService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<PaymentDTO>> response = paymentController.getAllPayments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(PaymentMethod.CARD, response.getBody().get(0).getMethod());
    }

    @Test
    @DisplayName("Devrait créer un nouveau paiement avec statut 201")
    void shouldCreatePayment() {
        PaymentDTO input = PaymentDTO.builder().orderId(1L).method(PaymentMethod.PAYPAL).build();
        PaymentDTO output = PaymentDTO.builder().id(2L).orderId(1L).method(PaymentMethod.PAYPAL).build();
        when(paymentService.create(any(PaymentDTO.class))).thenReturn(output);

        ResponseEntity<PaymentDTO> response = paymentController.createPayment(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    @DisplayName("Devrait retourner un paiement par son id")
    void shouldGetPaymentById() {
        PaymentDTO dto = PaymentDTO.builder().id(1L).method(PaymentMethod.PAYPAL).build();
        when(paymentService.findById(1L)).thenReturn(dto);

        ResponseEntity<PaymentDTO> response = paymentController.getPaymentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(PaymentMethod.PAYPAL, response.getBody().getMethod());
    }

    @Test
    @DisplayName("Devrait supprimer un paiement et retourner 204")
    void shouldDeletePayment() {
        doNothing().when(paymentService).delete(1L);

        ResponseEntity<Void> response = paymentController.deletePayment(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(paymentService).delete(1L);
    }
}
