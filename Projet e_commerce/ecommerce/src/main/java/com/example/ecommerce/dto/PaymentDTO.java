package com.example.ecommerce.dto;

import com.example.ecommerce.entity.PaymentMethod;
import com.example.ecommerce.entity.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private LocalDateTime paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;
}
