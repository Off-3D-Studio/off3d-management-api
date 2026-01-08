package com.off3d.studio.sales.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderRequestDTO(
        BigDecimal totalPrice,
        UUID customerId
) {}