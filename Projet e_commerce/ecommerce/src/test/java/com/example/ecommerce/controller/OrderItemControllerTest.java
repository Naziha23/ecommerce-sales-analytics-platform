package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.service.OrderItemService;
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
class OrderItemControllerTest {

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderItemController orderItemController;

    @Test
    @DisplayName("Devrait retourner tous les order items")
    void shouldGetAllOrderItems() {
        OrderItemDTO dto = OrderItemDTO.builder().id(1L).orderId(1L).productId(1L).quantity(2).build();
        when(orderItemService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<OrderItemDTO>> response = orderItemController.getAllOrderItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(2, response.getBody().get(0).getQuantity());
    }

    @Test
    @DisplayName("Devrait créer un nouvel order item avec statut 201")
    void shouldCreateOrderItem() {
        OrderItemDTO input = OrderItemDTO.builder().orderId(1L).productId(1L).quantity(5).build();
        OrderItemDTO output = OrderItemDTO.builder().id(2L).orderId(1L).productId(1L).quantity(5).build();
        when(orderItemService.create(any(OrderItemDTO.class))).thenReturn(output);

        ResponseEntity<OrderItemDTO> response = orderItemController.createOrderItem(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    @DisplayName("Devrait retourner un order item par son id")
    void shouldGetOrderItemById() {
        OrderItemDTO dto = OrderItemDTO.builder().id(1L).quantity(3).build();
        when(orderItemService.findById(1L)).thenReturn(dto);

        ResponseEntity<OrderItemDTO> response = orderItemController.getOrderItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getQuantity());
    }

    @Test
    @DisplayName("Devrait supprimer un order item et retourner 204")
    void shouldDeleteOrderItem() {
        doNothing().when(orderItemService).delete(1L);

        ResponseEntity<Void> response = orderItemController.deleteOrderItem(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderItemService).delete(1L);
    }
}
