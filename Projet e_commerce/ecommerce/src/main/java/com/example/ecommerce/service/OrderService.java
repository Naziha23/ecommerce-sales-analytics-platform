package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public List<OrderDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public OrderDTO findById(Long id) {
        return orderRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    public List<OrderDTO> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public OrderDTO create(OrderDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + dto.getCustomerId()));
        Order order = Order.builder()
                .customer(customer)
                .status(dto.getStatus() != null ? dto.getStatus() : OrderStatus.PENDING)
                .build();
        return toDTO(orderRepository.save(order));
    }

    public OrderDTO updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        return toDTO(orderRepository.save(order));
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        // Contrainte d'intégrité : impossible de supprimer une commande avec des lignes de commande
        List<?> items = orderItemRepository.findByOrderId(id);
        if (!items.isEmpty()) {
            throw new BusinessException(
                "Impossible de supprimer cette commande : elle contient " + items.size() +
                " ligne(s) de commande. Veuillez d'abord supprimer ces lignes."
            );
        }
        // Contrainte d'intégrité : impossible de supprimer une commande ayant un paiement
        paymentRepository.findByOrderId(id).ifPresent(p -> {
            throw new BusinessException(
                "Impossible de supprimer cette commande : un paiement y est associé (ID #" + p.getId() +
                "). Veuillez d'abord supprimer le paiement."
            );
        });
        orderRepository.deleteById(id);
    }

    private OrderDTO toDTO(Order o) {
        List<OrderItemDTO> items = o.getOrderItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .orderId(o.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(o.getId())
                .customerId(o.getCustomer().getId())
                .customerName(o.getCustomer().getFirstName() + " " + o.getCustomer().getLastName())
                .orderDate(o.getOrderDate())
                .status(o.getStatus())
                .totalAmount(o.getTotalAmount())
                .orderItems(items)
                .build();
    }
}

