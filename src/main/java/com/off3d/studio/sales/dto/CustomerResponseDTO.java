package com.off3d.studio.sales.dto;

import java.util.Set;
import java.util.UUID;

public record CustomerResponseDTO(
        UUID id,
        String name,
        String email,
        String phone,
        Set<OrderResponseDTO> orders
) {}