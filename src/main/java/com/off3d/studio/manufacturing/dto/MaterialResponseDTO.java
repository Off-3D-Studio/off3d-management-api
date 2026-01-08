package com.off3d.studio.manufacturing.dto;

import com.off3d.studio.manufacturing.domain.MaterialType;

import java.util.UUID;

public record MaterialResponseDTO(
        UUID id,
        String name,
        String color,
        String brand,
        Double weightGrams,
        MaterialType type,
        String description
) {}
