package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.service.OrderItemService;
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

@WebMvcTest(controllers = OrderItemController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderItemService orderItemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Devrait retourner tous les order items")
    public void shouldGetAllOrderItems() throws Exception {
        OrderItemDTO dto = OrderItemDTO.builder().id(1L).orderId(1L).productId(1L).quantity(2).build();
        Mockito.when(orderItemService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    @DisplayName("Devrait créer un nouvel order item")
    public void shouldCreateOrderItem() throws Exception {
        OrderItemDTO input = OrderItemDTO.builder().orderId(1L).productId(1L).quantity(5).build();
        OrderItemDTO output = OrderItemDTO.builder().id(2L).orderId(1L).productId(1L).quantity(5).build();
        
        Mockito.when(orderItemService.create(any(OrderItemDTO.class))).thenReturn(output);

        mockMvc.perform(post("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }
}
