package com.example.ecommerce.service;

import com.example.ecommerce.dto.PaymentDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public List<PaymentDTO> findAll() {
        return paymentRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public PaymentDTO findById(Long id) {
        return paymentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }

    public PaymentDTO findByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for order: " + orderId));
    }

    public PaymentDTO create(PaymentDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + dto.getOrderId()));
        Payment payment = Payment.builder()
                .order(order)
                .method(dto.getMethod())
                .status(dto.getStatus())
                .amount(dto.getAmount())
                .build();
        return toDTO(paymentRepository.save(payment));
    }

    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    private PaymentDTO toDTO(Payment p) {
        return PaymentDTO.builder()
                .id(p.getId())
                .orderId(p.getOrder().getId())
                .paymentDate(p.getPaymentDate())
                .method(p.getMethod())
                .status(p.getStatus())
                .amount(p.getAmount())
                .build();
    }
}
