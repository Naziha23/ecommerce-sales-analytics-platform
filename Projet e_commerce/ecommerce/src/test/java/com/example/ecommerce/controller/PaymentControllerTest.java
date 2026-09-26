package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PaymentDTO;
import com.example.ecommerce.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Devrait retourner tous les paiements")
    public void shouldGetAllPayments() throws Exception {
        PaymentDTO dto = PaymentDTO.builder().id(1L).orderId(1L).paymentMethod("CREDIT_CARD").build();
        Mockito.when(paymentService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].paymentMethod").value("CREDIT_CARD"));
    }

    @Test
    @DisplayName("Devrait créer un nouveau paiement")
    public void shouldCreatePayment() throws Exception {
        PaymentDTO input = PaymentDTO.builder().orderId(1L).paymentMethod("PAYPAL").build();
        PaymentDTO output = PaymentDTO.builder().id(2L).orderId(1L).paymentMethod("PAYPAL").build();
        
        Mockito.when(paymentService.create(any(PaymentDTO.class))).thenReturn(output);

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }
}
