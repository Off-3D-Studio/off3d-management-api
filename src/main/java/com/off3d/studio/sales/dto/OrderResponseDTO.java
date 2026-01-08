package com.off3d.studio.sales.dto;

import com.off3d.studio.sales.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        LocalDateTime orderDate,
        BigDecimal totalPrice,
        OrderStatus status
) {}