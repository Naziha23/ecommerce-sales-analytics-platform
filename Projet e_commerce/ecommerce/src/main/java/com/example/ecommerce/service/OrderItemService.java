package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderItemDTO> findAll() {
        return orderItemRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<OrderItemDTO> findByOrder(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public OrderItemDTO findById(Long id) {
        return orderItemRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id: " + id));
    }

    public OrderItemDTO create(OrderItemDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + dto.getOrderId()));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + dto.getProductId()));
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .build();
        return toDTO(orderItemRepository.save(item));
    }

    public void delete(Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new EntityNotFoundException("OrderItem not found with id: " + id);
        }
        orderItemRepository.deleteById(id);
    }

    private OrderItemDTO toDTO(OrderItem i) {
        return OrderItemDTO.builder()
                .id(i.getId())
                .orderId(i.getOrder().getId())
                .productId(i.getProduct().getId())
                .productName(i.getProduct().getName())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .build();
    }
}
